package Tools;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfWriter;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;


/**
 * This ImageJ plugin exports the current image and its attached
 * vector graphic overlay (if existent) as a PDF file.
 * It uses the free OpenPDF library, which is based on iText4 but LGPL-licensed
 * (see <a href="https://github.com/LibrePDF/OpenPDF">
 * https://github.com/LibrePDF/OpenPDF</a>).
 * 
 * @author W. Burger
 * @version 2021/04/05 (migrated to OpenPDF, added image properties)
 * @version 2021/04/06 (added automatic image upscaling, open PFD after export)
 * @version 2021/04/21 (converted from PlugInFilter to PlugIn)
 */
@Deprecated		// newer version available!
public class Export_PDF_With_Overlay implements PlugIn {
	
	private static boolean IncludeImage = true;
	private static boolean IncludeOverlay = true;
	private static boolean IncludeImageProps = true;
	private static boolean UpscaleImage = true;
	private static int ImageUpscaleFactor = 1;
	private static int MinImageWidth = 300; // used to suggest ImageUpscaleFactor
	private static boolean OpenPdfAfterExport = false;
	
	/**
	 * Set true to have a default ICC profile (sRGB) added
	 * to the PDF file. Solves problems with viewing
	 * files in Acrobat X+.
	 */
	private static boolean AddIccProfile = true;

	/**
	 * Used to draw graphic strokes with zero stroke width.
	 */
	private static double DefaultStrokeWidth = 0.01;		// substitute for zero-width strokes
	private static String DefaultFileExtension = ".pdf";
	private static String CurrentOutputDirectory = "D:\\tmp"; //IJ.getDirectory("temp");
	
	private ImagePlus img;
	
	@Override
	public void run(String arg) {
		this.img = IJ.getImage();
		if (img == null) 
			return;
	
		if (!verifyPdfLib()) {
			return;
		}
		
		if (img.getStackSize() != 1) {
			IJ.error("Can only export single images (no stacks)!");
			return;
		}
		
		ImageProcessor ip = img.getProcessor();
		int width = ip.getWidth();
		ImageUpscaleFactor = (width >= MinImageWidth) ?
				1 : (int) Math.ceil((double)MinImageWidth / width) ;
		
		if (!getUserInput()) {
			return;
		}
		
		String dir = CurrentOutputDirectory;
		String name = img.getShortTitle();
		if (!UpscaleImage) {
			ImageUpscaleFactor = 1;
		}
		if (ImageUpscaleFactor > 1) {
			name = name + "X" + ImageUpscaleFactor;
		}
		
		Path path = askForFilePath(dir, name, "Save as PDF");
		if (path == null) {
			return;
		}

		String finalPath = createPdf(img, ImageUpscaleFactor, path);
		if (finalPath == null) 
			IJ.error("PDF export failed to " + path.toString());
		else
			IJ.log(String.format("PDF exported to %s (%d x upscaled)", finalPath, ImageUpscaleFactor));
		
		if (OpenPdfAfterExport && Desktop.isDesktopSupported()) {
			Desktop dt = Desktop.getDesktop();
			try {
				dt.open(path.toFile());
			} catch (Exception ex) {
				IJ.error("Could not open PDF file " + finalPath);
			}
		}
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * Saves a PDF of the input image.
	 * @param im input image of type {@link ij.ImagePlus}, possibly with attached vector overlay
	 * @param upscale scale factor (must be greater than 0)
	 * @param path complete path to the created PDF file
	 * @return
	 */
	private String createPdf(ImagePlus im, int upscale, Path path) {
		final int width  = im.getWidth();	// original image size
		final int height = im.getHeight();
		Overlay overlay = im.getOverlay();	// original overlay (if any)
		
		// step 1: create the PDF document
		Rectangle pageSize = new Rectangle(width, height);
		Document document = new Document(pageSize);
		
		try {
			// step 2: create a PDF writer
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));
			
			// step 3: open the document
			document.open();
			
			// step 4: create a template and the associated Graphics2D context
			PdfContentByte cb = writer.getDirectContent();
			
			// optional: set sRGB default viewing profile
			if (AddIccProfile) {
				ICC_Profile colorProfile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
				writer.setOutputIntents("Custom", null, "http://www.color.org", "sRGB IEC61966-2.1", colorProfile);
				//byte[] iccdata = java.awt.color.ICC_Profile.getInstance(ColorSpace.CS_sRGB).getData();
				//com.itextpdf.text.pdf.ICC_Profile icc = com.itextpdf.text.pdf.ICC_Profile.getInstance(iccdata);
				//writer.setOutputIntents("Custom", null, "http://www.color.org", "sRGB IEC61966-2.1", icc);
			}
						
			// insert the image
			if (IncludeImage) {
				if (upscale > 1) {	// upscale the image if needed (without interpolation)
					im = im.resize(width * upscale, height * upscale, "none");
				}
				//IJ.log("exporting image of size " + im.getWidth() + "/" + im.getHeight());
				com.lowagie.text.Image pdfImg = com.lowagie.text.Image.getInstance(im.getImage(), null);
				pdfImg.setAbsolutePosition(0, 0);
				pdfImg.scaleToFit(width, height);	// fit to the original size 		
				cb.addImage(pdfImg);
			}
			
			// optional: draw the vector overlay
			if (overlay != null && IncludeOverlay) {
				//IJ.log("exporting overlay");
				Graphics2D g2 = new PdfGraphics2D(cb, width, height);
				Roi[] roiArr = overlay.toArray();
				for (Roi roi : roiArr) {
					float sw = roi.getStrokeWidth();
					if (sw < 0.001f) {	// sometimes stroke width is simply not set (= 0)
						roi.setStrokeWidth(DefaultStrokeWidth);	// temporarily change stroke width
					}
					ImagePlus tmpIm = roi.getImage();
					roi.setImage(null); 	// trick learned from Wayne to ensure magnification is 1
					roi.drawOverlay(g2);	// replacement (recomm. by Wayne)
					roi.setImage(tmpIm);
					roi.setStrokeWidth(sw); // restore original stroke width
				}
				g2.dispose();
			}
			
			// optional: copy ImagePlus custom properties to PDF
			if (IncludeImageProps) {
				String[] props = im.getPropertiesAsArray();
				if (props != null) {
					for (int i = 0; i < props.length; i+=2) {
						String key = props[i];
						String val = props[i + 1];
						document.addHeader(key, val);
					}
				}
			}
			
		} catch (DocumentException de) {
			IJ.log(de.getMessage());
		} catch (IOException ioe) {
			IJ.log(ioe.getMessage());
		}
		
		// step 5: close the document
		document.close();
		return path.toString();
	}
	
	// ----------------------------------------------------------------------
	
	private boolean getUserInput() {
		GenericDialog gd = new GenericDialog("Export PDF");
		
		gd.addCheckbox("Include overlay", IncludeOverlay);
		gd.addCheckbox("Include image", IncludeImage);
		gd.addCheckbox("Include image meta-data", IncludeImageProps);
		gd.addCheckbox("Add sRGB ICC profile", AddIccProfile);
		gd.addCheckbox("Upscale image", UpscaleImage);
		gd.addNumericField("Image upscale factor", ImageUpscaleFactor, 0);
		gd.addCheckbox("Open PDF after export", OpenPdfAfterExport);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		IncludeOverlay = gd.getNextBoolean();
		IncludeImage = gd.getNextBoolean();
		IncludeImageProps = gd.getNextBoolean();
		AddIccProfile = gd.getNextBoolean();
		UpscaleImage = gd.getNextBoolean();
		ImageUpscaleFactor = Math.max((int) gd.getNextNumber(), 1);
		OpenPdfAfterExport = gd.getNextBoolean();
		return true;
	}
	
	// ----------------------------------------------------------------------
	
    private Path askForFilePath(String dir, String name, String title) {
    	SaveDialog od = new SaveDialog(title, dir, name, DefaultFileExtension);
    	dir  = od.getDirectory();
    	name = od.getFileName();
    	
    	if(name != null) {
    		CurrentOutputDirectory = dir;
    		return Paths.get(dir, name);
    	}
    	else
    		return null;
    }
	
	@SuppressWarnings("unused")
	private String stripFileExtension(String fileName) {
		int dotInd = fileName.lastIndexOf('.');
		// if dot is in the first position,
		// we are dealing with a hidden file rather than a DefaultFileExtension
		return (dotInd > 0) ? fileName.substring(0, dotInd) : fileName;
	}
	
	
	private boolean verifyPdfLib() {
		try {
			if (Class.forName("com.lowagie.text.Document") != null) {
				return true;
			}
		} catch (ClassNotFoundException e) { }
		IJ.error("This plugin requires LibrePDF/OpenPDF to be installed!\n" +
				"see https://github.com/LibrePDF/OpenPDF\n");
		return false;
	}


}


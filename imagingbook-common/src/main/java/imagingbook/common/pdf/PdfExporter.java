package imagingbook.common.pdf;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfWriter;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import imagingbook.Info;
import imagingbook.common.util.ParameterBundle;


/**
 * Exports an {@link ImagePlus} instance, including its vector overly
 * (if existent) to a PDF file.
 * Configuration is accomplished by passing a {@link PdfExporter.Parameters}
 * instance to the constructor.
 * If selected, core Type1 fonts are substituted and embedded in the PDF.
 * 
 * @author WB
 * @version 2022/03/15
 */
public class PdfExporter {

	private static double MinStrokeWidth = 0.01;		// substitute for zero-width strokes

	private final Parameters params;
	private final ImagePlus im;

	/**
	 * Parameter bundle for class {@link PdfExporter}.
	 * Annotations are used for inserting the complete parameter bundle in
	 * ImageJ's {@code GenericDialog}.
	 */
	public static class Parameters implements ParameterBundle {

		@DialogLabel("Author:")
		public String author = System.getProperty("user.name");
		@DialogLabel("Title:")
		public String title = null;

		@DialogLabel("Subject:")
		public String subject = "";

		@DialogLabel("Keywords:")
		public String keywords = "";

		@DialogLabel("Include raster image:")
		public boolean includeImage = true;
		@DialogLabel("Upscale image:")
		public boolean upscaleImage = true;
		@DialogLabel("Upscale factor:")@DialogDigits(0)
		public int upscaleFactor = 1;
		@DialogLabel("Add sRGB color profile:")
		public boolean addIccProfile = true;

		@DialogLabel("Include vector overlay:")
		public boolean includeOverlay = true;
		@DialogLabel("Min. stroke width:")@DialogDigits(2)
		public double minStrokeWidth = MinStrokeWidth;
		@DialogLabel("Embed core fonts:")
		public boolean embedCoreFonts = true;

		@DialogLabel("Include image props:")
		public boolean includeImageProps = true;

		@DialogHide
		public boolean debug = false;
		
		@Override
		public boolean validate() {
			return 
			upscaleFactor >= 1 && 
			minStrokeWidth >= 0;
		}
	}

	// ----------------------------------------------------------------------------

	public PdfExporter(ImagePlus im, Parameters params) {
		this.im = im;
		this.params = params;
	}

	public PdfExporter(ImagePlus im) {
		this(im, new Parameters());
	}

	// ----------------------------------------------------------------------------

	public String exportTo(Path path) {
		final int width  = im.getWidth();	// original image size
		final int height = im.getHeight();
		Overlay overlay = im.getOverlay();	// original overlay (if any)

		// Step 1: create the PDF document
		Rectangle pageSize = new Rectangle(width, height);
		
		try (Document document = new Document(pageSize)) {

			// Step 2: create a PDF writer
			PdfWriter writer = null;
			try {
				writer = PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));
			} catch (DocumentException | FileNotFoundException e) {
				if (params.debug) System.out.println("Could no create PdfWriter to path " + path.toString());
				return null;
			}

			// Step 3: open the document and set various properties (TODO)
			document.open();
			document.addTitle(params.title);
			document.addAuthor(params.author);
			document.addSubject(params.subject);	
			document.addKeywords(params.keywords);
			document.addCreationDate();			
			document.addCreator("ImageJ Plugin (imagingbook)");
			document.addProducer(this.getClass().getName() + " " + Info.getVersionInfo());

			// Step 4: create a template and the associated Graphics2D context
			PdfContentByte cb = writer.getDirectContent();

			// Step 5: set sRGB default viewing profile
			if (params.addIccProfile) {
				ICC_Profile colorProfile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
				try {
					writer.setOutputIntents("Custom", null, "http://www.color.org", "sRGB IEC61966-2.1", colorProfile);
				} catch (IOException e) { 
					if (params.debug) System.out.println("Could not add ICC profile!");
				}
				//byte[] iccdata = java.awt.color.ICC_Profile.getInstance(ColorSpace.CS_sRGB).getData();
				//com.itextpdf.text.pdf.ICC_Profile icc = com.itextpdf.text.pdf.ICC_Profile.getInstance(iccdata);
			}

			// Step 6: insert the image
			if (params.includeImage) {
				ImagePlus im2 = this.im;
				int upscale = params.upscaleFactor;
				if (upscale > 1) {	// upscale the image if needed (without interpolation)
					im2 = im.resize(width * upscale, height * upscale, "none");
				}
				
				try {
					Image pdfImg = com.lowagie.text.Image.getInstance(im2.getImage(), null);
					pdfImg.setAbsolutePosition(0, 0);
					pdfImg.scaleToFit(width, height);	// fit to the original size 		
					cb.addImage(pdfImg);
				} catch (BadElementException | IOException e) {
					if (params.debug) System.out.println("Could not insert image!");
					return null;
				}
			}

			// Step 7: draw the vector overlay
			if (overlay != null && params.includeOverlay) {
				// https://stackoverflow.com/questions/17667615/how-can-itext-embed-font-used-by-jfreechart-for-chart-title-and-labels?rq=1
				Graphics2D g2 = (params.embedCoreFonts) ? 
						new PdfGraphics2D(cb, width, height, new CoreFontMapper(), false, false, 0) : 
						new PdfGraphics2D(cb, width, height); // no core font embedding

				Roi[] roiArr = overlay.toArray();
				for (Roi roi : roiArr) {
					double sw = roi.getStrokeWidth();
					if (sw < params.minStrokeWidth) {	// sometimes stroke width is simply not set (= 0)
						roi.setStrokeWidth(params.minStrokeWidth);	// temporarily change stroke width
					}
					ImagePlus tmpIm = roi.getImage();
					roi.setImage(null); 	// trick learned from Wayne to ensure magnification is 1
					roi.drawOverlay(g2);	// replacement (recomm. by Wayne)
					roi.setImage(tmpIm);
					if (sw < 0.001f) {
						roi.setStrokeWidth(sw); // restore original stroke width
					}	
				}
				g2.dispose();
			}

			// Step 8: copy ImagePlus custom properties to PDF
			if (params.includeImageProps) {
				String[] props = im.getPropertiesAsArray();
				if (props != null) {
					for (int i = 0; i < props.length; i+=2) {
						String key = props[i];
						String val = props[i + 1];
						document.addHeader(key, val);
					}
				}
			}

			// Step 9: close PDF document
			//document.close();
		}	// auto-close
		
		return path.toAbsolutePath().toString();
	}

}

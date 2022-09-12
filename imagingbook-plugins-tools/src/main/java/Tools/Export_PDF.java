/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Tools;

import java.awt.Desktop;
import java.nio.file.Path;
import java.nio.file.Paths;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import imagingbook.core.FileUtils;
import imagingbook.pdf.PdfExporter;
import imagingbook.pdf.PdfExporter.Parameters;
import imagingbook.pdf.Utils;


/**
 * This ImageJ plugin exports the current image and its attached
 * vector graphic overlay (if existent) as a PDF file.
 * It uses the free OpenPDF library, which is based on iText4 but LGPL-licensed
 * (see <a href="https://github.com/LibrePDF/OpenPDF">
 * https://github.com/LibrePDF/OpenPDF</a>).
 * 
 * @author WB
 * @version 2021/04/05 (migrated to OpenPDF, added image properties)
 * @version 2021/04/06 (added automatic image upscaling, open PFD after export)
 * @version 2021/04/21 (converted from PlugInFilter to PlugIn)
 * @version 2021/10/26 (added property insertion - TODO)
 * @version 2022/03/14 (complete rewrite, main functionality moved to
 * {@link PdfExporter}, fixed font embedding)
 * @version 2022/04/23 (removed DialogListener)
 * 
 * TODO: fix current directory mechanism
 * 
 */
public class Export_PDF implements PlugIn {
	
	static {
		LogStream.redirectSystem();
	}
	
	private static int MinImageWidth = 512; // used to suggest ImageUpscaleFactor
	private static String DefaultFileExtension = ".pdf";
	private static boolean OpenPdfAfterExport = false;
	
	private ImagePlus img;
	private Parameters params;
	
	@Override
	public void run(String arg) {
		this.img = IJ.getImage();
		if (img == null) 
			return;
	
		if (!Utils.verifyPdfLib()) {
			IJ.error("PDF library not found!");
			return;
		}
		
		if (img.getStackSize() != 1) {
			IJ.error("Can only export single images (no stacks)!");
			return;
		}
		
		// -------------------------------------------------
		
		int width = img.getWidth();
		String filename = img.getShortTitle();

		params = new Parameters();
		params.title = img.getShortTitle();
		params.upscaleFactor = (width >= MinImageWidth) ? 1 :
					(int) Math.ceil((double)MinImageWidth / width) ;
		params.upscaleImage = (params.upscaleFactor > 1);
		params.includeOverlay = (img.getOverlay() != null);
					
		if (!getUserInput(params)) {
			return;
		}
		
		if (!params.upscaleImage || params.upscaleFactor < 1) {
			params.upscaleFactor = 1;
		}
		
		if (params.upscaleImage && params.upscaleFactor > 1) {
			filename = filename + "-X" + params.upscaleFactor;
			params.title = params.title + " (" + params.upscaleFactor + "x upscaled)";
		}
		
		String directory = FileUtils.getCurrentDirectory(this.getClass());
		if (directory == null)
			directory = IJ.getDirectory("image");
		
		Path path = askForFilePath(directory, filename, "Save as PDF");
		if (path == null) {
			return;
		}
		
		directory = path.getParent().toString();
		FileUtils.setCurrentDirectory(this.getClass(), path);
		
		// ----------------------------------------------------------------
		PdfExporter exporter = new PdfExporter(img, params);
		String finalPath = exporter.exportTo(path);
		if (finalPath == null) 
			IJ.error("PDF export failed to " + path.toString());
		else
			IJ.log("PDF exported to " + finalPath);
		// ----------------------------------------------------------------
		
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
	
	private boolean getUserInput(Parameters params) {
		GenericDialog gd = new GenericDialog("Export PDF");
		
		params.addToDialog(gd);
		gd.addCheckbox("Open PDF after export", OpenPdfAfterExport);
		
		gd.showDialog();
		
		if (gd.wasCanceled()) {
			return false;
		}
		
		params.getFromDialog(gd);
		OpenPdfAfterExport = gd.getNextBoolean();
		return params.validate();
	}
	
	// ----------------------------------------------------------------------
	
    private Path askForFilePath(String dir, String name, String title) {
    	SaveDialog od = new SaveDialog(title, dir, name, DefaultFileExtension);
    	dir  = od.getDirectory();
    	name = od.getFileName();
    	
    	if(name != null) {
//    		DefaultOutputDirectory = dir;
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
	
}


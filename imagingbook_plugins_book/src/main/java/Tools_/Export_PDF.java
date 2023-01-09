/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Tools_;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import imagingbook.core.FileUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.pdf.PdfExporter;
import imagingbook.pdf.PdfExporter.Parameters;
import imagingbook.pdf.Utils;

import java.awt.Desktop;
import java.nio.file.Path;
import java.nio.file.Paths;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

/**
 * This ImageJ plugin exports the current image and its attached vector graphic overlay (if existent) as a PDF file. It
 * uses the free OpenPDF library, which is based on iText4 but LGPL-licensed (see
 * <a href="https://github.com/LibrePDF/OpenPDF">https://github.com/LibrePDF/OpenPDF</a>).
 *
 * @author WB
 * @version 2022/04/23 (removed DialogListener)
 * @see PdfExporter
 */
public class Export_PDF implements PlugIn, JavaDocHelp {
	
//	TODO: fix current directory mechanism
	
	private static int MinImageWidth = 512; // used to suggest ImageUpscaleFactor
	private static String DefaultFileExtension = ".pdf";
	private static boolean OpenPdfAfterExport = false;
	
	private ImagePlus im;
	private Parameters params;
	
	@Override
	public void run(String arg) {
		this.im = IJ.getImage();
		if (im == null) 
			return;
	
		if (!Utils.verifyPdfLib()) {
			IJ.error("PDF library not found!");
			return;
		}
		
		if (im.getStackSize() != 1) {
			IJ.error("Can only export single images (no stacks)!");
			return;
		}
		
		// -------------------------------------------------
		
		int width = im.getWidth();
		String filename = im.getShortTitle();

		params = new Parameters();
		params.title = im.getShortTitle();
		params.upscaleFactor = (width >= MinImageWidth) ? 1 :
					(int) Math.ceil((double)MinImageWidth / width) ;
		params.upscaleImage = (params.upscaleFactor > 1);
		params.includeOverlay = (im.getOverlay() != null);
					
		if (!runDialog(params)) {
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
		PdfExporter exporter = new PdfExporter(im, params);
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
	
	private boolean runDialog(Parameters params) {
		GenericDialog gd = new GenericDialog("Export PDF");
		addToDialog(params, gd);
		gd.addCheckbox("Open PDF after export", OpenPdfAfterExport);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		getFromDialog(params, gd);
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
	
}


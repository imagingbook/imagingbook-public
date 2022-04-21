/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package Color_Quantization;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.quantize.ColorQuantizer;
import imagingbook.common.color.quantize.MedianCutQuantizer;

/**
 * ImageJ plugin demonstrating the use of the {@link MedianCutQuantizer} class.
 * 
 * @author WB
 * @version 2017/01/03
 */
public class Median_Cut_Quantization implements PlugInFilter {
	
	private static int NCOLORS = 16;
	private static boolean CREATE_INDEXED_IMAGE = true; 
	private static boolean CREATE_RGB_IMAGE = false;
	private static boolean LIST_COLOR_TABLE = false;
	
	static {
		LogStream.redirectSystem();
	}
	
	String title;
	
	public int setup(String arg, ImagePlus imp) {
		this.title = imp.getShortTitle();
		return DOES_RGB + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
		//Parameters params = new Parameters();
		
		if (!showDialog())
			return;
		
		ColorProcessor cp = (ColorProcessor) ip;
		
		// create a quantizer object
		ColorQuantizer quantizer = new MedianCutQuantizer(cp, NCOLORS);
		int nCols = quantizer.getColorMap().length;
		
		if (CREATE_INDEXED_IMAGE) {
			// quantize to an indexed color image
			ByteProcessor qip = quantizer.quantize(cp);
			ImagePlus imp = new ImagePlus(title + "-MedianCut" + nCols, qip);
			imp.setTypeToColor256();
			imp.show();
		}
		
//		if (CREATE_RGB_IMAGE) {
//			// quantize to a full-color RGB image
//			int[] rgbPix = quantizer.quantize(pixels);
//			ColorProcessor rgbIp = new ColorProcessor(cp.getWidth(), cp.getHeight(), rgbPix);
//			(new ImagePlus("Quantized RGB Image (" + nCols + " colors)" , rgbIp)).show();
//		}
		
		if (LIST_COLOR_TABLE) {
			quantizer.listColorMap();
		}
	}
	
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog(Median_Cut_Quantization.class.getSimpleName());
		gd.addNumericField("No. of colors (2,..,256)", NCOLORS, 0);
		gd.addCheckbox("Create indexed color image", CREATE_INDEXED_IMAGE);
		gd.addCheckbox("Create quantized RGB image", CREATE_RGB_IMAGE);
		gd.addCheckbox("List quantized color table", LIST_COLOR_TABLE);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		int nc = (int) gd.getNextNumber();
		nc = Math.min(nc, 255);
		nc = Math.max(2, nc);
		
		NCOLORS = nc;
//		params.maxColors = nc;
		CREATE_INDEXED_IMAGE = gd.getNextBoolean();
		CREATE_RGB_IMAGE = gd.getNextBoolean();
		LIST_COLOR_TABLE = gd.getNextBoolean();
		return true;
	}
}

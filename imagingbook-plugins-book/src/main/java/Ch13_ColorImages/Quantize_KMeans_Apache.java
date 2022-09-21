/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package Ch13_ColorImages;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.quantize.ColorQuantizer;
import imagingbook.common.color.quantize.KMeansClusteringQuantizerApache;
import imagingbook.common.color.quantize.KMeansClusteringQuantizerApache.Parameters;


/**
 * ImageJ plugin demonstrating the use of the {@link KMeansClusteringQuantizerApache} class.
 * 
 * @author WB
 * @version 2017/01/03
 */
public class Quantize_KMeans_Apache implements PlugInFilter {
	
	private static boolean CREATE_INDEXED_IMAGE = true; 
	private static boolean CREATE_RGB_IMAGE = false;
	private static boolean LIST_COLOR_TABLE = false;
	
	static {
		LogStream.redirectSystem();
	}
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
		Parameters params = new Parameters();
	
		if (!showDialog(params))
			return;
		
		ColorProcessor cp = (ColorProcessor) ip;
		int[] pixels = (int[]) cp.getPixels();
		
		// create a quantizer object
		ColorQuantizer quantizer = new KMeansClusteringQuantizerApache(pixels, params);
		int nCols = quantizer.getColorMap().length;
		
		if (CREATE_INDEXED_IMAGE) {
			// quantize to an indexed color image
			ByteProcessor idxIp = quantizer.quantize(cp);
			(new ImagePlus("Quantized Index Color Image (" + nCols + " colors)", idxIp)).show();
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
	
	private boolean showDialog(Parameters params) {
		GenericDialog gd = new GenericDialog(Quantize_KMeans_Apache.class.getSimpleName());
		gd.addNumericField("No. of colors (2,..,256)", params.maxColors, 0);
		gd.addNumericField("Max. iterations", params.maxIterations, 0);
		
		gd.addCheckbox("Create indexed color image", CREATE_INDEXED_IMAGE);
		gd.addCheckbox("Create quantized RGB image", CREATE_RGB_IMAGE);
		gd.addCheckbox("List quantized color table", LIST_COLOR_TABLE);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		int nc = (int) gd.getNextNumber();
		nc = Math.min(nc, 255);
		nc = Math.max(2, nc);

		params.maxColors = nc;
		params.maxIterations = (int) gd.getNextNumber();
		
		CREATE_INDEXED_IMAGE = gd.getNextBoolean();
		CREATE_RGB_IMAGE = gd.getNextBoolean();
		LIST_COLOR_TABLE = gd.getNextBoolean();
		return true;
	}
}

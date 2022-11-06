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
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.quantize.ColorQuantizer;
import imagingbook.common.color.quantize.KMeansClusteringQuantizer;
import imagingbook.common.color.quantize.KMeansClusteringQuantizer.InitialClusterMethod;


/**
 * ImageJ plugin demonstrating the use of the {@link KMeansClusteringQuantizer} class.
 * 
 * @author WB
 * @version 2017/01/03
 */
public class Quantize_KMeans implements PlugInFilter {
	
	private static int NCOLORS = 16;
	private static int MaxIterations = 500;
	private static InitialClusterMethod SplMethod = InitialClusterMethod.Random;
	
	private static boolean CREATE_INDEXED_IMAGE = true; 
	private static boolean CREATE_RGB_IMAGE = false;
	private static boolean LIST_COLOR_TABLE = false;
	
	private ImagePlus im;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
//		Parameters params = new Parameters();
	
		if (!runDialog())
			return;
		
		ColorProcessor cp = (ColorProcessor) ip;
		int[] pixels = (int[]) cp.getPixels();
		
		// create a quantizer object
		ColorQuantizer quantizer = new KMeansClusteringQuantizer(pixels, NCOLORS, SplMethod, MaxIterations);
		int nCols = quantizer.getColorMap().length;
		
		String title = im.getShortTitle() + "-Octree";
		
		if (CREATE_INDEXED_IMAGE) {
			// quantize to an indexed color image
			ByteProcessor idxIp = quantizer.quantize(cp);
			(new ImagePlus("Quantized Index Color Image (" + nCols + " colors)", idxIp)).show();
		}
		
		if (CREATE_RGB_IMAGE) {
			// quantize to a full-color RGB image
			int[] rgbPix = quantizer.quantize((int[])cp.getPixels());
			ColorProcessor rgbIp = new ColorProcessor(cp.getWidth(), cp.getHeight(), rgbPix);
			new ImagePlus(title + "-RGB-" + nCols , rgbIp).show();
		}
		
		if (LIST_COLOR_TABLE) {
			quantizer.listColorMap();
		}
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(Quantize_KMeans.class.getSimpleName());
		gd.addNumericField("No. of colors (2,..,256)", NCOLORS, 0);
		gd.addNumericField("Max. iterations", MaxIterations, 0);
		gd.addEnumChoice("Sampling method", SplMethod);
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
		MaxIterations = (int) gd.getNextNumber();
		SplMethod = gd.getNextEnumChoice(InitialClusterMethod.class);
		CREATE_INDEXED_IMAGE = gd.getNextBoolean();
		CREATE_RGB_IMAGE = gd.getNextBoolean();
		LIST_COLOR_TABLE = gd.getNextBoolean();
		return true;
	}
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package HistogramsStatistics;

import java.awt.Rectangle;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.image.IntegralImage;

/**
 * This ImageJ plugin first calculates the integral image for the current
 * image (8 bit grayscale only) and uses it to find the mean and variance
 * inside the specified rectangle (ROI).
 *
 * @author WB
 *
 */
public class Integral_Image_GetRoiStatistics implements PlugInFilter {
	
	private ImagePlus im = null;

	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_8G + ROI_REQUIRED + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		Roi roi = im.getRoi();
		if (roi.getType() != Roi.RECTANGLE) {
			IJ.error("Rectangular selection required!");
			return;
		}
		
		Rectangle rect = roi.getBounds();
		
		int u0 = rect.x;
		int v0 = rect.y;
		int u1 = u0 + rect.width - 1;
		int v1 = v0 + rect.height - 1;
		
		IJ.log("rect = " + rect);
				
		IntegralImage iI = new IntegralImage((ByteProcessor) ip);
		double mean = iI.getMean(u0, v0, u1, v1);
		double var = iI.getVariance(u0, v0, u1, v1);
		
		IJ.log("ROI area = " + rect.width * rect.height);
		IJ.log("ROI mean = " + String.format("%.3f", mean));
		IJ.log("ROI variance = " + String.format("%.3f", var));
		IJ.log("ROI stddev = " + String.format("%.3f", Math.sqrt(var)));

	}
	
//	private float[][] toFloatArray(long[][] A) {
//		float[][] B = new float[A.length][A[0].length];
//		for (int i = 0; i < A.length; i++) {
//			for (int j = 0; j < A[0].length; j++) {
//				B[i][j] = (float) A[i][j];
//			}
//		}
//		return B;
//	}

}

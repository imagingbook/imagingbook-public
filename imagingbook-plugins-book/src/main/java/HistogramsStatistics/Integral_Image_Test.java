/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package HistogramsStatistics;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.image.IntegralImage;

/**
 * This ImageJ plugin calculates the integral image for the current
 * image (8 bit grayscale only) and displays the resulting first and
 * second order summed area tables.
 * @author WB
 *
 */
public class Integral_Image_Test implements PlugInFilter {

	public int setup(String arg0, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
			
		IntegralImage iI = new IntegralImage((ByteProcessor) ip);
		
		long[][] S1 = iI.getS1();
		long[][] S2 = iI.getS2();
		
		FloatProcessor fS1 = new FloatProcessor(toFloatArray(S1));
		new ImagePlus("S1", fS1).show();
		
		FloatProcessor fS2 = new FloatProcessor(toFloatArray(S2));
		new ImagePlus("S2", fS2).show();
	}
	
	float[][] toFloatArray(long[][] A) {
		float[][] B = new float[A.length][A[0].length];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[0].length; j++) {
				B[i][j] = (float) A[i][j];
			}
		}
		return B;
	}

}

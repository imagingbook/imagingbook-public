/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Sift;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.sift.scalespace.DogScaleSpace;
import imagingbook.common.sift.scalespace.GaussianScaleSpace;

/**
 * Builds a hierarchical Difference-of-Gaussian scale space representation 
 * from the input image and displays all scale levels.
 * To create a linear scale space (without decimation) set 'P' = 1 and 
 * 'topLevel' to an arbitrary value.
 * Consult the book for additional details.
 */

public class Show_DogScaleSpace implements PlugInFilter {
	
	static double sigma_0 = 1.6;	// initial scale level
	static double sigma_s = 0.5;	// original image (sampling) scale
	
	static int Q = 3;	// scale steps per octave
	static int P = 4;	// number of octaves
	static int botLevel = 0;	// index q of bottom level in each octave
	static int topLevel = Q;	// index q of top level in each octave

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		return DOES_8G + DOES_32 + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		FloatProcessor fp = (FloatProcessor) ip.convertToFloat();
		GaussianScaleSpace gss =
			new GaussianScaleSpace(fp, sigma_s, sigma_0, P, Q, botLevel, topLevel);
		DogScaleSpace dss = new DogScaleSpace(gss);
		dss.show("DoG");
	}

}

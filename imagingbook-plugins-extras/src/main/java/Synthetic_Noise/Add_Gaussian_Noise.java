/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Synthetic_Noise;

import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * TODO: Remove or bring up to standards!
 * @author WB
 *
 */
public class Add_Gaussian_Noise implements PlugInFilter {

	static double SIGMA = 5.0;

	public int setup(String args, ImagePlus imp) {
		return DOES_32;	// works only for FloatProcessor
	}

	public void run(ImageProcessor ip) {
		addGaussianNoise((FloatProcessor) ip, SIGMA);
	}

	void addGaussianNoise (FloatProcessor I, double sigma) {
		int w = I.getWidth();
		int h = I.getHeight();
		Random rg = new Random();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				float val = I.getf(u, v);
				float noise = (float) (rg.nextGaussian() * sigma);
				I.setf(u, v, val + noise);
			}
		}
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package Filters;
import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Simple version of the Jitter filter. Works for all image types
 * but does not handle image borders.
 * The input image is destructively modified.
 * 
 * @author wilbur
 * @version 2016/11/01
 */
public class Jitter_Filter_Simple implements PlugInFilter {
	
	int rad = 3;	// the radius (should be user-specified)
		
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip1) {
		final int w = ip1.getWidth();
		final int h = ip1.getHeight();
		
		Random rnd = new Random();
		ImageProcessor ip2 = ip1.duplicate();
		
		final int d = 2 * rad + 1;	// width/height of the "kernel"
		
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int rx = rnd.nextInt(d) - rad;
				int ry = rnd.nextInt(d) - rad;
				// pick a random position inside the current support region
				int p = ip2.getPixel(u + rx, v + ry);
				// replace the current center pixel
				ip1.putPixel(u, v, p);
			}
		}
	}
	
}

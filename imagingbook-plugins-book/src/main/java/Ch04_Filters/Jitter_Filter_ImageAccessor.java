/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch04_Filters;
import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.ImageAccessor;

/**
 * Jitter filter implemented with {@link ImageAccessor} for transparent
 * image access.
 * Works for all image types, using nearest-border-pixel strategy.
 * The input image is destructively modified.
 * 
 * @author wilbur
 * @version 2016/11/01
 * 
 * @see ImageAccessor
 * @see OutOfBoundsStrategy
 */
public class Jitter_Filter_ImageAccessor implements PlugInFilter {
	
	final int rad = 3;	// the radius (should be user-specified)
	
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip1) {
		final int w = ip1.getWidth();
		final int h = ip1.getHeight();
		final int d = 2 * rad + 1;	// width/height of the "kernel"
		
		ImageProcessor ip2 = ip1.duplicate();
		ImageAccessor ia1 = ImageAccessor.create(ip1);
		ImageAccessor ia2 = ImageAccessor.create(ip2, OutOfBoundsStrategy.NearestBorder, null);

		Random rnd = new Random();
		
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int rx = rnd.nextInt(d) - rad;
				int ry = rnd.nextInt(d) - rad;
				// pick a random position inside the current support region
				float[] p = ia2.getPix(u + rx, v + ry);
				// replace the current center pixel in ip1
				ia1.setPix(u, v, p);
			}
		}
		
		ia2 = null;
		ip2 = null;
	}
	
}

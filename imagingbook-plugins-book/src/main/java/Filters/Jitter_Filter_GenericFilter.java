/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Filters;
import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.image.PixelPack;

/**
 * Implementation of the "Jitter filter" based on the {@link GenericFilter}
 * class. Works for all image types, using nearest-border-pixel strategy.
 * The input image is destructively modified.
 * See the inner class {@link JitterFilter} for the actual implementation
 * of the filter.
 * 
 * @author WB
 * @version 2021/01/12
 */
public class Jitter_Filter_GenericFilter implements PlugInFilter {
	
	private static final int RAD = 3;	// the radius (should be user-specified)
		
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		GenericFilter filter = new JitterFilter();	// see below
		filter.applyTo(ip);
	}
	
	// --------------------------------------------------------------
	
	/**
	 * This inner class actually implements the Jitter filter,
	 * based on the functionality provided by {@link GenericFilter}.
	 */
	private static class JitterFilter extends GenericFilterVector {
		private final int dist = 2 * RAD + 1;	// width/height of the "kernel"
		private final Random rnd = new Random();
		
		@Override
		protected float[] doPixel(PixelPack source, int u, int v) {
			int rx = rnd.nextInt(dist) - RAD;
			int ry = rnd.nextInt(dist) - RAD;
			return source.getPix(rx, ry);
		}
	}
	
}

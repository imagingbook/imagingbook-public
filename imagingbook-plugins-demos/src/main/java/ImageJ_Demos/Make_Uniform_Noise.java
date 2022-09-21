/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Demos;

import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Fills the active image with uniform noise.
 * 
 * @author WB
 *
 */
public class Make_Uniform_Noise implements PlugInFilter {
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		Random rg = new Random();
		int w = ip.getWidth();
		int h = ip.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				ip.putPixel(u, v, rg.nextInt(256));
			}
		}
	}


}


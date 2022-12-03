/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch13_ColorImages;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * <p>
 * ImageJ plugin, increases the brightness of a RGB color image by 10 units
 * (each color component) without bit operations (Version 2). See Sec. 13.1
 * (Prog. 13.2) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class Brighten_Rgb_2 implements PlugInFilter {
	
	private static int INCREASE = 10;	// increase by 10 units
	
	static final int R = 0, G = 1, B = 2;	// component indices

	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;	// this plugin works on RGB images 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		//make sure image is of type ColorProcessor
		ColorProcessor cp = (ColorProcessor) ip; 
		int[] RGB = new int[3];

		for (int v = 0; v < cp.getHeight(); v++) {
			for (int u = 0; u < cp.getWidth(); u++) {
				cp.getPixel(u, v, RGB); 
				RGB[R] = Math.min(RGB[R] + INCREASE, 255);  // limit to 255
				RGB[G] = Math.min(RGB[G] + INCREASE, 255);
				RGB[B] = Math.min(RGB[B] + INCREASE, 255);
				cp.putPixel(u, v, RGB); 
			}
		}
	}
	
}

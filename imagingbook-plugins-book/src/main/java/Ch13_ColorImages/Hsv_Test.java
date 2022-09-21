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
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.colorspace.HsvColorSpace;

/* This plugin rotates the color hue by 120 degrees */

public class Hsv_Test implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		final int[] RGB = new int[3];
		
		HsvColorSpace cc = HsvColorSpace.getInstance();
		
		for (int v = 0; v < cp.getHeight(); v++) {
			for (int u = 0; u < cp.getWidth(); u++) {
				cp.getPixel(u, v, RGB);

				float[] HSV = cc.fromRGB (RgbUtils.normalize(RGB)); 	// all HSV components are in [0,1]
				HSV[0] = (HSV[0] + 1.0f/3) % 1.0f;	// shift hue by 120 degrees
				int[] RGBnew = RgbUtils.unnormalize(cc.toRGB(HSV));

				cp.putPixel(u, v, RGBnew);
			}
		}
	}
}
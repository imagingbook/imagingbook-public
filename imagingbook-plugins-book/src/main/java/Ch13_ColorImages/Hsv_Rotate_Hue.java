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
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.colorspace.HsvColorSpace;
import imagingbook.common.math.Arithmetic;

/**
 * <p>
 * ImageJ plugin, "rotates" the hue (H) of colors in HSV space by 120 degrees.
 * Saturation (S) and value (V) remains unchanged. The input image is modified.
 * Applying the plugin 3 times to the same image should reproduce the original
 * image. See Sec. 13.2.3 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class Hsv_Rotate_Hue implements PlugInFilter {
	
	private static double ROT = 120.0 / 360;	// shift hue by 1/3 of full circle (120 degrees)

	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;
	}

	@Override
	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		final int[] RGB = new int[3];
		
		HsvColorSpace cc = HsvColorSpace.getInstance();
		
		for (int v = 0; v < cp.getHeight(); v++) {
			for (int u = 0; u < cp.getWidth(); u++) {
				cp.getPixel(u, v, RGB);

				float[] HSV = cc.fromRGB(RgbUtils.normalize(RGB)); 	// all HSV components are in [0,1]
//				HSV[0] = (HSV[0] + ROT) % 1.0f;							// works for positive ROT only
				HSV[0] = (float) Arithmetic.mod(HSV[0] + ROT, 1.0);		// ROT may be pos. or negative
				int[] RGBnew = RgbUtils.denormalize(cc.toRGB(HSV));

				cp.putPixel(u, v, RGBnew);
			}
		}
	}
}
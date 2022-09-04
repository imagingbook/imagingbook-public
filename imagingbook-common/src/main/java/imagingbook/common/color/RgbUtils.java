/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color;

import java.awt.Color;

import imagingbook.common.math.Matrix;

public abstract class RgbUtils {
	
	private RgbUtils() {}

	public static int[] intToRgb(int p) {
		int[] RGB = new int[3];
		intToRgb(p, RGB);
		return RGB;
	}
	
	public static void intToRgb(int p, int[] RGB) {
		RGB[0] = ((p >> 16) & 0xFF);
		RGB[1] = ((p >> 8) & 0xFF);
		RGB[2] = (p & 0xFF);
	}

	public static int rgbToInt(int red, int grn, int blu) {
		return ((red & 0xff)<<16) | ((grn & 0xff)<<8) | blu & 0xff;
	}
	
	// -------------------------------------------------------------
	
	/**
	 * Converts integer RGB values (assumed to be in [0,255])
	 * to float values in [0,1].
	 * 
	 * @param RGB a sequence of R,G,B values or {@code int[]}
	 * @return the RGB values normalized to [0,1]
	 */
	public static float[] normalize(int... RGB) {
		float[] rgb = {RGB[0]/255f, RGB[1]/255f, RGB[2]/255f};
		return rgb;
	}
	
	/**
	 * Converts float RGB values (assumed to be in [0,1]) to integer values
	 * in [0,255].
	 * 
	 * @param rgb RGB float values in [0,1]
	 * @return RGB integer values in [0,255]
	 */
	public static int[] unnormalize(float[] rgb) {
		int[] RGB = new int[3];
		for (int i = 0; i < 3; i++) {			
			RGB[i] = Math.round(rgb[i] * 255f);
			if (RGB[i] < 0)
				RGB[i] = 0;
			else if (RGB[i] > 255)
				RGB[i] = 255;
		}
		return RGB;
	}
	
	
	// -------------------------------------------------------------

	/**
	 * Interpolates linearly between two specified colors.
	 * @param ca first color (to be interpolated from)
	 * @param cb second color (to be interpolated to)
	 * @param t interpolation coefficient, must be in [0,1]
	 * @return the interpolated color
	 */
	public static Color interpolate(Color ca, Color cb, double t) {
		if (t < 0 || t > 1) {
			throw new IllegalArgumentException("interpolation coefficient must be in [0,1] but is " + t);
		}
		float[] a = ca.getRGBColorComponents(null);
		float[] b = cb.getRGBColorComponents(null);
		float[] c = Matrix.lerp(a, b, (float) t);
		return new Color(c[0], c[1], c[2]);
	}

	/**
	 * Interpolates linearly between the colors in the specified color palette.
	 * The interpolation coefficient must be in [0,1]. If 0, the first palette color
	 * is returned, if 1 the last color.
	 * @param palette an array of colors (at least 2)
	 * @param t interpolation coefficient, must be in [0,1]
	 * @return the interpolated color
	 */
	public static Color interpolate(Color[] palette, double t) {
		if (palette.length < 2) {
			throw new IllegalArgumentException("length of color palette must be at least 2 but is " + palette.length);
		}
		if (t < 0 || t > 1) {
			throw new IllegalArgumentException("interpolation coefficient must be in [0,1] but is " + t);
		}
		final int n = palette.length;
		double x = t * (n - 1);			// x = 0,...,n-1 (float palette index)
		int lo = (int) Math.floor(x);	// lower palette color index
		int hi = (int) Math.ceil(x);	// upper palette color index
		return interpolate(palette[lo], palette[hi], x - lo);
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color;

import java.awt.Color;

import imagingbook.common.math.Matrix;

/**
 * This class defines static methods for manipulating and converting RGB colors.
 * @author WB
 *
 */
public abstract class RgbUtils {
	
	private RgbUtils() {}
	
	/** ITU BR.601 weights for RGB to Y (luma) conversion. */
	public static final double[] ITU601RgbWeights = {0.299, 0.587, 0.114}; 
	
	/** ITU BR.709 weights for RGB to Y (luma) conversion. */
	public static final double[] ITU709RgbWeights = {0.2126, 0.7152, 0.0722}; 
	
	public static double[] getDefaultWeights() {
		return ITU709RgbWeights.clone();
	}

	/**
	 * Converts the given integer-encoded 8-bit RGB color
	 * to a 3-element {@code int[]} with components red, green and blue.
	 * 
	 * @param argb integer-encoded 8-bit RGB color in ARGB format
	 * @return {@code int[]} with R, G, B components
	 */
	public static int[] intToRgb(int argb) {
		int[] RGB = new int[3];
		decodeIntToRgb(argb, RGB);
		return RGB;
	}
	
	/**
	 * Converts the given integer-encoded 8-bit RGB color to a 3-element
	 * {@code int[]} with components red, green and blue. Fills the specified
	 * {@code int[]}, nothing is returned.
	 * 
	 * @param argb integer-encoded 8-bit RGB color in ARGB format
	 * @param RGB  {@code int[]} with R, G, B components
	 */
	public static void decodeIntToRgb(int argb, int[] RGB) {
		RGB[0] = ((argb >> 16) & 0xFF);
		RGB[1] = ((argb >> 8) & 0xFF);
		RGB[2] = (argb & 0xFF);
	}

	/**
	 * Encodes the given RGB component values into a single 32-bit {@code int} value
	 * in ARGB format (with transparency A set to zero).
	 * 
	 * @param RGB {@code int[]} with R, G, B components
	 * @return integer-encoded 8-bit RGB color in ARGB format
	 */
	public static int encodeRgbToInt(int[] RGB) {
		return encodeRgbToInt(RGB[0], RGB[1], RGB[2]);
	}
	
	/**
	 * Encodes the given r, g, b component values into a single 32-bit {@code int}
	 * value in ARGB format (with transparency A set to zero).
	 * 
	 * @param r red component value
	 * @param g breen component value
	 * @param b blue component value
	 * @return integer-encoded 8-bit RGB color in ARGB format
	 */
	public static int encodeRgbToInt(int r, int g, int b) {
		return ((r & 0xff)<<16) | ((g & 0xff)<<8) | b & 0xff;
	}
	
	// -------------------------------------------------------------
	
	/**
	 * Converts integer RGB values (with components assumed to be in [0,255])
	 * to float values in [0,1].
	 * 
	 * @param RGB a sequence of R,G,B values or {@code int[]}
	 * @return the RGB values normalized to [0,1]
	 */
	public static float[] normalize(int[] RGB) {
		return new float[] {RGB[0]/255f, RGB[1]/255f, RGB[2]/255f};
	}
	
	public static double[] normalizeD(int[] RGB) {
		return new double[] {RGB[0]/255.0, RGB[1]/255.0, RGB[2]/255.0};
	}
	
	/**
	 * Converts float RGB values (with components assumed to be in [0,1]) to integer
	 * values in [0,255].
	 * 
	 * @param rgb RGB float values in [0,1]
	 * @return RGB integer values in [0,255]
	 */
	public static int[] denormalize(float[] rgb) {
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
	
	/**
	 * Converts double RGB values (with components assumed to be in [0,1]) to
	 * integer values in [0,255].
	 * 
	 * @param rgb RGB float values in [0,1]
	 * @return RGB integer values in [0,255]
	 */
	public static int[] denormalizeD(double[] rgb) {
		int[] RGB = new int[3];
		for (int i = 0; i < 3; i++) {			
			RGB[i] = (int) Math.round(rgb[i] * 255);
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
	 * 
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
	 * Interpolates linearly between the colors in the specified color palette. The
	 * interpolation coefficient must be in [0,1]. If 0, the first palette color is
	 * returned, if 1 the last color.
	 * 
	 * @param palette an array of colors (at least 2)
	 * @param t       interpolation coefficient, must be in [0,1]
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

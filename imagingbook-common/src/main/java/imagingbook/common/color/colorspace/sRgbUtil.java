/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import java.util.Locale;
import java.util.Random;

/**
 * This is a utility class with static methods for gamma correction
 * used by LabColorSpace and LuvColorSpace color spaces.
 * Implemented with double values for better accuracy.
 * Should be modified to implement a a subclass of ColorSpace.
 * TODO: duplicate in lib.color? add JavaDoc!
 */
public abstract class sRgbUtil {
	
	// specs according to official sRGB standard:
	private static final double s = 12.92;
	private static final double a0 = 0.0031308;
	private static final double b0 = s * a0;	// 0.040449936
	private static final double d = 0.055;
	private static final double gamma = 2.4;
	
	/**
	 * Forward Gamma correction (from linear to non-linear component values) for sRGB.
	 * 
	 * @param lc linear component value in [0,1]
	 * @return gamma-corrected (non-linear) component value
	 */
    public static double gammaFwd(double lc) {
		return (lc <= a0) ?
			(lc * s) :
			((1 + d) * Math.pow(lc, 1 / gamma) - d);
    }
    
    /**
	 * Inverse Gamma correction (from non-linear to linear component values) for sRGB.
	 * 
	 * @param nc non-linear (Gamma-corrected) component value in [0,1]
	 * @return linear component value
	 */
    public static double gammaInv(double nc) {
    	return (nc <= b0) ?
    		(nc / s) :
			Math.pow((nc + d) / (1 + d), gamma);
    }
    
	public static float[] sRgbToRgb(float[] srgb) { // all components in [0,1]
		float R = (float) sRgbUtil.gammaInv(srgb[0]);
		float G = (float) sRgbUtil.gammaInv(srgb[1]);
		float B = (float) sRgbUtil.gammaInv(srgb[2]);
		return new float[] { R, G, B };
	}

	public static float[] rgbToSrgb(float[] rgb) { // all components in [0,1]
		float sR = (float) sRgbUtil.gammaFwd(rgb[0]);
		float sG = (float) sRgbUtil.gammaFwd(rgb[1]);
		float sB = (float) sRgbUtil.gammaFwd(rgb[2]);
		return new float[] { sR, sG, sB };
	}
    
    // -------------------------------------------------------------------------
	
	public static void main(String[] args) {
		Random rg = new Random();
		for (int i = 0; i < 20; i++) {
			double lc = rg.nextDouble();
			double nc = gammaFwd(lc);
			System.out.format(Locale.US, "lc = %.8f,  nc = %.8f, check = %.8f\n", lc, nc, lc-gammaInv(nc));
		}
		System.out.println("" + (s * a0));

	}
    
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

/**
 * This class defines static methods for gamma correction for sRGB which are
 * used, e.g., by {@link LabColorSpace} and {@link LuvColorSpace} color spaces.
 * Implemented with {@code double} variables for better accuracy.
 * 
 * @author WB
 * @version 2022/09/01
 */
public abstract class sRgbUtil {
	
	private sRgbUtil() {}
	
	// specs according to official sRGB standard:
	private static final double S = 12.92;
	private static final double A0 = 0.0031308;
	private static final double B0 = S * A0;	// 0.040449936
	private static final double D = 0.055;
	private static final double GAMMA = 2.4;
	private static final double iGAMMA = 1.0 / GAMMA;
	
	/**
	 * Forward Gamma correction (from linear to non-linear component values) for sRGB.
	 * 
	 * @param lc linear component value in [0,1]
	 * @return gamma-corrected (non-linear) component value
	 */
    public static double gammaFwd(double lc) {
		return (lc <= A0) ?
			(lc * S) :
			((1 + D) * Math.pow(lc, iGAMMA) - D);
    }
    
    /**
	 * Inverse Gamma correction (from non-linear to linear component values) for sRGB.
	 * 
	 * @param nlc non-linear (Gamma-corrected) component value in [0,1]
	 * @return linear component value
	 */
    public static double gammaInv(double nlc) {
    	return (nlc <= B0) ?
    		(nlc / S) :
			Math.pow((nlc + D) / (1 + D), GAMMA);
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
    
}

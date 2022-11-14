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
	
	private static final GammaMappingFunction GammaMap = GammaMappingFunction.sRGB;
    
	public static float[] sRgbToRgb(float[] srgb) { // all components in [0,1]
		float[] rgb = srgb.clone();
		sRgbToRgbD(rgb);
		return rgb;
	}

	public static float[] rgbToSrgb(float[] rgb) { // all components in [0,1]
		float[] srgb = rgb.clone();
		rgbToSrgbD(srgb);
		return srgb;
	}
	
	// destructive versions
	
	public static void sRgbToRgbD(float[] srgb) { // all components in [0,1]
		for (int i = 0; i < srgb.length; i++) {
			srgb[i] = GammaMap.applyInv(srgb[i]);
		}
	}
	
	public static void rgbToSrgbD(float[] rgb) { // all components in [0,1]
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = GammaMap.applyFwd(rgb[i]);
		}
	}
	
}

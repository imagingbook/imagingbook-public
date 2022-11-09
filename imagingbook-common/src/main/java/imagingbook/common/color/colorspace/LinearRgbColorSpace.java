/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;


/**
 * Basic linear RGB color space, related to sRGB by gamma
 * correction.
 * Everything is D65, components of all {@code float[]} colors
 * are supposed to be in [0,1].
 * This is a singleton class with no public constructors,
 * use {@link #getInstance()} to obtain the single instance.
 * 
 */
@SuppressWarnings("serial")
public class LinearRgbColorSpace extends ColorSpace {
	
	private static final LinearRgbColorSpace instance = new LinearRgbColorSpace();
	
	public static LinearRgbColorSpace getInstance() {
		return instance;
	}
	
	/** Constructor, non-public */
	private LinearRgbColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}
	
	// --------------------------------------------------------------------

	@Override
	public float[] fromRGB(float[] srgb) {
		final float[] rgb = new float[3];
		for (int k = 0; k < 3; k++) {
			rgb[k] = (float) sRgbUtil.gammaInv(srgb[k]);
		}
		return rgb;
	}

	@Override
	public float[] toRGB(float[] rgb) {
		final float[] srgb = new float[3];
		for (int k = 0; k < 3; k++) {
			srgb[k] = (float) sRgbUtil.gammaFwd(rgb[k]);
		}
		return srgb;
	}

	@Override
	public float[] toCIEXYZ(float[] rgb) {
		throw new UnsupportedOperationException();
//		double[] xyz65 = Matrix.multiply(Mrgbi, rgb);
	}

	@Override
	public float[] fromCIEXYZ(float[] xyz50) {
		throw new UnsupportedOperationException();
	}
	
	// ---------------------------------------------------------------------
	
	private static final String[] ComponentNames = {"R", "G", "B"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}
	
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

import imagingbook.common.math.Matrix;

/**
 * This class implements a D65-based sRGBcolor space without performing
 * chromatic adaptation between D50 and D65, as required by Java's profile 
 * connection space. Everything is D65, components of all {@code float[]} colors
 * are supposed to be in [0,1].
 * 
 */
@SuppressWarnings("serial")
public class sRgb65ColorSpace extends ColorSpace {
	
	/** Matrix for conversion from linear RGB to XYZ. */
	public static final double[][] Mrgb =    // (R,G,B) = Mrgb * (X,Y,Z)   // Poynton (ITU 709) 
		{{3.240479, -1.537150, -0.498535},
		 {-0.969256, 1.875992, 0.041556},
		 {0.055648, -0.204043, 1.057311}};
	
	/** Matrix for conversion from XYZ to linear RGB (inverse of {@link #Mrgb}). 
	 * The actual inverse is calculated here for better accuracy. 
	 */
	public static final double[][] Mrgbi = Matrix.inverse(Mrgb);
	
//	public static final double[][] Mrgbi =    // (X,Y,Z) = Mrgbi * (R,G,B)   // Poynton (ITU 709) 
//		{{0.412453, 0.357580, 0.180423},
//		 {0.212671, 0.715160, 0.072169},
//		 {0.019334, 0.119193, 0.950227}};

	/**
	 * Constructor.
	 */
	public sRgb65ColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}

	// XYZ (D65) -> sRGB conversion
	@Override
	public float[] fromCIEXYZ(float[] xyz) {
		double[] rgb = Matrix.multiply(Mrgb, Matrix.toDouble(xyz));		
		float[] srgb = new float[3];
		for (int i = 0; i < 3; i++) {
			srgb[i] = (float) sRgbUtil.gammaFwd(rgb[i]);
		}
		return srgb;
	}
	
	@Override
	public float[] fromRGB(float[] srgb) {
		throw new UnsupportedOperationException();
//		return srgb;
	}

	// sRGB -> XYZ (D65) conversion
	@Override
	public float[] toCIEXYZ(float[] srgb) {
		// get linear rgb components:
		double[] rgb = new double[3];
		for (int i = 0; i < 3; i++) {
			rgb[i] = sRgbUtil.gammaInv(srgb[i]);
		}
		// convert to XYZ (Poynton / ITU 709) 
		double[] xyz = Matrix.multiply(Mrgbi, rgb);
		return Matrix.toFloat(xyz);
	}

	@Override
	public float[] toRGB(float[] srgb) {
		throw new UnsupportedOperationException();
//		return srgb;
	}

}

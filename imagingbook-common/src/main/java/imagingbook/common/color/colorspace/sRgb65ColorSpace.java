/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

//import imagingbook.lib.math.Matrix;
//import imagingbook.lib.settings.PrintPrecision;

/*
 * This class implements a D65-based sRGBcolor space without performing
 * chromatic adaptation between D50 and D65, as required by Java's profile 
 * connection space. Everything is D65!
 */

public class sRgb65ColorSpace extends ColorSpace {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static double[][] Mrgb =    // (R,G,B) = Mrgb * (X,Y,Z)   // Poynton (ITU 709) 
		{{3.240479, -1.537150, -0.498535},
		 {-0.969256, 1.875992, 0.041556},
		 {0.055648, -0.204043, 1.057311}};
	
	@SuppressWarnings("unused")
	private static double[][] Mrgbi =    // (X,Y,Z) = Mrgbi * (R,G,B)   // Poynton (ITU 709) 
		{{0.412453, 0.357580, 0.180423},
		 {0.212671, 0.715160, 0.072169},
		 {0.019334, 0.119193, 0.950227}};


	public sRgb65ColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}

	// XYZ (D65) -> sRGB
	@Override
	public float[] fromCIEXYZ(float[] xyz) {
		final double X = xyz[0];
		final double Y = xyz[1];
		final double Z = xyz[2];
		
		// XYZ -> RGB (linear components)
		final double r =  3.240479 * X + -1.537150 * Y + -0.498535 * Z;
		final double g = -0.969256 * X +  1.875992 * Y +  0.041556 * Z;
		final double b =  0.055648 * X + -0.204043 * Y +  1.057311 * Z;
		
		// RGB -> sRGB (nonlinear components)
		float rr = (float) sRgbUtil.gammaFwd(r);
		float gg = (float) sRgbUtil.gammaFwd(g);
		float bb = (float) sRgbUtil.gammaFwd(b);			
		return new float[] {rr,gg,bb} ;
	}

	@Override
	public float[] fromRGB(float[] srgb) {
		return srgb;
	}

	// sRGB -> XYZ (D65)
	@Override
	public float[] toCIEXYZ(float[] srgb) {
		// get linear rgb components:
		final double r = sRgbUtil.gammaInv(srgb[0]);
		final double g = sRgbUtil.gammaInv(srgb[1]);
		final double b = sRgbUtil.gammaInv(srgb[2]);
		
		// convert to XYZ (Poynton / ITU 709) 
		final float x = (float) (0.412453 * r + 0.357580 * g + 0.180423 * b);
		final float y = (float) (0.212671 * r + 0.715160 * g + 0.072169 * b);
		final float z = (float) (0.019334 * r + 0.119193 * g + 0.950227 * b);
		return new float[] {x, y, z};
	}

	@Override
	public float[] toRGB(float[] srgb) {
		return srgb;
	}
	
	// ---------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		PrintPrecision.set(4);
//		ColorSpace cs = new sRgb65ColorSpace();
//		float[] red = {1, 0, 0};
//		float[] grn = {0, 1, 0};
//		float[] blu = {0, 0, 1};
//		
//		float[] rgb1 = blu;
//		
//		System.out.println("rgb2 = " + Matrix.toString(rgb1));
//		float[] xyz = cs.toCIEXYZ(rgb1);
//		System.out.println("xyz = " + Matrix.toString(xyz));
//		float[] rgb2 = cs.fromCIEXYZ(xyz);
//		System.out.println("rgb3 = " + Matrix.toString(rgb2));
//	}


}

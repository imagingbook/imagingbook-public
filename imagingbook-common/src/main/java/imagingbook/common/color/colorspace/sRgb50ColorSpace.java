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
import java.util.Arrays;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

/**
 * Implementation of a sRGB color space, as a substitute for Java's built-in
 * standard sRGB space (obtained by
 * {@code ColorSpace.getInstance(ColorSpace.CS_sRGB)}), with improved accuracy.
 * All XYZ-coordinates are D50-based.
 * 
 * @author WB
 * @version 2022/11/07
 */
public class sRgb50ColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;

	private static final sRgb50ColorSpace instance = new sRgb50ColorSpace();
	
	public static sRgb50ColorSpace getInstance() {
		return instance;
	}
	/**
	 * Constructor (not public).
	 */
	private sRgb50ColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}

	// see book Eq. 14.48 (p. 443)
	public static final double[][] Mrgb50i =    // (X,Y,Z) = Mrgbi * (R,G,B) for D50 primaries
			{{0.436108, 0.385120, 0.143064},
			 {0.222517, 0.716873, 0.060610},
			 {0.013931, 0.097099, 0.714075}};
	
	// see book Eq. 14.49 (p. 443)
	public static final double[][] Mrgb50 =
			Matrix.inverse(Mrgb50i);
	
	// ----------------------------------------------------
	
	@Override
	public float[] toRGB(float[] srgb) {
		return srgb;	// nothing to do, in sRGB already
	}

	@Override
	public float[] fromRGB(float[] srgb) {
		return srgb;	// nothing to do, sRGB is what we want
	}
	
	// ----------------------------------------------------

	@Override
	public float[] toCIEXYZ(float[] srgb) {
		// convert to linear rgb
		double[] rgb = new double[3];
		for (int i = 0; i < 3; i++) {
			rgb[i] = sRgbUtil.gammaInv(srgb[i]);
		}
		// linear transform to xyz
		double[] xyz50 = Matrix.multiply(Mrgb50i, rgb);
		return Matrix.toFloat(xyz50);
	}

	@Override
	public float[] fromCIEXYZ(float[] xyz50) {
		// linear transform to rgb
		double[] rgb = Matrix.multiply(Mrgb50, Matrix.toDouble(xyz50));
		// convert to nonlinear srgb
		float[] srgb = new float[3];									
		for (int i = 0; i < 3; i++) {
			srgb[i] = (float) sRgbUtil.gammaFwd(rgb[i]);
		}
		return srgb;
	}
	
	// ----------------------------------------------------
	
	private static final String[] ComponentNames = {"R'", "G'", "B'"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}

	// ----------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(6);
		System.out.println("Mrgb50 = " + Matrix.toString(Mrgb50));
		System.out.println(Arrays.toString(ComponentNames));
	}


}

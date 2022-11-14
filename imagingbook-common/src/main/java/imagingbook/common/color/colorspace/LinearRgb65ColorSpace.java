/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;

import java.awt.color.ColorSpace;

import imagingbook.common.math.Matrix;


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
public class LinearRgb65ColorSpace extends CustomColorSpace {
	

	private static final double[][] Mrgbi = CieUtil.Mrgb65i;
	public static final double[][] Mrgb = CieUtil.Mrgb65;
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final GammaMappingFunction GammaMap = GammaMappingFunction.sRGB;
	
	private static final LinearRgb65ColorSpace instance = new LinearRgb65ColorSpace();
	
	public static LinearRgb65ColorSpace getInstance() {
		return instance;
	}
	
	/** Constructor, non-public */
	private LinearRgb65ColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}
	
	// --------------------------------------------------------------------

	@Override	// directly convert non-linear sRGB to linear rgb (D65-based)
	public double[] fromRGB(double[] srgb) {
		final double[] rgb = new double[3];
		for (int k = 0; k < 3; k++) {
			rgb[k] = GammaMap.applyInv(srgb[k]);
		}
		return rgb;
	}

	@Override	// directly convert linear rgb (D65-based) to non-linear sRGB
	public double[] toRGB(double[] rgb) {
		final double[] srgb = new double[3];
		for (int k = 0; k < 3; k++) {
			srgb[k] = GammaMap.applyFwd(rgb[k]);
		}
		return srgb;
	}
	
	public double[] toCIEXYZ65(double[] rgb) {
		double[] xyz65 = Matrix.multiply(Mrgbi, rgb);
		return xyz65;
	}

	@Override	// return D50-based PCS xyz
	public double[] toCIEXYZ(double[] rgb) {
		double[] xyz65 = this.toCIEXYZ65(rgb);
		double[] xyz50 = catD65toD50.applyTo(xyz65);
		return xyz50;
	}
	
	public double[] fromCIEXYZ65(double[] xyz65) {
		double[] rgb = Matrix.multiply(Mrgb, xyz65);
		return rgb;
	}

	@Override
	public double[] fromCIEXYZ(double[] xyz50) {
		double[] xyz65 = catD50toD65.applyTo(xyz50);
		return this.fromCIEXYZ65(xyz65);
	}
	
	// ---------------------------------------------------------------------
	
	private static final String[] ComponentNames = {"R", "G", "B"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}
	
}

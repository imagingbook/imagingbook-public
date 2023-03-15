/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.colorspace;

import imagingbook.common.color.adapt.BradfordAdaptation;
import imagingbook.common.color.adapt.ChromaticAdaptation;
import imagingbook.common.color.gamma.ModifiedGammaMapping;
import imagingbook.common.math.Matrix;

import java.awt.color.ColorSpace;

import static imagingbook.common.color.cie.StandardIlluminant.D50;
import static imagingbook.common.color.cie.StandardIlluminant.D65;


/**
 * Defines a linear RGB space with the same white point and primaries as sRGB. Everything is D65, components of all
 * {@code float[]} colors are supposed to be in [0,1]. This is a singleton class with no public constructors, use
 * {@link #getInstance()} to obtain the single instance.
 *
 * @author WB
 * @version 2022/11/14
 * @see sRGB65ColorSpace
 */
@SuppressWarnings("serial")
public class LinearRgb65ColorSpace extends ColorSpace implements DirectD65Conversion, RgbReferenceData {
	
	// we use the same RGB-XYZ matrices as sRGB
	/** Matrix for conversion from XYZ to linear RGB. Its column vectors are the 
	 * XYZ coordinates of the RGB primaries. */
	private static final double[][] Mrgbi = 
		{{0.412453, 0.357580, 0.180423},
		 {0.212671, 0.715160, 0.072169},
		 {0.019334, 0.119193, 0.950227}};
	
	private static final float[][] MrgbiF = Matrix.toFloat(Mrgbi);
	
	/** Matrix for conversion from linear RGB to XYZ (inverse of {@link #Mrgbi}). */
	private static final double[][] Mrgb = Matrix.inverse(Mrgbi);
	private static final float[][] MrgbF = Matrix.toFloat(Mrgb);
	
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final ModifiedGammaMapping GammaMap = ModifiedGammaMapping.sRGB;
	
	private static final LinearRgb65ColorSpace instance = new LinearRgb65ColorSpace();
	
	public static LinearRgb65ColorSpace getInstance() {
		return instance;
	}
	
	/** Constructor, non-public */
	private LinearRgb65ColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}
	
	// --------------------------------------------------------------------

	@Override
	public float[] getWhitePoint() {
		return Matrix.toFloat(D65.getXYZ());
	}
	
	@Override
	public float[] getPrimary(int idx) {
		return Matrix.toFloat(Matrix.getColumn(Mrgbi, idx));
	}
	
	// --------------------------------------------------------------------

	@Override	// directly convert non-linear sRGB to linear rgb (D65-based)
	public float[] fromRGB(float[] srgb) {
//		final float[] rgb = new float[3];
//		for (int k = 0; k < 3; k++) {
//			rgb[k] = GammaMap.applyInv(srgb[k]);
//		}
		float[] rgb = GammaMap.applyInv(srgb);
		return rgb;
	}

	@Override	// directly convert linear rgb (D65-based) to non-linear sRGB
	public float[] toRGB(float[] rgb) {
//		final float[] srgb = new float[3];
//		for (int k = 0; k < 3; k++) {
//			srgb[k] = GammaMap.applyFwd(rgb[k]);
//		}
		float[] srgb = GammaMap.applyFwd(rgb);
		return srgb;
	}
	
	@Override	// return D50-based PCS xyz
	public float[] toCIEXYZ(float[] rgb) {
		float[] xyz65 = this.toCIEXYZ65(rgb);
		float[] xyz50 = catD65toD50.applyTo(xyz65);
		return xyz50;
	}
	
	// ---------------------------------------------------------------------

	@Override
	public float[] fromCIEXYZ(float[] xyz50) {
		float[] xyz65 = catD50toD65.applyTo(xyz50);
		return this.fromCIEXYZ65(xyz65);
	}
	
	@Override
	public float[] toCIEXYZ65(float[] rgb) {
		float[] xyz65 = Matrix.multiply(MrgbiF, rgb);
		return xyz65;
	}
	
	@Override
	public float[] fromCIEXYZ65(float[] xyz65) {
		float[] rgb = Matrix.multiply(MrgbF, xyz65);
		return rgb;
	}
	
	// ---------------------------------------------------------------------
	
	private static final String[] ComponentNames = {"R", "G", "B"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}
	
}

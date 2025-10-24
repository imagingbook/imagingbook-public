/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
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
public class LinearRgb65ColorSpace extends AbstractRgbColorSpace implements DirectD65Conversion {

	/** Matrix for conversion from D65-based XYZ to linear RGB. */
	private final float[][] MrgbiF; // = Matrix.toFloat(this.getMrgbi());
	
	/** Matrix for conversion from linear RGB to D65-based XYZ (inverse of {@code Mrgbi}). */
	private final float[][] MrgbF; // = Matrix.toFloat(this.getMrgb());
	
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final ModifiedGammaMapping GammaMap = ModifiedGammaMapping.sRGB;

	// tristimulus values (ITU-709):
	private static final double xR = 0.64, yR = 0.33;
	private static final double xG = 0.30, yG = 0.60;
	private static final double xB = 0.15, yB = 0.06;
	// white point:
	private static final double[] xyW = D65.getXy();

	
	private static final LinearRgb65ColorSpace instance = new LinearRgb65ColorSpace();
	
	public static LinearRgb65ColorSpace getInstance() {
		return instance;
	}
	
	/** Constructor, non-public */
	private LinearRgb65ColorSpace() {
		super(xR, yR, xG, yG, xB, yB, xyW[0], xyW[1]);	//	super(0.64, 0.33, 0.30, 0.60, 0.15, 0.06, 0.3127, 0.3290);
		// super(ColorSpace.TYPE_RGB, 3);
		this.MrgbiF = Matrix.toFloat(this.getMrgbi());
		this.MrgbF = Matrix.toFloat(this.getMrgb());
	}
	
	// ---------------------------------------------------------------------
	
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
	

	// --------------------------------------------------------------------

	@Override	// directly convert non-linear sRGB to THIS linear rgb (D65-based)
	public float[] fromRGB(float[] srgb) {
		float[] rgb = GammaMap.applyInv(srgb);
		return rgb;
	}

	@Override	// directly convert THIS linear rgb (D65-based) to non-linear sRGB
	public float[] toRGB(float[] rgb) {
		float[] srgb = GammaMap.applyFwd(rgb);
		return srgb;
	}
	
	// --------------------------------------------------------------------
	
	@Override	// return D50-based PCS xyz from THIS linear rgb
	public float[] toCIEXYZ(float[] rgb) {
		float[] xyz65 = this.toCIEXYZ65(rgb);
		float[] xyz50 = catD65toD50.applyTo(xyz65);
		return xyz50;
	}
	

	@Override	// return THIS linear rgb from D50-based PCS xyz
	public float[] fromCIEXYZ(float[] xyz50) {
		float[] xyz65 = catD50toD65.applyTo(xyz50);
		return this.fromCIEXYZ65(xyz65);
	}
	
}

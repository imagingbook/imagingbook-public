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
 * <p>
 * This class implements the sRGBcolor space with D65 white point. See Sec. 14.4 of [1] for details. Components of all
 * {@code float[]} colors are supposed to be in [0,1]. This is a singleton class with no public constructors, use
 * {@link #getInstance()} to obtain the single instance.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/14
 * @see LinearRgb65ColorSpace
 */
@SuppressWarnings("serial")
public class sRgbColorSpace extends ColorSpace implements DirectD65Conversion, RgbReferenceData {
	
	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final ModifiedGammaMapping GammaMap = ModifiedGammaMapping.sRGB;
	
	private static final sRgbColorSpace instance = new sRgbColorSpace();
	
	public static sRgbColorSpace getInstance() {
		return instance;
	}

	/**
	 * Matrix for conversion from XYZ to linear RGB. Its column vectors are the XYZ coordinates of the RGB primaries.
	 */
	private static final double[][] Mrgbi = 
		{{0.412453, 0.357580, 0.180423},
		 {0.212671, 0.715160, 0.072169},
		 {0.019334, 0.119193, 0.950227}};
	private static final float[][] MrgbiF = Matrix.toFloat(Mrgbi);
	
	/** Matrix for conversion from linear RGB to XYZ (inverse of {@link #Mrgbi}). */
	private static final double[][] Mrgb = Matrix.inverse(Mrgbi);
	private static final float[][] MrgbF = Matrix.toFloat(Mrgb);
	
	// ----------------------------------------------------
	
	@Override
	public float[] getWhitePoint() {
		return Matrix.toFloat(D65.getXYZ());
	}
	
	@Override
	public float[] getPrimary(int idx) {
		return Matrix.toFloat(Matrix.getColumn(Mrgbi, idx));
	}
	
	// ----------------------------------------------------
	
	/** Constructor (not public). */
	private sRgbColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}
	
	// direct conversion from/to D65-based XYZ space ------------------------------
	
	@Override
	public float[] fromCIEXYZ65(float[] xyz65) {
		float[] rgb = Matrix.multiply(MrgbF, xyz65);	// to linear RGB
		return GammaMap.applyFwd(rgb);					// to non-linear sRGB
	}
	
	@Override
	public float[] toCIEXYZ65(float[] srgbTHIS) {
		float[] rgb = GammaMap.applyInv(srgbTHIS);		// to linear rgb
		return Matrix.multiply(MrgbiF, rgb);	// to XYZ (D65)
	}
	
	// Methods required by ColorSpace (conversion from/to PCS space) ------------------

	// assumes xyz50 is in D50-based CS_CIEXYZ color space
	@Override
	public float[] fromCIEXYZ(float[] xyz50PCS) {
		float[] xyz65 = catD50toD65.applyTo(xyz50PCS);	// to XYZ (D65)
		return this.fromCIEXYZ65(xyz65);				// to sRGB
	}

	// returned colors are in D50-based CS_CIEXYZ color space 
	@Override
	public float[] toCIEXYZ(float[] srgbTHIS) {
		float[] xyz65 = this.toCIEXYZ65(srgbTHIS);		// to XYZ (D65)
		return catD65toD50.applyTo(xyz65);		// to XYZ (D50)
	}
	
	// ----------------------------------------------------
	
	@Override // no conversion needed, since this is sRGB
	public float[] fromRGB(float[] srgb) {
		return srgb;
	}

	@Override // no conversion needed, since this is sRGB
	public float[] toRGB(float[] srgbTHIS) {
		return srgbTHIS;
	}

}

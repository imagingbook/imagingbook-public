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
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.color.gamma.ModifiedGammaMapping;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

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
public class AdobeRgbColorSpaceDirect extends AbstractRgbColorSpace implements DirectD65Conversion {

	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final ModifiedGammaMapping GammaMap = ModifiedGammaMapping.sRGB;

	// tristimulus values and white point:
	private static final double xR = 0.64, yR = 0.33;
	private static final double xG = 0.21, yG = 0.71;
	private static final double xB = 0.15, yB = 0.06;

	private static final double[] W = D65.getXYZ();
	private static final double[] xyW = D65.getXy();

	private static final AdobeRgbColorSpaceDirect instance = new AdobeRgbColorSpaceDirect();

	public static AdobeRgbColorSpaceDirect getInstance() {
		return instance;
	}


	private final float[][] MrgbiF = Matrix.toFloat(Mrgbi);

	/** Matrix for conversion from linear RGB to XYZ (inverse of {@link #Mrgbi}). */
	// private static final double[][] Mrgb = Matrix.inverse(Mrgbi);
	private final float[][] MrgbF = Matrix.toFloat(Mrgb);

	// ----------------------------------------------------

	// @Override
	// public float[] getWhitePoint() {
	// 	return Matrix.toFloat(W);
	// }

	// @Override
	// public float[] getPrimary(int idx) {
	// 	return Matrix.toFloat(Matrix.getColumn(Mrgbi, idx));
	// }

	// ----------------------------------------------------

	/** Constructor (not public). */
	private AdobeRgbColorSpaceDirect() {
		super(xR, yR, xG, yG, xB, yB, xyW[0], xyW[1]);
		// super(ColorSpace.TYPE_RGB, 3);
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
	
	// @Override // no conversion needed, since this is sRGB
	// public float[] fromRGB(float[] srgb) {
	// 	return srgb;
	// }
	//
	// @Override // no conversion needed, since this is sRGB
	// public float[] toRGB(float[] srgbTHIS) {
	// 	return srgbTHIS;
	// }

	public static void main(String[] args) {
		PrintPrecision.set(9);
		AdobeRgbColorSpaceDirect CS = AdobeRgbColorSpaceDirect.getInstance();

		float[] wXYZ = CS.toCIEXYZ65(new float[] {1, 1, 1});
		float[] rXYZ = CS.toCIEXYZ65(new float[] {1, 0, 0});
		float[] gXYZ = CS.toCIEXYZ65(new float[] {0, 1, 0});
		float[] bXYZ = CS.toCIEXYZ65(new float[] {0, 0, 1});
		System.out.println("XYZ65 white = " + Matrix.toString(wXYZ));	// {0.964295685, 1.000000000, 0.825104535} = D65

		System.out.println("XYZ65 red = " + Matrix.toString(rXYZ));	// {0.436065733, 0.222493172, 0.013923921}
		System.out.println("XYZ65 grn = " + Matrix.toString(gXYZ));	//
		System.out.println("XYZ65 blu = " + Matrix.toString(bXYZ));	//

		System.out.println("xy65 red = " + Matrix.toString(CieUtils.XYZToxy(rXYZ)));	//
		System.out.println("xy65 grn = " + Matrix.toString(CieUtils.XYZToxy(gXYZ)));	//
		System.out.println("xy65 blu = " + Matrix.toString(CieUtils.XYZToxy(bXYZ)));	//

		System.out.println("Mrgbi = \n" + Matrix.toString(CS.getMrgbi()));
		System.out.println("Mrgb  = \n" + Matrix.toString(CS.getMrgb()));

	}

}

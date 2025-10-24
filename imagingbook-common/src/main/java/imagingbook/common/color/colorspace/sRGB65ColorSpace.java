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
 * This is a sample implementation the sRGBcolor space which does not depend on ICC profiles. See Sec. 14.4 of [1] for details.
 * Components of all {@code float[]} colors are supposed to be in [0,1]. This is a singleton class with no public constructors,
 * use {@link #getInstance()} to obtain the associated instance.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2023/03/31
 * @see LinearRgb65ColorSpace
 */
@SuppressWarnings("serial")
public class sRGB65ColorSpace extends AbstractRgbColorSpace implements DirectD65Conversion {

	// tristimulus values and white point:
	private static final double xR = 0.64, yR = 0.33;
	private static final double xG = 0.30, yG = 0.60;
	private static final double xB = 0.15, yB = 0.06;
	private static final double[] xyW = D65.getXy();
	private static final ModifiedGammaMapping GammaMap = ModifiedGammaMapping.sRGB;

	private static final sRGB65ColorSpace instance = new sRGB65ColorSpace();
	public static sRGB65ColorSpace getInstance() {
		return instance;
	}

	/** Matrix for conversion from XYZ to linear RGB. Its column vectors are the XYZ coordinates of the RGB primaries. */
	private final float[][] MrgbiF = Matrix.toFloat(Mrgbi);
	
	/** Matrix for conversion from linear RGB to XYZ (inverse of {@link #Mrgbi}). */
	private final float[][] MrgbF = Matrix.toFloat(Mrgb);
	
	// ----------------------------------------------------
	
	/** Constructor (not public). */
	private sRGB65ColorSpace() {
		super(xR, yR, xG, yG, xB, yB, xyW[0], xyW[1]);
	}

	// Methods required by ColorSpace (conversion from/to PCS space) ------------------

	@Override	// assumes xyz50 is in D50-based CS_CIEXYZ color space
	public float[] fromCIEXYZ(float[] xyz50PCS) {
		float[] xyz65 = catD50toW.applyTo(xyz50PCS);	// to XYZ (D65)
		return this.fromCIEXYZ65(xyz65);				// to sRGB
	}

	@Override // returned colors are in D50-based CS_CIEXYZ color space
	public float[] toCIEXYZ(float[] srgbTHIS) {
		float[] xyz65 = this.toCIEXYZ65(srgbTHIS);		// to XYZ (D65)
		return catWtoD50.applyTo(xyz65);				// to XYZ (D50)
	}

	// direct conversion from/to D65-based XYZ space ------------------------------
	
	@Override	// converts from XYZ65 to this sRGB
	public float[] fromCIEXYZ65(float[] xyz65) {
		float[] rgb = Matrix.multiply(MrgbF, xyz65);	// to linear RGB
		return GammaMap.applyFwd(rgb);					// to non-linear sRGB
	}
	
	@Override	// converts from sRGB to XYZ65
	public float[] toCIEXYZ65(float[] srgbTHIS) {
		float[] rgb = GammaMap.applyInv(srgbTHIS);		// to linear rgb
		return Matrix.multiply(MrgbiF, rgb);			// to XYZ (D65)
	}
	
	// ----------------------------------------------------

	@Override 	// convert from a color in this space to sRGB
	public float[] fromRGB(float[] srgb) {
		return srgb;	// no conversion needed, since this is sRGB
	}

	@Override 	// convert from sRGB to a color in this space
	public float[] toRGB(float[] srgbTHIS) {
		return srgbTHIS;	// no conversion needed, since this is sRGB
	}

	// public static void main(String[] args) {
	// 	PrintPrecision.set(9);
	// 	sRGB65ColorSpace CS = sRGB65ColorSpace.getInstance();
	//
	// 	float[] wXYZ = CS.toCIEXYZ65(new float[] {1, 1, 1});
	// 	float[] rXYZ = CS.toCIEXYZ65(new float[] {1, 0, 0});
	// 	float[] gXYZ = CS.toCIEXYZ65(new float[] {0, 1, 0});
	// 	float[] bXYZ = CS.toCIEXYZ65(new float[] {0, 0, 1});
	// 	System.out.println("XYZ65 white = " + Matrix.toString(wXYZ));	// {0.950455904, 1.000000000, 1.089057684} = D65
	//
	// 	System.out.println("XYZ65 red = " + Matrix.toString(rXYZ));	// {0.412390798, 0.212639004, 0.019330818}
	// 	System.out.println("XYZ65 grn = " + Matrix.toString(gXYZ));	// {0.357584327, 0.715168655, 0.119194783}
	// 	System.out.println("XYZ65 blu = " + Matrix.toString(bXYZ));	// {0.180480793, 0.072192319, 0.950532138}
	//
	// 	System.out.println("xy65 red = " + Matrix.toString(CieUtils.XYZToxy(rXYZ)));	// {0.639999986, 0.330000013}
	// 	System.out.println("xy65 grn = " + Matrix.toString(CieUtils.XYZToxy(gXYZ)));	// {0.300000012, 0.600000024}
	// 	System.out.println("xy65 blu = " + Matrix.toString(CieUtils.XYZToxy(bXYZ)));	// {0.150000006, 0.060000002}
	//
	// 	System.out.println("Mrgbi = \n" + Matrix.toString(CS.getMrgbi()));
	// 		// {{0.412390799, 0.357584339, 0.180480788},
	// 		// {0.212639006, 0.715168679, 0.072192315},
	// 		// {0.019330819, 0.119194780, 0.950532152}}
	// 	System.out.println("Mrgb  = \n" + Matrix.toString(CS.getMrgb()));
	// 		// {{3.240969942, -1.537383178, -0.498610760},
	// 		// {-0.969243636, 1.875967502, 0.041555057},
	// 		// {0.055630080, -0.203976959, 1.056971514}}
	// }

}

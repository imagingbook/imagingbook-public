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
import imagingbook.common.math.PrintPrecision;

import static imagingbook.common.color.cie.StandardIlluminant.D50;
import static imagingbook.common.color.cie.StandardIlluminant.D65;


/**
 * <p>
 * For testing only!
 * This class implements the sRGBcolor space with D50 white point. See Sec. 14.4 of [1] for details. Components of all
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
public class sRGB50ColorSpace extends AbstractRgbColorSpace implements DirectD65Conversion {

	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final ModifiedGammaMapping GammaMap = ModifiedGammaMapping.sRGB;

	// tristimulus values and white point:
	private static final double xR = 0.6484, yR = 0.3309;
	private static final double xG = 0.3212, yG = 0.5978;
	private static final double xB = 0.1559, yB = 0.0660;

	private static final double[] W = D50.getXYZ();
	private static final double[] xyW = D50.getXy();

	private static final sRGB50ColorSpace instance = new sRGB50ColorSpace();

	public static sRGB50ColorSpace getInstance() {
		return instance;
	}

	private final float[][] MrgbiF = Matrix.toFloat(this.Mrgbi);	// calculated by super constructor
	private final float[][] MrgbF = Matrix.toFloat(Mrgb);

	// ----------------------------------------------------

	@Override
	public float[] getWhitePoint() {
		return Matrix.toFloat(W);
	}

	// ----------------------------------------------------

	/** Constructor (not public). */
	private sRGB50ColorSpace() {
		super(xR, yR, xG, yG, xB, yB, xyW[0], xyW[1]);
		// super(ColorSpace.TYPE_RGB, 3);
	}
	
	// direct conversion from/to D65-based XYZ space ------------------------------
	
	// @Override
	// public float[] fromCIEXYZ65(float[] xyz65) {
	// 	float[] rgb = Matrix.multiply(MrgbF, xyz65);	// to linear RGB
	// 	return GammaMap.applyFwd(rgb);					// to non-linear sRGB
	// }
	//
	// @Override
	// public float[] toCIEXYZ65(float[] srgbTHIS) {
	// 	float[] rgb = GammaMap.applyInv(srgbTHIS);		// to linear rgb
	// 	return Matrix.multiply(MrgbiF, rgb);	// to XYZ (D65)
	// }
	
	// Methods required by ColorSpace (conversion from/to PCS space) ------------------

	// assumes xyz50 is in D50-based CS_CIEXYZ color space
	@Override
	public float[] fromCIEXYZ(float[] xyz50) {
		float[] rgb = Matrix.multiply(MrgbF, xyz50);	// to linear RGB
		return GammaMap.applyFwd(rgb);					// to non-linear sRGB
	}

	// returned colors are in D50-based CS_CIEXYZ color space 
	@Override
	public float[] toCIEXYZ(float[] srgbTHIS) {
		float[] rgb = GammaMap.applyInv(srgbTHIS);		// to linear rgb
		return Matrix.multiply(MrgbiF, rgb);			// to XYZ (D50)
	}
	
	// ----------------------------------------------------

	public static void main(String[] args) {
		PrintPrecision.set(9);
		sRGB50ColorSpace CS = sRGB50ColorSpace.getInstance();

		System.out.println("D50 white = " + Matrix.toString(D50.getXYZ()));
		float[] wXYZ = CS.toCIEXYZ(new float[] {1, 1, 1});
		System.out.println("XYZ50 white = " + Matrix.toString(wXYZ));	// {0.964295685, 1.000000000, 0.825104535} = D50

		// float[] rXYZ = CS.toCIEXYZ(new float[] {1, 0, 0});
		// System.out.println("XYZ50 red = " + Matrix.toString(rXYZ));	// {0.436065733, 0.222493172, 0.013923921}
		//
		// float[] gXYZ = CS.toCIEXYZ(new float[] {0, 1, 0});
		// System.out.println("XYZ50 grn = " + Matrix.toString(gXYZ));	// {0.385151505, 0.716886997, 0.097081326}
		//
		// float[] bXYZ = CS.toCIEXYZ(new float[] {0, 0, 1});
		// System.out.println("XYZ50 blu = " + Matrix.toString(bXYZ));	// {0.143078431, 0.060619812, 0.714099348}

		System.out.println("XYZ50 red = " + Matrix.toString(CS.getPrimary(0)));
		System.out.println("XYZ50 grn = " + Matrix.toString(CS.getPrimary(1)));
		System.out.println("XYZ50 blu = " + Matrix.toString(CS.getPrimary(2)));

		System.out.println("Mrgbi = \n" + Matrix.toString(CS.getMrgbi()));
		System.out.println("Mrgb  = \n" + Matrix.toString(CS.getMrgb()));

	}

}

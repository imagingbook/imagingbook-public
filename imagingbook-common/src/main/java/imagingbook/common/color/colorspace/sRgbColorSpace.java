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
import imagingbook.common.math.PrintPrecision;


/**
 * This class implements the sRGBcolor space with D65 white point. Components of
 * all {@code float[]} colors are supposed to be in [0,1]. This is a singleton
 * class with no public constructors, use {@link #getInstance()} to obtain the
 * single instance.
 * 
 * @author WB
 * @version 2022/11/14
 */
@SuppressWarnings("serial")
public class sRgbColorSpace extends CustomColorSpace implements RgbColorSpace {
	
	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final GammaMappingFunction GammaMap = GammaMappingFunction.sRGB;
	
	private static final sRgbColorSpace instance = new sRgbColorSpace();
	
	public static sRgbColorSpace getInstance() {
		return instance;
	}
	
	/** Matrix for conversion from XYZ to linear RGB. Its column vectors are the 
	 * XYZ coordinates of the RGB primaries. */
	private static final double[][] Mrgbi = 
		{{0.412453, 0.357580, 0.180423},
		 {0.212671, 0.715160, 0.072169},
		 {0.019334, 0.119193, 0.950227}};
	
	/** Matrix for conversion from linear RGB to XYZ (inverse of {@link #Mrgbi}). */
	private static final double[][] Mrgb = Matrix.inverse(Mrgbi);
	
	// ----------------------------------------------------
	
	@Override
	public double[] getWhitePoint() {
		return D65.getXYZ();
	}
	
	@Override
	public double[] getPrimary(int idx) {
		return Matrix.getColumn(Mrgbi, idx);
	}
	
	// ----------------------------------------------------
	
	/**
	 * Constructor (not public).
	 */
	private sRgbColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}
	
	// direct conversion from/to D65-based XYZ space ------------------------------
	
	public double[] fromCIEXYZ65(double[] xyz65) {
		double[] rgb = Matrix.multiply(Mrgb, xyz65);	// linear RGB
		// perform forward gamma mapping:
		double[] srgb = new double[3];									
		for (int i = 0; i < 3; i++) {
			srgb[i] = GammaMap.applyFwd(rgb[i]);
		}
		return srgb;
	}
	
	public double[] toCIEXYZ65(double[] srgbTHIS) {
		// get linear rgb components:
		double[] rgb = new double[3];
		for (int i = 0; i < 3; i++) {
			rgb[i] = GammaMap.applyInv(srgbTHIS[i]);
		}
		// convert to D65-based XYZ (Poynton / ITU 709) 
		double[] xyz65 = Matrix.multiply(Mrgbi, rgb);
		return xyz65;
	}
	
	// Methods required ColorSpace (conversion from/to PCS space) ------------------

	// assumes xyz50 is in D50-based CS_CIEXYZ color space
	// TODO: check double/float mix
	@Override
	public double[] fromCIEXYZ(double[] xyz50PCS) {
		double[] xyz65 = catD50toD65.applyTo(xyz50PCS);
		return this.fromCIEXYZ65(xyz65);
	}

	// returned colors are in D50-based CS_CIEXYZ color space 
	// TODO: check double/float mix
	@Override
	public double[] toCIEXYZ(double[] srgbTHIS) {
		double[] xyz65 = this.toCIEXYZ65(srgbTHIS);
		double[] xyz50 = catD65toD50.applyTo(xyz65);
		return xyz50;
	}
	
	// ----------------------------------------------------
	
	@Override // no conversion needed, since this is sRGB
	public double[] fromRGB(double[] srgb) {
		return srgb;
	}

	@Override // no conversion needed, since this is sRGB
	public double[] toRGB(double[] srgbTHIS) {
		return srgbTHIS;
	}
	
	// ----------------------------------------------------
	
	private static final String[] ComponentNames = {"R65", "G65", "B65"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}
	
	
	public static void main(String[] args) {
		PrintPrecision.set(6);
		sRgbColorSpace cs = sRgbColorSpace.getInstance();
		System.out.println("w = " + Matrix.toString(cs.getWhitePoint()));
//		System.out.println("R = " + Matrix.toString(cs.getPrimary(0)));
//		System.out.println("G = " + Matrix.toString(cs.getPrimary(1)));
//		System.out.println("B = " + Matrix.toString(cs.getPrimary(2)));
		for (int i = 0; i < 3; i++) {
			double[] p = cs.getPrimary(i);
			double[] xy = CieUtil.XYZToXy(p);
			System.out.println(i + " = " + Matrix.toString(p) + " xy = " + Matrix.toString(xy));
		}
	}

}

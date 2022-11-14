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


// D65 tristimulus: 0.9504, 1.0000, 1.0888

/**
 * This class implements the sRGBcolor space with D65 white point. 
 * Components of all {@code float[]} colors
 * are supposed to be in [0,1]. This is a singleton class with no public
 * constructors, use {@link #getInstance()} to obtain the single instance.
 * 
 * @author WB
 * @version 2022/11/07
 */
public class sRgbColorSpace extends CustomColorSpace {
	private static final long serialVersionUID = 1L;

	private static final sRgbColorSpace instance = new sRgbColorSpace();
	
	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final GammaMappingFunction GammaMap = GammaMappingFunction.sRGB;
	
	public static sRgbColorSpace getInstance() {
		return instance;
	}
	
	/** Matrix for conversion from XYZ to linear RGB. */
	public static final double[][] Mrgbi = CieUtil.Mrgb65i;
	
	/** Matrix for conversion from linear RGB to XYZ (inverse of {@link #Mrgbi}). */
	public static final double[][] Mrgb = CieUtil.Mrgb65;


//	private static final float[][] MrgbiF = Matrix.toFloat(Mrgbi);
//	private static final float[][] MrgbF = Matrix.toFloat(Mrgb);
	
	// ----------------------------------------------------
	
	public double[] getPrimary(int idx) {
		return Matrix.getColumn(Mrgbi, idx);
	}
	
	// The white point of D65 (
	public double[] getWhiteXYZ() {
		return Matrix.multiply(Mrgbi, new double[] {1, 1, 1});
		// {0.95045, 1.000000000, 1.08905}
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
	
//	public float[] fromCIEXYZ(float[] xyz50PCS) {
//		double[] xyz65 = catD50toD65.applyTo(Matrix.toDouble(xyz50PCS));
//		double[] rgb = Matrix.multiply(Mrgb, xyz65);	// linear RGB
//		// perform forward gamma mapping:
//		float[] srgb = new float[3];									
//		for (int i = 0; i < 3; i++) {
//			srgb[i] = (float) sRgbUtil.gammaFwd(rgb[i]);
//		}
//		return srgb;
//	}

	// returned colors are in D50-based CS_CIEXYZ color space 
	// TODO: check double/float mix
	@Override
	public double[] toCIEXYZ(double[] srgbTHIS) {
		double[] xyz65 = this.toCIEXYZ65(srgbTHIS);
		double[] xyz50 = catD65toD50.applyTo(xyz65);
		return xyz50;
	}
	
//	public float[] toCIEXYZ(float[] srgbTHIS) {
//		// get linear rgb components:
//		double[] rgb = new double[3];
//		for (int i = 0; i < 3; i++) {
//			rgb[i] = sRgbUtil.gammaInv(srgbTHIS[i]);
//		}
//		// convert to D65-based XYZ (Poynton / ITU 709) 
//		double[] xyz65 = Matrix.multiply(Mrgbi, rgb);
//		double[] xyz50 = catD65toD50.applyTo(xyz65);
//		return Matrix.toFloat(xyz50);
//	}
	
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
	
	// ------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(15);
		sRgbColorSpace cs = sRgbColorSpace.getInstance();
		
		
		System.out.println("Mrgbi = \n" + Matrix.toString(Mrgbi) + "\n");
		System.out.println("Mrgb = \n" + Matrix.toString(Mrgb) + "\n");
		
		double[] trist1 = Matrix.multiply(Mrgbi, new double[] {1,1,1});
		System.out.println("W  = " + Matrix.toString(trist1));
		System.out.println("Xr = " + Matrix.toString(Matrix.multiply(Mrgbi, new double[] {1,0,0})));
		System.out.println("Xg = " + Matrix.toString(Matrix.multiply(Mrgbi, new double[] {0,1,0})));
		System.out.println("Xb = " + Matrix.toString(Matrix.multiply(Mrgbi, new double[] {0,0,1})));
		
		double[] whiteXYZ = cs.getWhiteXYZ();
		System.out.println("getWhiteXYZ() = " + Matrix.toString(whiteXYZ));
		System.out.println("          xy = " + Matrix.toString(CieUtil.XYZToXy(whiteXYZ)));	// {0.3457029085924369, 0.3585385827835399}
		
		
//		ColorSpace cs1 = sRgb50ColorSpace.getInstance();
//		ColorSpace cs2 = ColorSpace.getInstance(ColorSpace.CS_sRGB);
//		PrintPrecision.set(6);
//		for(int c = 0; c < 256; c++) {
//			int[] srgb = {c, 0, 0};
//			float[] srgbA = RgbUtils.normalize(srgb);
//			float[] xyz1 = cs1.toCIEXYZ(srgbA);
//			float[] xyz2 = cs2.toCIEXYZ(srgbA);
//			System.out.println(Arrays.toString(srgb) + " -> " +
//					Matrix.toString(xyz1) + " / " +
//					Matrix.toString(xyz2));
//		}
		
	}


}

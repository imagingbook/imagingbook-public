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
 * This class implements a D65-based sRGBcolor space without performing
 * chromatic adaptation between D50 and D65, as required by Java's profile
 * connection space. Everything is D65, components of all {@code float[]} colors
 * are supposed to be in [0,1]. This is a singleton class with no public
 * constructors, use {@link #getInstance()} to obtain the single instance.
 * 
 * @author WB
 * @version 2022/11/07 revised to comply with D50-based {@code CS_sRGB} and {@code CS_CIEXYZ} color spaces
 */
public class sRgb65ColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;

	private static final sRgb65ColorSpace instance = new sRgb65ColorSpace();
	
	// the standard D50-based CS_sRGB
//	private static final ColorSpace sRgb50CS = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	private static final ColorSpace sRgb50CS = sRgb50ColorSpace.getInstance();
	
	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = new BradfordAdaptation(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = new BradfordAdaptation(D50, D65);
	
	public static sRgb65ColorSpace getInstance() {
		return instance;
	}
	

	
	/** Matrix for conversion from linear RGB to XYZ. */
	private static final double[][] Mrgb = // Matrix.inverse(Mrgbi)  // (R,G,B) = Mrgb * (X,Y,Z)
			//from sRGB specs:
			{{3.2406255, -1.537208, -0.4986286},
			 {-0.9689307, 1.8757561, 0.0415175},
			 {0.0557101, -0.2040211, 1.0569959}};
			
	// Poynton (ITU 709) 
//		{{3.240479, -1.537150, -0.498535},
//		 {-0.969256, 1.875992, 0.041556},
//		 {0.055648, -0.204043, 1.057311}};
	

	
//	from http://www.brucelindbloom.com/index.html?ColorCalculator.html:
//		3.2404542 -1.5371385 -0.4985314
//		-0.9692660  1.8760108  0.0415560
//		 0.0556434 -0.2040259  1.0572252
	
	// ------------------------------------
	
	/** Matrix for conversion from XYZ to linear RGB (inverse of {@link #Mrgb}). 
	 * The actual inverse is calculated here for better accuracy. 
	 */
	private static final double[][] Mrgbi = Matrix.inverse(Mrgb);

//	public static final double[][] Mrgbi =    // (X,Y,Z) = Mrgbi * (R,G,B)   // Poynton (ITU 709) 
//		{{0.412453, 0.357580, 0.180423},
//		 {0.212671, 0.715160, 0.072169},
//		 {0.019334, 0.119193, 0.950227}};

//	from http://www.brucelindbloom.com/index.html?ColorCalculator.html:
//		 0.4124564  0.3575761  0.1804375
//		 0.2126729  0.7151522  0.0721750
//		 0.0193339  0.1191920  0.9503041
	
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
	private sRgb65ColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}

	// assumes xyz50 is in D50-based CS_CIEXYZ color space
	// TODO: check double/float mix
	@Override
	public float[] fromCIEXYZ(float[] xyz50PCS) {
		float[] xyz65 = catD50toD65.applyTo(xyz50PCS);
		double[] rgb = Matrix.multiply(Mrgb, Matrix.toDouble(xyz65));	// linear RGB
		// perform forward gamma mapping:
		float[] srgb = new float[3];									
		for (int i = 0; i < 3; i++) {
			srgb[i] = (float) sRgbUtil.gammaFwd(rgb[i]);
		}
		return srgb;
	}

	// returned colors are in D50-based CS_CIEXYZ color space 
	// TODO: check double/float mix
	@Override
	public float[] toCIEXYZ(float[] srgbTHIS) {
		// get linear rgb components:
		double[] rgb = new double[3];
		for (int i = 0; i < 3; i++) {
			rgb[i] = sRgbUtil.gammaInv(srgbTHIS[i]);
		}
		// convert to D65-based XYZ (Poynton / ITU 709) 
		double[] xyz65 = Matrix.multiply(Mrgbi, rgb);
		float[] xyz50 = catD65toD50.applyTo(Matrix.toFloat(xyz65));	// PROBLEM: is this needed??
		return xyz50;
	}
	
	// ----------------------------------------------------
	
	@Override // assumes that srgb is in CS_sRGB color space
	public float[] fromRGB(float[] srgb50) {
		float[] xyz50 = sRgb50CS.toCIEXYZ(srgb50);
		return this.fromCIEXYZ(xyz50);
	}

	@Override // returns srgb colors in CS_sRGB color space
	public float[] toRGB(float[] srgb65) {
		float[] xyz50 = this.toCIEXYZ(srgb65);
		return sRgb50CS.fromCIEXYZ(xyz50);
	}
	
	// ----------------------------------------------------
	
	private static final String[] ComponentNames = {"R65", "G65", "B65"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}
	
	// ------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(16);
		sRgb65ColorSpace cs = sRgb65ColorSpace.getInstance();
		
		System.out.println("Mrgb = \n" + Matrix.toString(Mrgb) + "\n");
		System.out.println("Mrgbi = \n" + Matrix.toString(Mrgbi) + "\n");
		
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

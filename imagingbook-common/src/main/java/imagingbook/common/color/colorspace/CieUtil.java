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

import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;


/**
 * This class defines static methods for working with CIE-XYZ color space.
 * @author WB
 */
public abstract class CieUtil {
	
	private CieUtil() {}
	
	/**
	 * Calculates the XYZ coordinates for a given point (x,y) in the CIE
	 * xy-color diagram. XYZ is located on the 3D plane X + Y + Z = 1.
	 * Returns (0,0,0) for y = 0;
	 * @param x x-coordinate (in the 2D xy-diagram)
	 * @param y y-coordinate (in the 2D xy-diagram)
	 * @return the associated XYZ coordinate
	 */
	public static double[] xyToXYZ(double x, double y) {
		if (Arithmetic.isZero(y)) {
			return new double[] {0, 0, 0};
		}
		else {
			double Y = 1;
			double X = x * Y / y;
			double Z = (1 - x - y) * Y / y;
			double mag = X + Y + Z;
			return new double[] {X/mag, Y/mag, Z/mag};
		}
	}
	
	/**
	 * Calculates the XYZ coordinates for a given point (x,y) in the CIE
	 * xy-color diagram, with Y explicitly specified.
	 * @param x x-coordinate (in the 2D xy-diagram)
	 * @param y y-coordinate (in the 2D xy-diagram)
	 * @param Y the Y-coordinate (in 3D color space)
	 * @return the associated XYZ coordinate
	 */
	public static double[] xyToXYZ(double x, double y, double Y) {
		if (Arithmetic.isZero(Y)) {
			return new double[] {0, 0, 0};
		}
		else {
			double X = x * Y / y;
			double Z = (1 - x - y) * Y / y;
			//double mag = X + Y + Z;
			//return new double[] {X/mag, Y/mag, Z/mag};
			return new double[] {X, Y, Z};
		}
	}
	
	
	/**
	 * Calculates the 2D (x,y) color diagram coordinates for 3D XYZ color 
	 * coordinates (X,Y,Z).
	 * 
	 * @param XYZ the XYZ coordinate (3D)
	 * @return the xy-coordinate (2D)
	 */
	public static double[] XYZToXy(double[] XYZ) {
		double X = XYZ[0];
		double Y = XYZ[1];
		double Z = XYZ[2];
		double mag = X + Y + Z; 
		return (Arithmetic.isZero(mag)) ? new double[] {0, 0} : new double[] {X/mag, Y/mag};
	}
	
	// ------------------------------------------------------------------------
	

	// Poynton (ITU 709) 
	public static final double[][] Mrgb65i = 
		{{0.412453, 0.357580, 0.180423},
		 {0.212671, 0.715160, 0.072169},
		 {0.019334, 0.119193, 0.950227}};
	
	
	/** Matrix for conversion from linear RGB to XYZ. */
	public static final double[][] Mrgb65 = Matrix.inverse(Mrgb65i);  // (R,G,B) = Mrgb * (X,Y,Z)
	//from sRGB specs:
//	{{3.2406255, -1.537208, -0.4986286},
//	 {-0.9689307, 1.8757561, 0.0415175},
//	 {0.0557101, -0.2040211, 1.0569959}};
	
	// Poynton (ITU 709) 
//		{{ 3.240479, -1.537150, -0.498535},
//		 {-0.969256, 1.875992,  0.041556},
//		 { 0.055648, -0.204043,  1.057311}}; 
	
	// D50 from sRGB specs, most accurate for desired tristimulus X = 0.9642, Y = 1, Z = 0.8249 (https://www.color.org/sRGB.pdf)
	public static final double[][] Mrgb50i = 
		{{0.436030342570117, 0.385101860087134, 0.143067806654203},
		 {0.222438466210245, 0.716942745571917, 0.060618777416563},
		 {0.013897440074263, 0.097076381494207, 0.713926257896652}};
	
	// see book Eq. 14.49 (p. 443)
	public static final double[][] Mrgb50 = Matrix.inverse(Mrgb50i);
	
	
	// ------------------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(15);
		System.out.println("Mrgb50i = \n" + Matrix.toString(Mrgb50i));
		System.out.println("Mrgb50 = \n" + Matrix.toString(Mrgb50));
		
		System.out.println("------------------------------------");
		
		System.out.println("Mrgb65i = \n" + Matrix.toString(Mrgb65i));
		System.out.println("Mrgb65 = \n" + Matrix.toString(Mrgb65));
		
		System.out.println("----- Approximation of Mrgb50 by Bradford adaptation----------");
		
		BradfordAdaptation catD65toD50 = BradfordAdaptation.getInstance(D50, D65);
		double[][] Madapt = catD65toD50.getAdaptationMatrix();
		double[][] Mrgb50X = Matrix.multiply(Mrgb65, Madapt);
		double[][] Mrgb50Xi = Matrix.inverse(Mrgb50X);
		System.out.println("Mrgb50Xi = \n" + Matrix.toString(Mrgb50Xi));
		System.out.println("Mrgb50X = \n" + Matrix.toString(Mrgb50X));
		
		System.out.println("------------------------------------");
		double[][] Mrgb50_65 = Matrix.multiply(Mrgb65i, Mrgb50);
		System.out.println("Mrgb50_65 = \n" + Matrix.toString(Mrgb50_65));
	}
}

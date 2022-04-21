/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

//import java.util.Locale;

public abstract class CieUtil {
	
	/**
	 * Calculates the XYZ coordinates for a given point (x,y) in the CIE
	 * xy-color diagram. XYZ is located on the 3D plane X + Y + Z = 1.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return the associated XYZ coordinate
	 */
	public static double[] xyToXyz(double x, double y) {
		double Y = 1;
		double X = x * Y / y;				// TODO: check for y == 0
		double Z = (1 - x - y) * Y / y;		// TODO: check for y == 0
		double mag = X + Y + Z;
		return new double[] {X/mag, Y/mag, Z/mag};
	}
	
	public static double[] xyToXyz(double x, double y, double Y) {
		double X = x * Y / y;				// TODO: check for y == 0
		double Z = (1 - x - y) * Y / y;		// TODO: check for y == 0
		//double mag = X + Y + Z;
		//return new double[] {X/mag, Y/mag, Z/mag};
		return new double[] {X, Y, Z};
	}
	
	
	/**
	 * Calculates the (x, y) color diagram coordinates for XYZ color coordinates (X,Y,Z).
	 * 
	 * @param XYZ the XYZ coordinate
	 * @return the xy-coordinate
	 */
	public static double[] xyzToxy(double[] XYZ) {
		double X = XYZ[0];
		double Y = XYZ[1];
		double Z = XYZ[2];
		double mag = X + Y + Z; 
		return new double[] {X/mag, Y/mag};
	}
	
	public static float[] xyzToxy(float[] XYZ) {
		double X = XYZ[0];
		double Y = XYZ[1];
		double Z = XYZ[2];
		double mag = X + Y + Z; 
		return new float[] {(float) (X/mag), (float) (Y/mag)};
	}

//	public static void main(String[] args) {
//		double[] XYZ = {8,7,9};
//		double[] xy = xyzToxy(XYZ);
//		System.out.format(Locale.US, "x=%f, y =%f\n", xy[0], xy[1]);
//		
//		double[] XYZ2 = xyToXyz(xy[0], xy[1], XYZ[1]);
//		
//		System.out.format(Locale.US, "X=%f, Y=%f, Z=%f\n", XYZ2[0], XYZ2[1], XYZ2[2]);
//	}
}

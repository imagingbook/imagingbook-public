/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import imagingbook.common.math.Arithmetic;


/**
 * This class defines static methods for working with CIE-XYZ color space.
 * @author WB
 */
public abstract class CieUtil {
	
	private CieUtil() {}
	
	/**
	 * Calculates the XYZ coordinates for a given point (x,y) in the CIE
	 * xy-color diagram. XYZ is located on the 3D plane X + Y + Z = 1.
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
			double X = x * Y / y;				// TODO: check for y == 0
			double Z = (1 - x - y) * Y / y;		// TODO: check for y == 0
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
	
}

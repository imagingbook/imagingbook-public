/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.moments;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;

//TODO: make more efficient versions!

/**
 * This class defines static methods for moment calculations on 2D point sets.
 * 
 * @author WB
 *
 */
public abstract class Moments2D {
	
	private Moments2D() {}
	
	public static double ordinaryMoment(Iterable<Pnt2d> R, int p, int q) {
		double mpq = 0.0;
		if (p == 0 && q == 0) {	// just count the number of points
			for (@SuppressWarnings("unused") Pnt2d pnt : R) {
				mpq += 1;
			}
		}
		else {
			for (Pnt2d pnt : R) {
				mpq += Math.pow(pnt.getX(), p) * Math.pow(pnt.getY(), q);
			}
		}
		return mpq;
	}

	public static double centralMoment(Iterable<Pnt2d> R, int p, int q) {
		double m00 = ordinaryMoment(R, 0, 0); // region area
		if (Arithmetic.isZero(m00)) {
			throw new RuntimeException("empty point set");
		}
		double xc = ordinaryMoment(R, 1, 0) / m00;
		double yc = ordinaryMoment(R, 0, 1) / m00;
		double mupq = 0.0;
		for (Pnt2d pnt : R) {
			mupq += Math.pow(pnt.getX() - xc, p) * Math.pow(pnt.getY() - yc, q);
		}
		return mupq;
	}

	public static double normalizedCentralMoment(Iterable<Pnt2d> R, int p, int q) {
		double m00 = ordinaryMoment(R, 0, 0);
		double scale = 1.0 / Math.pow(m00, 0.5 * (p + q) + 1);
		return centralMoment(R, p, q) * scale;
	}


//	public static void main(String[] args) {
//
//		List<Pnt2d> points = Arrays.asList(
//				Pnt2d.from(10, 15), 
//				Pnt2d.from(3, 7), 
//				Pnt2d.from(-1, 5), 
//				Pnt2d.from(-1, 5));
//
//		System.out.println("Ordinary moments:");
//		System.out.println("m00 = " + ordinaryMoment(points, 0, 0));
//		System.out.println("m10 = " + ordinaryMoment(points, 1, 0));
//		System.out.println("m01 = " + ordinaryMoment(points, 0, 1));
//		System.out.println("m11 = " + ordinaryMoment(points, 1, 1));
//		System.out.println("m20 = " + ordinaryMoment(points, 2, 0));
//		System.out.println("m02 = " + ordinaryMoment(points, 0, 2));
//
//		double a = ordinaryMoment(points, 0, 0);
//		System.out.println("a = " + a);
//		System.out.println("xc = " + (ordinaryMoment(points, 1, 0) / a));
//		System.out.println("yc = " + (ordinaryMoment(points, 0, 1) / a));	
//
//		System.out.println("Central moments:");
//		System.out.println("mu10 = " + centralMoment(points, 1, 0));
//		System.out.println("mu01 = " + centralMoment(points, 0, 1));
//		System.out.println("mu11 = " + centralMoment(points, 1, 1));
//		System.out.println("mu20 = " + centralMoment(points, 2, 0));
//		System.out.println("mu02 = " + centralMoment(points, 0, 2));
//
//		System.out.println("Normalized central moments:");
//		System.out.println("nu10 = " + normalizedCentralMoment(points, 1, 0));
//		System.out.println("nu01 = " + normalizedCentralMoment(points, 0, 1));
//		System.out.println("nu11 = " + normalizedCentralMoment(points, 1, 1));
//		System.out.println("nu20 = " + normalizedCentralMoment(points, 2, 0));
//		System.out.println("nu02 = " + normalizedCentralMoment(points, 0, 2));
//	}

}

/*
Ordinary moments:
m00 = 4.0
m10 = 11.0
m01 = 32.0
m11 = 161.0
m20 = 111.0
m02 = 324.0
a = 4.0
xc = 2.75
yc = 8.0
Central moments:
mu10 = 0.0
mu01 = 0.0
mu11 = 73.0
mu20 = 80.75
mu02 = 68.0
Normalized central moments:
nu10 = 0.0
nu01 = 0.0
nu11 = 4.5625
nu20 = 5.046875
nu02 = 4.25
 */

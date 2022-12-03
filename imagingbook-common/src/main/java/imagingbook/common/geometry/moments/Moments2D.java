/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.moments;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;


/**
 * <p>
 * This class defines methods for statistical moment calculations on 2D point
 * sets. See Sec. 8.5 of [1] for details. This abstract class defines static
 * methods only.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/17
 */
public abstract class Moments2D { 	//TODO: make more efficient versions!
	
	private Moments2D() {}
	
	/**
	 * Calculates and returns the ordinary moment of order (p,q) for the
	 * specified set of 2D points.
	 * 
	 * @param points a set of 2D points
	 * @param p order index p
	 * @param q order index q
	 * @return the moment value
	 */
	public static double ordinaryMoment(Iterable<Pnt2d> points, int p, int q) {
		double mpq = 0.0;
		if (p == 0 && q == 0) {	// just count the number of points
			for (@SuppressWarnings("unused") Pnt2d pnt : points) {
				mpq += 1;
			}
		}
		else {
			for (Pnt2d pnt : points) {
				mpq += Math.pow(pnt.getX(), p) * Math.pow(pnt.getY(), q);
			}
		}
		return mpq;
	}

	/**
	 * Calculates and returns the central moment of order (p,q) for the
	 * specified set of 2D points.
	 * 
	 * @param points a set of 2D points
	 * @param p order index p
	 * @param q order index q
	 * @return the moment value
	 */
	public static double centralMoment(Iterable<Pnt2d> points, int p, int q) {
		double m00 = ordinaryMoment(points, 0, 0); // region area
		if (Arithmetic.isZero(m00)) {
			throw new RuntimeException("empty point set");
		}
		double xc = ordinaryMoment(points, 1, 0) / m00;
		double yc = ordinaryMoment(points, 0, 1) / m00;
		double mupq = 0.0;
		for (Pnt2d pnt : points) {
			mupq += Math.pow(pnt.getX() - xc, p) * Math.pow(pnt.getY() - yc, q);
		}
		return mupq;
	}

	/**
	 * Calculates and returns the normalized central moment of order (p,q) for the
	 * specified set of 2D points.
	 * 
	 * @param points a set of 2D points
	 * @param p order index p
	 * @param q order index q
	 * @return the moment value
	 */
	public static double normalizedCentralMoment(Iterable<Pnt2d> points, int p, int q) {
		double m00 = ordinaryMoment(points, 0, 0);
		double scale = 1.0 / Math.pow(m00, 0.5 * (p + q) + 1);
		return scale * centralMoment(points, p, q);
	}

}


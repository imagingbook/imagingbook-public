/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.basic;

import java.util.Arrays;

public abstract class PntUtils {
	
	private PntUtils() {}
	
	/**
	 * Calculates and returns the centroid of the specified
	 * point set.
	 * @param pts an {@link Iterable} of {@link Pnt2d} instances
	 * @return the centroid (as a {@link Pnt2d} instance)
	 */
	public static Pnt2d centroid(Iterable<Pnt2d> pts) {
		double sx = 0;
		double sy = 0;
		int n = 0;
		for (Pnt2d p : pts) {
			sx = sx + p.getX();
			sy = sy + p.getY();
			n++;
		}
		if (n == 0) {
			throw new IllegalArgumentException("at least one point is required for centroid calculation");
		}
		return Pnt2d.from(sx/n, sy/n);
	}
	
	/**
	 * Calculates and returns the centroid of the specified
	 * point set.
	 * @param pts an array of {@link Pnt2d} instances
	 * @return the centroid (as a {@link Pnt2d} instance)
	 */
	public static Pnt2d centroid(Pnt2d[] pts) {
		return centroid(() -> Arrays.stream(pts).iterator());
	}
	
	// -------------------------------------------------------------------
	
	
	/**
	 * Converts a given point array {@code Pnt2d[n]} to a 2D double array {@code double[n][2]},
	 * with x-coordinates in column 0 and y-coordinates in column 1.
	 * @param pts the point array
	 * @return a 2D double array
	 */
	public static double[][] toDoubleArray(Pnt2d[] pts) {
		final int n = pts.length;
		double[][] pa = new double[n][2];
		for (int i = 0; i < n; i++) {
			pa[i][0] = pts[i].getX();
			pa[i][1] = pts[i].getY();
		}
		return pa;
	}
	
	/**
	 * Converts a given 2D double array {@code double[n][2]} to a point array {@code Pnt2d[n]},
	 * taking x-coordinates from column 0 and y-coordinates from column 1.
	 * @param da a 2D double array 
	 * @return a point array
	 */
	public static Pnt2d[] fromDoubleArray(double[][] da) {
		final int n = da.length;
		Pnt2d[] pts = new Pnt2d[n];
		for (int i = 0; i < n; i++) {
			pts[i] = Pnt2d.from(da[i]);
		}
		return pts;
	}
	
}

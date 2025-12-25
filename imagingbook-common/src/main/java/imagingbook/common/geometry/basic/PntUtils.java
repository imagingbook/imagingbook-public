/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines static methods for manipulating 2D points.
 */
public final class PntUtils {
	
	private PntUtils() {}

	/**
	 * Simply counts the points in the specified point set.
	 * @param pts an {@link Iterable} of {@link Pnt2d} instances
	 * @return the number of points
	 */
	public static int count(Iterable<Pnt2d> pts) {
		int n = 0;
		for (Pnt2d p : pts) {
			n++;
		}
		return n;
	}

	/**
	 * Calculates and returns the centroid of the specified point set.
	 *
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
	 * Calculates and returns the centroid of the specified point set.
	 *
	 * @param pts an array of {@link Pnt2d} instances
	 * @return the centroid (as a {@link Pnt2d} instance)
	 */
	public static Pnt2d centroid(Pnt2d[] pts) {
		return centroid(() -> Arrays.stream(pts).iterator());
	}
	
	// -------------------------------------------------------------------

	/**
	 * Converts a given point array {@code Pnt2d[n]} to a 2D double array {@code double[n][2]}, with x-coordinates in
	 * column 0 and y-coordinates in column 1.
	 *
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
	 * Converts a given 2D double array {@code double[n][2]} to a point array {@code Pnt2d[n]}, taking x-coordinates
	 * from column 0 and y-coordinates from column 1.
	 *
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
	
	// -------------------------------------------------------------------

	/**
	 * Creates and returns an array of {@link Pnt2d.PntInt} points from the given sequence of {@code int} coordinate
	 * values, interpreted as x/y pairs. Throws an exception if the length of the coordinate sequence is not even. Usage
	 * example:
	 * <pre>
	 * Pnt2d[] pts = PntUtils.makeIntPoints(1, 2, 3, 4, 5, 6);
	 *    // gives [PntInt(1, 2), PntInt(3, 4), PntInt(5, 6)]
	 * </pre>
	 *
	 * @param xy an even-numbered sequence of point coordinates
	 * @return an array of {@link Pnt2d.PntInt} points
	 */
	public static Pnt2d[] makeIntPoints(int... xy) {
		if (xy.length % 2 != 0) {
			throw new IllegalArgumentException("even number of point coordinates required");
		}
		final int n = xy.length/2;
		Pnt2d[] pts = new Pnt2d[n];
		for (int i = 0, j = 0; i < n; i++, j += 2) {
			pts[i] = new PntInt(xy[j], xy[j + 1]);
		}
		return pts;
	}

	/**
	 * Creates and returns an array of {@link Pnt2d.PntDouble} points from the given sequence of {@code double}
	 * coordinate values, interpreted as x/y pairs. Throws an exception if the length of the coordinate sequence is not
	 * even. Usage example:
	 * <pre>
	 * Pnt2d[] pts = PntUtils.makeDoublePoints(1, 2, 3, 4, 5, 6);
	 * // gives [PntDouble(1.0, 2.0), PntDouble(3.0, 4.0), PntDouble(5.0, 6.0)]
	 * </pre>
	 *
	 * @param xy an even-numbered sequence of point coordinates
	 * @return an array of {@link Pnt2d.PntDouble} points
	 */
	public static Pnt2d[] makeDoublePoints(double... xy) {
		if (xy.length % 2 != 0) {
			throw new IllegalArgumentException("even number of point coordinates required");
		}
		final int n = xy.length/2;
		Pnt2d[] pts = new Pnt2d[n];
		for (int i = 0, j = 0; i < n; i++, j += 2) {
			pts[i] = new PntDouble(xy[j], xy[j + 1]);
		}
		return pts;
	}

    /**
     * For testing.
     * @param coords a sequence of x/y coordinate pairs
     * @return
     */
    public static List<Pnt2d> makePntList(double... coords) {
        List<Pnt2d> pntList = new ArrayList<>();
        for (int i = 0; i < coords.length; i+=2) {
            pntList.add(Pnt2d.from(coords[i], coords[i + 1]));
        }
        return pntList;
    }

    /**
     * For testing.
     * @param coords a Nx2 array of x/y coordinate pairs
     * @return
     */
    public static List<Pnt2d> makePntList(double[][] coords) {
        List<Pnt2d> pntList = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            pntList.add(Pnt2d.from(coords[i][0], coords[i][1]));
        }
        return pntList;
    }

	// --------------------------------------------------------------------------------------

	/**
	 * Creates and returns a {@code double[2][N]} array, i.e., x and y
	 * coordinates placed in a pair of separate arrays.
	 * @param pts a list of 2D points of length {@code N}
	 * @return a {@code double[2][N]} coordinate array
	 */
	public static double[][] toXYArray(List<Pnt2d> pts) {
		int m = pts.size();
		double[][] XY = new double[2][m]; // XY[0] are x-values, XY[1] are y-values
		int i = 0;
		for (Pnt2d p : pts) {
			XY[0][i] = p.getX();
			XY[1][i] = p.getY();
			i++;
		}
		return XY;
	}

	/**
	 * Creates and returns a list of {@code N} {@link Pnt2d} points derived
	 * from the supplied {@code double[2][N]} array.
	 * @param XY a {@code double[2][N]} coordinate array
	 * @return a list of {@code N} {@link Pnt2d} instances
	 */
	public static List<Pnt2d> fromXYArray(double[][] XY) {
		int m = XY[0].length;
		if (m != XY[1].length) {
			throw new IllegalArgumentException("mismatch XY subarray length");
		}
		List<Pnt2d> pntList = new ArrayList<>(m);
		for (int i = 0; i < m; i++) {
			pntList.add(Pnt2d.from(XY[0][i], XY[1][i]));
		}
		return pntList;
	}

	// ------------------------------------------------------------------------------------------

	public static List<Pnt2d> makeCircle(double radius, int steps) {
		List<Pnt2d> circle = new ArrayList<>(steps);
		for (int i = 0; i < steps; i++) {
			double angle = i * 2 * Math.PI / steps;
			double x = radius * Math.cos(angle);
			double y = radius * Math.sin(angle);
			circle.add(Pnt2d.from(x, y));
		}
		return circle;
	}
}

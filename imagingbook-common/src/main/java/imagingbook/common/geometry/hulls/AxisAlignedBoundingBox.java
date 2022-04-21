/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.hulls;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.radius;
import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.common.math.Matrix.add;
import static imagingbook.common.math.Matrix.multiply;
import static java.lang.Math.sqrt;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;

/**
 * Represents a major axis-aligned bounding box of a 2D point set.
 * 
 * @author WB
 * @version 2021/10/11
 * 
 */
public class AxisAlignedBoundingBox {
	
	private final Pnt2d[] boundingBox;	
	
	public AxisAlignedBoundingBox(Iterable<Pnt2d> points) {
		this.boundingBox = makeBox(points);
	}
	
	/**
	 * Returns an array holding the 4 corner points of the bounding box or 
	 * {@code null} if the orientation of the point set is undefined.
	 * @return as described above
	 */
	public Pnt2d[] getCornerPoints() {
		return (boundingBox == null) ? null : boundingBox;
	}
		
	/**
	 * Calculates the major axis-aligned bounding box of 
	 * the supplied region, as a sequence of four point
	 * coordinates (p0, p1, p2, p3).
	 * 
	 * @param points binary region
	 * @return the region's bounding box as a sequence of 4 coordinates (p0, p1, p2, p3)
	 */
	private Pnt2d[] makeBox(Iterable<Pnt2d> points) {
		//double theta = getOrientationAngle(points);
		
		double[] xy = getOrientationVector(points);
		if (xy == null) {	// regin's orientation is undefined
			return null;
		}
			
		double xa = xy[0]; // = Math.cos(theta);
		double ya = xy[1]; // = Math.sin(theta);
		double[] ea = {xa,  ya};
		double[] eb = {ya, -xa};
		
		double amin = Double.POSITIVE_INFINITY;
		double amax = Double.NEGATIVE_INFINITY;
		double bmin = Double.POSITIVE_INFINITY;
		double bmax = Double.NEGATIVE_INFINITY;
		
		for (Pnt2d p : points) {
			double u = p.getX();
			double v = p.getY();
			double a = u * xa + v * ya;	// project (u,v) on the major axis vector
			double b = u * ya - v * xa;	// project (u,v) on perpendicular vector
			amin = Math.min(a, amin);
			amax = Math.max(a, amax);
			bmin = Math.min(b, bmin);
			bmax = Math.max(b, bmax);
		}
		
		Pnt2d[] corners = new Pnt2d[4];
		corners[0] = PntDouble.from(add(multiply(amin, ea), multiply(bmin, eb)));
		corners[1] = PntDouble.from(add(multiply(amin, ea), multiply(bmax, eb)));
		corners[2] = PntDouble.from(add(multiply(amax, ea), multiply(bmax, eb)));
		corners[3] = PntDouble.from(add(multiply(amax, ea), multiply(bmin, eb)));
		return corners;
	}

	private double[] getOrientationVector(Iterable<Pnt2d> points) {
		double[] centroid = getCentroid(points);
		final double xc = centroid[0];
		final double yc = centroid[1];
		double mu20 = 0;
		double mu02 = 0;
		double mu11 = 0;

		for (Pnt2d p : points) {
			double dx = (p.getX() - xc);
			double dy = (p.getY() - yc);
			mu20 = mu20 + dx * dx;
			mu02 = mu02 + dy * dy;
			mu11 = mu11 + dx * dy;
		}
		
		double A = 2 * mu11;
		double B = mu20 - mu02;
		
		double xTheta = B + sqrt(sqr(A) + sqr(B));
		double yTheta = A;
		double d = radius(xTheta, yTheta);
		
		return (isZero(d)) ? null : new double[] {xTheta / d, yTheta / d};
	}
	
	private double[] getCentroid(Iterable<Pnt2d> points) {
		int n = 0;
		double su = 0;
		double sv = 0;
		for (Pnt2d p : points) {
			su += p.getX();
			sv += p.getY();
			n++;
		}
		if (n == 0) {
			throw new IllegalArgumentException("empty point sequence!");
		}
		return new double[] {su/n, sv/n};
	}
	
}

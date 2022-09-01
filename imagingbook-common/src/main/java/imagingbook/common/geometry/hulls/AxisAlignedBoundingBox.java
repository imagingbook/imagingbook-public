/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.hulls;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.radius;
import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.common.math.Matrix.add;
import static imagingbook.common.math.Matrix.multiply;
import static java.lang.Math.sqrt;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Arrays;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.shape.ShapeProducer;

/**
 * Represents a major axis-aligned bounding box of a 2D point set.
 * 
 * @author WB
 * @version 2022/06/23
 * 
 */
public class AxisAlignedBoundingBox implements ShapeProducer {
	
	private final Iterable<Pnt2d> points;
	private final PntDouble centroid;
	private final double[] orientationVector;
	private final Pnt2d[] boundingbox;
	private final AlgebraicLine[] boundinglines;

	
	public AxisAlignedBoundingBox(Iterable<Pnt2d> points) {
		this.points = points;
		this.centroid = makeCentroid();
		this.orientationVector = makeOrientationVector();
		this.boundingbox = makeBox();
		this.boundinglines = new AlgebraicLine[] {
			AlgebraicLine.from(boundingbox[0], boundingbox[1]),
			AlgebraicLine.from(boundingbox[1], boundingbox[2]),
			AlgebraicLine.from(boundingbox[2], boundingbox[3]),
			AlgebraicLine.from(boundingbox[3], boundingbox[0])};
	}
	
	// TODO: test!
	public AxisAlignedBoundingBox(Pnt2d[] points) {
		this(() -> Arrays.stream(points).iterator());	// https://stackoverflow.com/questions/10335662/convert-java-array-to-iterable
	}
	
	public Pnt2d getCentroid() {
		return centroid;
	}
	
	public double[] getOrientationVector() {
		return orientationVector;
	}
	
	/**
	 * Returns an array holding the 4 corner points of the bounding box or 
	 * {@code null} if the orientation of the point set is undefined.
	 * @return as described above
	 */
	public Pnt2d[] getCornerPoints() {
		return (boundingbox == null) ? null : boundingbox;
	}
		
	/**
	 * Calculates the major axis-aligned bounding box of 
	 * the supplied region, as a sequence of four point
	 * coordinates (p0, p1, p2, p3).
	 * 
	 * @param points binary region
	 * @return the region's bounding box as a sequence of 4 coordinates (p0, p1, p2, p3)
	 */
	private Pnt2d[] makeBox() {
		//double theta = getOrientationAngle(points);
		
		double[] xy = this.orientationVector;
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
		
//		double delta = 1e-6;
//		amin -= delta;
//		bmin -= delta;
//		amax += delta;
//		bmax += delta;
		
		Pnt2d[] corners = new Pnt2d[4];
		corners[0] = PntDouble.from(add(multiply(amin, ea), multiply(bmin, eb)));
		corners[1] = PntDouble.from(add(multiply(amin, ea), multiply(bmax, eb)));
		corners[2] = PntDouble.from(add(multiply(amax, ea), multiply(bmax, eb)));
		corners[3] = PntDouble.from(add(multiply(amax, ea), multiply(bmin, eb)));
		return corners;
	}

	public double[] makeOrientationVector() {
		double mu20 = 0;
		double mu02 = 0;
		double mu11 = 0;

		for (Pnt2d p : points) {
			double dx = (p.getX() - centroid.x);
			double dy = (p.getY() - centroid.y);
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
	
	private PntDouble makeCentroid() {
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
		return PntDouble.from(su/n, sv/n);
	}


	// shape-related methods:
	
	@Override
	public Path2D getShape(double scale) {
		// shape of the actual bounding box
		Path2D p = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
		p.moveTo(boundingbox[0].getX(), boundingbox[0].getY());
		p.lineTo(boundingbox[1].getX(), boundingbox[1].getY());
		p.lineTo(boundingbox[2].getX(), boundingbox[2].getY());
		p.lineTo(boundingbox[3].getX(), boundingbox[3].getY());
		p.closePath();
		return p;
	}
	
	@Override
	public Shape[] getShapes(double scale) {
		return new Shape[] { 
				getShape(scale), 				// primary shape element
				getCentroid().getShape(scale) 	// additional shape elements
				};
	}
	
	public static final double DefaultContainsTolerance = 1e-12;
	
	public boolean contains(Pnt2d p) {
		return contains(p, DefaultContainsTolerance);
	}
	
	/**
	 * Checks if this bounding box contains the specified point.
	 * This method is used instead of {@link Path2D#contains(double, double)}
	 * to avoid false results due to roundoff errors.
	 * 
	 * @param p some 2D point
	 * @param tolerance positive quantity for being outside
	 * @return true if the point is inside the bounding box
	 */
	public boolean contains(Pnt2d p, double tolerance) {
		for (int i = 0; i < 4; i++) {
			double dist = boundinglines[0].getSignedDistance(p);
			// positive signed distance means that the point is to the left
			if (dist + tolerance < 0) {
				return false;
			}
		}
		return true;
	}
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.hulls;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.shape.ShapeProducer;
import imagingbook.common.util.ArrayUtils;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Arrays;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.radius;
import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.common.math.Matrix.add;
import static imagingbook.common.math.Matrix.makeDoubleVector;
import static imagingbook.common.math.Matrix.multiply;
import static java.lang.Math.sqrt;

/**
 * <p>
 * Represents a major axis-aligned bounding box of a 2D point set. At least one point is required to set up a bounding
 * box. If the direction vector is undefined (e.g., in the case of a single input point), the unit vector in x-direction
 * is assumed. See Sec. 8.6.4 (Alg. 8.6) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/01
 */
public class AxisAlignedBoundingBox implements ShapeProducer {
	
	private final Pnt2d centroid;	// centroid of the point set
	private final double[] dir;		// orientation vector
	private final Pnt2d[] corners;	// corner points of the bounding box
	private final AlgebraicLine[] boundinglines;

	/**
	 * Constructor, creates a {@link AxisAlignedBoundingBox} instance from an {@link Iterable} over {@link Pnt2d}. At
	 * least one distinct point is required.
	 *
	 * @param points an iterator over 2D points
	 */
	public AxisAlignedBoundingBox(Iterable<Pnt2d> points) {
		if (!points.iterator().hasNext()) {
			throw new IllegalArgumentException("empty point sequence, at least one input point required");
		}
		this.centroid = PntUtils.centroid(points); //makeCentroid();	
		this.dir = makeOrientationVector(points);
		this.corners = makeBox(points);
		this.boundinglines = makeBoundingLines();
	}

	/**
	 * Constructor, creates a {@link AxisAlignedBoundingBox} instance from an array of {@link Pnt2d} points. At least
	 * one distinct point is required.
	 *
	 * @param points an array of 2D points
	 */
	public AxisAlignedBoundingBox(Pnt2d[] points) {
		this(() -> ArrayUtils.getIterator(points));
	}

	/**
	 * Returns the orientation vector of this bounding box (pointing in the direction of its major axis).
	 *
	 * @return the orientation vector of this bounding box
	 */
	public double[] getOrientationVector() {
		return dir;
	}

	/**
	 * Returns an array of four {@link AlgebraicLine} instances which coincide with the outline of the bounding box. The
	 * lines can be used to determine if a given point is inside the box or not (see {@link #contains(Pnt2d, double)}).
	 *
	 * @return an array of bounding lines
	 */
	public AlgebraicLine[] getBoundingLines() {
		return this.boundinglines;
	}
	
	/**
	 * Returns an array holding the 4 corner points of the bounding box.
	 * 
	 * @return as described above
	 */
	public Pnt2d[] getVertices() {
		return this.corners;
	}

	/**
	 * Calculates the major axis-aligned bounding box of the supplied region, as a sequence of four point coordinates
	 * (p0, p1, p2, p3).
	 *
	 * @return the region's bounding box as a sequence of the 4 corner coordinates (p0, p1, p2, p3)
	 */
	private Pnt2d[] makeBox(Iterable<Pnt2d> points) {
		//double theta = getOrientationAngle(points);
		
		double[] xy = this.dir;
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

	private double[] makeOrientationVector(Iterable<Pnt2d> points) {
		double mu20 = 0;
		double mu02 = 0;
		double mu11 = 0;
		double xc = centroid.getX();
		double yc = centroid.getY();
		
		for (Pnt2d p : points) {
			double dx = p.getX() - xc;
			double dy = p.getY() - yc;
			mu20 = mu20 + dx * dx;
			mu02 = mu02 + dy * dy;
			mu11 = mu11 + dx * dy;
		}
		
		double A = 2 * mu11;
		double B = mu20 - mu02;
		
		double xTheta = B + sqrt(sqr(A) + sqr(B));
		double yTheta = A;
		double d = radius(xTheta, yTheta);
		
		return (isZero(d)) ? new double[] {1, 0} : new double[] {xTheta / d, yTheta / d};
	}
	
	// shape-related methods:
	
	@Override
	public Path2D getShape(double scale) {
		// shape of the actual bounding box
		Path2D p = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
		p.moveTo(corners[0].getX(), corners[0].getY());
		p.lineTo(corners[1].getX(), corners[1].getY());
		p.lineTo(corners[2].getX(), corners[2].getY());
		p.lineTo(corners[3].getX(), corners[3].getY());
		p.closePath();
		return p;
	}
	
	@Override
	public Shape[] getShapes(double scale) {
		return new Shape[] { 
				getShape(scale), 				// primary shape element
				this.centroid.getShape(scale) 	// additional shape elements
				};
	}
	
	private AlgebraicLine[] makeBoundingLines() {
		double dx = dir[0];
		double dy = dir[1];
		return new AlgebraicLine[] {
				AlgebraicLine.from(corners[0].toDoubleArray(), makeDoubleVector( dy, -dx)),		// 0 -> 1
				AlgebraicLine.from(corners[1].toDoubleArray(), makeDoubleVector( dx,  dy)),		// 1 -> 2
				AlgebraicLine.from(corners[2].toDoubleArray(), makeDoubleVector(-dy,  dx)),		// 2 -> 3
				AlgebraicLine.from(corners[3].toDoubleArray(), makeDoubleVector(-dx, -dy))
				};	// 3 -> 0
	}
	
	public static final double DefaultContainsTolerance = 1e-12;


	/**
	 * Checks if this bounding box contains the specified point using the default tolerance. This method is used instead
	 * of {@link Path2D#contains(double, double)} to avoid false results due to roundoff errors.
	 *
	 * @param p some 2D point
	 * @return true if the point is inside the bounding box
	 */
	public boolean contains(Pnt2d p) {
		return contains(p, DefaultContainsTolerance);
	}

	/**
	 * Checks if this bounding box contains the specified point using the specified tolerance. This method is used
	 * instead of {@link Path2D#contains(double, double)} to avoid false results due to roundoff errors.
	 *
	 * @param p some 2D point
	 * @param tolerance positive quantity for being outside
	 * @return true if the point is inside the bounding box
	 */
	public boolean contains(Pnt2d p, double tolerance) {
		for (int i = 0; i < 4; i++) {
			double dist = boundinglines[i].getSignedDistance(p);
			// positive signed distance means that the point is to the left
			if (dist + tolerance < 0) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", this.getClass().getSimpleName(), Arrays.toString(this.corners));
	}
}

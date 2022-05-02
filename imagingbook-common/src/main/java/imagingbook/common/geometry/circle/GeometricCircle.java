/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.circle;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.util.Locale;

import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.shape.ShapeProducer;
import imagingbook.common.math.Arithmetic;

/**
 * Represents a geometric circle with center point (xc, yc) and radius r.
 * Instances are immutable.
 * 
 * @author WB
 *
 */
public class GeometricCircle implements ShapeProducer, Curve2d {
	
	private final double xc, yc, r;

	public double getXc() {
		return xc;
	}

	public double getYc() {
		return yc;
	}

	public double getR() {
		return r;
	}

	public GeometricCircle(double xc, double yc, double r) {
		if (r < 0) {
			throw new IllegalArgumentException("negative circle radius");
		}
		this.xc = xc;
		this.yc = yc;
		this.r = r;		
	}
	
	/**
	 * Constructor.
	 * Creates a new {@link GeometricCircle} instance from parameters p = [xc, yc, r].
	 * @param p circle parameters [xc, yc, r]
	 */
	public GeometricCircle(double[] p) {
		this(p[0], p[1], p[2]);
	}
	
	/**
	 * Constructor.
	 * Creates a new {@link GeometricCircle} from a {@link AlgebraicCircle} instance.
	 * @param ac a {@link AlgebraicCircle} instance
	 */
	public GeometricCircle(AlgebraicCircle ac) {
		this(getGeometricCircleParameters(ac));
	}
	
	private static double[] getGeometricCircleParameters(AlgebraicCircle ac) {
		double[] p = ac.getParameters();
		if (Arithmetic.isZero(p[0])) {
			throw new RuntimeException("infinite circle radius");
		}
		double A = p[0];
		double B = p[1];
		double C = p[2];
		double D = p[3];
		double xc = -B / (2 * A);
		double yc = -C / (2 * A);
		double r = sqrt(sqr(B) + sqr(C) - 4 * A * D) / abs(2 * A);
		return new double[] {xc, yc, r};
	}
	
	// --------------------------------------------------------------------------------
	
	/**
	 * Return a vector of parameters for this circle.
	 * The length of the vector and the meaning of the parameters depends
	 * on the concrete circle type.
	 * 
	 * @return a vector of parameters
	 */
	public double[] getParameters() {
		return new double[] {xc, yc, r};
	}
	
	public Pnt2d getCenter() {
		return Pnt2d.from(xc, yc);
	}

	// --------------------------------------------------------------------------------
	
	/**
	 * Calculates and returns the mean of the squared distances between this circle 
	 * and a set of 2D points.
	 *
	 * @param points a set of sample points (usually the points used for fitting)
	 * @return the mean squared error
	 */
	public double getMeanSquareError(Pnt2d[] points) {
		final int n = points.length;
		double sumR2 = 0;
		for (int i = 0; i < n; i++) {
			sumR2 += sqr(getDistance(points[i]));
		}
		return sumR2 / n;
	}
	
	/**
	 * Returns the (unsigned) distance between the specified point
	 * and this circle. The result is always non-negative.
	 * 
	 * @param p a 2D point
	 * @return the point's distance from the circle
	 */
	@Override
	public double getDistance(Pnt2d p) {
		return Math.abs(getSignedDistance(p));
	}
	
	/**
	 * Returns the signed distance between the specified point
	 * and this circle. The result is positive for points outside
	 * the circle, negative inside.
	 * 
	 * @param p a 2D point
	 * @return the point's signed distance from the circle
	 */
	public double getSignedDistance(Pnt2d p) {
		double dx = p.getX() - this.xc;
		double dy = p.getY() - this.yc;
		double rp = Math.hypot(dx, dy);
		return rp - this.r;
	}
	
	// ------------------------------------------------------------------

	private Shape getCenterShape(double radius) {
		Path2D path = new Path2D.Double();
		path.moveTo(xc - radius, yc);
		path.lineTo(xc + radius, yc);
		path.moveTo(xc, yc - radius);
		path.lineTo(xc, yc + radius);
		return path;
	}
	
	private Shape getOuterShape() {
		return new Arc2D.Double(xc - r, yc - r, 2 * r, 2 * r, 0, 360, Arc2D.OPEN);
	}
	
	@Override
	public Shape getShape(double scale) {
		return getOuterShape() ;
	}
	
	@Override
	public Shape[] getShapes(double scale) {
		return new Shape[] {getOuterShape(), getCenterShape(scale)};
	}
	
	// ---------------------------------------------------------------------------------
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GeometricCircle)) {
            return false;
        }
		return this.equals((GeometricCircle) other, Arithmetic.EPSILON_DOUBLE);
	}
	
	
	public boolean equals(GeometricCircle other, double tolerance) {
		return 
				Arithmetic.equals(xc, other.xc, tolerance) &&
				Arithmetic.equals(yc, other.yc, tolerance) &&
				Arithmetic.equals(r, other.r, tolerance) ;
	}
	

	public GeometricCircle duplicate() {
		return new GeometricCircle(this.getParameters());
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s [xc=%f, yc=%f, r=%f]", 
				this.getClass().getSimpleName(), xc, yc, r);
	}
}

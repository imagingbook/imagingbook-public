/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Locale;

import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.shape.ShapeProducer;
import imagingbook.common.hough.HoughLine;
import imagingbook.common.math.Arithmetic;

/**
 * This class represents an algebraic line of the form A x + B y + C = 0.
 * Instances are immutable and normalized such that ||(A,B)|| = 1.
 * @author WB
 *
 */
public class AlgebraicLine implements ShapeProducer, Curve2d {
	
	public final double A, B, C;
	
	// constructors --------------------------------------------------

	/**
	 * Constructor. Creates a {@link AlgebraicLine} instance with parameters A, B, C.
	 * @param A line parameter A
	 * @param B line parameter B
	 * @param C line parameter C
	 */
	public AlgebraicLine(double A, double B, double C) {
		double norm = Math.sqrt(sqr(A) + sqr(B));
		if (isZero(norm)) {
			throw new IllegalArgumentException("A and B may not both be zero");
		}
		if (A >= 0) {
			this.A = A / norm;
			this.B = B / norm;
			this.C = C / norm;
		}
		else {
			this.A = -A / norm;
			this.B = -B / norm;
			this.C = -C / norm;
		}
	}
	
	/**
	 * Constructor.  Creates a {@link AlgebraicLine} instance from a 
	 * parameter vector [A, B, C].
	 * @param p parameter vector [A, B, C]
	 */
	public AlgebraicLine(double[] p) {
		this(p[0], p[1], p[2]);
	}
	
	
	// static factory methods ----------------------------------------
	
	// Line from start point s and direction vector v
	public static AlgebraicLine from(double[] s, double[] v) {
		double a = -v[1];
		double b = v[0];
		double c = v[1] * s[0] - v[0] * s[1];
		return new AlgebraicLine(a, b, c);
	}
	
	public static AlgebraicLine from(ParametricLine pl) {
		return AlgebraicLine.from(pl.getS(), pl.getV());
	}
	
	public static AlgebraicLine from(Pnt2d p0, Pnt2d p1) {
		double[] s = p0.toDoubleArray();
		double[] v = p1.minus(p0).toDoubleArray();
		return AlgebraicLine.from(s, v);
	}
	
	public static AlgebraicLine from(SlopeInterceptLine sil) {
//		double a = sil.getK();
//		double c = sil.getD();
//		return new AlgebraicLine(a, -1, c);	
		return new AlgebraicLine(sil.A, sil.B, sil.C);	
	}
	
	// getter/setter methods ------------------------------------------
	
	/**
	 * Returns the algebraic line parameters [A, B, C].
	 * @return algebraic line parameters
	 */
	public final double[] getParameters() {
		return new double[] {A, B, C};
	}
	
	/**
	 * Returns the x-coordinate of the reference point.
	 * For a {@link AlgebraicLine}, the result is always zero.
	 * Inheriting classes (like {@link HoughLine}) override this method.
	 * 
	 * @return the x-coordinate reference
	 */
	public double getXref() {
		return 0.0;
	}
	
	/**
	 * Returns the y-coordinate of the reference point.
	 * For a {@link AlgebraicLine}, the result is always zero.
	 * Inheriting classes (like {@link HoughLine}) override this method.
	 * 
	 * @return the y-coordinate reference
	 */
	public double getYref() {
		return 0.0;
	}
	
	// other methods ------------------------------------------
	
	/**
	 * Returns the orthogonal distance between this line and the point (x, y).
	 * The result may be positive or negative, depending on which side of the line
	 * (x, y) is located. It is assumed that the line is normalized, i.e.,
	 * ||(a,b)|| = 1.
	 * 
	 * @param x x-coordinate of point position.
	 * @param y y-coordinate of point position.
	 * @return The perpendicular distance between this line and the point (x, y).
	 */
	public double getDistance(double x, double y) {
		return Math.abs(getSignedDistance(x, y));
	}
	
	/**
	 * Returns the orthogonal (unsigned) distance between this line and the point p. The
	 * result is always non-negative.
	 * 
	 * @param p point position.
	 * @return The perpendicular distance between this line and the point p.
	 */
	@Override
	public double getDistance(Pnt2d p) {
		return Math.abs(getSignedDistance(p));
	}
	
	/**
	 * Returns the orthogonal (signed) distance between this line and the point (x, y).
	 * The result may be positive or negative, depending on which side of the line
	 * (x, y) is located. It is assumed that the line is normalized, i.e.,
	 * ||(A,B)|| = 1.
	 * 
	 * @param x x-coordinate of point position.
	 * @param y y-coordinate of point position.
	 * @return The perpendicular distance between this line and the point (x, y).
	 */
	public double getSignedDistance(double x, double y) {
		return (A * (x - this.getXref()) + B * (y - this.getYref()) + C);
	}
	
	public double getSignedDistance(Pnt2d p) {
		return getSignedDistance(p.getX(), p.getY());
	}
	
	
	/**
	 * Returns the point on the line that is closest to the specified
	 * 2D point. The line is assumed to be normalized.
	 * 
	 * @param p an arbitrary 2D point
	 * @return the closest line point
	 */
	public Pnt2d getClosestLinePoint(Pnt2d p) {
		final double s = 1.0; // 1.0 / (sqr(a) + sqr(b)); // assumed to be normalized
		final double xr = this.getXref();
		final double yr = this.getYref();
		double xx = p.getX() - xr;
		double yy = p.getY() - yr;
		double x0 = xr + s * (sqr(B) * xx - A * B * yy - A * C);
		double y0 = yr + s * (sqr(A) * yy - A * B * xx - B * C);
		return PntDouble.from(x0, y0);
	}	

	public double getSquareError(Pnt2d[] points) {
		double sum2 = 0;
		for (Pnt2d p : points) {
			sum2 = sum2 + sqr(getDistance(p));
		}
		return sum2;
	}
	
	public Pnt2d intersect(AlgebraicLine L2) {
		AlgebraicLine L1 = this;	
		double det = L1.A * L2.B - L2.A * L1.B;
		if (isZero(det)) {
			return null;
		}
		else {
			double x = (L1.B * L2.C - L2.B * L1.C) / det;
			double y = (L2.A * L1.C - L1.A * L2.C) / det;
			return Pnt2d.from(x, y);
		}
	}
	
	// -------------------------------------------------------------------
	
	@Override
	public Shape getShape(double length) {
		double xRef = this.getXref();
		double yRef = this.getYref();
//		double angle = Math.atan2(this.B, this.A); 	//this.getAngle();
		double radius = -this.C;					//this.getRadius();
		// unit vector perpendicular to the line
		double dx = this.A; 						// Math.cos(angle);	
		double dy = this.B;							// Math.sin(angle);
		// calculate the line's center point (closest to the reference point)
		double x0 = xRef + radius * dx;
		double y0 = yRef + radius * dy;
		// calculate the line end points (using normal vectors)
		double x1 = x0 + dy * length;
		double y1 = y0 - dx * length;
		double x2 = x0 - dy * length;
		double y2 = y0 + dx * length;
		Path2D path = new Path2D.Double();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		return path;
	}
	
	/**
	 * Returns a {@link Shape} for this line to be drawn in a canvas of the
	 * specified size. Since an algebraic line is of infinite extent, we need
	 * to know the drawing size. The returned line segment is sufficiently long 
	 * to cover the entire canvas, i.e., both end points are outside the
	 * canvas.
	 *
	 * @param width the width of the drawing canvas
	 * @param height the height of the drawing canvas
	 * @return a {@link Shape} instance for this line
	 */
	public Shape getShape(int width, int height) {
		double length = Math.sqrt(sqr(width) + sqr(height));
		return this.getShape(length);
	}
	
//	public Shape getShape(int width, int height) {
//		HoughLine hl = new HoughLine(this, 0.5 * width, 0.5 * height, 0);
//		return hl.getShape(width, height);
//		
//	}
	
	// -------------------------------------------------------------------
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof AlgebraicLine) {
			return this.equals((AlgebraicLine) other, Arithmetic.EPSILON_DOUBLE);
		}
		else {
			return false;
		}
	}
	
	// TODO: this should be easier to do if parameters are normalized
	/**
	 * Evaluates if this {@link AlgebraicLine} is equal to another {@link AlgebraicLine}.
	 * 
	 * 
	 * @param other another {@link AlgebraicLine}
	 * @param tolerance the maximum deviation
	 * @return true if both lines are equal
	 */
	public boolean equals(AlgebraicLine other, double tolerance) {
		AlgebraicLine L1 = this;
		AlgebraicLine L2 = (AlgebraicLine) other;
		// get two different points on L1:
		Pnt2d xA = L1.getClosestLinePoint(PntDouble.ZERO);
		Pnt2d xB = PntDouble.from(xA.getX() - L1.B, xA.getY() + L1.A);
		// check if both points are on L2 too:
		return (isZero(L2.getDistance(xA), tolerance) && isZero(L2.getDistance(xB), tolerance));
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <a=%.3f, b=%.3f, c=%.3f>",
				this.getClass().getSimpleName(), A, B, C);
	}
	
	// -------------------------------------------------------------------
	
	public static void main (String[] args) {
		Pnt2d p1 = Pnt2d.from(1, 2);
		Pnt2d p2 = Pnt2d.from(4, 3);
		Pnt2d p3 = Pnt2d.from(9, -7);
		AlgebraicLine L1 = AlgebraicLine.from(p1, p2);
		AlgebraicLine L2 = AlgebraicLine.from(p3, p2);
		{
			Pnt2d x = L1.intersect(L2);
			System.out.println("x = " + x);
			System.out.println("x = p2 ? " + x.equals(p2));
		}
		{
			Pnt2d x = L2.intersect(L1);
			System.out.println("x = " + x);
			System.out.println("x = p2 ? " + x.equals(p2));
		}
		
		
		Pnt2d y = L1.intersect(L1);	// --> null
		System.out.println("y = " + y);
	}

}

//	x = PntDouble[4.000, 3.000]
//	x = p2 ? true
//	x = PntDouble[4.000, 3.000]
//	x = p2 ? true
//	y = null

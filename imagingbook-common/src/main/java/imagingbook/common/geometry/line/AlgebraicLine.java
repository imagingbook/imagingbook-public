/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

import java.awt.Shape;
import java.util.Locale;

import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.hough.lines.HoughLine;

/**
 * This class represents an algebraic line of the form A x + B  y + C = 0.
 * Instances are immutable and normalized such that ||(A,B)|| = 1.
 * @author WB
 *
 */
public class AlgebraicLine implements Curve2d {
	
	private final double A, B, C;
	
	// constructors --------------------------------------------------

	public AlgebraicLine(double a, double b, double c) {
		double norm = Math.sqrt(sqr(a) + sqr(b));
		if (isZero(norm)) {
			throw new IllegalArgumentException("a and b may not both be zero");
		}
		this.A = a / norm;
		this.B = b / norm;
		this.C = c / norm;
	}
	
	public AlgebraicLine(double[] p) {
		this(p[0], p[1], p[2]);
	}
	
	public AlgebraicLine(AlgebraicLine L) {
		this(L.A, L.B, L.C);
	}
	
	// static factory methods ----------------------------------------
	
	// Line from start point s and direction vector v
	public static AlgebraicLine from(double[] s, double[] v) {
		double A = -v[1];
		double B = v[0];
		double C = v[1] * s[0] - v[0] * s[1];
		return new AlgebraicLine(A, B, C);
	}
	
	public static AlgebraicLine from(ParametricLine pl) {
		return AlgebraicLine.from(pl.getS(), pl.getV());
	}
	
	public static AlgebraicLine from(Pnt2d p0, Pnt2d p1) {
//		double A = p0.getY() - p1.getY();
//		double B = p1.getX() - p0.getX();
//		double C = -A * p0.getX() - B * p0.getY();
//		return new AlgebraicLine(A, B, C);
		double[] s = p0.toDoubleArray();
		double[] v = p1.minus(p0).toDoubleArray();
		return AlgebraicLine.from(s, v);
	}
	
	// TODO: replace by direct calculation
	public static AlgebraicLine from(SlopeInterceptLine sil) {
		double A = sil.getK();
		double B = sil.getD();
//		Pnt2d p0 = Pnt2d.from(0, B);
//		Pnt2d p1 = Pnt2d.from(1, A + B);
//		return AlgebraicLine.from(p0, p1);
		return new AlgebraicLine(A, -1, B);
	}
	
	// getter/setter methods ------------------------------------------
	
	public double[] getParameters() {
		return new double[] {A, B, C};
	}
	
	public final double getA() {
		return A;
	}

	public final double getB() {
		return B;
	}

	public final double getC() {
		return C;
	}
	
	public double getXref() {
		return 0.0;
	}
	
	public double getYref() {
		return 0.0;
	}
	
	// other methods ------------------------------------------
	
	/**
	 * Returns the perpendicular distance between this line and the point (x, y).
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
//		return (A * (x - this.getXref()) + B * (y - this.getYref()) + C);
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
	 * Returns the perpendicular (signed) distance between this line and the point (x, y).
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
	
	// TODO: this is temporary, write a general method (without HoughLine)!
	public Shape getShape(int width, int height) {
		HoughLine hl = new HoughLine(this, 0.5 * width, 0.5 * height, 0);
		return hl.getShape(width, height);
	}
	
	// -------------------------------------------------------------------
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof AlgebraicLine) {
			AlgebraicLine L1 = this;
			AlgebraicLine L2 = (AlgebraicLine) other;
			double delta = 1E-6;
			// get two different points on L1:
			Pnt2d xA = L1.getClosestLinePoint(PntDouble.ZERO);
			Pnt2d xB = PntDouble.from(xA.getX() - L1.B, xA.getY() + L1.A);
			// check if both points are on L2 too:
			return (isZero(L2.getDistance(xA), delta) && isZero(L2.getDistance(xB), delta));
		}
		else {
			return false;
		}
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

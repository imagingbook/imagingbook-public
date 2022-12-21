/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.ellipse;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Primitive2d;
import imagingbook.common.geometry.ellipse.project.OrthogonalEllipseProjector;
import imagingbook.common.geometry.shape.ShapeProducer;
import imagingbook.common.math.Arithmetic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Locale;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * <p>
 * Represents an ellipse with major axis length ra, minor axis length rb, center point (xc, yc), and orientation theta.
 * Instances are immutable. See Secs. 11.2.2 and F.3.1 for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/17
 */
public class GeometricEllipse implements ShapeProducer, Primitive2d {
	
	/**
	 * Ellipse parameters.
	 */
	public final double ra, rb, xc, yc, theta;
	private final OrthogonalEllipseProjector projector;

	/**
	 * Constructor. Axis lengths may be exchanged to ensure ra &ge; rb.
	 *
	 * @param ra major axis length
	 * @param rb minor axis length
	 * @param xc center point (x)
	 * @param yc center point (y)
	 * @param theta orientation
	 */
	public GeometricEllipse(double ra, double rb, double xc, double yc, double theta) {
		this.xc = xc;
		this.yc = yc;
		if (ra >= rb) {	// make sure ra is always the longer axis!
			this.ra = ra;
			this.rb = rb;
			this.theta = Arithmetic.mod(theta, PI);	// theta = 0,...,pi always
		}
		else {
			this.ra = rb; // swap ra/rb
			this.rb = ra;
			this.theta = Arithmetic.mod(theta + PI/2, PI);	// theta = 0,...,pi always
		}
		this.projector = new OrthogonalEllipseProjector(this);
		
		if (this.ra < this.rb) {
			throw new RuntimeException(this.toString());
		}
	}

	/**
	 * Constructor, short version for axis-aligned ellipses. Axis lengths may be exchanged to ensure ra &ge; rb.
	 *
	 * @param ra major axis length
	 * @param rb minor axis length
	 * @param xc center point (x)
	 * @param yc center point (y)
	 */
	public GeometricEllipse(double ra, double rb, double xc, double yc) {
		this(ra, rb, xc, yc, 0.0);
	}

	/**
	 * Constructor.
	 *
	 * @param p parameter vector [ra, rb, xc, yc, theta].
	 * @see #getParameters()
	 */
	public GeometricEllipse(double[] p) {
		this(p[0], p[1], p[2], p[3], p[4]);
	}

	/**
	 * Constructor. Creates a new {@link GeometricEllipse} from a {@link AlgebraicEllipse} instance.
	 *
	 * @param ae a {@link AlgebraicEllipse} instance
	 */
	public GeometricEllipse(AlgebraicEllipse ae) {
		this(getGeometricEllipseParameters(ae));
	}

	/**
	 * Calculates and returns the geometric ellipse parameters from a given algebraic ellipse (see Eqns. 19-23 at
	 * <a href="http://mathworld.wolfram.com/Ellipse.html">http://mathworld.wolfram.com/Ellipse.html</a>).
	 *
	 * @param ae a {@linkplain AlgebraicEllipse} instance with parameters (A,...,F)
	 * @return the geometric ellipse parameters (ra, rb, xc, yc, theta)
	 * @see AlgebraicEllipse
	 */
	public static double[] getGeometricEllipseParameters(AlgebraicEllipse ae) {
		// see Eq. 19-23 at http://mathworld.wolfram.com/Ellipse.html
		double[] params = ae.getParameters(); 
		final double A = params[0]; // ae.A;
		final double B = params[1]; // ae.B;
		final double C = params[2]; // ae.C;
		final double D = params[3]; // ae.D;
		final double E = params[4]; // ae.E;
		final double F = params[5]; // ae.F;		
		final double d = sqr(B) - 4*A*C;
		
		if (d >= 0) {
			throw new IllegalArgumentException("B^2 - 4AC must be negative for an ellipse");
		}

		final double q = sqrt(sqr(A-C) + sqr(B));
		final double s = 2*(A*sqr(E) + C*sqr(D) + F*sqr(B) - B*D*E - 4*A*C*F);
		
		double xc = (2 * C * D - B * E) / d;
		double yc = (2 * A * E - B * D) / d;
		double ra = sqrt(s / (d * (-A - C + q)));
		double rb = sqrt(s / (d * (-A - C - q)));
		double theta = 0.5 * Math.atan2(-B, C - A); // theta = -pi/2,...,+pi/2
		
		return (ra >= rb) ? // make sure ra >= rb (ra is the major axis)
			new double[] {ra, rb, xc, yc, theta} :
			new double[] {rb, ra, xc, yc, theta + PI/2} ;
	}
	
	// ---------------------------------------

	/**
	 * Returns a vector of parameters for this ellipse. The length of the vector and the meaning of the parameters
	 * depends on the concrete ellipse type.
	 *
	 * @return a vector of parameters [ra, rb, xc, yc, theta]
	 */
	public double[] getParameters() {
		return new double[] {ra, rb, xc, yc, theta};
	}
	
	public Pnt2d getCenter() {
		return Pnt2d.from(xc, yc);
	}
	
	public double getArea() {
		return this.ra * this.rb * Math.PI;
	}
	
	public double getAlgebraicDistance(Pnt2d p) {
		return new AlgebraicEllipse(this).getAlgebraicDistance(p);
	}

	/**
	 * Returns the ellipse point closest to the specified point. To perform this calculation for multiple points on the
	 * same ellipse use {@link OrthogonalEllipseProjector}.
	 *
	 * @param pnt some 2D point
	 * @return the closest ("contact") point on the ellipse
	 * @see OrthogonalEllipseProjector
	 */
	public Pnt2d getClosestPoint(Pnt2d pnt) {
		return projector.project(pnt);
	}
	
	public double[] getClosestPoint(double[] pnt) {
		return projector.project(pnt);
	}
	
	// ---------------------------------------------------------------------------------

	/**
	 * Returns the mean squared error between this ellipse and a set of 2D points.
	 *
	 * @param points a set of sample points (usually the points used for fitting)
	 * @return the mean squared error
	 */
	public double getMeanSquareError(Pnt2d[] points) {
		double sum2 = 0;
		for (Pnt2d p : points) {
			sum2 = sum2 + p.distanceSq(getClosestPoint(p));
		}
		return sum2 / points.length;
	}

	/**
	 * Returns the closest (geometric) distance of the specified point to this ellipse.
	 *
	 * @param p some 2D point
	 * @return the distance to the closest point on the ellipse
	 */
	@Override
	public double getDistance(Pnt2d p) {
		return p.distance(getClosestPoint(p));
	}
	
	// ------------------------------------------------------------------------------------------
	
	@Override
	public Shape getShape(double scale) {
		return this.getOuterShape();
	}
	
	@Override
	public Shape[] getShapes(double scale) {
		return new Shape[] {
				getOuterShape(),
				getCenterShape(scale),
				getAxisShape()
		};
	}
	
	private Shape getOuterShape() {
		Ellipse2D oval = new Ellipse2D.Double(-ra, -rb, 2 * ra, 2 * rb);
		AffineTransform trans = new AffineTransform();
		trans.translate(xc, yc);
		trans.rotate(theta);
		return trans.createTransformedShape(oval);
	}
	
	private Shape getCenterShape(double radius) {
		double dxa = 2 * radius * cos(theta);	// major axis is drawn longer
		double dya = 2 * radius * sin(theta);
		double dxb = 1 * radius * cos(theta + PI/2);
		double dyb = 1 * radius * sin(theta + PI/2);
		Path2D path = new Path2D.Double();
		path.moveTo(xc - dxa, yc - dya);
		path.lineTo(xc + dxa, yc + dya);
		path.moveTo(xc - dxb, yc - dyb);
		path.lineTo(xc + dxb, yc + dyb);
		return path;
	}
	
	private Shape getAxisShape() {
		double dxa = ra * cos(theta);
		double dya = ra * sin(theta);
		double dxb = rb * cos(theta + PI/2);
		double dyb = rb * sin(theta + PI/2);
		Path2D path = new Path2D.Double();
		path.moveTo(xc - dxa, yc - dya);
		path.lineTo(xc + dxa, yc + dya);
		path.moveTo(xc - dxb, yc - dyb);
		path.lineTo(xc + dxb, yc + dyb);
		return path;
	}
	
	@Deprecated		// used for book figures only
	public Pnt2d getLeftAxisPoint() {
		return Pnt2d.from(xc - ra * cos(theta), yc - ra * sin(theta));
	}
	
	// -------------------------------
	
	public GeometricEllipse duplicate() {
		return new GeometricEllipse(this.getParameters());
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GeometricEllipse)) {
            return false;
        }
		return this.equals((GeometricEllipse) other, Arithmetic.EPSILON_DOUBLE);
	}
	
	public boolean equals(GeometricEllipse other, double tolerance) {
		return 
				Arithmetic.equals(xc, other.xc, tolerance) &&
				Arithmetic.equals(yc, other.yc, tolerance) &&
				Arithmetic.equals(ra, other.ra, tolerance) &&
				Arithmetic.equals(rb, other.rb, tolerance) &&
				Arithmetic.equals(Math.cos(theta), Math.cos(other.theta), tolerance) &&
				Arithmetic.equals(Math.sin(theta), Math.sin(other.theta), tolerance) ;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s [ra=%.8f, rb=%.8f, xc=%.8f, yc=%.8f, theta=%.8f]", 
				this.getClass().getSimpleName(), ra, rb, xc, yc, theta);
	}

}

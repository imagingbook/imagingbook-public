/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.ellipse;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Matrix;

import java.util.Locale;

import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.common.math.Matrix.multiply;
import static imagingbook.common.math.Matrix.normL2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * <p>
 * Represents an algebraic ellipse with the implicit equation A x^2 + B x y + C y^2 + D x + E y + F = 0. Parameters A,
 * ..., F are normalized such that B^2 - 4 A C = -1. Instances are immutable. See Secs. 11.2.1 and F.3.1 for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/17
 */
public class AlgebraicEllipse {
	
	/** Ellipse parameters. */
	private final double A, B, C, D, E, F;

	/**
	 * Constructor. Creates a {@link AlgebraicEllipse} instance by normalizing the supplied parameters [A,...,F] such
	 * that d = B^2 - 4 A C = -1. Throws an exception if d is non-negative.
	 *
	 * @param A ellipse parameter A
	 * @param B ellipse parameter B
	 * @param C ellipse parameter C
	 * @param D ellipse parameter D
	 * @param E ellipse parameter E
	 * @param F ellipse parameter F
	 */
	public AlgebraicEllipse(double A, double B, double C, double D, double E, double F) {
		double d = sqr(B) - 4 * A * C;
		if (d > -Arithmetic.EPSILON_DOUBLE) {	// discriminant (d) must be negative!
			throw new IllegalArgumentException("illegal ellipse parameters, non-negative discriminant " + d);
		}
		// d < 0
		// normalize to B^2 - 4 A C = -1
		double s = 1 / sqrt(-d);
		if (A >= 0) {
			this.A = A * s;
			this.B = B * s;
			this.C = C * s;
			this.D = D * s;
			this.E = E * s;
			this.F = F * s;
		}
		else {
			this.A = -A * s;
			this.B = -B * s;
			this.C = -C * s;
			this.D = -D * s;
			this.E = -E * s;
			this.F = -F * s;
		}
	}

	/**
	 * Constructor. Creates a {@link AlgebraicEllipse} instance from the specified parameter vector [A,...,F].
	 *
	 * @param p algebraic ellipse parameters
	 */
	public AlgebraicEllipse(double[] p) {
		this(p[0], p[1], p[2], p[3], p[4], p[5]);
	}

	/**
	 * Constructor. Creates a {@link AlgebraicEllipse} instance from a {@link GeometricEllipse}.
	 *
	 * @param ge a {@link GeometricEllipse}
	 */
	public AlgebraicEllipse(GeometricEllipse ge) {
		this(getAlgebraicEllipseParameters(ge));
	}
	
	private static double[] getAlgebraicEllipseParameters(GeometricEllipse ge) {
		double ra = ge.ra;
		double rb = ge.rb;
		double xc = ge.xc;
		double yc = ge.yc;
		double theta = ge.theta;
		
		double cosT = cos(theta);
		double sinT = sin(theta);
		
		double A = sqr(ra * sinT) + sqr(rb * cosT);
		double B = 2 * (sqr(rb) - sqr(ra)) * sinT * cosT;
		double C = sqr(ra * cosT) + sqr(rb * sinT);
		double D = -2 * A * xc - B * yc;
		double E = -2 * C * yc - B * xc;
		double F = A * sqr(xc) + B * xc * yc + C * sqr(yc) - sqr(ra * rb);	
		return new double[] {A, B, C, D, E, F};
	}

	/**
	 * Return a vector of parameters for this ellipse. The length of the vector and the meaning of the parameters
	 * depends on the concrete ellipse type.
	 *
	 * @return a vector of parameters [A, B, C, D, E, F]
	 */
	public double[] getParameters() {
		return new double[] {A, B, C, D, E, F};
	}
	
	public double getAlgebraicDistance(Pnt2d p) {
		double x = p.getX();
		double y = p.getY();
		return A * sqr(x) + B * x * y + C * sqr(y) + D * x + E * y + F;
	}
	
	public double getSampsonDistance(Pnt2d p) {
		double x = p.getX();
		double y = p.getY();
		double ad = A * sqr(x) + B * x * y + C * sqr(y) + D * x + E * y + F;	// algebraic distance
		double nGrad = Math.hypot(2*A*x + B*y + D, 2*C*y + B*x + E);
		return ad / nGrad;
	}
	
	public double getGoncharovaDistance(Pnt2d p) {
		double x = p.getX();
		double y = p.getY();
		double G = A * sqr(x) + B * x * y + C * sqr(y) + D * x + E * y + F;	// algebraic distance
		
		double[] gV = {2*A*x + B*y + D, 2*C*y + B*x + E};
		double ngV = normL2(gV);
		double sd = G / ngV;
		
		double[][] H = {{ 2*A, B }, { B, 2*C }};
		double gd = sd * (1 + G * Matrix.dotProduct(multiply(gV, H), gV) / (2 * Math.pow(ngV, 4)));
		return gd;
	}
	
	// --------------------------------------------------------------------------
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AlgebraicEllipse)) {
            return false;
        }
		return this.equals((AlgebraicEllipse) other, Arithmetic.EPSILON_DOUBLE);
	}
	
	public boolean equals(AlgebraicEllipse other, double tolerance) {
		return 
				Arithmetic.equals(A, other.A, tolerance) &&
				Arithmetic.equals(B, other.B, tolerance) &&
				Arithmetic.equals(C, other.C, tolerance) &&
				Arithmetic.equals(D, other.D, tolerance) &&
				Arithmetic.equals(E, other.E, tolerance) &&
				Arithmetic.equals(F, other.F, tolerance) ;
	}
	
	// --------------------------------------------------------------------------
	
	public AlgebraicEllipse duplicate() {
		return new AlgebraicEllipse(this.getParameters());
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s %s", 
				this.getClass().getSimpleName(), 
				Matrix.toString(getParameters()));
	}
	
	// --------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		PrintPrecision.set(9);
//		GeometricEllipse eg  = new GeometricEllipse(150, 50, 150, 150, 0.5);
//		System.out.println("eg = " + eg);
//		AlgebraicEllipse ea = new AlgebraicEllipse(eg);
//		System.out.println("ea = " + ea);
//		
//		Pnt2d p = Pnt2d.from(300, 300);
//		System.out.println("p  = " + p);
//		Pnt2d pc = eg.getClosestPoint(p);
//		System.out.println("pc = " + pc);
//		
//		System.out.println("Sampson dist(p) = " + ea.getSampsonDistance(p));
//		System.out.println("Gonch dist(p) = " + ea.getGoncharovaDistance(p));
//		
//	}

}

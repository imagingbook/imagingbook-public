/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.circle;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;

/**
 * Represents an algebraic circle with four parameters A, B, C, D in the form
 * A * (x^2 + y^2) + B * x + C * y + D = 0.
 * Instances are immutable.
 * 
 * TODO: add implementation of Curve2d
 * 
 * @author WB
 *
 */
public class AlgebraicCircle  implements Circle {

	private final double A, B, C, D;
	
	/**
	 * Constructor. Creates a {@link AlgebraicCircle} instance
	 * whose parameters A, B, C, D are normalized to B^2 + C^2 - 4 * A * D = 1.
	 * Throws an exception if d = B^2 + C^2 - 4 * A * D (the <em>discriminant</em>) is negative.
	 * 
	 * @param A circle parameter A
	 * @param B circle parameter B
	 * @param C circle parameter C
	 * @param D circle parameter D
	 */
	public AlgebraicCircle(double A, double B, double C, double D) {
		double d = sqr(B) + sqr(C) - 4 * A * D;
		if (isZero(A) || d < Arithmetic.EPSILON_DOUBLE) {
			throw new IllegalArgumentException("illegal circle parameters (zero A or non-positive discriminant)");
		}
		// normalize parameters to (B^2 + C^2 - 4 * A * D) = 1
		double s = 1 / sqrt(d);
		this.A = A * s;
		this.B = B * s;
		this.C = C * s;
		this.D = D * s;
	}
	
	/**
	 * Constructor. Creates a {@link AlgebraicCircle} instance from the
	 * specified parameter vector [A, B, C, D].
	 * 
	 * @param p algebraic circle parameters
	 */
	public AlgebraicCircle(double[] p) {
		this(p[0], p[1], p[2], p[3]);
	}
	
	/**
	 * Constructor. Creates a {@link AlgebraicCircle} instance from
	 * a {@link GeometricCircle}.
	 * 
	 * @param gc a {@link GeometricCircle}
	 */
	public AlgebraicCircle(GeometricCircle gc) {
		this(algebraicParameters(gc));
	}

//	/**
//	 * Creates a {@link AlgebraicCircle} instance from the specified
//	 * {@link GeometricCircle}.
//	 * 
//	 * @param gc a {@link GeometricCircle} instance
//	 * @return a new algebraic circle
//	 */
//	public static AlgebraicCircle from(GeometricCircle gc) {
////		double A = 1 / (2 * gc.getR());
////		double B = -2 * A * gc.getXc();
////		double C = -2 * A * gc.getYc();
////		double D = (sqr(B) + sqr(C) - 1) / (4 * A);
//		return new AlgebraicCircle(algebraicParameters(gc));
//	}
	
	private static double[] algebraicParameters(GeometricCircle gc) {
		double A = 1 / (2 * gc.getR());
		double B = -2 * A * gc.getXc();
		double C = -2 * A * gc.getYc();
		double D = (sqr(B) + sqr(C) - 1) / (4 * A);
		return new double[] {A, B, C, D};
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {A, B, C, D};
	}
	
//	/**
//	 * Creates and returns a new {@link AlgebraicCircle} whose parameters A, B, C, D
//	 * are normalized such that (B^2 + C^2 - 4 * A * D) = 1.
//	 * @return
//	 */
//	public AlgebraicCircle normalize() {
//		double s = 1 / sqrt(sqr(B) + sqr(C) - 4 * A * D);
//		System.out.println("s = " + s);
//		return new AlgebraicCircle(s*A, s*B, s*C, s*D);
//	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AlgebraicCircle)) {
            return false;
        }
		return this.equals((AlgebraicCircle) other, Arithmetic.EPSILON_DOUBLE);
	}
	
	public boolean equals(AlgebraicCircle other, double tolerance) {
		return 
				Arithmetic.equals(A, other.A, tolerance) &&
				Arithmetic.equals(B, other.B, tolerance) &&
				Arithmetic.equals(C, other.C, tolerance) &&
				Arithmetic.equals(D, other.D, tolerance) ;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s [A=%f, B=%f, C=%f, D=%f]", 
				AlgebraicCircle.class.getSimpleName(), A, B, C, D);
	}
	
	// ------------------------------------------------------------------
	
	public static void main(String[] args) {
		
		GeometricCircle gc1 = new GeometricCircle(200, -300, 777);
		System.out.println("gc1 = " + gc1.toString());
		
		AlgebraicCircle ac1 = new AlgebraicCircle(gc1);
		System.out.println("ac1 = " + ac1.toString());
		
		GeometricCircle gc2 = GeometricCircle.from(ac1);
		System.out.println("gc2 = " + gc2.toString());
		
		AlgebraicCircle ac2 = new AlgebraicCircle(gc2);
		System.out.println("ac2 = " + ac2.toString());
		
		System.out.println("ac1 == ac2 ? " + ac1.equals(ac2, 1e-9));
		System.out.println("ac2 == ac1 ? " + ac2.equals(ac1, 1e-9));
		
//		AlgebraicCircle ac2 = ac1.normalize();
//		System.out.println("ac2 = " + ac2.toString());
		
//		GeometricCircle gc3 = GeometricCircle.from(ac2);
//		System.out.println("gc3 = " + gc3.toString());
		
//		AlgebraicCircle ac4 = new AlgebraicCircle(3,5,2,1);
//		System.out.println("gc4 = " + ac4.toString());
//		AlgebraicCircle ac4N = ac4.normalize();
//		System.out.println("gc4N = " + ac4N.toString());
	}
	
	
}

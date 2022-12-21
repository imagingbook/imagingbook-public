/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.circle;

import imagingbook.common.math.Arithmetic;

import java.util.Locale;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

/**
 * <p>
 * Represents an algebraic circle with four parameters A, B, C, D in the form A * (x^2 + y^2) + B * x + C * y + D = 0.
 * Parameters are normalized such that B^2 + C^2 - 4 * A * D = 1 and A &ge; 0 Circle instances are immutable. See Sec.
 * 11.1.1 and Appendix F.2.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/17
 */
public class AlgebraicCircle {

	/**
	 * Circle parameter.
	 */
	public final double A, B, C, D;

	/**
	 * Constructor. Creates a {@link AlgebraicCircle} instance whose parameters A, B, C, D are normalized such that B^2
	 * + C^2 - 4 * A * D = 1 and A &ge; 0. Throws an exception if d = B^2 + C^2 - 4 * A * D (the <em>discriminant</em>)
	 * is negative.
	 *
	 * @param A circle parameter A
	 * @param B circle parameter B
	 * @param C circle parameter C
	 * @param D circle parameter D
	 */
	public AlgebraicCircle(double A, double B, double C, double D) {
		// discriminant (d) must be positive!
		double d = sqr(B) + sqr(C) - 4 * A * D;
		if (isZero(A) || d < Arithmetic.EPSILON_DOUBLE) {
			throw new IllegalArgumentException("illegal circle parameters (zero A or non-positive discriminant)");
		}
		// normalize parameters to (B^2 + C^2 - 4 * A * D) = 1 and A >= 0
		double s = 1 / sqrt(d);
		if (A >= 0) {
			this.A = A * s;
			this.B = B * s;
			this.C = C * s;
			this.D = D * s;
		}
		else {
			this.A = -A * s;
			this.B = -B * s;
			this.C = -C * s;
			this.D = -D * s;
		}
	}

	/**
	 * Constructor. Creates a {@link AlgebraicCircle} instance from the specified parameter vector [A, B, C, D].
	 *
	 * @param p algebraic circle parameters
	 */
	public AlgebraicCircle(double[] p) {
		this(p[0], p[1], p[2], p[3]);
	}

	/**
	 * Constructor. Creates a {@link AlgebraicCircle} instance from a {@link GeometricCircle}.
	 *
	 * @param gc a {@link GeometricCircle}
	 */
	public AlgebraicCircle(GeometricCircle gc) {
		this(getAlgebraicCircleParameters(gc));
	}

	private static double[] getAlgebraicCircleParameters(GeometricCircle gc) {
		double A = 1 / (2 * gc.r);
		double B = -2 * A * gc.xc;
		double C = -2 * A * gc.yc;
		double D = (sqr(B) + sqr(C) - 1) / (4 * A);
		return new double[] {A, B, C, D};
	}
	
	/**
	 * Returns a vector of algebraic circle parameters (A, B, C, D).
	 * 
	 * @return algebraic circle parameters (A, B, C, D)
	 */
	public double[] getParameters() {
		return new double[] {A, B, C, D};
	}
	
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

	/**
	 * Same as {@link #equals(Object)} but with a numeric tolerance on parameters.
	 * @param other some other circle
	 * @param tolerance numeric tolerance
	 * @return true is equal
	 */
	public boolean equals(AlgebraicCircle other, double tolerance) {
		return 
				Arithmetic.equals(A, other.A, tolerance) &&
				Arithmetic.equals(B, other.B, tolerance) &&
				Arithmetic.equals(C, other.C, tolerance) &&
				Arithmetic.equals(D, other.D, tolerance) ;
	}
	
	/**
	 * Returns a copy of this circle.
	 * @return a copy of this circle
	 */
	public AlgebraicCircle duplicate() {
		return new AlgebraicCircle(this.getParameters());
	}
	
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s [A=%f, B=%f, C=%f, D=%f]", 
				AlgebraicCircle.class.getSimpleName(), A, B, C, D);
	}
	
}

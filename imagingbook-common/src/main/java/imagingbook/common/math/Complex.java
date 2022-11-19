/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

import java.util.Locale;

/**
 * <p>
 * This class represents complex numbers. All instances are immutable. Methods
 * are mostly defined to be compatible with
 * org.apache.commons.math3.complex.Complex and (newer)
 * org.apache.commons.numbers.complex.Complex. Arithmetic operations are
 * generally more precise than with the Apache implementation. See also Appendix
 * Sec. A.5 of [1].
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/07/05
 */
public class Complex {
	
	/** Constant - real unit value (z = 1 + i 0). */
	public static final Complex ONE = new Complex(1, 0);
	/** Constant - complex zero value (z = 0 + i 0). */
	public static final Complex ZERO = new Complex(0, 0);
	/** Constant -  imaginary unit value (z = 0 + i 1). */
	public static final Complex I = new Complex(0, 1);

	/** The real part of this complex number (publicly accessible but read-only). */
	public final double re;
	/** The imaginary part of this complex number (publicly accessible but read-only). */
	public final double im;

	/**
	 * Constructor.
	 * @param re real part
	 * @param im imaginary part
	 */
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Constructor.
	 * @param z a two-element {@code double} array with real and imaginary part
	 */
	public Complex(double[] z) {
		this(z[0], z[1]);
	}

	/**
	 * Constructor.
	 * @param z complex quantity, which is duplicated
	 */
	public Complex(Complex z) {
		this.re = z.re;
		this.im = z.im;
	}

	/**
	 * Constructor, creates a complex quantity on the unit circle with angle {@code phi}:
	 * {@code e^(i * phi) = cos(phi) + i * sin(phi)}.
	 * @param phi the angle
	 * @see #arg()
	 */
	public Complex(double phi) {
		this.re = Math.cos(phi);
		this.im = Math.sin(phi);
	}
	
	// -------------------------------------------------------------------

	/**
	 * Returns the absolute value of this complex number, i.e.,
	 * its radius or distance from the origin.
	 * @return the absolute value
	 */
	public double abs() {
		return Math.hypot(re, im);
	}

	/**
	 * Returns the squared absolute value of this complex number, i.e.,
	 * its squared radius or distance from the origin.
	 * @return the squared absolute value
	 */
	public double abs2() {
		return sqr(re) + sqr(im);
	}

	/**
	 * Returns the 'argument' of this complex number, i.e.,
	 * its angle w.r.t. to the real axis. 
	 * @return the argument (in radians)
	 */
	public double arg() {
		return Math.atan2(im, re);
	}

	/**
     * Returns the conjugate {@code z*} of this complex number, i.e,
     * if {@code z = a + i b} then {@code z* = a - i b}.
     * @return the complex conjugate
     */
	public Complex conjugate() {
		return new Complex(this.re, -this.im);
	}

	/**
	 * Adds a complex quantity to this complex number and returns
	 * a new complex number.
	 * @param z complex value
	 * @return the sum of this complex number and {@code z}
	 */
	public Complex add(Complex z) {
		return new Complex(this.re + z.re,  this.im + z.im);
	}

	/**
	 * Rotates this complex number by angle {@code phi} and returns
	 * the resulting complex number.
	 * @param phi the angle (in radians)
	 * @return the rotated complex value
	 */
	public Complex rotate(double phi) {
		return this.multiply(new Complex(phi));
	}

	@Override
    public String toString() {
        return String.format(Locale.US, "(%.9f, %.9f)", re, im);
    }
	
    /**
     * Returns true if the real or imaginary component of this complex number
     * is {@code NaN}.
     * @return true if {@code NaN}, otherwise false
     */
	public boolean isNaN() {
		return Double.isNaN(this.getRe()) || Double.isNaN(this.getIm());
	}
	
	/**
	 * Returns the real part of this complex number.
	 * @return the real part
	 */
	public double getRe() {
		return this.re;
	}
	
	/**
	 * Returns the imaginary part of this complex number.
	 * @return the imaginary part
	 */
	public double getIm() {
		return this.im;
	}

	// -------------------------------------------------------
	
	/**
	 * Multiplies this complex number with another complex quantity and returns
	 * a new complex number.
	 * @param z a complex quantity
	 * @return this complex number multiplied by {@code z}
	 */
	public Complex multiply(Complex z) {
		// (x1 + i y1)(x2 + i y2) = (x1 x2 + y1 y2) + i (x1 y2 + y1 x2)
		final double x = this.re * z.re - this.im * z.im;
		final double y = this.re * z.im + this.im * z.re;
		return new Complex(x, y);
	}
	
	/**
	 * Multiplies this complex number with the scalar factor {@code s} and returns
	 * a new complex number.
	 * @param s a scalar factor
	 * @return this complex number multiplied by {@code s}
	 */
	public Complex multiply(double s) {
		return new Complex(this.re * s, this.im * s);
	}
	
	 /**
     * Returns of value of this complex number ({@code z}) raised to the power {@code k}
     * (integer).
     * @param k the integer exponent (&ge; 0)
     * @return {@code z^k}
     */
	public Complex pow(int k) {
		if (k < 0) throw new IllegalArgumentException("exponent k >= 0 expected");
		Complex prod = new Complex(1, 0);
		for (int i = 0; i < k; i++) {
			prod = prod.multiply(this);
		}
		return prod;
	}
	
	/**
	 * Returns a 2-element array with the real and imaginary part of this
	 * complex number.
	 * @return (re, im)
	 */
	public double[] toArray() {
		return new double[] {this.re, this.im};
	}

	/**
	 * Checks if the given {@link Object} is equal to
	 * this {@link Complex} quantity.
	 * Calls {@link #equals(Complex, double)} if the argument is
	 * of type {@link Complex}, otherwise {@code null} is returned.
	 */
	@Override
	public boolean equals(Object other) {
        if (other == this) {
            return true;
        }       
        if (other instanceof Complex) {
//	        final Complex z = (Complex) other;
//	        return Double.compare(this.re, z.re) == 0 && Double.compare(this.im, z.im) == 0;
	        return this.equals((Complex) other, Arithmetic.EPSILON_DOUBLE);
        }
        return false;
	}
	
	/**
	 * Checks if the given {@link Complex} quantity is equal to
	 * this {@link Complex} quantity.
	 * 
	 * @param z another {@link Complex} quantity
	 * @param tolerance the maximum difference of real and imaginary parts
	 * @return true if the two complex quantities are sufficiently close, false otherwise
	 */
	public boolean equals(Complex z, double tolerance) {
		return isZero(this.re - z.re, tolerance) 
				&& isZero(this.im - z.im, tolerance);
	}
	
	/**
	 * Checks if the given complex quantity is equal to
	 * this {@link Complex} quantity, using the default tolerance
	 * ({@link Arithmetic#EPSILON_DOUBLE}).
	 * 
	 * @param re real part of other complex quantity
	 * @param im imaginary part of other complex quantity
	 * @return true if the two complex quantities are sufficiently close, false otherwise
	 */
	public boolean equals(double re, double im) {
		//return Double.compare(this.re, re) == 0 && Double.compare(this.im, im) == 0;
		return this.equals(re, im, Arithmetic.EPSILON_DOUBLE);
	}
	
	/**
	 * Checks if the given complex quantity is equal to
	 * this {@link Complex} quantity, using the specified tolerance.
	 * 
	 * @param re real part of other complex quantity
	 * @param im imaginary part of other complex quantity
	 * @param tolerance the maximum difference of real and imaginary parts
	 * @return true if the two complex quantities are sufficiently close, false otherwise
	 */
	public boolean equals(double re, double im, double tolerance) {
		return isZero(this.re - re, tolerance) 
				&& isZero(this.im - im, tolerance);
	}
	
	// -------------------------------------------------------------------
	
}

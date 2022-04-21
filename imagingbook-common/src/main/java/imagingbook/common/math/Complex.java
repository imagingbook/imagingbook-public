/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * This class represents complex numbers. Instances are immutable.
 * Methods are mostly defined to be compatible with 
 * org.apache.commons.math3.complex.Complex and (newer)
 * org.apache.commons.numbers.complex.Complex.
 * Arithmetic operations are generally more precise than with the
 * Apache implementation.
 * 
 * @author W. Burger
 * @version 2020/11/20
 */
public class Complex {
	
	/** Real unit value (z = 1 + i 0). */
	public static final Complex ONE = new Complex(1, 0);
	/** Complex zero value (z = 0 + i 0). */
	public static final Complex ZERO = new Complex(0, 0);
	/** Imaginary unit value (z = 0 + i 1). */
	public static final Complex I = new Complex(0, 1);

	/** The real part of this complex number */
	public final double re;
	/** The imaginary part of this complex number */
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
	 * Constructor
	 * @param z complex quantity, which is duplicated
	 */
	public Complex(Complex z) {
		this.re = z.re;
		this.im = z.im;
	}

	/**
	 * Create a complex quantity on the unit circle with angle 'phi':
	 * e^{\i \phi} = \cos(\phi) + \i \cdot \sin(\phi)
	 * @param phi angle
	 */
	public Complex(double phi) {
		this.re = Math.cos(phi);
		this.im = Math.sin(phi);
	}
	
	
	public Complex(Pnt2d pnt) {
		this.re = pnt.getX();
		this.im = pnt.getY();
	}
	
	// -------------------------------------------------------------------

	/**
	 * Returns the absolute value of this complex number, i.e.,
	 * its radius or distance from the origin.
	 * @return the absolute value
	 */
	public double abs() {
		return Math.sqrt(this.abs2());
	}

	/**
	 * Returns the squared absolute value of this complex number, i.e.,
	 * its squared radius or distance from the origin.
	 * @return the squared absolute value
	 */
	public double abs2() {
		return re * re + im * im;
	}

	/**
	 * Returns the 'argument' of this complex number, i.e.,
	 * its angle relative to the real axis. 
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
	 * Rotates this complex number by the angle {@code phi} and returns
	 * a new complex number.
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

	/**
	 * Multiplies this complex number with another complex quantity {@code z2} and returns
	 * a new complex number.
	 * @param z2 a complex quantity
	 * @return this complex number multiplied by {@code z2}
	 */
	public Complex multiply(Complex z2) {
		// (x1 + i y1)(x2 + i y2) = (x1 x2 + y1 y2) + i (x1 y2 + y1 x2)
		Complex z1 = this;
		double x = z1.re * z2.re - z1.im * z2.im;
		double y = z1.re * z2.im + z1.im * z2.re;
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
     * Returns of value of this complex number raised to the power of {@code k}.
     * @param k the exponent
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
	
	@Override
	public boolean equals(Object o) {
        if (o == this) {
            return true;
        }       
        if (o instanceof Complex) {
	        final Complex c = (Complex) o;
	        return Double.compare(this.re, c.re) == 0 && Double.compare(this.im, c.im) == 0;
        }
        return false;
	}
	
	public boolean equals(double a, double b) {
		return Double.compare(this.re, a) == 0 && Double.compare(this.im, b) == 0;
	}

	//------------ TESTING only ------------------------------

//	public static void main(String[] args) {
//		Complex z1 = new Complex(0.3, 0.6);
//		Complex z2 = new Complex(-1, 0.2);
//		
//		System.out.println("z1 = " + z1);
//		System.out.println("z2 = " + z2);
//		
//		System.out.println("z1 + z2 = " + z1.add(z2));
//		System.out.println("z2 + z1 = " + z2.add(z1));
//		
//		
//		System.out.println("z1 = " + z1);
//		System.out.println("z2 = " + z2);
//		Complex z3 = z1.multiply(z2);
//		System.out.println("z1 * z2 = " + z3);
//		Complex z4 = z2.multiply(z1);
//		System.out.println("z2 * z1 = " + z4);
//		
//		System.out.println("z1.pow(5) = " + z1.pow(5));
//		
//		System.out.println("z1.rotate(0.1) = " + z1.rotate(0.1));
//	}

}
/*
z1 = (0.300000000, 0.600000000)
z2 = (-1.000000000, 0.200000000)
z1 + z2 = (-0.700000000, 0.800000000)
z2 + z1 = (-0.700000000, 0.800000000)
z1 = (0.300000000, 0.600000000)
z2 = (-1.000000000, 0.200000000)
z1 * z2 = (-0.420000000, -0.540000000)
z2 * z1 = (-0.420000000, -0.540000000)
z1.pow(5) = (0.099630000, -0.092340000)
z1.rotate(0.1) = (0.238601200, 0.626952524)
*/
/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math;

import java.util.Arrays;

import org.apache.commons.math3.util.FastMath;

public abstract class Arithmetic {
	
	// machine accuracy for IEEE 754 float/double;
	/** Tolerance used for comparing {@code float} quantities. */
	public static final float EPSILON_FLOAT 	= 1e-7f;	// 1.19 x 10^-7
	/** Tolerance used for comparing {@code double} quantities. */
	public static final double EPSILON_DOUBLE 	= 2e-16;	// 2.22 x 10^-16

	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static int sqr(int x) {
		return x * x;
	}
	
	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static long sqr(long x) {
		return x * x;
	}
	
	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static float sqr(float x) {
		return x * x;
	}
	
	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static double sqr(double x) {
		return x * x;
	}
	
	/**
	 * Returns the radius of the Cartesian point 
	 * @param x x-component
	 * @param y y-component
	 * @return the radius
	 */
	public static double radius(double x, double y) {
		return FastMath.hypot(x, y);
	}
	
	/**
	 * Returns the angle of the Cartesian point (x,y)
	 * @param x x-component
	 * @param y y-component
	 * @return the angle
	 */
	public static double angle(double x, double y) {
		return FastMath.atan2(y, x);
	}
	
	public static double[] toPolar(double x, double y) {
		return new double[] {FastMath.hypot(x, y), FastMath.atan2(y, x)};
	}
	
	public static double[] toPolar(double[] cart) {
		return toPolar(cart[0], cart[1]);
	}
	
	public static double[] toCartesian(double radius, double angle) {
		return new double[] {radius * FastMath.cos(angle), radius * FastMath.sin(angle)};
	}
	
	public static double[] toCartesian(double[] polar) {
		return toCartesian(polar[0], polar[1]);
	}
	
	/**
	 * Integer version of the modulus operator ({@code a mod b}).
	 * Also see <a href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>.
	 * Calls {@code Math.floorMod(a,b)} (available in Java 8 and higher).
	 * @param a dividend
	 * @param b divisor (modulus), must be nonzero
	 * @return {@code a mod b}
	 */
	public static int mod(int a, int b) {
		return FastMath.floorMod(a, b);
	}
	
	// original implementation (obsolete)
//	public static int mod(int a, int b) {
//		if (b == 0)
//			return a;
//		// a, b have the same sign OR the remainder is zero
//        if ((long)a * b >= 0 || a % b == 0)	// or (a / b) * b == a
//        	return a - b * (a / b);
//        else
//        	return a - b * (a / b - 1);
//	}

	
	/**
	 * Non-integer version of modulus operator using floored division
	 * (see <a href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>),
	 * with results identical to Mathematica. 
	 * Calculates {@code a mod b} for floating-point arguments.
	 * An exception is thrown if  {@code b} is zero.
	 * Examples:
	 * <pre>
	 * mod( 3.5, 2.1) =  1.4
	 * mod(-3.5, 2.1) =  0.7
	 * mod( 3.5,-2.1) = -0.7
	 * mod(-3.5,-2.1) = -1.4</pre>
	 * @param a dividend
	 * @param b divisor (modulus), must be nonzero
	 * @return {@code a mod b}
	 */
	public static double mod(double a, double b) {
		if (isZero(b))
				throw new IllegalArgumentException("zero modulus in mod");
		return a - b * FastMath.floor(a / b);
	}
	
	/**
	 * Test for zero (float version) using a predefined tolerance.
	 * Returns true if the argument's absolute value
	 * is less than {@link EPSILON_FLOAT}.
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(float x) {
		return FastMath.abs(x) < EPSILON_FLOAT;
	}
	
	/**
	 * Test for zero (float version) using a specified tolerance.
	 * Returns true if the argument's absolute value
	 * is less than the specified tolerance.
	 * @param x quantity to be tested
	 * @param tolerance the tolerance to be used
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(float x, float tolerance) {
		return FastMath.abs(x) < tolerance;
	}
	
	/**
	 * Test for zero (double version) using a predefined tolerance.
	 * Returns true if the argument's absolute value
	 * is less than {@link EPSILON_DOUBLE}.
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(double x) {
		return FastMath.abs(x) < EPSILON_DOUBLE;
	}
	
	/**
	 * Test for zero (double version) using a specified tolerance.
	 * Returns true if the argument's absolute value
	 * is less than the specified tolerance.
	 * @param x quantity to be tested
	 * @param tolerance the tolerance to be used
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(double x, double tolerance) {
		return FastMath.abs(x) < tolerance;
	}
	
	/**
	 * Test for numerical equality (double version) using a predefined tolerance.
	 * Returns true if the absolute difference of the arguments
	 * is less than {@link EPSILON_DOUBLE}.
	 * @param x first argument
	 * @param y second argument
	 * @return true if both arguments are numerically the same.
	 */
	public static boolean equals(double x, double y) {
		return Arithmetic.isZero(x - y);
	}
	
	public static boolean equals(double x, double y, double tolerance) {
		return Arithmetic.isZero(x - y, tolerance);
	}
	
	/**
	 * Test for numerical equality (float version) using a predefined tolerance.
	 * Returns true if the absolute difference of the arguments
	 * is less than {@link EPSILON_FLOAT}.
	 * @param x first argument
	 * @param y second argument
	 * @return true if both arguments are numerically the same.
	 */
	public static boolean equals(float x, float y) {
		return Arithmetic.isZero(x - y);
	}
	
	public static boolean equals(float x, float y, float tolerance) {
		return Arithmetic.isZero(x - y, tolerance);
	}
	
	// ---------------------------------------------------------------------------
	
	/**
	 * Returns the maximum of one or more integer values.
	 * @param a the first value
	 * @param vals more values
	 * @return the maximum value
	 */
	public static int max(int a, int... vals) {
		int maxVal = a;
		for (int v : vals) {
			if (v > maxVal) {
				maxVal = v;
			}
		}
		return maxVal;
	}
	
	/**
	 * Returns the minimum of one or more integer values.
	 * @param a the first value
	 * @param vals more values
	 * @return the minimum value
	 */
	public static int min(int a, int... vals) {
		int minVal = a;
		for (int v : vals) {
			if (v < minVal) {
				minVal = v;
			}
		}
		return minVal;
	}
	
	/**
	 * Returns the maximum of one or more float values.
	 * @param a the first value
	 * @param vals more values
	 * @return the maximum value
	 */
	public static float max(float a, float... vals) {
		float maxVal = a;
		for (float v : vals) {
			if (v > maxVal) {
				maxVal = v;
			}
		}
		return maxVal;
	}
	
	/**
	 * Returns the minimum of one or more float values.
	 * @param a the first value
	 * @param vals more values
	 * @return the minimum value
	 */
	public static float min(float a, float... vals) {
		float minVal = a;
		for (float v : vals) {
			if (v < minVal) {
				minVal = v;
			}
		}
		return minVal;
	}
	
	/**
	 * Returns the maximum of one or more double values.
	 * @param a the first value
	 * @param vals more values
	 * @return the maximum value
	 */
	public static double max(double a, double... vals) {
		double maxVal = a;
		for (double v : vals) {
			if (v > maxVal) {
				maxVal = v;
			}
		}
		return maxVal;
	}
	
	/**
	 * Returns the minimum of one or more double values.
	 * @param a the first value
	 * @param vals more values
	 * @return the minimum value
	 */
	public static double min(double a, double... vals) {
		double minVal = a;
		for (double v : vals) {
			if (v < minVal) {
				minVal = v;
			}
		}
		return minVal;
	}
	
	/**
	 * Returns the two real roots of the quadratic function
	 * f(x) = ax^2 + bx + c.
	 * @param a coefficient
	 * @param b coefficient
	 * @param c coefficient
	 * @return an array [x1, x2] with the two roots
	 */
	public static double[] getRoots(double a, double b, double c) {
		double d = Math.sqrt(sqr(b) - 4 * a * c);
		double x1 = (-b - d) / (2 * a);
		double x2 = (-b + d) / (2 * a);
		return new double[] {x1, x2};
	}
	
	//--------------------------------------------------------------------------
	
	public static class DivideByZeroException extends ArithmeticException {
		private static final long serialVersionUID = 1L;
		private static String DefaultMessage = "zero denominator in division";
		
		public DivideByZeroException() {
			super(DefaultMessage);
		}
	}
	
	// -------------------------------------
	
//	public static void main(String[] args) {
//		System.out.println(Arithmetic.mod(13, 4));
//		System.out.println(Arithmetic.mod(13, -4));
//		System.out.println(Arithmetic.mod(-13, 4));
//		System.out.println(Arithmetic.mod(-13, -4));
//		
//		int b = 7;
//		System.out.format("   i  -> floor |  mod\n");
//		for (int i = -25; i < 25; i++) {
//			System.out.format("%4d  -> %4d  | %4d \n", i, Math.floorMod(i, b), Arithmetic.mod(i, b));
//		}
//	}
	
	public static void main(String[] args) {	// TODO: add to tests
//		System.out.println(Arithmetic.max(13));
//		System.out.println(Arithmetic.max(13, 7, 22));
//		System.out.println(Math.max(13,Math.max(7,22)));
//		
//		System.out.println(Arithmetic.min(13));
//		System.out.println(Arithmetic.min(13, 7, 22));
		double[] x12 = getRoots(1, -7, 10);
		System.out.println("x12 = " + Arrays.toString(x12));		// x12 = [2.0, 5.0]
		
		x12 = getRoots(-2, 2, 1);
		System.out.println("x12 = " + Arrays.toString(x12));		// x12 = [1.3660254037844386, -0.3660254037844386]
	}

}

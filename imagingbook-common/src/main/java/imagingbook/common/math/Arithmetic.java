/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.math;

import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.atan2;
import static org.apache.commons.math3.util.FastMath.cos;
import static org.apache.commons.math3.util.FastMath.floor;
import static org.apache.commons.math3.util.FastMath.floorMod;
import static org.apache.commons.math3.util.FastMath.hypot;
import static org.apache.commons.math3.util.FastMath.sin;

/**
 * This class defines static methods implementing arithmetic operations and predicates.
 * 
 * @author WB
 *
 */
public abstract class Arithmetic {
	
	private Arithmetic() {}
	
	// machine accuracy for IEEE 754 float/double;
	/** Default tolerance used for comparing {@code float} quantities. */
	public static final float EPSILON_FLOAT = 1e-7f;	// 1.19 x 10^-7
	/** Default tolerance used for comparing {@code double} quantities. */
	public static final double EPSILON_DOUBLE = 2e-16;	// 2.22 x 10^-16

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
		return hypot(x, y);
	}
	
	/**
	 * Returns the angle of the Cartesian point (x,y)
	 * @param x x-component
	 * @param y y-component
	 * @return the angle
	 */
	public static double angle(double x, double y) {
		return atan2(y, x);
	}
	
	/**
	 * Returns the polar coordinates of the Cartesian point (x,y).
	 * @param x x-component
	 * @param y y-component
	 * @return a 2-element array holding the polar coordinates (radius, angle)
	 */
	public static double[] toPolar(double x, double y) {
		return new double[] {hypot(x, y), atan2(y, x)};
	}
	
	/**
	 * Returns the polar coordinates of the Cartesian point xy.
	 * @param xy the Cartesian coordinates
	 * @return a 2-element array holding the polar coordinates (radius, angle)
	 */
	public static double[] toPolar(double[] xy) {
		return toPolar(xy[0], xy[1]);
	}
	
	/**
	 * Converts polar point coordinates to Cartesian coordinates.
	 * @param radius the radius 
	 * @param angle the angle
	 * @return a 2-element array holding the Cartesian coordinates (x,y)
	 */
	public static double[] toCartesian(double radius, double angle) {
		return new double[] {radius * cos(angle), radius * sin(angle)};
	}
	
	/**
	 * Converts polar point coordinates to Cartesian coordinates.
	 * @param polar the polar coordinates (radius, angle)
	 * @return a 2-element array holding the Cartesian coordinates (x,y)
	 */
	public static double[] toCartesian(double[] polar) {
		return toCartesian(polar[0], polar[1]);
	}

	/**
	 * Integer version of the modulus operator ({@code a mod b}). Also see <a
	 * href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>. Calls {@code Math.floorMod(a,b)} (available in
	 * Java 8 and higher).
	 *
	 * @param a dividend
	 * @param b divisor (modulus), must be nonzero
	 * @return {@code a mod b}
	 */
	public static int mod(int a, int b) {
		return floorMod(a, b);
	}

	/**
	 * Non-integer version of modulus operator using floored division (see <a
	 * href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>), with results identical to Mathematica. Calculates
	 * {@code a mod b} for floating-point arguments. An exception is thrown if  {@code b} is zero. Examples:
	 * <pre>
	 * mod( 3.5, 2.1) =  1.4
	 * mod(-3.5, 2.1) =  0.7
	 * mod( 3.5,-2.1) = -0.7
	 * mod(-3.5,-2.1) = -1.4</pre>
	 *
	 * @param a dividend
	 * @param b divisor (modulus), must be nonzero
	 * @return {@code a mod b}
	 */
	public static double mod(double a, double b) {
		if (isZero(b))
				throw new IllegalArgumentException("zero modulus in mod");
		return a - b * floor(a / b);
	}

	/**
	 * Test for zero (float version) using a predefined tolerance. Returns true if the argument's absolute value is less
	 * than {@link #EPSILON_FLOAT}.
	 *
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(float x) {
		return abs(x) < EPSILON_FLOAT;
	}

	/**
	 * Test for zero (float version) using a specified tolerance. Returns true if the argument's absolute value is less
	 * than the specified tolerance.
	 *
	 * @param x quantity to be tested
	 * @param tolerance the tolerance to be used
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(float x, float tolerance) {
		return abs(x) < tolerance;
	}

	/**
	 * Test for zero (double version) using a predefined tolerance. Returns true if the argument's absolute value is
	 * less than {@link #EPSILON_DOUBLE}.
	 *
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(double x) {
		return abs(x) < EPSILON_DOUBLE;
	}

	/**
	 * Test for zero (double version) using a specified tolerance. Returns true if the argument's absolute value is less
	 * than the specified tolerance.
	 *
	 * @param x quantity to be tested
	 * @param tolerance the tolerance to be used
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(double x, double tolerance) {
		return abs(x) < tolerance;
	}

	/**
	 * Test for numerical equality (double version) using the default tolerance. Returns true if the absolute difference
	 * of the arguments is less than {@link #EPSILON_DOUBLE}.
	 *
	 * @param x first argument
	 * @param y second argument
	 * @return true if the absolute difference of the arguments is less than the tolerance
	 */
	public static boolean equals(double x, double y) {
		return isZero(x - y);
	}

	/**
	 * Test for numerical equality (double version) using a specific tolerance.
	 *
	 * @param x first argument
	 * @param y second argument
	 * @param tolerance the maximum (absolute) deviation
	 * @return true if the absolute difference of the arguments is less than the tolerance
	 */
	public static boolean equals(double x, double y, double tolerance) {
		return isZero(x - y, tolerance);
	}

	/**
	 * Test for numerical equality (float version) using the default tolerance. Returns true if the absolute difference
	 * of the arguments is less than {@link #EPSILON_FLOAT}.
	 *
	 * @param x first argument
	 * @param y second argument
	 * @return true if the absolute difference of the arguments is less than the tolerance
	 */
	public static boolean equals(float x, float y) {
		return isZero(x - y);
	}

	/**
	 * Test for numerical equality (float version) using a specific tolerance.
	 *
	 * @param x first argument
	 * @param y second argument
	 * @param tolerance the maximum (absolute) deviation
	 * @return true if the absolute difference of the arguments is less than the tolerance
	 */
	public static boolean equals(float x, float y, float tolerance) {
		return isZero(x - y, tolerance);
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
	 * Limits the first argument to the clipping interval specified by the two other arguments ({@code double} version).
	 * The clipped value is returned. Throws an exception if the clipping interval is empty.
	 *
	 * @param x the value to be clipped
	 * @param low the lower boundary of the clipping interval
	 * @param high the upper boundary of the clipping interval
	 * @return the clipped value
	 */
	public static double clipTo(double x, double low, double high) {
		if (low > high) {
			throw new IllegalArgumentException("clip interval low > high");
		}
		if (x < low) return low;
		if (x > high) return high;
		return x;
	}

	/**
	 * Limits the first argument to the clipping interval specified by the two other arguments ({@code float} version).
	 * The clipped value is returned. Throws an exception if the clipping interval is empty.
	 *
	 * @param x the value to be clipped
	 * @param low the lower boundary of the clipping interval
	 * @param high the upper boundary of the clipping interval
	 * @return the clipped value
	 */
	public static float clipTo(float x, float low, float high) {
		if (low > high) {
			throw new IllegalArgumentException("clip interval low > high");
		}
		if (x < low) return low;
		if (x > high) return high;
		return x;
	}

	/**
	 * Limits the first argument to the clipping interval specified by the two other arguments ({@code float} version).
	 * The clipped value is returned. Throws an exception if the clipping interval is empty.
	 *
	 * @param x the value to be clipped
	 * @param low the lower boundary of the clipping interval
	 * @param high the upper boundary of the clipping interval
	 * @return the clipped value
	 */
	public static int clipTo(int x, int low, int high) {
		if (low > high) {
			throw new IllegalArgumentException("clip interval low > high");
		}
		if (x < low) return low;
		if (x > high) return high;
		return x;
	}

	/**
	 * Returns the two real roots of the quadratic function f(x) = ax^2 + bx + c. Null is returned if roots are
	 * non-real.
	 *
	 * @param a function coefficient
	 * @param b function coefficient
	 * @param c function coefficient
	 * @return an array with the two roots x1, x2 (in no particular order) or null if the function has complex roots
	 */
	public static double[] getRealRoots(double a, double b, double c) {
		double d = sqr(b) - 4 * a * c;
		if (d < 0) {
			return null;	// complex roots
		}
		double dr = Math.sqrt(d);	
		double x1 = (-b - dr) / (2 * a);
		double x2 = (-b + dr) / (2 * a);
		return new double[] {x1, x2};
	}

}

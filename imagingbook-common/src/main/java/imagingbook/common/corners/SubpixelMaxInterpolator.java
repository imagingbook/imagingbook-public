/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.corners;

import static imagingbook.common.math.Arithmetic.EPSILON_DOUBLE;
import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.common.math.Matrix.add;
import static imagingbook.common.math.Matrix.distL2;
import static imagingbook.common.math.Matrix.dotProduct;
import static imagingbook.common.math.Matrix.multiply;
import static imagingbook.common.math.Matrix.normL2;

/**
 * <p>
 * Common interface for all sub-pixel maximum locator implementations.
 * A  sub-pixel maximum locator tries to interpolate the continuous position
 * and value of a local maximum for a discrete 3x3 neighborhood.
 * The center value in the discrete neighborhood is assumed to be a local maximum.
 * See Sec. 6.5 and Appendix E of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @see QuadraticTaylor
 * @see QuadraticLeastSquares
 * @see Quartic
 * @author WB
 * @version 2021/01/19
 * @version 2022/09/11 revised instance creation
 */
public abstract class SubpixelMaxInterpolator {
	
	private SubpixelMaxInterpolator() {}
	
	/**
	 * Tries to locate the sub-pixel maximum from the 9 discrete sample values
	 * (s0,...,s8) taken from a 3x3 neighborhood, arranged in the following order:
	 * <pre>
	 * s4 s3 s2
	 * s5 s0 s1
	 * s6 s7 s8
	 * </pre>
	 * The center value (s0) is associated with position (0,0).
	 * 
	 * @param s a vector containing 9 sample values in the order described above
	 * @return a 3-element array [x,y,z], with the estimated maximum position (x,y) 
	 * and the associated max. value (z). 
	 * The position is relative to the center coordinate (0,0).
	 * {@code null} is returned if the maximum position could not be located.
	 */
	public abstract float[] getMax(float[] s);
	
	/**
	 * Enumeration of different {@link SubpixelMaxInterpolator} methods.
	 */
	public enum Method {
		/** Second-order (quadratic) Taylor interpolation */
		QuadraticTaylor(SubpixelMaxInterpolator.QuadraticTaylor.getInstance()),
		
		/** Second-order (quadratic) least-squares interpolation" */
		QuadraticLeastSquares(SubpixelMaxInterpolator.QuadraticLeastSquares.getInstance()),
		
		/** Quartic interpolation */ 
		Quartic(SubpixelMaxInterpolator.Quartic.getInstance()),
		
		/** No interpolation */
		None(null);
		
		private final SubpixelMaxInterpolator inst;
		
		private Method(SubpixelMaxInterpolator instance) {
			this.inst = instance;
		}
		
		public SubpixelMaxInterpolator getInstance() {
			return this.inst;
		}
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * <p>
	 * 2D interpolator using second-order Taylor expansion to find the coefficients
	 * of the quadratic interpolating polynomial 
	 * </p>
	 * <pre>f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2  + c_5 xy</pre>
	 * <p>
	 * The resulting function fits exactly the 5 on-axis samples 
	 * (s[0], s[1], s[3], s[5], s[7]) but in general does not pass
	 * through the outer diagonal samples (s[2], s[4], s[6], s[8]).
	 * See Appendix E.2.2 (Alg. E.1, 'FindMaxQuadraticTaylor') of [1] for 
	 * additional details.
	 * </p>
	 * <p>
	 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
	 * 3rd ed, Springer (2022).
	 * </p>
	 */
	public static class QuadraticTaylor extends SubpixelMaxInterpolator {
		
		private static final QuadraticTaylor instance = new QuadraticTaylor();
		private QuadraticTaylor() {};
		public static QuadraticTaylor getInstance() {
			return instance;
		}
		
		private final double[] c = new double[6];	// polynomial coefficients

		@Override
		public float[] getMax(float[] s) {
			c[0] = s[0];
			c[1] = (s[1] - s[5]) / 2;
			c[2] = (s[7] - s[3]) / 2;
			c[3] = (s[1] - 2*s[0] + s[5]) / 2;
			c[4] = (s[3] - 2*s[0] + s[7]) / 2;
			c[5] = (-s[2] + s[4] - s[6] + s[8]) / 4;
			
			double d = (4*c[3]*c[4] - sqr(c[5]));	// determinant of Hessian
			
			if (d < EPSILON_DOUBLE || c[3] >= 0) {	// undefined or not a maximum
				return null;
			}
			
			// max position:
			float x = (float) ((c[2]*c[5] - 2*c[1]*c[4]) / d);
			float y = (float) ((c[1]*c[5] - 2*c[2]*c[3]) / d);
			// max value:
			float z = (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y + c[5]*x*y);
			return new float[] {x, y, z};
		}
	}
	
	/**
	 * 2D interpolator using second-order Taylor expansion to find the coefficients
	 * of the quadratic interpolating polynomial 
	 * <pre>f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2  + c_5 xy</pre>
	 * The resulting function fits exactly the 5 on-axis samples 
	 * (s[0], s[1], s[3], s[5], s[7]) but in general does not pass
	 * through the outer diagonal samples (s[2], s[4], s[6], s[8]).
	 * Alternate version using explicit inverse Hessian.
	 */
	@SuppressWarnings("unused")
	private static class QuadraticTaylorAlt extends SubpixelMaxInterpolator {
		
		private static final QuadraticTaylorAlt instance = new QuadraticTaylorAlt();
		private QuadraticTaylorAlt() {};
		public static QuadraticTaylorAlt getInstance() {
			return instance;
		}

		@Override
		public float[] getMax(float[] s) {
			double[] g = { 							// the gradient
					(s[1] - s[5]) / 2, 
					(s[7] - s[3]) / 2};	
			
			double H00 = s[1] - 2*s[0] + s[5];		// elements of the Hessian matrix
			double H11 = s[3] - 2*s[0] + s[7];
			double H01 = (s[4] + s[8] - s[2] - s[6]) / 4;			
			double d = H00 * H11 - sqr(H01);		// = det(H)
			
			if (d < EPSILON_DOUBLE || H00 >= 0) {	// not a maximum (minimum or saddle point)
				return null;
			}
			
			double[][] Hi = {			// inverse Hessian
					{ H11 / d, -H01 / d}, 
					{-H01 / d,  H00 / d}};	
			
			double[] delta = multiply(Hi, g);
			double[] xy = multiply(-1, delta);				// maximum position
			double q = s[0] - 0.5 * dotProduct(g, delta);	// maximum value
			return new float[] {(float) xy[0], (float) xy[1], (float) q};
		}
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * <p>
	 * 2D interpolator based on least-squares fitting a quadratic polynomial 
	 * </p>
	 * <pre>f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2  + c_5 xy</pre>
	 * <p>to the supplied sample values.
	 * See Sec. E.2.3 (Alg. E2, 'FindMaxQuadraticLeastSquares') of [1] for
	 * additional details.
	 * </p>
	 * <p>
	 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
	 * 3rd ed, Springer (2022).
	 * </p>
	 */
	public static class QuadraticLeastSquares extends SubpixelMaxInterpolator {
		
		private static final QuadraticLeastSquares instance = new QuadraticLeastSquares();
		private QuadraticLeastSquares() {};
		public static QuadraticLeastSquares getInstance() {
			return instance;
		}
		
		private final double[] c = new double[6];	// polynomial coefficients

		@Override
		public float[] getMax(float[] s) {
			c[0] = (5 * s[0] + 2 * s[1] - s[2] + 2 * s[3] - s[4] + 2 * s[5] - s[6] + 2 * s[7] - s[8]) / 9;
			c[1] = (s[1] + s[2] - s[4] - s[5] - s[6] + s[8]) / 6;
			c[2] = (-s[2] - s[3] - s[4] + s[6] + s[7] + s[8]) / 6;
			c[3] = (-2 * s[0] + s[1] + s[2] - 2 * s[3] + s[4] + s[5] + s[6] - 2 * s[7] + s[8]) / 6;
			c[4] = (-2 * s[0] - 2 * s[1] + s[2] + s[3] + s[4] - 2 * s[5] + s[6] + s[7] + s[8]) / 6;
			c[5] = (-s[2] + s[4] - s[6] + s[8]) / 4;
			
			double d = (4*c[3]*c[4] - sqr(c[5]));
			
			if (d < EPSILON_DOUBLE || c[3] >= 0) {	// not a maximum (minimum or saddle point)
				return null;
			}
			
			// max position:
			float x = (float) ((c[2]*c[5] - 2*c[1]*c[4]) / d);
			float y = (float) ((c[1]*c[5] - 2*c[2]*c[3]) / d);
			// max value:
			float z = (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y + c[5]*x*y);
			return new float[] {x, y, z};
		}
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * <p>
	 * 2D interpolator based on fitting a 'quartic' (i.e., 4th-order) polynomial
	 * </p>
	 * <pre>
	 * f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2 + c_5 x y + c_6 x^2 y + c_7 x y^2 + c_8 x^2 y^2</pre>
	 * <p>
	 * to the supplied sample values. The interpolation function passes through
	 * all sample values. The local maximum cannot be found in closed form but 
	 * is found iteratively, which is not guaranteed to succeed.
	 * See Appendix E.2.4 (Alg. E.3, 'FindMaxQuartic') of [1] for 
	 * additional details.
	 * </p>
	 * <p>
	 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
	 * 3rd ed, Springer (2022).
	 * </p>
	 */
	public static class Quartic extends SubpixelMaxInterpolator {
		public static final int DefaultMaxIterations = 20;	// iteration limit
		public static final double DefaulMaxDelta = 1e-6;	// smallest x/y move to continue search 
		public static final double DefaultMaxRad = 1.0;		// x/y search boundary (-xyLimit, +xyLimit)
		
		private final int maxIterations;
		private final double maxDelta;
		private final double maxRad;
		
		private final double[] c = new double[9];	// polynomial coefficients
		
		private static final Quartic instance = new Quartic();
		
		private Quartic() {
			this(DefaultMaxIterations, DefaulMaxDelta, DefaultMaxRad);
		}
		
		private Quartic(int maxIterations, double maxDelta, double maxRad) {
			this.maxIterations = maxIterations;
			this.maxDelta = maxDelta;
			this.maxRad = maxRad;
		}
		
		public static Quartic getInstance() {
			return instance;
		}
		
		public static Quartic getInstance(int maxIterations, double maxDelta, double maxRad) {
			return new Quartic(maxIterations, maxDelta, maxRad);
		}
		
		// -----------------------------------------------------------
		
		@Override
		public float[] getMax(float[] s) {
			c[0] = s[0];
			c[1] = (s[1] - s[5]) / 2;
			c[2] = (s[7] - s[3]) / 2;
			c[3] = (s[1] - 2 * s[0] + s[5]) / 2;
			c[4] = (s[3] - 2 * s[0] + s[7]) / 2;
			c[5] = (-s[2] + s[4] - s[6] + s[8]) / 4;
			c[6] = (-s[2] + 2 *s[3] - s[4] + s[6] - 2 * s[7] + s[8]) / 4;
			c[7] = (-2 * s[1] + s[2] - s[4] + 2 * s[5] - s[6] + s[8]) / 4;
			c[8] = s[0] + (-2 * s[1] + s[2] - 2 * s[3] + s[4] - 2 * s[5] + s[6] - 2 * s[7] + s[8]) / 4;
			
			// iteratively find the max location:
			boolean done = false;
			int n = 0;
			double[] xCur = {0, 0};
			double H00 = 0, H11 = 0, H01 = 0, d = 0;
			while (!done && n < maxIterations && normL2(xCur) < maxRad) {
				double[] g = this.getGradient(xCur);
				double[][] H = this.getHessian(xCur);
				H00 = H[0][0];
				H11 = H[1][1];
				H01 = H[0][1];
				d = H00 * H11 - sqr(H01);
				if (isZero(d)) {
//					throw new RuntimeException(Quartic.class.getSimpleName() + ": zero determinant");
					return null;
				}
				double[][] Hi = {			// inverse Hessian
						{ H11 / d, -H01 / d}, 
						{-H01 / d,  H00 / d}};	
				double[] xNext = add(xCur, multiply(multiply(-1, Hi), g));
				if (distL2(xCur, xNext) < maxDelta) {
					done = true;
				}
				else {
					xCur[0] = xNext[0];
					xCur[1] = xNext[1];
					n = n + 1;
				}
			}
			
			boolean isMax = (d > 0) && (H00 < 0);	// false if saddle point or relative minimum
			
			if (done && isMax) {
				float z = (float) getInterpolatedValue(xCur);
				return new float[] {(float) xCur[0], (float) xCur[1], z};
			}
			else {
				return null;
			}
		}
		
		private double getInterpolatedValue(double[] X) {
			final double x = X[0];
			final double y = X[1];
			return c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y
						+ c[5]*x*y + c[6]*x*x*y + c[7]*x*y*y + c[8]*x*x*y*y;
		}
		
		private double[] getGradient(double[] X) {
			return this.getGradient(X[0], X[1]);
		}

		private double[] getGradient(double x, double y) {
			double gx = c[1] + 2*c[3]*x + c[5]*y + 2*c[6]*x*y + c[7]*y*y + 2*c[8]*x*y*y;
			double gy = c[2] + c[5]*x + 2*c[4]*y + 2*c[7]*x*y + c[6]*x*x + 2*c[8]*x*x*y;
			return new double[] {gx, gy};
		}
		
		private double[][] getHessian(double[] X) {
			return this.getHessian(X[0], X[1]);
		}
		
		private double[][] getHessian(double x, double y) {
			final double H00 = 2*(c[3] + y*(c[6] + c[8]*y));
			final double H11 = 2*(c[4] + x*(c[7] + c[8]*x));
			final double H01 = c[5] + 2*c[6]*x + 2*c[7]*y + 4*c[8]*x*y;
			double[][] Hi = {
					{ H00, H01}, 
					{ H01, H11}};
			return Hi;
		}
		
	}
}

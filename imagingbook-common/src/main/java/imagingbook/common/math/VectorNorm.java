/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.sqr;

import java.lang.reflect.Array;

/**
 * This class defines various vector norms for calculating
 * the magnitude of a vector and the distance between vectors.
 * 
 * @author WB
 * @version 2022/09/11
 */
public abstract class VectorNorm {
	
	private VectorNorm() {}

	/**
	 * Returns the magnitude of the specified {@code double[]} vector under this norm.
	 * @param a a vector
	 * @return the magnitude of the vector
	 */
	public abstract double magnitude(double[] a);
	
	/**
	 * Returns the magnitude of the specified {@code float[]} vector under this norm.
	 * @param a a vector
	 * @return the magnitude of the vector
	 */
	public abstract double magnitude(float[] a);
	
	/**
	 * Returns the magnitude of the specified {@code int[]} vector under this norm.
	 * @param a a vector
	 * @return the magnitude of the vector
	 */
	public abstract double magnitude(int[] a);

	/**
	 * Returns the distance between two {@code double[]} vectors under this norm.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance between vectors a and b
	 */
	public abstract double distance(double[] a, double[] b);
	
	/**
	 * Returns the distance between two {@code float[]} vectors under this norm.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance between vectors a and b
	 */
	public abstract double distance(float[] a, float[] b);
	
	/**
	 * Returns the distance between two {@code int[]} vectors under this norm.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance between vectors a and b
	 */
	public abstract double distance(int[] a, int[] b);

	/**
	 * Returns the squared distance between two {@code double[]} vectors under this norm.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance between vectors a and b
	 */
	public abstract double distance2(double[] a, double[] b);
	
	/**
	 * Returns the squared distance between two {@code float[]} vectors under this norm.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance between vectors a and b
	 */
	public abstract double distance2(float[] a, float[] b);
	
	/**
	 * Returns the squared distance between two {@code int[]} vectors under this norm.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance between vectors a and b
	 */
	public abstract double distance2(int[] a, int[] b);

	/**
	 * Returns a factor to scale magnitude and distance values 
	 * to the range of the vector components of dimensionality
	 * n. This is prim. used for scaling color distances (n = 3).
	 * E.g., if components are distributed in [0,255], the distances
	 * multiplied by this factor should again be in [0,255].
	 * 
	 * @param n dimensionality
	 * @return scale factor
	 */
	public abstract double getScale(int n);

	/**
	 * Enumeration type for {@link VectorNorm} to be used as
	 * parameter choice.
	 */
	public enum NormType {
		/** L1 (Manhattan) norm/distance. */
		L1(VectorNorm.L1.getInstance()),
		/** L2 (Euclidean) norm/distance. */
		L2(VectorNorm.L2.getInstance()),
		/** L-infinity (maximum) norm/distance. */
		Linf(VectorNorm.Linf.getInstance());
				
		private final VectorNorm inst;
		
		private NormType() {
			this.inst = null;
		}
		private NormType(VectorNorm instance) {
			this.inst = instance;
		}
		
		/**
		 * Returns the (singleton) {@link VectorNorm} instance associated with this
		 * {@link NormType}
		 * @return the {@link VectorNorm} instance
		 */
		public VectorNorm getInstance() {
			return this.inst;
		}
	}
	
	// ---------------------------------------------------------------------------
	
	private static void checkLengths(Object a, Object b) {
		if (Array.getLength(a) != Array.getLength(b)) {
			throw new IllegalArgumentException("vectors a, b must be of same length");
		}
	}
		
//	private static void checkLengths(double[] a, double[] b) {
//		if (a.length != b.length) {
//			throw new IllegalArgumentException("vectors must be of same length");
//		}	
//	}
	
//	private static void checkLengths(float[] a, float[] b) {
//		if (a.length != b.length) {
//			throw new IllegalArgumentException("vectors must be of same length");
//		}	
//	}
	
//	private static void checkLengths(int[] a, int[] b) {
//		if (a.length != b.length) {
//			throw new IllegalArgumentException("vectors must be of same length");
//		}	
//	}

	//  concrete classes -----------------------------------------------

	/**
	 * Implementation of the L1 vector norm (Manhattan norm/distance).
	 * This class defines no public constructor, use methods
	 * {@link L1#getInstance()} or {@link NormType#L1#getInstance()} to
	 * retrieve the associated (singleton) instance.
	 */
	public static class L1 extends VectorNorm {
		
		private static final L1 instance = new L1();
		private L1() {}
		
		/**
		 * Returns an instance of this specific {@link VectorNorm} type.
		 * @return a {@link VectorNorm} instance
		 */
		public static L1 getInstance() {
			return L1.instance;
		}

		@Override
		public double magnitude(double[] a) {
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + Math.abs(a[i]);
			}
			return sum;
		}
		
		@Override
		public double magnitude(float[] a) {
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + Math.abs(a[i]);
			}
			return sum;
		}

		@Override
		public double magnitude(int[] a) {
			long sum = 0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + Math.abs(a[i]);
			}
			return sum;
		}

		@Override
		public double distance(final double[] a, final double[] b) {
			checkLengths(a, b);
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				double d = a[i] - b[i];
				sum = sum + Math.abs(d);
			}
			return sum;
		}

		@Override
		public double distance(final int[] a, final int[] b) {
			checkLengths(a, b);
			int sum = 0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + Math.abs(a[i] - b[i]);
			}
			return sum;
		}

		@Override
		public double distance2(double[] a, double[] b) {
			double d = distance(a, b);
			return d * d;
		}

		public double distance2(int[] a, int[] b) {
			double d = distance(a, b);
			return d * d;
		}

		@Override
		public double distance2(float[] a, float[] b) {
			double d = distance(a, b);
			return d * d;
		}

		@Override
		public double distance(float[] a, float[] b) {
			checkLengths(a, b);
			double sum = 0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + Math.abs(a[i] - b[i]);
			}
			return sum;
		}
		
		@Override
		public double getScale(int n) {
			return 1.0 / n;
		}

	}

	// ------------------------------------------------------------------------------

	/**
	 * Implementation of the L2 vector norm (Euclidean norm/distance).
	 * This class defines no public constructor, use methods
	 * {@link L2#getInstance()} or {@link NormType#L2#getInstance()} to
	 * retrieve the associated (singleton) instance.
	 */
	public static class L2 extends VectorNorm {
		
		private static final L2 instance = new L2();
		private L2() {}
		
		/**
		 * Returns an instance of this specific {@link VectorNorm} type.
		 * @return a {@link VectorNorm} instance
		 */
		public static L2 getInstance() {
			return L2.instance;
		}

		@Override
		public double magnitude(double[] a) {
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + a[i] * a[i];
			}
			return Math.sqrt(sum);
		}
		
		@Override
		public double magnitude(float[] a) {
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + Arithmetic.sqr((double) a[i]);
			}
			return Math.sqrt(sum);
		}

		@Override
		public double magnitude(int[] a) {
			long sum = 0;
			for (int i = 0; i < a.length; i++) {
				sum = sum + a[i] * a[i];
			}
			return Math.sqrt(sum);
		}

		@Override
		public double distance(double[] a, double[] b) {
			return Math.sqrt(distance2(a, b));
		}

		@Override
		public double distance2(final double[] a, final double[] b) {
			checkLengths(a, b);
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				double d = a[i] - b[i];
				sum = sum + d * d;
			}
			return sum;
		}

		@Override
		public double distance(int[] a, int[] b) {
			return Math.sqrt(distance2(a, b));
		}

		@Override
		public double distance2(final int[] a, final int[] b) {
			checkLengths(a, b);
			int sum = 0;
			for (int i = 0; i < a.length; i++) {
				int d = a[i] - b[i];
				sum = sum + d * d;
			}
			return sum;
		}

		@Override
		public double distance(float[] a, float[] b) {
			return Math.sqrt(distance2(a, b));
		}
		
		@Override
		public double distance2(float[] a, float[] b) {
			checkLengths(a, b);
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				double d = a[i] - b[i];
				sum = sum + d * d;
			}
			return sum;
		}
		
		@Override
		public double getScale(int n) {
			return Math.sqrt(1.0 / n);
		}

	}

	// ------------------------------------------------------------------------------

	/**
	 * Implementation of the L-infinity vector norm (maximum norm/distance).
	 * This class defines no public constructor, use methods
	 * {@link Linf#getInstance()} or {@link NormType#Linf#getInstance()} to
	 * retrieve the associated (singleton) instance.
	 */
	public static class Linf extends VectorNorm {
		
		private static final Linf instance = new Linf();
		private Linf() {}
		
		/**
		 * Returns an instance of this specific {@link VectorNorm} type.
		 * @return a {@link VectorNorm} instance
		 */
		public static Linf getInstance() {
			return Linf.instance;
		}

		@Override
		public double magnitude(double[] a) {
			double dmax = 0.0;
			for (int i = 0; i < a.length; i++) {
				dmax = Math.max(dmax, Math.abs(a[i]));
			}
			return dmax;
		}
		
		@Override
		public double magnitude(float[] a) {
			float dmax = 0.0f;
			for (int i = 0; i < a.length; i++) {
				dmax = Math.max(dmax, Math.abs(a[i]));
			}
			return dmax;
		}

		@Override
		public double magnitude(int[] a) {
			int dmax = 0;
			for (int i = 0; i < a.length; i++) {
				dmax = Math.max(dmax, Math.abs(a[i]));
			}
			return dmax;
		}

		@Override
		public double distance(double[] a, final double[] b) {
			checkLengths(a, b);
			double dmax = 0.0;
			for (int i = 0; i < a.length; i++) {
				double d = a[i] - b[i];
				dmax = Math.max(dmax, Math.abs(d));
			}
			return dmax;
		}
		
		@Override
		public double distance2(double[] a, double[] b) {
			return sqr(distance(a, b));
		}

		@Override
		public double distance(int[] a, final int[] b) {
			checkLengths(a, b);
			int dmax = 0;
			for (int i = 0; i < a.length; i++) {
				int d = Math.abs(a[i] - b[i]);
				dmax = Math.max(dmax, d);
			}
			return dmax;
		}
		
		@Override
		public double distance2(int[] a, int[] b) {
			double d = distance(a, b);
			return d * d;
		}

		@Override
		public double distance2(float[] a, float[] b) {
			return sqr(distance(a, b));
		}

		@Override
		public double distance(float[] a, float[] b) {
			checkLengths(a, b);
			float dmax = 0;
			for (int i = 0; i < a.length; i++) {
				float d = Math.abs(a[i] - b[i]);
				dmax = Math.max(dmax, d);
			}
			return dmax;
		}
		
		@Override
		public double getScale(int n) {
			return 1.0;
		}

	}

}


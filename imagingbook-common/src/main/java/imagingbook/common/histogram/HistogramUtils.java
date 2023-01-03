/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.histogram;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * This class defines static methods related to histograms. See Ch. 2 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/08/24
 */
public abstract class HistogramUtils {
	
	private HistogramUtils() { }

	/**
	 * Calculates and returns the total population (sum of all bin counts) of a histogram.
	 *
	 * @param h a histogram
	 * @return the histogram's total count
	 */
	public static int count(int[] h) {
		return count(h, 0, h.length - 1);
	}

	/**
	 * Calculates and returns the population (sum of bin counts) of a histogram over the specified range of indexes. The
	 * range is automatically clipped.
	 *
	 * @param h a histogram
	 * @param lo the lower index (inclusive)
	 * @param hi the upper index (inclusive)
	 * @return the population count
	 */
	public static int count(int[] h, int lo, int hi) {
		if (lo < 0) lo = 0;
		if (hi >= h.length) hi = h.length-1;
		int cnt = 0;
		for (int i = lo; i <= hi; i++) {
			cnt += h[i];
		}
		return cnt;
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Returns the maximum bin value (count) of the given histogram.
	 * 
	 * @param h a histogram
	 * @return the maximum bin value
	 */
	public static int max(int[] h) {
		int hmax = -1;
		for (int i = 0; i < h.length; i++) {
			if (h[i] > hmax)
				hmax = h[i];
		}
		return hmax;
	}

	/**
	 * Returns the maximum bin value (count) of the given frequency distribution (histogram).
	 *
	 * @param h a histogram
	 * @return the maximum bin value
	 */
	public static double max(double[] h) {
		double hmax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < h.length; i++) {
			if (h[i] > hmax)
				hmax = h[i];
		}
		return hmax;
	}
	
	// -----------------------------------------------------------

	/**
	 * Calculates and returns the cumulative histogram from a given histogram.
	 *
	 * @param h a histogram
	 * @return the cumulative histogram
	 */
	public static int[] cumulate(int[] h) {
		final int K = h.length;
		int[] C = new int[K];
		C[0] = h[0];
		for (int i = 1; i < K; i++) {
	        C[i] = C[i-1] + h[i];
	    }
	    return C;
	}
	
	// -----------------------------------------------------------

	/**
	 * Calculates and returns the <em>probability distribution function</em> (pdf) for the given histogram. The
	 * resulting {@code double} array has the same length as the original histogram. Its values sum to 1.
	 *
	 * @param h a histogram
	 * @return the probability distribution function
	 */
	public static double[] pdf(int[] h) {
		final int K = h.length;
		final int n = count(h);			// sum all histogram values	
		double[] p = new double[K];
		for (int i = 0; i < h.length; i++) {
			p[i] =  (double) h[i] / n;
		}
		return p;
	}

	/**
	 * Calculates and returns the <em>cumulative distribution function</em> (cdf) for the given histogram. The resulting
	 * {@code double} array has the same length as the original histogram. Its maximum value is 1.
	 *
	 * @param h a histogram
	 * @return the cumulative distribution function
	 */
	public static double[] cdf(int[] h) {
		// returns the cumul. probability distribution function (cdf) for histogram h
		final int K = h.length;
		final int n = count(h);		// sum all histogram values		
		double[] P = new double[K];
		long c = h[0];
		P[0] = (double) c / n;
		for (int i = 1; i < K; i++) {
	    	c = c + h[i];
	        P[i] = (double) c / n;
	    }
	    return P;
	}
	
	// -----------------------------------------------------------

	/**
	 * Calculates and returns the intensity mean (average) of the distribution represented by the given histogram.
	 *
	 * @param h a histogram
	 * @return the mean intensity
	 */
	public static double mean(int[] h) {
		return mean(h, 0, h.length - 1);
	}

	/**
	 * Calculates and returns the intensity mean (average) of the distribution represented by the given histogram,
	 * limited to the specified intensity range. The range is automatically clipped.
	 *
	 * @param h a histogram
	 * @param lo the lower index (inclusive)
	 * @param hi the upper index (inclusive)
	 * @return the mean intensity
	 */
	public static double mean(int[] h, int lo, int hi) {
		if (lo < 0)
			lo = 0;
		if (hi >= h.length)
			hi = h.length - 1;
		long cnt = 0;
		long sum = 0;
		for (int i = lo; i <= hi; i++) {
			cnt = cnt + h[i];
			sum = sum + i * h[i];
		}
		if (cnt > 0)
			return ((double) sum) / cnt;
		else
			return 0;
	}
	
	// -----------------------------------------------------------

	/**
	 * Calculates and returns the intensity variance (&sigma;<sup>2</sup>) of the distribution represented by the given
	 * histogram.
	 *
	 * @param h a histogram
	 * @return the intensity variance
	 */
	public static double variance(int[] h) {
		return variance(h, 0, h.length-1);
	}

	/**
	 * Calculates and returns the intensity variance (&sigma;<sup>2</sup>) of the distribution represented by the given
	 * histogram, limited to the specified intensity range (fast version). The range is automatically clipped.
	 *
	 * @param h a histogram
	 * @param lo the lower index (inclusive)
	 * @param hi the upper index (inclusive)
	 * @return the intensity variance
	 */
	public static double variance(int[] h, int lo, int hi) {
		if (lo < 0)
			lo = 0;
		if (hi >= h.length)
			hi = h.length - 1;
		long A = 0;
		long B = 0;
		int N = 0;
		for (int i = lo; i <= hi; i++) {
			int ni = h[i];
			A = A + (long) i * ni;
			B = B + (long) i * i * ni;
			N = N + ni;
		}
		
		if (N == 0) {
			throw new IllegalArgumentException("empty histogram or range");
		}
		
		return (B - (double) (A * A) / N) / N;
	}
	
	// This is a naive (slow) version, for testing only:
	public static double varianceSlow(int[] h, int lo, int hi) {
		if (lo < 0)
			lo = 0;
		if (hi >= h.length)
			hi = h.length - 1;
		final double mu = mean(h, lo, hi);
		int N = 0;
		double sum = 0;
		for (int i = lo; i <= hi; i++) {
			N = N + h[i];
			sum = sum + sqr(i - mu) * h[i];
		}
		
		if (N == 0) {
			throw new IllegalArgumentException("empty histogram or range");
		}

		return sum / N;
	}
	
	// -----------------------------------------------------------

	/**
	 * Calculates and returns the intensity <em>median</em> of the distribution represented by the given histogram.
	 *
	 * @param h a histogram
	 * @return the intensity median
	 */
	public static int median(int[] h) {
		final int K = h.length;
		final int N = count(h);
		final int m = N / 2;
		int i = 0;
		int sum = h[0];
		while (sum <= m && i < K) {
			i++;
			sum += h[i];
		}
		return i;
	}
	
	// -----------------------------------------------------------

	/**
	 * Returns a normalized frequency distribution for the given histogram whose maximum entry is 1 ({@code int}
	 * version). Mainly intended for displaying histograms.
	 *
	 * @param h a histogram
	 * @return the max-normalized frequency distribution
	 */
	public static double[] normalizeMax(int[] h) {
		// find the max histogram entry
		final int max = max(h);
		if (max == 0) {
			throw new IllegalArgumentException("empty histogram");
		}
		double[] hn = new double[h.length];
		double s = 1.0 / max;
		for (int i = 0; i < h.length; i++) {
			hn[i] = s * h[i];
		}
		return hn;
	}

	/**
	 * Returns a normalized frequency distribution for the given histogram whose maximum entry is 1 ({@code double}
	 * version). Mainly intended for displaying histograms.
	 *
	 * @param h a histogram
	 * @return the max-normalized frequency distribution
	 */
	public static double[] normalizeMax(double[] h) {
		// find max histogram entry
		final double max = max(h);
		if (max == 0) {
			throw new IllegalArgumentException("empty histogram");
		}
		double[] hn = new double[h.length];
		double s = 1.0 / max;
		for (int i = 0; i < h.length; i++) {
			hn[i] = s * h[i];
		}
		return hn;
	}
	
	// -----------------------------------------------------------------

	/**
	 * Histogram matching. Given are two histograms: the histogram hA of the target image IA and a reference histogram
	 * hR, both of size K. The result is a discrete mapping f which, when applied to the target image, produces a new
	 * image with a distribution function similar to the reference histogram.
	 *
	 * @param hA histogram of the target image
	 * @param hR reference histogram (the same size as hA)
	 * @return a discrete mapping f to be applied to the values of a target image
	 */
	public static int[] matchHistograms (int[] hA, int[] hR) {
		int K = hA.length;
		double[] PA = HistogramUtils.cdf(hA); // get CDF of histogram hA
		double[] PR = HistogramUtils.cdf(hR); // get CDF of histogram hR
		int[] f = new int[K]; // pixel mapping function f()

		// compute pixel mapping function f():
		for (int a = 0; a < K; a++) {
			int j = K - 1;
			do {
				f[a] = j;
				j--;
			} while (j >= 0 && PA[a] <= PR[j]);
		}
		return f;
	}

	/**
	 * Histogram matching to a reference cumulative distribution function that is piecewise linear.
	 *
	 * @param hA histogram of the target image
	 * @param PR a piecewise linear reference cumulative distribution function ({@link PiecewiseLinearCdf})
	 * @return a discrete mapping f to be applied to the values of a target image
	 * @see PiecewiseLinearCdf
	 * @see #matchHistograms(int[], int[])
	 */
	public static int[] matchHistograms(int[] hA, PiecewiseLinearCdf PR) {
		int K = hA.length;
		double[] PA = HistogramUtils.cdf(hA); // get p.d.f. of histogram Ha
		int[] f = new int[K]; 					// pixel mapping function f()

		// compute pixel mapping function f():
		for (int a = 0; a < K; a++) {
			double b = PA[a];
			f[a] = (int) Math.round(PR.getInverseCdf(b));
		}
		return f;
	}
	
}

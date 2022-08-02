/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.histogram;

// TODO: needs revision, histogram should get its own class.

public class Util {

//	static int[] makeGaussianHistogram () {
//		return makeGaussianHistogram(128, 50);
//	}

	public static int[] makeGaussianHistogram (double mean, double sigma) {
		int[] h = new int[256];
		double sigma2 = 2 * sigma * sigma;
		for (int i = 0; i < h.length; i++) {
			double x = mean - i;
			double g = Math.exp(-(x * x) / sigma2) / sigma;
			h[i] = (int) Math.round(10000 * g);
		}
		return h;
	}

	public static double[] normalizeHistogram (double[] h) {
		// find max histogram entry
		double max = h[0];
		for (int i = 0; i < h.length; i++) {
			if (h[i] > max)
				max = h[i];
		}
		if (max == 0) return null;
		// normalize
		double[] hn = new double[h.length];
		double s = 1.0/max;
		for (int i = 0; i < h.length; i++) {
			hn[i] = s * h[i];
		}
		return hn;
	}

	//------------------------------------------------------
	
	public static double[] normalizeHistogram (int[] h) {
		// find the max histogram entry
		int max = h[0];
		for (int i = 0; i < h.length; i++) {
			if (h[i] > max)
				max = h[i];
		}
		if (max == 0) return null;
		// normalize
		double[] hn = new double[h.length];
		double s = 1.0/max;
		for (int i = 0; i < h.length; i++) {
			hn[i] = s * h[i];
		}
		return hn;
	}

	public static double[] Cdf (int[] h) {
		// returns the cumul. probability distribution function (cdf) for histogram h
		int K = h.length;
		int n = 0;		// sum all histogram values		
		for (int i=0; i<K; i++)	{ 	
			n = n + h[i]; 
		}
		double[] P = new double[K];
		int c = h[0];
		P[0] = (double) c / n;
		for (int i = 1; i < K; i++) {
	    	c = c + h[i];
	        P[i] = (double) c / n;
	    }
	    return P;
	}

	public static double[] Pdf (int[] h) {
		// returns the probability distribution function (pdf) for histogram h
		int K = h.length;
		int n = 0;			// sum all histogram values	
		for (int i = 0; i < K; i++) {
			n = n + h[i]; 
		}
		double[] p = new double[K];
		for (int i = 0; i < h.length; i++) {
			p[i] =  (double) h[i] / n;
		}
		return p;
	}

	// methods moved from Thresholder -------------------------------
	
	// compute the sum of a histogram array
	public static int sum(int[] h) {
		int cnt = 0;
		for (int i = 0; i < h.length; i++) {
			cnt += h[i];
		}
		return cnt;
	}

	public static int count(int[] h) {
		return count(h, 0, h.length - 1);
	}

	// compute the population of a histogram from index lo...hi
	public static int count(int[] h, int lo, int hi) {
		if (lo < 0) lo = 0;
		if (hi >= h.length) hi = h.length-1;
		int cnt = 0;
		for (int i = lo; i <= hi; i++) {
			cnt += h[i];
		}
		return cnt;
	}

	public static double mean(int[] h) {
		return mean(h,0,h.length-1);
	}

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

	public static double sigma2(int[] h) {
		return sigma2(h, 0, h.length-1);
	}

	// fast version
	public static double sigma2(int[] h, int lo, int hi) {
		if (lo < 0)
			lo = 0;
		if (hi >= h.length)
			hi = h.length - 1;
		long A = 0;
		long B = 0;
		long N = 0;
		for (int i = lo; i <= hi; i++) {
			int ni = h[i];
			A = A + i * ni;
			B = B + i * i * ni;
			N = N + ni;
		}
		if (N > 0)
			return ((double) B - (double) A * A / N) / N;
		else
			return 0;
	}
	
	// this is a slow version, only for testing:
//	protected double sigma2(int[] h, int lo, int hi) {
//		if (lo < 0) lo = 0;
//		if (hi >= h.length) hi = h.length-1;
//		double mu = mean(h,lo,hi);
//		long cnt = 0;
//		double sum = 0;
//		for (int i=lo; i<=hi; i++) {
//			cnt = cnt + h[i];
//			sum = sum + (i - mu) * (i - mu) * h[i];
//		}
//		if (cnt > 0)
//			return ((double)sum) / cnt;
//		else 
//			return 0;
//	}


	// compute the median of a histogram 
	public static int median(int[] h) {
		int K = h.length;
		int N = sum(h);
		int m = N / 2;
		int i = 0;
		int sum = h[0];
		while (sum <= m && i < K) {
			i++;
			sum += h[i];
		}
		return i;
	}

	public static double[] normalize(int[] h) {
		int K = h.length;
		int N = count(h);
		double[] nh = new double[K];
		for (int i = 0; i < K; i++) {
			nh[i] = ((double) h[i]) / N;
		}
		return nh;
	}

	public static double[] normalize(double[] h) {
		double[] nh = new double[h.length];
		double hmax = max(h);
		for (int i = 0; i < h.length; i++) {
			nh[i] = (double) h[i] / hmax;
		}
		return nh;
	}

	public static double max(double[] h) {
		double hmax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < h.length; i++) {
			double n = h[i];
			if (n > hmax)
				hmax = n;
		}
		return hmax;
	}
	
	
	
	

}

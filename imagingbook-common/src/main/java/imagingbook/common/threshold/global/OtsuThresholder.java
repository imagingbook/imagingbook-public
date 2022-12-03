/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.threshold.global;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * This is an implementation of the global thresholder proposed by Otsu [1]. See
 * Sec. 9.1.4 (Alg. 9.4 and 9.3) in [2] for a detailed description.
 * </p>
 * <p>
 * [1] N. Otsu, "A threshold selection method from gray-level histograms", IEEE
 * Transactions on Systems, Man, and Cybernetics 9(1), 62-66 (1979). <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/21
 * 
 */
public class OtsuThresholder implements GlobalThresholder {
	
	private int[] h = null;
	private double[] M0 = null;		// table of background means
	private double[] M1 = null;		// table of foreground means
	private int N = 0;				// number of image pixels
	
	public OtsuThresholder() {
		super();
	}
	
	@Override
	public int getThreshold(int[] hist) {
		h = hist;
		int K = h.length;
		makeMeanTables(h);

		double sigma2Bmax = 0;
		int qMax = -1;
		int n0 = 0;
		
		// examine all possible threshold values q:
		for (int q = 0; q <= K-2; q++) {
			n0 = n0 +  h[q]; 			// # of background pixels for q
			int n1 = N - n0;			// # of foreground pixels for q
			if (n0 > 0 && n1 > 0) {		// both sets must be non-empty
				double sigma2B =  sqr(M0[q] - M1[q]) * n0 * n1; 	// factor (1/N^2) omitted
				if (sigma2B > sigma2Bmax) {
					sigma2Bmax = sigma2B;
					qMax = q;
				}
			}
		}
		
		return qMax;
	}
	
	private void makeMeanTables(int[] h) {
		int K = h.length;
		this.M0 = new double[K];
		this.M1 = new double[K];
		int n0 = 0;
		long s0 = 0;
		for (int q = 0; q < K; q++) {
			n0 = n0 + h[q];
			s0 = s0 + q * h[q];
			M0[q] = (n0 > 0) ? ((double) s0) / n0 : -1;
		}
		
		this.N = n0;
		
		int n1 = 0;
		long s1 = 0;
		M1[K-1] = 0;
		for (int q = h.length-2; q >= 0; q--) {
			n1 = n1 + h[q+1];
			s1 = s1 + (q+1) * h[q+1];
			M1[q] = (n1 > 0) ? ((double) s1)/n1 : -1;
		}
	}
}

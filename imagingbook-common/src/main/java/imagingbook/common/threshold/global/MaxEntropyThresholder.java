/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.threshold.global;

import imagingbook.common.histogram.HistogramUtils;

/**
 * <p>
 * This is an implementation of the global thresholder proposed by Kapur et al.
 * in [1]. See Sec. 9.1.5 (Alg. 9.5) of [2] for a detailed description.
 * </p>
 * <p>
 * [1] J. N. Kapur, P. K. Sahoo, and A. K. C. Wong. A new method for gray-level
 * picture thresholding using the entropy of the histogram. Computer Vision,
 * Graphics, and Image Processing 29, 273–285 (1985). <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/21
 */
public class MaxEntropyThresholder implements GlobalThresholder {
	
	static final double EPSILON = 1E-16;
	
	private double[] S0 = null;
	private double[] S1 = null;
	
	public MaxEntropyThresholder() {
		super();
	}
	
	@Override
	public int getThreshold(int[] h) {
		int K = h.length;	
		double[] p = HistogramUtils.pdf(h);			// normalized histogram (to probabilities)	
		makeTables(p);	// initialize S0, S1
		
		double P0 = 0;	// cumulative probability
		
		int qMax = -1;
		double Hmax = Double.NEGATIVE_INFINITY;
		
		for (int q = 0; q <= K-2; q++) {		
			P0 = P0 + p[q];	
			if (P0 < EPSILON) continue;		// empty histogram so far, nothing to do
				
			double P1 = 1 - P0;
			if (P1 < EPSILON) break;		// no more histogram entries, finished
			
			// P0, P1 are nonzero!			
			double H0 = -S0[q]/P0 + Math.log(P0);
			double H1 = -S1[q]/P1 + Math.log(P1);
			double H01 = H0 + H1;		
			
			if (H01 > Hmax) {
				Hmax = H01;
				qMax = q;
			}		
		}

		return qMax;
	}
	
	// pre-calculate tables S0 and S1
	private void makeTables(double[] p) {
		int K = p.length;		
		S0 = new double[K];		
		S1 = new double[K];		

		// S0[q] = \sum_{i=0}^{q} p[i] * \log(p[i])
		double s0 = 0;
		for (int q = 0; q < K; q++) {
			if (p[q] > 0) {
				s0 = s0 + p[q] * Math.log(p[q]);
			}
			S0[q] = s0;
		}
		
		// S1[q] = \sum_{i=q+1}^{K-1} p[i] * \log(p[i])
		double s1 = 0;
		for (int q = K-1; q >= 0; q--) {
			S1[q] = s1;
			if (p[q] > 0) {
				s1 = s1 + p[q] * Math.log(p[q]);
			}
		}
	}
}

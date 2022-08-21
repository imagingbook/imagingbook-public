/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.threshold.global;

import ij.IJ;
import imagingbook.common.histogram.Util;

/**
 * <p>
 * This is an implementation of the global thresholder proposed by Kapur et al. in [1].
 * See Sec. 9.1.5 (Alg. 9.5) of [2] for a detailed description.
 * </p>
 * <p>
 * [1] J. N. Kapur, P. K. Sahoo, and A. K. C. Wong. A new
 * method for gray-level picture thresholding using the entropy of the
 * histogram. Computer Vision, Graphics, and Image Processing 29, 273–285 (1985).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/21
 */
public class MaxEntropyThresholder implements GlobalThresholder {
	
	static final double EPSILON = 1E-12;
	
//	private double[] H0array = new double[256]; 	// only used for reporting
//	private double[] H1array = new double[256]; 	// only used for reporting
//	private double[] H01array = new double[256]; 	// only used for reporting
	
	private double[] S0 = null;
	private double[] S1 = null;
	
	public MaxEntropyThresholder() {
		super();
	}
	
	@Override
	public int getThreshold(int[] h) {
		int K = h.length;	
		double[] p = Util.normalize(h);		// normalized histogram (to probabilities)
		makeTables(p);	// initialize S0, S1
		
		double P0 = 0;	// cumulative background probability
		double P1;		// cumulative foreground probability
		int qMax = -1;
		double Hmax = Double.NEGATIVE_INFINITY;
		
		for (int q = 0; q <= K-2; q++) {
			P0 = P0 + p[q];
			P1 = 1 - P0;
			double H0 = (P0 > EPSILON) ? -S0[q]/P0 + Math.log(P0) : 0;
			double H1 = (P1 > EPSILON) ? -S1[q]/P1 + Math.log(P1) : 0;
			double H01 = H0 + H1;
			
//			H0array[q] = H0;	// logging only
//			H1array[q] = H1;	// logging only
//			H01array[q] = H01;	// logging only
			
			if (H01 > Hmax) {
				Hmax = H01;
				qMax = q;
			}
			
			IJ.log(String.format("q=%3d | H0=%.2f H1=%.2f | H01=%.6f", q, H0, H1, H01));
		}
		return qMax;
	}
	
	private void makeTables(double[] p) {
		int K = p.length;

		// make tables S0[], S1[]
		S0 = new double[K];
		S1 = new double[K];
		
		double s0 = 0;
		for (int q = 0; q < K; q++) {
			if (p[q] > EPSILON) {
				s0 = s0 + p[q] * Math.log(p[q]);
			}
			S0[q] = s0;			// S0[q] <- S0[q-1] + p[q] * log(p[q])
		}

		double s1 = 0;
		for (int i = K-1; i >= 0; i--) {
			S1[i] = s1;			// S1[q] <- S0[q+1] + p[q+1] * log(p[q+1])	CHECK *************
			if (p[i] > EPSILON) {
				s1 = s1 + p[i] * Math.log(p[i]);
			}
		}
	}
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.matching;

import ij.process.FloatProcessor;

/**
 * <p>
 * Instances of this class perform matching on scalar-valued images
 * based on the correlation coefficient.
 * The "search" image I (to be searched for matches of the "reference" image R) is
 * initially associated with the {@link CorrCoeffMatcher}.
 * The assumption is, that the search image I is fixed and the matcher
 * tries to match multiple reference images R.
 * See Sec. 23.1.1 (Alg. 23.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/02/10
 * @version 2022/09/16 revised
 */
public class CorrCoeffMatcher {
	
	private final FloatProcessor I; // search image
	private final int MI, NI; 		// width/height of image

	private FloatProcessor R; 		// reference image
	private int MR, NR; 			// width/height of reference
	private int K;
	private double meanR; 			// mean value of reference
	private double varR;  			// square root of reference variance

	/**
	 * Constructor.
	 * @param I the reference image (to be matched to)
	 */
	public CorrCoeffMatcher(FloatProcessor I) {
		this.I = I;
		this.MI = this.I.getWidth();
		this.NI = this.I.getHeight();
	}
	
	/**
	 * Matches the specified reference image R to the (fixed) search image I.
	 * @param R some scalar-valued reference image
	 * @return a 2D array Q[r][s] of match scores
	 */
	public float[][] getMatch(FloatProcessor R) {
		this.R = R;
		this.MR = R.getWidth();
		this.NR = R.getHeight();
		this.K = MR * NR;

		// calculate the mean and variance of template
		double sumR = 0;
		double sumR2 = 0;
		for (int j = 0; j < NR; j++) {
			for (int i = 0; i < MR; i++) {
				float b = R.getf(i,j);
				sumR  += b;
				sumR2 += b * b;
			}
		}
		
		this.meanR = sumR / K;
		this.varR = Math.sqrt(sumR2 - K * meanR * meanR);
		
		float[][] C = new float[MI - MR + 1][NI - NR + 1];
		for (int r = 0; r <= MI - MR; r++) {
			for (int s = 0; s <= NI - NR; s++) {
				float d = (float) getMatchValue(r, s);
				C[r][s] = d;
			}	
		}
		this.R = null;
		return C;
	}
	
	private double getMatchValue(int r, int s) {
		double sumI = 0, sumI2 = 0, sumIR = 0;
		for (int j = 0; j < NR; j++) {
			for (int i = 0; i < MR; i++) {
				float a = I.getf(r + i, s + j);
				float b = R.getf(i, j);
				sumI  += a;
				sumI2 += a * a;
				sumIR += a * b;
			}
		}
		double meanI = sumI / K;
		return (sumIR - K * meanI * meanR) / 
			   (1 + Math.sqrt(sumI2 - K * meanI * meanI) * varR);
				// WB: added 1 in denominator to handle flat image regions (w. zero variance)
	}  
	
}

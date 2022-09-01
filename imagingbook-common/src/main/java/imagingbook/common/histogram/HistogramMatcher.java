/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.histogram;

public class HistogramMatcher {
	
	public HistogramMatcher() {
	}
	
	/**
	 * @param hA histogram of target image
	 * @param hR reference histogram
	 * @return the mapping function fhs() to be applied to the target image as an int table.
	 */
	public int[] matchHistograms (int[] hA, int[] hR) {
		int K = hA.length;
		double[] PA = HistogramUtils.cdf(hA); // get CDF of histogram hA
		double[] PR = HistogramUtils.cdf(hR); // get CDF of histogram hR
		int[] fhs = new int[K]; // pixel mapping function f()

		// compute pixel mapping function f():
		for (int a = 0; a < K; a++) {
			int j = K - 1;
			do {
				fhs[a] = j;
				j--;
			} while (j >= 0 && PA[a] <= PR[j]);
		}
		return fhs;
	}
	
	public int[] matchHistograms(int[] hA, PiecewiseLinearCdf PR) {
		int K = hA.length;
		double[] PA = HistogramUtils.cdf(hA); // get p.d.f. of histogram Ha
		int[] F = new int[K]; 		// pixel mapping function f()

		// compute pixel mapping function f():
		for (int a = 0; a < K; a++) {
			double b = PA[a];
			F[a] = PR.getInverseCdf(b);
		}
		return F;
	}
}

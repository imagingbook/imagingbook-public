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
 * Slow version of the {@link IsodataThresholder} using explicit recalculation
 * of background and foreground means in every iteration. See [1], Alg. 9.2.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/01
 */
public class IsodataThresholderSlow implements GlobalThresholder {
	
	
	private int MAX_ITERATIONS = 100;
	
	public IsodataThresholderSlow() {
		super();
	}

	@Override
	public int getThreshold(int[] h) {
		int K = h.length;
		int q = (int) HistogramUtils.mean(h, 0, K-1); 	// start with the total mean
		int q_;
		
		int i = 0;	// iteration counter
		do {
			i++;
			int nB = HistogramUtils.count(h, 0, q);
			int nF = HistogramUtils.count(h, q+1, K-1);
			if (nB == 0 || nF == 0)
				return -1;
			double meanB = HistogramUtils.mean(h, 0, q);
			double meanF = HistogramUtils.mean(h, q+1, K-1);
			q_ = q;				
			q = (int)((meanB + meanF)/2);
		} while (q != q_ && i < MAX_ITERATIONS);
		
		return q;
	}

}

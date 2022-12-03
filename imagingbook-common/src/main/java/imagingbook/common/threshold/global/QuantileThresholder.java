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
 * This is an implementation of the global "quantile" thresholder, described in
 * Sec. 9.1 (Alg. 9.1) of [1]. Requires the quantile (b) to be specified at
 * instantiation. Method {@link #getThreshold(int[])} returns a threshold that
 * will put AT LEAST the b-fraction of pixels (but not all pixels) in the
 * background.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/22
 */
public class QuantileThresholder implements GlobalThresholder {
	
	private final double b;	// quantile of expected background pixels
	
	public QuantileThresholder(double b) {
		if (b <= 0 || b >= 1) {
			throw new IllegalArgumentException("quantile b must be in [0,1]");
		}
		this.b = b;
	}

	@Override
	public int getThreshold(int[] h) {
		int K = h.length;
		int N = HistogramUtils.count(h);	// total number of pixels	
		double n = N * b;		// number of pixels in quantile
		
		int i = 0;
		int c = h[0];
		while (i < K && c < n) {
			i++;
			c = c + h[i];
		}
		
		int q = (c < N) ? i : -1; // check if all pixels are included by the estimated threshold
		return q;
	}
}

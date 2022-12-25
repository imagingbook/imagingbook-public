/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.threshold.global;

import imagingbook.common.histogram.HistogramUtils;

/**
 * <p>
 * This is an implementation of the global "quantile" thresholder, described in Sec. 9.1 (Alg. 9.1) of [1]. Requires the
 * quantile (p) to be specified at instantiation. Method {@link #getThreshold(int[])} returns the minimal threshold that
 * will put AT LEAST the p-fraction of pixels (but not all pixels) in the background. If the underlying image is flat
 * (i.e., contains only a single pixel value), {@link GlobalThresholder#NoThreshold} is returned to indicate an invalid
 * threshold. Similarly there is no valid threshold if it takes the pixels from all brightness levels to fill the
 * p-quantile.
 * </p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022). Also see the Errata p. 245!
 * </p>
 *
 * @author WB
 * @version 2022/08/22
 */
public class QuantileThresholder implements GlobalThresholder {
	
	private final double p;	// quantile of expected background pixels
	
	public QuantileThresholder(double p) {
		if (p <= 0 || p >= 1) {
			throw new IllegalArgumentException("quantile p must be 0 < p < 1");
		}
		this.p = p;
	}

	@Override
	public float getThreshold(int[] h) {
		int K = h.length;
		int N = HistogramUtils.count(h);		// total number of pixels
		double np = N * p;						// number of pixels in quantile
		int i = 0;
		int c = h[0];							// c = cumulative histogram [i]
		while (i < K && c < np) {				// quantile calculation
			i = i + 1;
			c = c + h[i];
		}
		return (c < N) ? i : NoThreshold;		// return i if level i does not include all pixels
	}

}

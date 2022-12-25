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
 * (i.e., contains only a single pixel value), q = -1 is returned to indicate an invalid threshold.
 * </p>
 * <p>
 * Note that this implementation slightly deviates from [1] in the way it handles binary images. Let's assume that the
 * image contains only two pixel values a, b (with a &lt& b):
 * </p>
 * <ul>
 * <li>Case 1: Assume count(a) &gt; np, where np is the number of pixels in the p-quantile.</li>
 * In this case, q = q is returned as the (correct) threshold.
 * <li>Case 2: When count(q) &lt; np, there are not enough A-pixels to fill the p-quantile, but increasing the threshold
 * to level b would include all image pixels and thus have no effect. In this case, q = b-1 is returned as the
 * threshold, which means that the thresholded image will have fewer than the p-fraction of pixels in the background.
 * </li>
 * </ul>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022). See Errata p. 245!
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
	public int getThreshold(int[] h) {
		int K = h.length;
		int N = HistogramUtils.count(h);		// total number of pixels
		double np = N * p;						// number of pixels in quantile
		int i = 0;
		int c = h[0];							// c = cumulative histogram [i]
		while (i < K && c < np) {				// quantile calculation
			i = i + 1;
			c = c + h[i];
		}
		return (c < N) ? i : -1;			// level i does not include all pixels
	}

}

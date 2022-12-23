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
 * quantile (b) to be specified at instantiation. Method {@link #getThreshold(int[])} returns the minimal threshold that
 * will put AT LEAST the b-fraction of pixels (but not all pixels) in the background. If the underlying image is flat
 * (i.e., contains only a single pixel value), q = -1 is returned to indicate an invalid threshold.
 * </p>
 * <p>
 * Note that this implementation slightly deviates from [1] in the way it handles binary images. Let's assume that the
 * image contains only two pixel values A, B, with A &lt& B:
 * </p>
 * <ul>
 * <li>Case 1: Assume count(A) &gt; n, where n is the number of pixels in the b-quantile.</li>
 * In this case, q = A is returned as the (correct) threshold.
 * <li>Case 2: When count(A) &lt; n, there are not enough A-pixels to fill the b-quantile, but increasing the threshold
 * to level B would include all image pixels and thus have no effect. In this case, q = B-1 is returned as the
 * threshold, which means that the thresholded image will have fewer than the b-fraction of pixels in the background.
 * </li>
 * </ul>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
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
		int[] H = HistogramUtils.cumulate(h);	// cumulative histogram
		double n = N * b;					// number of pixels in quantile

		int i = 0;
		while (i < K && H[i] < n) {
			i++;
		}
		// H[i] >= n

		if (H[i] < N) {              // level i does not include all pixels
			return i;                // q = i
		} else if (i > 0 && H[i - 1] > 0) {    // there are pixels at a lower level
			return i - 1;
		} else {    				// image has only one pixel value, no threshold
			return -1;
		}
	}

}

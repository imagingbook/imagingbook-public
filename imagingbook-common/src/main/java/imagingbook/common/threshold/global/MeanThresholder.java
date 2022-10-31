/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.threshold.global;

/**
 * <p>
 * Simple global thresholder which uses the mean pixel value as the threshold.
 * See Sec. 9.1.2 of [1] for additional details (Eq. 9.10).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/01
 */
public class MeanThresholder implements GlobalThresholder {
	
	public MeanThresholder() {
	}

	@Override
	public int getThreshold(int[] h) {
		// calculate mean of the entire image:
		int K = h.length;
		int N = 0;
		
		long sum = 0;
		for (int i = 0; i < K; i++) {
			N += h[i];
			sum += i * h[i];
		}
		
		int mean = (int) Math.floor((double)sum / N);	// important to use floor if only two pixel values

		// count resulting background pixels:
		int n0 = 0;
		for (int i = 0; i <= mean; i++) {
			n0 += h[i];
		}
		
		// determine if background or foreground is empty:
		int q = (n0 < N) ? mean : -1;
		return q;
	}
}

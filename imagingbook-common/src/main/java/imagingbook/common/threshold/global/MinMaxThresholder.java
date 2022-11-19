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
 * Simple global thresholder which sets the threshold centered between the
 * image's minimum and maximum pixel value. See Sec. 9.1.2 of [1] for additional
 * details (Eq. 9.12).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/23
 *
 */
public class MinMaxThresholder implements GlobalThresholder {
	
	public MinMaxThresholder() {
		super();
	}

	@Override
	public int getThreshold(int[] h) {
		// calculate mean of entire image:
		int K = h.length;
		int minVal, maxVal;
		
		// find the min. pixel value
		for (minVal = 0; minVal < K; minVal++) {
			if (h[minVal] > 0) {
				break;
			}
		}
		
		// find the max. pixel value
		for (maxVal = K - 1; maxVal >= 0; maxVal--) {
			if (h[maxVal] > 0) {
				break;
			}
		}
		
		int q = (minVal < maxVal) ? 
				(int) Math.floor((minVal + maxVal) / 2.0) : -1;
		
		return q;
	}
}

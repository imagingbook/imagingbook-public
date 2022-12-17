/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.threshold.global;

import ij.process.ByteProcessor;
import imagingbook.common.threshold.Thresholder;

/**
 * <p>
 * Common interface to be implemented by all global thresholders. See Sec. 9.1
 * of [1] for an overview.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/01
 */
public interface GlobalThresholder extends Thresholder {
	
	/**
	 * Returns a single (global) threshold value for the
	 * specified histogram.
	 * 
	 * @param h a histogram (array of frequencies)
	 * @return a single (global) threshold value
	 */
	public int getThreshold(int[] h);	// TODO: change return type to double?
	
	/**
	 * Returns a single (global) threshold value for the
	 * specified {@link ByteProcessor} (8-bit image).
	 * 
	 * @param bp a {@link ByteProcessor} (8-bit image)
	 * @return a single (global) threshold value
	 */
	public default int getThreshold(ByteProcessor bp) {
		int[] h = bp.getHistogram();
		return getThreshold(h);
	}
	
	@Override
	public default boolean threshold(ByteProcessor ip) {
		int q = getThreshold(ip);
		if (q > 0) {
			ip.threshold(q);
			return true;
		}
		else {
			return false;
		}
	}
	
}

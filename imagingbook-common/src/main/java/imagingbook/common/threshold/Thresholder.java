/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.threshold;

import ij.process.ByteProcessor;

/**
 * Common interface to be implemented by all thresholders (global and adaptive).
 * 
 * @author WB
 * @version 2022/08/02
 *
 */
public interface Thresholder {

	/**
	 * Enum type to discriminate if the image background is assumed to be bright or dark.
	 */
	public enum BackgroundMode {
		/** bright background */
		BRIGHT,
		/** dark background */
		DARK}

	/**
	 * Thresholds the specified {@link ByteProcessor} (8-bit image), which is modified. Does nothing and returns
	 * {@code true} if no valid threshold could be found (e.g., if all image pixels have the same value).
	 *
	 * @param ip a {@link ByteProcessor} (8-bit image)
	 * @return {@code true} iff the operation was successful
	 */
	public boolean threshold(ByteProcessor ip);

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.threshold.adaptive;

import ij.process.ByteProcessor;
import imagingbook.common.threshold.Thresholder;

/**
 * <p>
 * Common interface to be implemented by all adaptive (i.e., non-global) thresholders.
 * See Sec. 9.2 of [1] for an overview.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/08/01
 */
public interface AdaptiveThresholder extends Thresholder {
	
	/**
	 * Calculates a adaptive "threshold surface" for the specified
	 * {@link ByteProcessor} and returns it as another {@link ByteProcessor}.
	 * 
	 * @param bp the input image
	 * @return the threshold surface
	 */
	public ByteProcessor getThreshold(ByteProcessor bp);
	
	/**
	 * Thresholds a  {@link ByteProcessor} image by the specified
	 * threshold surface.
	 * 
	 * @param bp the input image (gets modified)
	 * @param Q the threshold surface
	 */
	public default void threshold(ByteProcessor bp, ByteProcessor Q) {
		final int w = bp.getWidth();
		final int h = bp.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int p = bp.get(u, v);
				int q = Q.get(u, v);
				bp.set(u, v, (p <= q) ? 0 : 255);
			}
		}
	}
	
	@Override
	public default boolean threshold(ByteProcessor ip) {
		ByteProcessor Q = this.getThreshold(ip);
		if (Q != null) {
			this.threshold(ip, Q);
			return true;
		}
		else {
			return false;
		}
	}

}

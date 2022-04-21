/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.threshold.global;

import ij.process.ByteProcessor;
import imagingbook.common.threshold.Thresholder;

/**
 * TODO: convert to interface.
 * @author WB
 * @version 2022/04/02
 *
 */
public abstract class GlobalThresholder extends Thresholder {
	
	// must be implemented by concrete subclasses
	protected abstract int getThreshold(int[] h);
	
	public int getThreshold(ByteProcessor bp) {
		int[] h = bp.getHistogram();
		return getThreshold(h);
	}
	
	public boolean threshold(ByteProcessor ip) {
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

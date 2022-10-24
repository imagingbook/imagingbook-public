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
 * This is a special case of a {@link QuantileThresholder} with quantile b = 0.5.
 * See Sec. 9.1 (Alg. 9.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 
 * @author WB
 * @version 2022/08/01
 * 
 */
public class MedianThresholder extends QuantileThresholder {
	
	/**
	 * Constructor, effectively creates a {@link QuantileThresholder} with quantile b = 0.5.
	 */
	public MedianThresholder() {
		super(0.5);
	}

}

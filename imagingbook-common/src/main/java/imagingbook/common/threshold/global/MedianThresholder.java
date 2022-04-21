/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.threshold.global;

/**
 * This is only a special case of a QuantileThresholder with b = 0.5.
 * 
 * @author WB
 * @version 2022/04/02
 * 
 */
public class MedianThresholder extends QuantileThresholder {
	
	// b is fixed at 0.5 and cannot be set, so we do not 
	// provide a constructor with this parameter
	public MedianThresholder() {
		super(0.5);
	}

}

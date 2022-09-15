/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.linear;

import imagingbook.common.image.OutOfBoundsStrategy;

/**
 * This class implements a 2D Gaussian filter by extending
 * {@link LinearFilter}.
 * This version uses a non-separated 2D convolution kernel.
 * 
 * @author WB
 * @version 2020/12/29
 * 
 * @see LinearFilter
 * @see GaussianFilterSeparable
 */
public class GaussianFilter extends LinearFilter {
	
	public static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	/**
	 * Constructor.
	 * @param sigma the width of the 2D Gaussian in x- and y-direction
	 */
	public GaussianFilter(double sigma) {
		super(new GaussianKernel2D(sigma));
	}
	
	/**
	 * Constructor.
	 * @param sigmaX the width of the 2D Gaussian in x-direction
	 * @param sigmaY the width of the 2D Gaussian in y-direction
	 */
	public GaussianFilter(double sigmaX, double sigmaY) {
		super(new GaussianKernel2D(sigmaX, sigmaY));
	}
}

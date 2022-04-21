/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.filter.linear;

import imagingbook.common.math.Arithmetic;

/**
 * This class implements a separable 2D Gaussian filter by extending
 * {@link LinearFilterSeparable}.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianFilterSeparable extends LinearFilterSeparable {
	
	/**
	 * Constructor.
	 * @param sigma the width of the 2D Gaussian in x- and y-direction
	 */
	public GaussianFilterSeparable(double sigma) {
		super(new GaussianKernel1D(sigma));
	}
	
	/**
	 * Constructor.
	 * Passes {@code null} as kernel to super constructor ({@link LinearFilterSeparable}) if any
	 * sigma &le; 0 or NaN, i.e., the corresponding x- or y-pass is not performed
	 * 
	 * @param sigmaX the width of the 2D Gaussian in x-direction
	 * @param sigmaY the width of the 2D Gaussian in y-direction
	 */
	public GaussianFilterSeparable(double sigmaX, double sigmaY) {
		super(makeKernel(sigmaX), makeKernel(sigmaY));
	}
	
	private static GaussianKernel1D makeKernel(double sigma) {
		if (Double.isFinite(sigma) && sigma > Arithmetic.EPSILON_DOUBLE) {
			return new GaussianKernel1D(sigma);
		}
		else {
			return null;
		}
	}
	
}

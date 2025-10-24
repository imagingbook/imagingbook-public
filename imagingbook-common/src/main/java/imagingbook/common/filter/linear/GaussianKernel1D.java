/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.linear;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * This class represents a 2D filter kernel.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianKernel1D extends Kernel1D {
	
	public static final double SIZE_FACTOR = 3.5;	
	
	public GaussianKernel1D(double sigma) {
		super(makeGaussKernel1D(sigma));
	}

	// ----------------------------------------------------------------

	/**
	 * Creates and returns a 1D Gaussian filter kernel large enough to avoid truncation effects. The length of the
	 * resulting array is odd. The returned kernel is normalized.
	 *
	 * @param sigma the width (standard deviation) of the Gaussian
	 * @return the Gaussian filter kernel
	 */
	public static float[] makeGaussKernel1D(double sigma) {
		return makeGaussKernel1D(sigma, true);
	}

	/**
	 * Creates and returns a 1D Gaussian filter kernel large enough to avoid truncation effects. The length of the
	 * resulting array is odd. The returned kernel is optionally normalized.
	 *
	 * @param sigma the width (standard deviation) of the Gaussian
	 * @param normalize set true to normalize the kernel
	 * @return the Gaussian filter kernel
	 */
	public static float[] makeGaussKernel1D(double sigma, boolean normalize) {
		if (sigma < 0) {
			throw new IllegalArgumentException("positive sigma required for Gaussian kernel");
		}
		final int rad = (int) Math.ceil(SIZE_FACTOR * sigma);
		final int size = rad + rad + 1;
		final float[] kernel = new float[size]; // odd size, center cell = kernel[rad]
		final double sigma2 = sqr(sigma);
		
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] = (float) Math.exp(-0.5 * sqr(rad - i) / sigma2);
		}
		
		return (normalize) ? normalize(kernel) : kernel;
	}
	
}

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
 * @version 2025/12/27
 */
public class GaussianKernel1D extends Kernel1D {
	
	public static final double DEFAULT_SIZE_FACTOR = 3.5;
	
	public GaussianKernel1D(double sigma) {
		super(makeGaussKernel1D(sigma));
	}

	// ----------------------------------------------------------------

	/**
	 * Creates and returns a 1D Gaussian filter kernel large enough to avoid truncation effects. The length of the
	 * resulting array is odd. The returned kernel is normalized.
	 *
	 * @param sigma the width (standard deviation) of the Gaussian (min. 0.3)
	 * @return the Gaussian filter kernel
	 */
	public static float[] makeGaussKernel1D(double sigma) {
		return makeGaussKernel1D(sigma, true, DEFAULT_SIZE_FACTOR);
	}

	/**
	 * Creates and returns a 1D Gaussian filter kernel large enough to avoid truncation effects. The length of the
	 * resulting array is odd. The returned kernel is optionally normalized.
	 *
	 * @param sigma the width (standard deviation) of the Gaussian (min. 0.3)
	 * @param normalize set true to normalize the kernel
	 * @return the Gaussian filter kernel
	 */
	public static float[] makeGaussKernel1D(double sigma, boolean normalize) {
		return makeGaussKernel1D(sigma, normalize, DEFAULT_SIZE_FACTOR);
	}

	/**
	 * Creates and returns a 1D Gaussian filter kernel large enough to avoid truncation effects. The length of the
	 * resulting array is odd. The returned kernel is optionally normalized.
	 * @param sigma the width (standard deviation) of the Gaussian (min. 0.3)
	 * @param normalize set true to normalize the kernel
	 * @param sizeFactor kernel size relative to {@code sigma}, min 1, see {@link #DEFAULT_SIZE_FACTOR})
	 * @return the Gaussian filter kernel
	 */
	public static float[] makeGaussKernel1D(double sigma, boolean normalize, double sizeFactor) {
		if (sigma < 0.3) {
			throw new IllegalArgumentException("sigma must be > 0.3");
		}
		if (sizeFactor < 1) {
			throw new IllegalArgumentException("sizeFactor > 1 required for Gaussian kernel");
		}
		final int rad = (int) Math.ceil(sizeFactor * sigma);
		final int size = rad + rad + 1;
		final float[] kernel = new float[size]; // odd size, center cell = kernel[rad]
		final double sigma2 = sqr(sigma);
		
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] = (float) Math.exp(-0.5 * sqr(rad - i) / sigma2);
		}

		return (normalize) ? normalize(kernel) : kernel;
	}
	
}

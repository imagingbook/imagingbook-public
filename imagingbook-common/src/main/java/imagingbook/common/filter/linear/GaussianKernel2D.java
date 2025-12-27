/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.linear;

import static imagingbook.common.filter.linear.GaussianKernel1D.DEFAULT_SIZE_FACTOR;
import static imagingbook.common.math.Arithmetic.sqr;

/**
 * This class represents a 2D filter kernel.
 * 
 * @author WB
 * @version 2025/12/27
 */
public class GaussianKernel2D extends Kernel2D {
	
	/**
	 * Constructor with individual sigmas for x/y.
	 * @param sigmaX sigma for X
	 * @param sigmaY sigma for Y
	 */
	public GaussianKernel2D(double sigmaX, double sigmaY) {
		super(makeGaussKernel2D(sigmaX, sigmaY));
	}
	
	/**
	 * Constructor with common sigma for x/y.
	 * @param sigma common sigma for X/Y
	 */
	public GaussianKernel2D(double sigma) {
		super(makeGaussKernel2D(sigma, sigma));
	}

	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough to avoid truncation effects. The associated array is
	 * odd-sized in both dimensions. The returned kernel is normalized.
	 *
	 * @param sigmaX the width (standard deviation) of the Gaussian in x-direction (min. 0.3)
	 * @param sigmaY the width (standard deviation) of the Gaussian in y-direction (min. 0.3)
	 * @return the Gaussian filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigmaX, double sigmaY) {
		return makeGaussKernel2D(sigmaX, sigmaY, true, DEFAULT_SIZE_FACTOR);
	}

	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough to avoid truncation effects. The associated array is
	 * odd-sized in both dimensions. The returned kernel is optionally normalized.
	 * @param sigmaX the width (standard deviation) of the Gaussian in x-direction (min. 0.3)
	 * @param sigmaY the width (standard deviation) of the Gaussian in y-direction (min. 0.3)
	 * @param normalize set true to normalize the kernel
	 * @return the Gaussian filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigmaX, double sigmaY, boolean normalize) {
		return makeGaussKernel2D(sigmaX, sigmaY, normalize, DEFAULT_SIZE_FACTOR);
	}

	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough to avoid truncation effects. The associated array is
	 * odd-sized in both dimensions. The returned kernel is optionally normalized.
	 * @param sigmaX the width (standard deviation) of the Gaussian in x-direction (min. 0.3)
	 * @param sigmaY the width (standard deviation) of the Gaussian in y-direction (min. 0.3)
	 * @param normalize set true to normalize the kernel
	 * @param sizeFactor kernel size relative to {@code sigma}, min 1, see {@link GaussianKernel1D#DEFAULT_SIZE_FACTOR})
	 * @return the Gaussian filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigmaX, double sigmaY, boolean normalize, double sizeFactor) {
		if (sigmaX < 0.3 || sigmaY < 0.3) {
			throw new IllegalArgumentException("sigmaX and sigmaY must be > 0.3");
		}
		if (sizeFactor < 1) {
			throw new IllegalArgumentException("sizeFactor > 1 required for Gaussian kernel");
		}
		final int radX = (int) Math.ceil(sizeFactor * sigmaX);
		final int radY = (int) Math.ceil(sizeFactor * sigmaY);
		final int sizeX = radX + radX + 1;
		final int sizeY = radY + radY + 1;

		final float[][] kernel = new float[sizeX][sizeY]; //center cell = kernel[rad][rad]
		final double sigmaX2 = sqr(sigmaX);
		final double sigmaY2 = sqr(sigmaY);
		
		for (int i = 0; i < sizeY; i++) {
			final double  b = sqr(radY - i) / (2 * sigmaY2);
			for (int j = 0; j < sizeX; j++) {
				final double a = sqr(radX - j) / (2 * sigmaX2);
				kernel[i][j] = (float) Math.exp(-(a + b));
			}
		}
		return (normalize) ? normalize(kernel) : kernel;
	}
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ij;

import ij.plugin.filter.Convolver;
import ij.process.ImageProcessor;

/**
 * Utility methods for filtering images. 
 * None of the filter methods modifies the kernel, i.e., kernels are 
 * used as supplied and never normalized.
 * 
 * @author WB
 * @version 2020-02-19
 */
public abstract class Filter {

	/**
	 * Applies a one-dimensional convolution kernel to the given image,
	 * which is modified. The 1D kernel is applied in horizontal direction only.#
	 * The supplied filter kernel is not normalized.
	 * 
	 * @param fp the image to be filtered (modified)
	 * @param h the filter kernel
	 */
	public static void convolveX (ImageProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, h.length, 1);
	}

	/**
	 * Applies a one-dimensional convolution kernel to the given image,
	 * which is modified. The 1D kernel is applied in vertical direction only.
	 * The supplied filter kernel is not normalized.
	 * 
	 * @param fp the image to be filtered (modified)
	 * @param h the filter kernel
	 */
	public static void convolveY (ImageProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, 1, h.length);
	}

	/**
	 * Applies a one-dimensional convolution kernel to the given image,
	 * which is modified.
	 * The 1D kernel is applied twice, once in horizontal and once in
	 * vertical direction.
	 * The supplied filter kernel is not normalized.
	 * 
	 * @param fp the image to be filtered (modified)
	 * @param h the filter kernel
	 */
	public static void convolveXY (ImageProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, h.length, 1);
		conv.convolve(fp, h, 1, h.length);
	}

}

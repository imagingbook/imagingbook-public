/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.examples;

import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.generic.GenericFilterScalar;
import imagingbook.common.filter.linear.LinearFilter;
import imagingbook.common.filter.linear.LinearFilterSeparable;
import imagingbook.common.image.PixelPack.PixelSlice;
import imagingbook.common.math.Matrix;

/**
 * <p>
 * Example filter based on {@link GenericFilterScalar} performing
 * linear convolution with a custom 3x3 filter kernel.
 * This filter may be applied to any image. 
 * On vector-valued images (e.g., RGB images) the filter is applied
 * independently to each component.
 * </p>
 * <p>
 * Usage:
 * </p>
 * <pre>
 * ImageProcessor ip = ... 	// any image
 * GenericFilter filter = new ExampleFilter3x3Scalar();
 * filter.applyTo(ip);		// modifies ip
 * </pre>
 * 
 * @author WB
 * @see LinearFilter
 * @see LinearFilterSeparable
 * @see GenericFilter#applyTo(ij.process.ImageProcessor)
 * @see GenericFilter#applyTo(ij.process.ImageProcessor, imagingbook.common.image.access.OutOfBoundsStrategy)
 */
public class ExampleFilter3x3Scalar extends GenericFilterScalar {
	
	// custom convolution kernel
	private final static float[][] H = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	private final static int width = H[0].length;			// = 3
	private final static int height = H.length;				// = 3
	private final static int xc = width / 2;				// = 1
	private final static int yc = height / 2;				// = 1
	private final static float s = (float) Matrix.sum(H);	// = 16
	
	@Override
	protected float doPixel(PixelSlice slice, int u, int v) {	// return scalar value
		float sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				float p = slice.getVal(ui, vj);		// read scalar pixel value
				sum = sum + p * H[i][j];
			}
		}
		return sum / s;
	}

}

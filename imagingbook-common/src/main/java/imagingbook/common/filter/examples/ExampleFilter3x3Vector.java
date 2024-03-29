/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.examples;

import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.filter.linear.LinearFilter;
import imagingbook.common.filter.linear.LinearFilterSeparable;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.PixelPack;
import imagingbook.common.math.Matrix;

/**
 * <p>
 * Example filter based on {@link GenericFilterVector} performing linear convolution with a custom 3x3 filter kernel.
 * Typically used on RGB color images (with 3-element pixel vectors). Pixels in scalar-valued images are treated as
 * 1-element vectors.
 * </p>
 * <p>
 * Usage:
 * </p>
 * <pre>
 * ImageProcessor ip = ... 	// any image but typically a RGB color image
 * GenericFilter filter = new ExampleFilter3x3Vector();
 * filter.applyTo(ip);		// modifies ip
 * </pre>
 * <p>
 * Note: This is merely a linear filter with a custom kernel to demonstrate the concept of a generic filter. In practice
 * (depending on the type of kernel) this would be implemented as a sub-class of {@link LinearFilter} or
 * {@link LinearFilterSeparable}, which is much more efficient.
 * </p>
 *
 * @author WB
 * @see LinearFilter
 * @see LinearFilterSeparable
 * @see GenericFilter#applyTo(ij.process.ImageProcessor)
 * @see GenericFilter#applyTo(ij.process.ImageProcessor, OutOfBoundsStrategy)
 */
public class ExampleFilter3x3Vector extends GenericFilterVector {

	// custom (nonsymmetric) convolution kernel
	private final static float[][] H = {
			{1, 8, 7},
			{2, 9, 6},
			{3, 4, 5}};
	
	private final static int width = H[0].length;			// = 3
	private final static int height = H.length;				// = 3
	private final static int xc = width / 2;				// = 1
	private final static int yc = height / 2;				// = 1
	private final static float s = (float) Matrix.sum(H);
	
	@Override
	protected float[] doPixel(PixelPack pack, int u, int v) { // returns float[] pixel value
		int depth = pack.getDepth();
		float[] sum = new float[depth];
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				float[] p = pack.getPix(ui, vj);	// read float[] pixel value
				for (int k = 0; k < depth; k++) {
					sum[k] = sum[k] + (p[k] * H[i][j]) / s;
				}
			}
		}
		return sum;
	}

}

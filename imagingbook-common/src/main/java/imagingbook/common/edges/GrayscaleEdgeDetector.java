/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.edges;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.math.Matrix;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

/**
 * <p>
 * Simple grayscale edge detector for all types of images. Color images are converted to grayscale before edge
 * detection. See Sec. 5.3 of [1] for a detailed description.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 */
public class GrayscaleEdgeDetector implements EdgeDetector {

	private final int M;	// image width
	private final int N;	// image height
	private final FloatProcessor Emag;	// edge magnitude map
	private final FloatProcessor Eort;	// edge orientation map
	
	// Sobel-kernels for x/y-derivatives:
    private static final float[][] HxS = Matrix.multiply(1.0f/8, new float[][] {
			{-1, 0, 1},
		    {-2, 0, 2},
		    {-1, 0, 1}});

    private static final float[][] HyS = Matrix.multiply(1.0f/8, new float[][] {
			{-1, -2, -1},
			{ 0,  0,  0},
			{ 1,  2,  1}});

	public GrayscaleEdgeDetector(ImageProcessor ip) {
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		Emag = new FloatProcessor(M, N);
		Eort = new FloatProcessor(M, N);
		findEdges(ip);
	}

	private void findEdges(ImageProcessor ip) {
		FloatProcessor I = (ip instanceof ColorProcessor) ?
				IjUtils.toFloatProcessor((ColorProcessor) ip) :
				ip.convertToFloatProcessor();
		
	    FloatProcessor Ix = I;
	    FloatProcessor Iy = (FloatProcessor) Ix.duplicate();

		IjUtils.convolve(Ix, HxS);
		IjUtils.convolve(Iy, HyS);
		
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				// extract the gradients of the R, G, B channels:
				double dx = Ix.getf(u, v);	
				double dy = Iy.getf(u, v);
				// calculate local edge magnitude:
				Emag.setf(u, v, (float) sqrt(sqr(dx) + sqr(dy)));
				// calculate edge orientation for the maximum channel:
				Eort.setf(u, v, (float) atan2(dy, dx));
			}
		}
	}
	
	@Override
	public FloatProcessor getEdgeMagnitude() {
		return Emag;
	}

	@Override
	public FloatProcessor getEdgeOrientation() {
		return Eort;
	}
	
}

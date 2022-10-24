/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.edges;

import static imagingbook.common.math.Arithmetic.sqr;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.PixelPack;
import imagingbook.common.math.Matrix;
import imagingbook.common.util.ParameterBundle;

/**
 * <p>
 * Monochromatic edge detector, applicable to color images ({@link ColorProcessor}) only.
 * See Sec. 16.2 (Alg. 16.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Approach</em>, 
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2014/02/16
 * @version 2022/09/04 converted to implement interface
 * @version 2022/09/11 changed to 2D kernel arrays using IjUtils
 */
public class MonochromaticEdgeDetector implements EdgeDetector {
	
	/**
	 * Parameters for {@link MonochromaticEdgeDetector} (currently none defined).
	 */
	public static class Parameters implements ParameterBundle {
	}

	@SuppressWarnings("unused")
	private final Parameters params;
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
    
	public MonochromaticEdgeDetector(ColorProcessor cp) {
		this(cp, new Parameters());
	}
	
	public MonochromaticEdgeDetector(ColorProcessor cp, Parameters params) {
		this.params = params;
		this.M = cp.getWidth();
		this.N = cp.getHeight();
		this.Emag = new FloatProcessor(M, N);
		this.Eort = new FloatProcessor(M, N);
		findEdges(cp);
	}
	
	private void findEdges(ColorProcessor cp) {
		FloatProcessor[] I = new PixelPack(cp).getFloatProcessors();
	    FloatProcessor[] Ix = new FloatProcessor[3];
	    FloatProcessor[] Iy = new FloatProcessor[3];
	    
		for (int k = 0; k < 3; k++) {
			Ix[k] = I[k];
			Iy[k] = (FloatProcessor) Ix[k].duplicate();
			IjUtils.convolve(Ix[k], HxS);
			IjUtils.convolve(Iy[k], HyS);
		}

		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				// extract the gradients of the R, G, B channels:
				double rx = Ix[0].getf(u, v), ry = Iy[0].getf(u, v);
				double gx = Ix[1].getf(u, v), gy = Iy[1].getf(u, v);
				double bx = Ix[2].getf(u, v), by = Iy[2].getf(u, v);
				
				double er2 = sqr(rx) + sqr(ry);
				double eg2 = sqr(gx) + sqr(gy);
				double eb2 = sqr(bx) + sqr(by);
				
				// assign local edge magnitude:
				Emag.setf(u, v, (float) Math.sqrt(er2 + eg2 + eb2));
				
				// find the maximum gradient channel:
				double e2max = er2, cx = rx, cy = ry;	// assume red is the max channel			
				if (eg2 > e2max) {
					e2max = eg2; cx = gx; cy = gy;		// green is the max channel
				}
				if (eb2 > e2max) {
					e2max = eb2; cx = bx; cy = by;		// blue is the max channel
				}
				
				// calculate edge orientation angle for the maximum channel:
				Eort.setf(u, v, (float) Math.atan2(cy, cx));
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

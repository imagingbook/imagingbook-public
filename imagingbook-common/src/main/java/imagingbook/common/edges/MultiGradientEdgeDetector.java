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
import static java.lang.Math.sqrt;

import ij.plugin.filter.Convolver;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.PixelPack;
import imagingbook.common.math.Matrix;
import imagingbook.common.util.ParameterBundle;

/**
 * <p>
 * Multi-Gradient ("DiZenzo/Cumani-style") color edge detector.
 * Applicable to color images ({@link ColorProcessor}) only.
 * See Sec. 16.2 (Alg. 16.2) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2013/05/30
 * @version 2022/09/07 implement interface, use PixelPack, renamed to MultiGradientEdgeDetector
 * @version 2022/09/11 convolutions implemented with IjUtils
 */
public class MultiGradientEdgeDetector implements EdgeDetector {
	
	/**
	 * Parameters for {@link MultiGradientEdgeDetector} (currently unused, no parameters to set).
	 */
	public static class Parameters implements ParameterBundle {
	}
	
	@SuppressWarnings("unused")
	private final Parameters params;
	private final int M;	// image width
	private final int N;	// image height
	private final FloatProcessor E_mag;	// edge magnitude map
	private final FloatProcessor E_ort;	// edge orientation map

	// Sobel-kernels for x/y-derivatives:
    private static final float[][] HxS = Matrix.multiply(1.0f/8, new float[][] {
			{-1, 0, 1},
		    {-2, 0, 2},
		    {-1, 0, 1}});

    private static final float[][] HyS = Matrix.multiply(1.0f/8, new float[][] {
			{-1, -2, -1},
			{ 0,  0,  0},
			{ 1,  2,  1}});
    
	public MultiGradientEdgeDetector(ColorProcessor cp) {
		this(cp, new Parameters());
	}
	
	public MultiGradientEdgeDetector(ColorProcessor cp, Parameters params) {
		this.params = params;
		this.M = cp.getWidth();
		this.N = cp.getHeight();
		this.E_mag = new FloatProcessor(M, N);
		this.E_ort = new FloatProcessor(M, N);
		findEdges(cp);
	}

	private void findEdges(ColorProcessor cp) {
		FloatProcessor[] I = new PixelPack(cp).getFloatProcessors();
		
		// calculate image derivatives in x/y for all color channels:
	    FloatProcessor[] Ix = new FloatProcessor[3];
	    FloatProcessor[] Iy = new FloatProcessor[3];
	    Convolver conv = new Convolver();
		conv.setNormalize(false);
		for (int k = 0; k < 3; k++) {
			Ix[k] = I[k];
			Iy[k] = (FloatProcessor) Ix[k].duplicate();
			IjUtils.convolve(Ix[k], HxS);
			IjUtils.convolve(Iy[k], HyS);
		}
		
		// calculate color edge magnitude and orientation:
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				double rx = Ix[0].getf(u, v), ry = Iy[0].getf(u, v);
				double gx = Ix[1].getf(u, v), gy = Iy[1].getf(u, v);
				double bx = Ix[2].getf(u, v), by = Iy[2].getf(u, v);
				
				double A = rx*rx + gx*gx + bx*bx;
				double B = ry*ry + gy*gy + by*by;
				double C = rx*ry + gx*gy + bx*by;
				
				double lambda0 = 0.5 * (A + B + sqrt(sqr(A - B) + 4 * sqr(C)));
				double theta0 =  0.5 * Math.atan2(2 * C, A - B);

				E_mag.setf(u, v, (float) sqrt(lambda0));
				E_ort.setf(u, v, (float) theta0);
			}
		}
	}
	
	@Override
	public FloatProcessor getEdgeMagnitude() {
		return E_mag;
	}

	@Override
	public FloatProcessor getEdgeOrientation() {
		return E_ort;
	}
	
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.filter.edgepreserving;

import imagingbook.common.filter.generic.GenericFilterScalar;
import imagingbook.common.filter.linear.GaussianKernel2D;
import imagingbook.common.image.PixelPack.PixelSlice;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * Scalar (non-separable) version of the Bilateral filter as proposed in [1]. On color images, this filter is applied
 * separately to each color component. The filter uses Gaussian domain and range kernels and can be applied to all image
 * types. See Sec. 17.2 of [2] for additional details.
 * </p>
 * <p>
 * [1] C. Tomasi and R. Manduchi, "Bilateral Filtering for Gray and Color Images", Proceedings of the 1998 IEEE
 * International Conference on Computer Vision, Bombay, India.
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2021/01/01
 */
public class BilateralFilterScalar extends GenericFilterScalar implements BilateralF {
	
	private final float[][] Hd;	// the domain kernel
	private final int K;		// the domain kernel size [-K,...,K]
	private final double sigmaR2;
	
	public BilateralFilterScalar() {
		this(new Parameters());
	}
	
	public BilateralFilterScalar(Parameters params) {
		GaussianKernel2D kernel = new GaussianKernel2D(params.sigmaD);
		this.Hd = kernel.getH();
		this.K = kernel.getXc();
		this.sigmaR2 = sqr(params.sigmaR);
	}
	
	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		float S = 0;			// sum of weighted pixel values
		float W = 0;			// sum of weights		
		final float a = plane.getVal(u, v); // value of the current center pixel
		for (int m = -K; m <= K; m++) {
			for (int n = -K; n <= K; n++) {
				float b = plane.getVal(u + m, v + n);
				float wd = Hd[m + K][n + K];		// domain weight
				float wr = gauss(a - b, sigmaR2);	// range weight
				float w = wd * wr;
				S = S + w * b;
				W = W + w;
			}
		}
		return S / W;
	}
	
	private float gauss(double x, double sigma2) {
		return (float) Math.exp(-(x * x) / (2 * sigma2));
	}

}

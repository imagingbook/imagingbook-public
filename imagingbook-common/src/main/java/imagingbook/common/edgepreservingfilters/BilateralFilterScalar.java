/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.edgepreservingfilters;

import static imagingbook.common.edgepreservingfilters.BilateralF.gauss;
import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.filter.GenericFilterScalar;
import imagingbook.common.filter.linear.GaussianKernel2D;
import imagingbook.common.image.data.PixelPack.PixelSlice;

/**
 * Scalar version, applicable to all image types.
 * On color images, this filter is applied separately to each color component.
 * This class implements a bilateral filter as proposed in
 * C. Tomasi and R. Manduchi, "Bilateral Filtering for Gray and Color Images",
 * Proceedings of the 1998 IEEE International Conference on Computer Vision,
 * Bombay, India.
 * The filter uses Gaussian domain and range kernels and can be applied to all 
 * image types.
 * 
 * @author W. Burger
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

}

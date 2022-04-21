/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.edgepreservingfilters;

import static imagingbook.common.math.Arithmetic.sqr;

import ij.process.ColorProcessor;
import imagingbook.common.edgepreservingfilters.BilateralF.Parameters;
import imagingbook.common.filter.GenericFilterVector;
import imagingbook.common.filter.linear.GaussianKernel2D;
import imagingbook.common.image.data.PixelPack;
import imagingbook.common.math.VectorNorm;

/**
 * Vector version, for RGB images only (accepts {@link ColorProcessor} only).
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
public class BilateralFilterVector extends GenericFilterVector {
	
	private final float[][] Hd;	// the domain kernel
	private final int K;		// the domain kernel size [-K,...,K]
	private final double sigmaR2;
	private final VectorNorm colorNorm;
	private final double colorScale2;
	
	public BilateralFilterVector() {
		this(new Parameters());
	}
	
	public BilateralFilterVector(Parameters params) {
		GaussianKernel2D kernel = new GaussianKernel2D(params.sigmaD);
		this.Hd = kernel.getH();
		this.K = kernel.getXc();
		this.sigmaR2 = sqr(params.sigmaR);
		this.colorNorm = params.colorNormType.create();
		this.colorScale2 = sqr(colorNorm.getScale(3));
	}
	
	@Override
	protected float[] doPixel(PixelPack pack, int u, int v) {
		float[] S = new float[3]; 	// sum of weighted RGB values
		float W = 0;				// sum of weights
		float[] a = pack.getVec(u, v);			// value of the current center pixel
		
		for (int m = -K; m <= K; m++) {
			for (int n = -K; n <= K; n++) {
				float[] b = pack.getVec(u + m, v + n);
				float wd = Hd[m + K][n + K];				// domain weight
				float wr = similarityGauss(a, b);			// range weight
				float w = wd * wr;
				S[0] = S[0] + w * b[0];
				S[1] = S[1] + w * b[1];
				S[2] = S[2] + w * b[2];
				W = W + w;
			}
		}
		S[0] = S[0] / W;
		S[1] = S[1] / W;
		S[2] = S[2] / W;
 		return S;
 	}
	
	// ------------------------------------------------------
	
	// This returns the weights for a Gaussian range kernel (color vector version):
	private float similarityGauss(float[] A, float[] B) {
		double d2 = colorScale2 * colorNorm.distance2(A, B);
		return (float) Math.exp(-d2 / (2 * sigmaR2));
	}
}

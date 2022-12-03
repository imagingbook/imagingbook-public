/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.filter.edgepreserving;

import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.linear.GaussianFilterSeparable;
import imagingbook.common.filter.linear.LinearFilter;
import imagingbook.common.image.PixelPack;
import imagingbook.common.image.PixelPack.PixelSlice;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.eigen.Eigensolver2x2;

/**
 * <p>
 * This class implements the Anisotropic Diffusion filter described in [1].
 * It is based on an earlier C++ (CImg) implementation (pde_TschumperleDeriche2d.cpp) by the original
 * author, made available under the CeCILL v2.0 license 
 * (http://www.cecill.info/licences/Licence_CeCILL_V2-en.html).
 * See Sec. 17.3.5 of [2] for additional details.
 * </p>
 * <p>
 * [1] D. Tschumperle and R. Deriche, "Diffusion PDEs on vector-valued images", 
 * IEEE Signal Processing Magazine, vol. 19, no. 5, pp. 16-25 (Sep. 2002).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @version 2021/01/06 (complete rewrite from scratch)
 * @version 2022/09/09 added zero-gradient check
 */

public class TschumperleDericheFilter extends GenericFilter implements TschumperleDericheF {

	// ----------------------------------------------------------------------------------
	
	private final Parameters params;
	private int M;				// image width
	private int N;				// image height
	private int K;				// number of color channels (any, but typ. 1 or 3)
	
	// temporary data
	private PixelPack Dx, Dy;	// maps for x/y gradients
	private PixelPack G;		// structure matrix as (u,v) with 3 elements
	
	private GenericFilter filterDx, filterDy;
	private GenericFilter gradientBlurFilter;
	private GenericFilter structureBlurFilter;
	
	private int iterations;		// number of iterations
	private float alpha;
	private double a0, a1;
	private PixelPack source, target;
	
	
	public TschumperleDericheFilter() {
		this(new Parameters());
	}
	
	public TschumperleDericheFilter(Parameters params) {
		this.params = params;
	}
	
	@Override
	protected void initFilter(PixelPack source, PixelPack target) {	// called by {@link GenericFilter}
		this.source = source;
		this.target = target;
		
		this.M = source.getWidth(); 
		this.N = source.getHeight(); 
		this.K = source.getDepth();
		
		this.iterations = params.iterations;
		this.alpha = params.alpha0;
		this.a0 = params.a0;
		this.a1 = params.a1;
		
		this.Dx = new PixelPack(source, false);	// container for X/Y-derivatives
		this.Dy = new PixelPack(source, false);
		
		this.filterDx = new LinearFilter(Constants.kernelDx);
		this.filterDy = new LinearFilter(Constants.kernelDy);
		
		this.gradientBlurFilter = new GaussianFilterSeparable(params.sigmaD);	
		this.structureBlurFilter = new GaussianFilterSeparable(params.sigmaM);
		
		this.G = new PixelPack(M, N, 3, null);	// structure matrix as (u,v) with 3 elements	
	}
	
	// ----------------------------------------------------------------------------------
	
	@Override
	protected void runPass(PixelPack source, PixelPack target) {
		makeGradients();							// Step 1
		makeStructureMatrix();						// Step 2
		float maxVelocity = updateImage(); 			// Step 3
		alpha = (float) params.dt / maxVelocity;	// Step 4: re-adjust alpha
	}
	
	// ------------------------------------------------------------
	
	private void makeGradients() {
		// x-gradients:
		source.copyTo(Dx);
		filterDx.applyTo(Dx);
		gradientBlurFilter.applyTo(Dx);
		// y-gradients:
		source.copyTo(Dy);
		filterDy.applyTo(Dy);
		gradientBlurFilter.applyTo(Dy);
	}
	
	private void makeStructureMatrix() {	// make M
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				final float[] dx = Dx.getPix(u, v);
				final float[] dy = Dy.getPix(u, v);
				float g0 = 0; float g2 = 0; float g1 = 0;
				for (int k = 0; k < K; k++) {
					final float fx = dx[k];
					final float fy = dy[k];
					g0 += fx * fx;
					g1 += fy * fy;
					g2 += fx * fy;
				}
				G.setPix(u, v, g0, g2, g1);
			}
		}
		structureBlurFilter.applyTo(G);
	}

	private float updateImage() {
		float betaMax = Float.NEGATIVE_INFINITY;	// maximum velocity
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				// calculate the local geometry matrix A(u,v), which has only 3 distinct elements
				float[] A = getGeometryMatrix(u, v);
				if (A != null) {
					float[] p = source.getPix(u, v);
					for (int k = 0; k < K; k++) {
						float[] H = getHessianMatrix(k, u, v); // local Hessian for channel k at pos u,v (3 elements)
						float beta = A[0] * H[0] + 2 * A[1] * H[1] + A[2] * H[2]; // = trace (A*H)
						betaMax = Math.max(betaMax, Math.abs(beta)); // find max absolute velocity for time-step adaptation
						// update the image (result goes to target)
						p[k] = p[k] + alpha * beta;	// we use alpha from the previous pass!
					}
					target.setPix(u, v, p);
				}
			}
		}
		return betaMax;
	}
	
	private float[] getGeometryMatrix(int u, int v) {
		float[] Guv = G.getPix(u, v); // 3 elements of local geometry matrix (2x2)
		
		if (Matrix.isZero(Guv)) {
			return null;	// if Guv (gradients) are zero, the geometry matrix is undefined
		}
		
		// calculate the 2 eigenvalues lambda1, lambda2 and the greater eigenvector e1
		Eigensolver2x2 solver = new Eigensolver2x2(Guv[0], Guv[1], Guv[1], Guv[2]);
		if (solver.hasComplexEigenvalues()) {
			throw new RuntimeException("undefined eigenvalues in " + 
					this.getClass().getSimpleName());
		}
		double lambda0 = solver.getRealEigenvalue(0);
		double lambda1 = solver.getRealEigenvalue(1);
		double[] evec0 = solver.getEigenvector(0).toArray();
		
		Matrix.normalizeD(evec0);	//  normalize eigenvector to unit length;
		
		double arg = 1.0 + lambda0 + lambda1;	// 1 + lambda_1 + lambda_2
		float c0 = (float) Math.pow(arg, -a0);
		float c1 = (float) Math.pow(arg, -a1);
		
		// mount geometry matrix A:
		float x0 = (float) evec0[0];
		float y0 = (float) evec0[1];
		float xx0 = x0 * x0;
		float xy0 = x0 * y0;
		float yy0 = y0 * y0;
		
		float A0 = c0 * yy0 + c1 * xx0;
		float A1 = c0 * xx0 + c1 * yy0;
		float A2 = (c1 - c0)* xy0;
		return new float[] {A0, A2, A1};
	}

	// Calculate the Hessian matrix Hk for channel k at position (u,v)
	private float[] getHessianMatrix(int k, int u, int v) {
		float[] Hk = new float[3];
		PixelSlice Ik = source.getSlice(k);
		float icc = Ik.getVal(u, v);
		Hk[0] = Ik.getVal(u-1,v) + Ik.getVal(u+1,v) - 2 * icc;								// = H_xx(u,v)
		Hk[1] = 0.25f * (Ik.getVal(u-1,v-1) + Ik.getVal(u+1,v+1) - Ik.getVal(u-1,v+1) - Ik.getVal(u+1,v-1));	// = H_xy(u,v)
		Hk[2] = Ik.getVal(u,v+1) + Ik.getVal(u,v-1) - 2 * icc;								// = H_yy(u,v)
		return Hk;
	}
	
	// --------------------------------------------------------------------------
	
//	private void normalize(double[] vec) {
//		double norm = Matrix.normL2(vec);
//		if (norm > 1E-6) {
//			for (int i = 0; i < vec.length; i++) {
//				vec[i] = vec[i] / norm;
//			}
//		}
//	}
	
	// ----------------------------------------------------------------------
	
	@Override
	protected final int passesRequired() {
		return iterations;	// this filter needs T passes
	}
	
	@Override
	// cleanup temporary data (probably unnecessary)
	protected void closeFilter() {
		this.Dx = null;
		this.Dy = null;
		this.G = null;
	}

}


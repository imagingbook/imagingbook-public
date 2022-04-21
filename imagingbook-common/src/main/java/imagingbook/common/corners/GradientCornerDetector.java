/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.corners;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ij.plugin.filter.Convolver;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.corners.subpixel.SubpixelMaxInterpolator;
import imagingbook.common.corners.subpixel.SubpixelMaxInterpolator.Method;
import imagingbook.common.filter.linear.GaussianFilterSeparable;
import imagingbook.common.filter.linear.LinearFilterSeparable;
import imagingbook.common.image.ImageMath;
import imagingbook.common.util.ParameterBundle;

/**
 * Abstract super class for all corner detectors based on local 
 * structure tensor.
 * This is the superclass for various concrete implementations:
 * {@link HarrisCornerDetector},
 * {@link ShiTomasiCornerDetector},
 * {@link MopsCornerDetector}.
 * 
 * TODO: replace 'border' by ROI rectangle
 * 
 * @author W. Burger
 * @version 2022/03/30
 * 
 * @see HarrisCornerDetector
 * @see MopsCornerDetector
 * @see ShiTomasiCornerDetector
 */
public abstract class GradientCornerDetector {
	
	/** For testing/example calculations only! */
	public static boolean RETAIN_TEMPORARY_DATA = false;
	
	public static class Parameters implements ParameterBundle {
		@DialogLabel("Apply pre-filter")
		public boolean doPreFilter = true;
		
		@DialogLabel("Gaussian smoothing radius (\u03C3)")@DialogDigits(3)
		public double sigma = 1.275;
		
		@DialogLabel("Border margins")
		public int border = 20;
		
		@DialogLabel("Clean up corners")
		public boolean doCleanUp = true;
		
		@DialogLabel("Min. distance between final corners")@DialogDigits(1)
		public double dmin = 10;
		
		@DialogLabel("Subpixel localization method")
		public Method maxLocatorMethod = Method.None;
		
		@DialogLabel("Corner response threshold (th)")@DialogDigits(1)
		public double scoreThreshold = 100;
		
		@Override
		public boolean validate() {
			return sigma > 0 && border >= 0 && dmin >= 0 && scoreThreshold > 0;
		}
	}
	
	protected static final float UndefinedScoreValue = 0;	// to be returned when corner score is undefined
	
	//filter kernels (one-dim. part of separable 2D filters)
	private final static float[] hp = {2f/9, 5f/9, 2f/9};		// pre-smoothing filter kernel
	private final static float[] hd = {0.5f, 0, -0.5f};			// first-derivative kernel
//	private final static float[] hb = {1f/64, 6f/64, 15f/64, 20f/64, 15f/64, 6f/64, 1f/64};	// original gradient blur filter kernel
	
	protected final int M, N;
	protected final Parameters params;
	protected final FloatProcessor Q;
	
	private final SubpixelMaxInterpolator maxLocator;
//	private final List<Corner> corners;
	
	// retained mainly for debugging
	private FloatProcessor A = null;
	private FloatProcessor B = null;
	private FloatProcessor C = null;
	
	// ---------------------------------------------------------------------------
	
	protected GradientCornerDetector(ImageProcessor ip, Parameters params) {
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		this.params = params;
		this.maxLocator = (params.maxLocatorMethod == Method.None) ? null : 
			SubpixelMaxInterpolator.getInstance(params.maxLocatorMethod);
		this.Q = makeCornerScores(ip);
//		this.corners = makeCorners();
	}
	
	/**
	 * Calculates the corner response score for a single image position (u,v)
	 * from the elements A, B, C of the local structure matrix.
	 * The method should normalize the score value, such that 100
	 * is returned for the default threshold value.
	 * To be implemented by concrete sub-classes.
	 * 
	 * @see HarrisCornerDetector
	 * @see ShiTomasiCornerDetector
	 * @param A = Ixx(u,v)
	 * @param B = Iyy(u,v)
	 * @param C = Ixy(u,v)
	 * @return the corner score
	 */
	protected abstract float getCornerScore(float A, float B, float C);
	
	// -------------------------------------------------------------
	
	private FloatProcessor makeCornerScores(ImageProcessor I) {
		FloatProcessor Ix = I.convertToFloatProcessor(); 
		FloatProcessor Iy = I.convertToFloatProcessor();
		
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		
		// nothing really but a Sobel-type gradient:
		if (params.doPreFilter) {
//			Filter.convolveY(Ix, hp);			// pre-filter Ix vertically
//			Filter.convolveX(Iy, hp);			// pre-filter Iy horizontally
			conv.convolve(Ix, hp, 1, hp.length);	// pre-filter Ix vertically
			conv.convolve(Iy, hp, hp.length, 1);	// pre-filter Iy horizontally

		}
		
//		Filter.convolveX(Ix, hd);				// get first derivative in x
//		Filter.convolveY(Iy, hd);				// get first derivative in y
		conv.convolve(Ix, hd, hd.length, 1);	// get first derivative in x
		conv.convolve(Iy, hd, 1, hd.length);	// get first derivative in y
		
		// gradient products:
		A = ImageMath.sqr(Ix);				// A(u,v) = Ixx(u,v) = (Ix(u,v))^2
		B = ImageMath.sqr(Iy);				// B(u,v) = Iyy(u,v) = (Iy(u,v))^2
		C = ImageMath.mult(Ix, Iy);			// C(u,v) = Ixy(u,v) = Ix(u,v)*Iy(u,v)
		
		// blur the gradient components with a small Gaussian:
		LinearFilterSeparable gf = new GaussianFilterSeparable(params.sigma);
		gf.applyTo(A);
		gf.applyTo(B);
		gf.applyTo(C);
		
		FloatProcessor Q = new FloatProcessor(M, N);
		
		final float[] pA = (float[]) A.getPixels();
		final float[] pB = (float[]) B.getPixels();
		final float[] pC = (float[]) C.getPixels();
		final float[] pQ = (float[]) Q.getPixels();
		
		for (int i = 0; i < M * N; i++) {
			pQ[i] = getCornerScore(pA[i], pB[i], pC[i]);
		}
		
		return Q;
	}
	

	/**
	 * Returns the corner score function as a {@link FloatProcessor} object.
	 * @return the score function
	 */
	public FloatProcessor getQ() {
		return this.Q;
	}
	
	public FloatProcessor getA() {
		return this.A;
	}
	
	public FloatProcessor getB() {
		return this.B;
	}
	
	public FloatProcessor getC() {
		return this.C;
	}
	
	public List<Corner> getCorners() {
		List<Corner> cl = collectCorners(params.scoreThreshold, params.border);
		if (params.doCleanUp) {
			cl = cleanupCorners(cl);
		}
		return cl;
	}
	
	// ----------------------------------------------------------
	
	/*
	 * Returned samples are arranged as follows:
	 * 	s4 s3 s2
	 *  s5 s0 s1
	 *  s6 s7 s8
	 */
	private float[] getNeighborhood(FloatProcessor Q, int u, int v) {
		if (u <= 0 || u >= M - 1 || v <= 0 || v >= N - 1) {
			return null;
		} 
		else {
			final float[] q = (float[]) Q.getPixels();
			float[] s = new float[9];
			final int i0 = (v - 1) * M + u;
			final int i1 = v * M + u;
			final int i2 = (v + 1) * M + u;
			s[0] = q[i1];
			s[1] = q[i1 + 1];
			s[2] = q[i0 + 1];
			s[3] = q[i0];
			s[4] = q[i0 - 1];
			s[5] = q[i1 - 1];
			s[6] = q[i2 - 1];
			s[7] = q[i2];
			s[8] = q[i2 + 1];
			return s;
		}
	}
	
	private boolean isLocalMax(float[] s) {
		if (s == null) {
			return false;
		}
		else {
			final float s0 = s[0];
			return	// check 8 neighbors of q0
					s0 > s[4] && s0 > s[3] && s0 > s[2] &&
					s0 > s[5] &&              s0 > s[1] && 
					s0 > s[6] && s0 > s[7] && s0 > s[8] ;
		}
	}
	
	private List<Corner> collectCorners(double scoreThreshold, int borderWidth) {
		float th = (float) scoreThreshold; //params.scoreThreshold;
		//int border = params.border;
		List<Corner> C = new ArrayList<>();
		for (int v = borderWidth; v < N - borderWidth; v++) {
			for (int u = borderWidth; u < M - borderWidth; u++) {
				float[] qn = getNeighborhood(Q, u, v);
				if (qn != null && qn[0] > th && isLocalMax(qn)) {
					Corner c = makeCorner(u, v, qn);
					if (c != null) {
						C.add(c);
					}
				}
			}
		}
		return C;
	}
	
	private List<Corner> cleanupCorners(List<Corner> C) {
		final double dmin2 = sqr(params.dmin);
		// sort corners by descending q-value:
		Collections.sort(C);
		// we use an array of corners for efficiency reasons:
		Corner[] Ca = C.toArray(new Corner[C.size()]);
		List<Corner> Cclean = new ArrayList<>(C.size());
		for (int i = 0; i < Ca.length; i++) {
			Corner c0 = Ca[i];		// get next strongest corner
			if (c0 != null) {
				Cclean.add(c0);
				// delete all remaining corners cj too close to c0:
				for (int j = i + 1; j < Ca.length; j++) {
					Corner cj = Ca[j];
					if (cj != null && c0.distanceSq(cj) < dmin2)
						Ca[j] = null;   //delete corner cj from C
				}
			}
		}
		return Cclean;
	}
	
	/**
	 * Creates a new {@link Corner} instance. Performs sub-pixel
	 * position refinement if a {@link #maxLocator} is defined.
	 * @param u the corner's horizontal position (int)
	 * @param v the corner's vertical position (int)
	 * @param qn the 9 corner scores in the 3x3 neighborhood
	 * @return a new {@link Corner} instance
	 */
	private Corner makeCorner(int u, int v, float[] qn) {
		if (maxLocator == null) {
			// no sub-pixel refinement, use original integer coordinates
			return new Corner(u, v, qn[0]);
		}
		else {
			// do sub-pixel refinement
			float[] xyz = maxLocator.getMax(qn);
			return (xyz == null) ? null : new Corner(u + xyz[0], v + xyz[1], xyz[2]);
		}
	}
	
	
}

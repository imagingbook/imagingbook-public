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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.linear.GaussianKernel1D;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.PixelPack;
import imagingbook.common.util.parameters.DialogParameterBundle;

/**
 * <p>
 * This class implements a Canny edge detector for grayscale and RGB images.
 * See Sec. 16.3 (Alg. 16.3) of [1] for more detail.
 * The edge detector is "lazy" in the sense that it performs local non-
 * maximum suppression and edge tracing only when the results are explicitly 
 * queried (by the methods {@link #getEdgeBinary()} and {@link #getEdgeTraces()}).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2021/11/26
 * @version 2022/03/22 added parameter annotations for dialogs
 * @version 2022/09/04 converted to implement interface, use {@link PixelPack}
 */
public class CannyEdgeDetector implements EdgeDetector {
	
	// TODO: implement convolutions with GenericFilter
	
	public static class Parameters implements DialogParameterBundle {
		
		/** Gaussian sigma (scale, default = 2) */
		@DialogLabel("Gaussian sigma (scale)")@DialogDigits(1)
		public double gSigma = 2.0f;
		
		/** High threshold (defaults to 20% of maximum edge magnitude) */
		@DialogLabel("High threshold (% of max. edge magnitude)")@DialogDigits(1)
		public double hiThr  = 20.0f;
		
		/** Low threshold (defaults to 5% of maximum edge magnitude) */
		@DialogLabel("Low threshold (% of max. edge magnitude)")@DialogDigits(1)
		public double loThr = 5.0f;
		
		/** Set {@code true} to normalize gradient magnitude */
		@DialogLabel("normalize gradient magnitude")
		public boolean normGradMag = true;
		
		@Override
		public boolean validate () { 
			return gSigma >= 0.1f && loThr < hiThr;
		}
	}
	
	private static final float CosPi8 = (float) Math.cos(Math.PI/8);
	private static final float SinPi8 = (float) Math.sin(Math.PI/8);
	
	private final Parameters params;
	private final int M, N;							// width and height of I
	
	private FloatProcessor Emag = null;				// gradient magnitude
	private FloatProcessor Enms = null;				// non-max suppressed gradient magnitude
	private FloatProcessor Ex = null, Ey = null;	// edge normal vectors
	private ByteProcessor  Ebin = null;				// final (binary) edge map
	private List<EdgeTrace> edgeTraces = null;		// list of edge traces
	
	
	/**
	 * Constructor using default parameters.
	 * @param ip the image to be processed
	 */
	public CannyEdgeDetector(ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	/**
	 * Constructor using specific parameters.
	 * @param ip the image to be processed
	 * @param params a {@link CannyEdgeDetector.Parameters} object
	 */
	public CannyEdgeDetector(ImageProcessor ip, Parameters params) {
		if (!params.validate()) throw new IllegalArgumentException();
		this.params = params;
		M = ip.getWidth();
		N = ip.getHeight();
		makeGradientsAndMagnitude(ip);
	}

	//---------------------------------------------------------------------------
	
	// do the inital work only
	private void makeGradientsAndMagnitude(ImageProcessor I) {
		if (I instanceof ColorProcessor) 
			makeGradientsAndMagnitudeColor((ColorProcessor) I);
		else
			makeGradientsAndMagnitudeGray(I);
		// this is done "on demand":
		//nonMaxSuppression();
		//detectAndTraceEdges();
	}

	private void makeGradientsAndMagnitudeGray(ImageProcessor ip) {		
		FloatProcessor fp = ip.convertToFloatProcessor();	// always makes a copy
		// pre-smooth the image with a separable Gaussian filter
		float[] gaussKernel = GaussianKernel1D.makeGaussKernel1D(params.gSigma, true);
		IjUtils.convolveX(fp, gaussKernel);
		IjUtils.convolveY(fp, gaussKernel);
		
		// calculate the gradients in X- and Y-direction
		Ex = fp;
		Ey = (FloatProcessor) fp.duplicate();
		
		float[] gradKernel = {-0.5f, 0, 0.5f};
		IjUtils.convolveX(Ex, gradKernel);
		IjUtils.convolveY(Ey, gradKernel);
		
		Emag = new FloatProcessor(M, N);
		float emax = 0;
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				double dx = Ex.getf(u,v);
				double dy = Ey.getf(u,v);
				float mag = (float) Math.hypot(dx, dy);
				if (mag > emax) 
					emax = mag;
				Emag.setf(u, v, mag);
			}
		}
		
		// normalize gradient magnitude (to max. value 100):
		if (params.normGradMag && emax > 0.001f) {
			Emag.multiply(100.0/emax);
		}
	}
	
	private void makeGradientsAndMagnitudeColor(ColorProcessor cp) {
		FloatProcessor[] I = new PixelPack(cp).getFloatProcessors();	
		// pre-smooth the image with a separable Gaussian filter (on each RGB channel):
		float[] gaussKernel = GaussianKernel1D.makeGaussKernel1D(params.gSigma, true);
		for (int k = 0; k < I.length; k++) {
			IjUtils.convolveX(I[k], gaussKernel);
			IjUtils.convolveY(I[k], gaussKernel);
		}
		
		// calculate the gradients in X- and Y-direction for each RGB channel:
		FloatProcessor[] Ix = new FloatProcessor[3];
		FloatProcessor[] Iy = new FloatProcessor[3];
		float[] gradKernel = {-0.5f, 0, 0.5f};
		for (int k = 0; k < I.length; k++) {
			Ix[k] = I[k];
			Iy[k] = (FloatProcessor) I[k].duplicate();
			IjUtils.convolveX(Ix[k], gradKernel);
			IjUtils.convolveY(Iy[k], gradKernel);
		}

		// calculate color gradient magnitude:
		Ex = new FloatProcessor(M, N);
		Ey = new FloatProcessor(M, N);
		Emag = new FloatProcessor(M, N);
		
		float emax = 0;
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				double rx = Ix[0].getf(u,v), ry = Iy[0].getf(u,v);
				double gx = Ix[1].getf(u,v), gy = Iy[1].getf(u,v);
				double bx = Ix[2].getf(u,v), by = Iy[2].getf(u,v);
				double A = rx*rx + gx*gx + bx*bx;
				double B = ry*ry + gy*gy + by*by;
				double C = rx*ry + gx*gy + bx*by;
				
//				Eigensolver2x2 es = new Eigensolver2x2(A, C, C, B);
//				if (!es.isReal()) {
//					throw new ArithmeticException(
//							String.format("No real eigenvalues for structure matrix at position (%d, %d)", u, v));
//				}
//				float mag = (float) Math.sqrt(es.getEigenvalue(0));
//				double[] eVec0 = es.getEigenvector(0);
//				Emag.setf(u, v, mag);
//				Ex.setf(u, v, (float) eVec0[0]);
//				Ey.setf(u, v, (float) eVec0[1]);
				
				double D = (float) Math.sqrt(sqr(A - B) + 4 * sqr(C));	
				double lambda0 = (A + B + D) / 2;						// eigenvalue lambda_0
				Emag.setf(u, v, (float) Math.sqrt(lambda0));
				Ex.setf(u, v, (float) (A - B + D));						// eigenvector x_0
				Ey.setf(u, v, (float) (2 * C));							// eigenvector y_0
			}
		}
		// normalize gradient magnitude 
		if (params.normGradMag && emax > 0.001) 
			Emag.multiply(100.0/emax);
	}
	
	//---------------------------------------------------------------------------
	
	// perform non-maximum suppression along gradient direction
	private void nonMaxSuppression() {
		Enms = new FloatProcessor(M, N);
		for (int v = 1; v < N - 1; v++) {
			for (int u = 1; u < M - 1; u++) {
				int s_theta = getOrientationSector(Ex.getf(u, v), Ey.getf(u, v));
				if (isLocalMaximum(Emag, u, v, s_theta, (float) params.loThr)) {
					Enms.setf(u, v, Emag.getf(u, v)); // keep local maximum only
				}
			}
		}
	}
	
	private void detectAndTraceEdges() {
		if (Enms == null) {
			nonMaxSuppression();
		}
		Ebin = new ByteProcessor(M, N);
		int color = 255;
		edgeTraces = new ArrayList<>();
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				if (Enms.getf(u, v) >= params.hiThr && Ebin.get(u, v) == 0) { // unmarked edge point
					List<PntInt> pnts = traceAndThreshold(u, v, (float) params.loThr, color);
					if (!pnts.isEmpty()) {
						edgeTraces.add(new EdgeTrace(pnts));
					}
				}
			}
		}
	}
	
	// Determines if the gradient magnitude is a local maximum at position (u,v)
	// in direction s_theta.
	private boolean isLocalMaximum(FloatProcessor gradMagnitude, int u, int v, int s_theta, float mMin) {
		float mC = gradMagnitude.getf(u, v);
		if (mC < mMin) {
			return false;
		}
		else {
			float mL = 0, mR = 0;
			switch (s_theta) {
			case 0 : 
				mL = gradMagnitude.getf(u-1, v);
				mR = gradMagnitude.getf(u+1, v);
				break;
			case 1 : 
				mL = gradMagnitude.getf(u-1, v-1);
				mR = gradMagnitude.getPixelValue(u+1, v+1);
				break;
			case 2 : 
				mL = gradMagnitude.getf(u, v-1);
				mR = gradMagnitude.getf(u, v+1);
				break;
			case 3 : 
				mL = gradMagnitude.getf(u-1, v+1);
				mR = gradMagnitude.getf(u+1, v-1);
				break;
			}
			return (mL <= mC && mC >= mR);
		}
	}
	
	/**
	 * Recursively collect and mark all pixels of an edge that are 8-connected to 
	 * (u0,v0) and have a gradient magnitude above loThr.
	 * @param u0 start coordinate (x)
	 * @param v0 start coordinate (y)
	 * @param loThr low threshold (min. edge magnitude to continue tracing)
	 * @param markVal value used for marking pixels on an edge trace
	 * @return a list of Point objects.
	 */
	private List<PntInt> traceAndThreshold(int u0, int v0, float loThr, int markVal) {
		//IJ.log("  working point " + u + " " + v);
		Stack<PntInt> pointStack = new Stack<>();
//		EdgeTrace trace = new EdgeTrace();
		List<PntInt> trace = new ArrayList<>();
		pointStack.push(PntInt.from(u0, v0));
		while (!pointStack.isEmpty()) {
			PntInt p = pointStack.pop();
			int up = p.x;
			int vp = p.y;
			Ebin.putPixel(up, vp, markVal);	// mark this edge point
			trace.add(p);
				
			int uL = Math.max(up - 1, 0); 		// (up > 0) ? up-1 : 0;
			int uR = Math.min(up + 1, M - 1); 	// (up < M-1) ? up+1 : M-1;
			int vT = Math.max(vp - 1, 0); 		// (vp > 0) ? vp - 1 : 0;
			int vB = Math.min(vp + 1, N - 1); 	// (vp < N-1) ? vp+1 : N-1;
			
			for (int u = uL; u <= uR; u++) {
				for (int v = vT; v <= vB; v++) {
					if (Ebin.get(u, v) == 0 && Enms.getf(u, v) >= loThr) { 
						pointStack.push(PntInt.from(u,v));
					}
				}
			}
		}
		return trace;
	}
	
	// returns the quantized orientation sector for vector (dx, dy)
	private int getOrientationSector(float dx, float dy) {
		// rotate the gradient vector by PI/8
		float dxR = CosPi8 * dx - SinPi8 * dy;
		float dyR = SinPi8 * dx + CosPi8 * dy;	
		// mirror vector (dxR,dyR) to [0,PI]
		if (dyR < 0) {
			dxR = -dxR;
			dyR = -dyR;
		}
		// determine the octant for (dx, dy)
		int s_theta;
		if (dxR >= 0) { // dxR >= 0, dyR >= 0
			if (dxR >= dyR)
				s_theta = 0; // return 0;
			else
				s_theta = 1; // return 1;
		}
		else {  // dxR < 0, dyR >= 0
			if (-dxR < dyR)
				s_theta = 2; // return 2;
			else
				s_theta = 3; //return 3;
		}
		return s_theta;
	}
	
	@Override
	public FloatProcessor getEdgeMagnitude() {
		return Emag;
	}

	@Override
	public FloatProcessor getEdgeOrientation() {
		FloatProcessor E_theta = new FloatProcessor(M, N);
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				double ex = Ex.getf(u, v);
				double ey = Ey.getf(u, v);
				float theta = (float) Math.atan2(ey, ex);
				E_theta.setf(u, v, theta);
			}
		}
		return E_theta;
	}
	
	/**
	 * Returns a binary edge image, obtained by rendering the
	 * detected edge traces.
	 * See also {@link #getEdgeTraces()}
	 * @return a binary image ({@link ByteProcessor})
	 */
	public ByteProcessor getEdgeBinary() {
		if (Ebin == null) {
			detectAndTraceEdges();
		}
		return Ebin;
	}
	
	/**
	 * Returns a list of detected {@link EdgeTrace} instances.
	 * @return a list of {@link EdgeTrace} instances
	 */
	public List<EdgeTrace> getEdgeTraces() {
		if (edgeTraces == null) {
			detectAndTraceEdges();
		}
		return edgeTraces;
	}

}

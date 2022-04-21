/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.lucaskanade;

import ij.IJ;
import ij.process.FloatProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.math.Matrix;


/**
 * Inverse Compositional Matcher, as described in Simon Baker and Iain Matthews, 
 * "Lucas-Kanade 20 Years On: A Unifying Framework: Part 1", CMU-RI-TR-02-16 (2002)
 *  @author Wilhelm Burger
 *  @version 2014/02/08
 */
public class LucasKanadeInverseMatcher extends LucasKanadeMatcher {
	
	private int n;					// number of warp parameters
	private float[][] Rx, Ry;		// gradient of reference image
	private double[][] Hi; 			// inverse of cumulated Hessian matrix
	private double[][][] S;			// S[u][u][n] = the steepest descent image for dimension n at pos. u,v (same size as R)

	private double qmag = Double.MAX_VALUE;		// magnitude of parameter difference vector
	private double sqrError = Double.MAX_VALUE;	// squared sum of differences between I and R
	
	/**
	 * Creates a new matcher of type {@link LucasKanadeInverseMatcher}.
	 * @param I the search image (of type {@link FloatProcessor}).
	 * @param R the reference image (of type {@link FloatProcessor})
	 * @param params a parameter object of type  {@link LucasKanadeMatcher.Parameters}.
	 */
	public LucasKanadeInverseMatcher(FloatProcessor I, FloatProcessor R, Parameters params) {
		super(I, R, params);
	}
	
	public LucasKanadeInverseMatcher(FloatProcessor I, FloatProcessor R) {
		super(I, R, new Parameters());
	}
	
	private void initializeMatch(ProjectiveMapping2D Tinit) {
//		n = Tinit.getParameters().length;	// number of transformation parameters
		n = getParameters(Tinit).length;	// number of transformation parameters
		S = new double[wR][hR][];			// S[u][v] holds a double vector of length n
		Rx = gradientX(R).getFloatArray();	// gradient of R
		Ry = gradientY(R).getFloatArray();
		double[][] H = new double[n][n]; 	// cumulated Hessian matrix of size n x n (initially zero)
		
		ProjectiveMapping2D Tp = Tinit;
		
		for (int u = 0; u < wR; u++) {			// for all coordinates (u,v) in R do
			for (int v = 0; v < hR; v++) {
//				double[] x = {u - xc, v - yc};	// position w.r.t. the center of R
				Pnt2d x = PntDouble.from(u - xc, v - yc);	// position w.r.t. the center of R
				double[] gradR = {Rx[u][v], Ry[u][v]};

				double[][] J = Tp.getJacobian(x);
				
				double[] sx = Matrix.multiply(gradR, J);		// column vector of length n (6)
				
				S[u][v] = sx;									// keep for use in main loop
				double[][] Hxy = Matrix.outerProduct(sx, sx);
				
				H = Matrix.add(H, Hxy);							// cumulate the Hessian
			}
		}
		
		Hi = Matrix.inverse(H);									// inverse of Hessian
		if (Hi == null) {
			IJ.log("singular Hessian!");
			throw new RuntimeException("could not invert Hessian");
		}
		
		iteration = 0;
		
		if (params.showSteepestDescentImages) 
			showSteepestDescentImages(S);
		if (params.showHessians) {
			IjUtils.createImage("H", H).show();
			IjUtils.createImage("Hi", Matrix.inverse(H)).show();
		}
	}
	
	@Override
	public ProjectiveMapping2D iterateOnce(ProjectiveMapping2D Tp) {
		if (iteration < 0) {
			initializeMatch(Tp);
		}
		iteration = iteration + 1;
		double[] dp = new double[n];	// n-dim vector \delta_p = 0
		sqrError = 0;
		
		// for all positions (u,v) in R do
		for (int u = 0; u < wR; u++) {
			for (int v = 0; v < hR; v++) {
				
				// get coordinate relative to center of R
				Pnt2d x = PntDouble.from(u - xc, v - yc);
				
				// warp I to I' (onto R)
				Pnt2d xT = Tp.applyTo(x);		// warp from x -> x'

				// calculate pixel difference d for pos. (u,v)
				double d = I.getInterpolatedValue(xT.getX(), xT.getY()) - R.getf(u, v);
				sqrError = sqrError + d * d;
				
				// multiply the pixel difference d with the corresponding steepest descent image sx
				// and sum into dp:
				double[] sx = S[u][v];
				dp = Matrix.add(dp, Matrix.multiply(d, sx));
			}
		}
		
		// estimate the parameter difference vector qopt:
		double[] qopt = Matrix.multiply(Hi, dp);
		if (params.debug) IJ.log("qopt  = " + Matrix.toString(qopt));
		
		// measure the magnitude of the difference vector:
		qmag = Matrix.normL2squared(qopt);
		
		// Calculate the warp parameters p', such that T_p'(x) = T_p (T^-1_q (x), for any point x.
		ProjectiveMapping2D Tqopt = toProjectiveMap(qopt);
		ProjectiveMapping2D Tqopti = Tqopt.getInverse();
		return Tqopti.concat(Tp);
	}
	
	@Override
	public boolean hasConverged() {
		return (qmag < params.tolerance);
	}
	
	@Override
	public double getRmsError() {
		return Math.sqrt(sqrError);
	}
	
}

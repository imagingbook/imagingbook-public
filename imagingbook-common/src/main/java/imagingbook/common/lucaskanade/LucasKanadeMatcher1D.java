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
import imagingbook.common.math.Matrix;


public class LucasKanadeMatcher1D {

	
	final double[] I;
	final double[] R;
	final int M, N;
	final double xc;
	final double[] gI;
	
	int maxIterations = 100;
	
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	double tolerance = 0.00001;
	
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	private int n;			// number of warp parameters
	private double qmag; 	// magnitude of parameter difference vector
	private double sqrError;	// squared sum of differences between I and R
	
	private int margin = 0; // left and right margins to be ignored for matching
	
	private int iteration;

	
	public LucasKanadeMatcher1D(double[] I, double[] R) {
		this.I = I;
		this.R = R;
		this.M = I.length;
		this.N = R.length;
		xc = 0.5 * M;
		this.gI = makeGradient(I);
	}
	
	public double[] getBestMatch(double[] pInit) {
		double[] p = pInit.clone();
		n = p.length;
		iteration = 0;
		qmag = Double.MAX_VALUE;
		do {
			p = iterateOnce(p);	
		} while (p != null && qmag > tolerance && iteration < maxIterations);
		return p;
	}
	
	
	private double[] iterateOnce(double[] p) {
		iteration = iteration + 1;
		
		double[][] H = new double[n][n];	// n x n cumulated Hessian matrix, initialized to 0
		double[] dp = new double[n];		// n-dim vector \delta_p = 0
		sqrError = 0;

		// for all positions u in R do
		for (int u = margin; u < N - margin; u++) {
			double x = u - xc;	// position w.r.t. the center of R
			double xT = T(p, x);		// warp x -> x'
			double g = interpolate(gI, xT + xc);	// interpolated gradient in x-direction
			double[] G = {g};				// degenerate (1D) gradient vector
			
			// Step 4: Evaluate the Jacobian of the warp T at position x
			double[][] J = getJacobianT(p, x); // a (1 x 2) matrix
			
			// Step 5: compute the steepest descent image:
			double[] sx = Matrix.multiply(G, J); // a 2-dim vector
			
			// Step 6: Update the cumulative Hessian matrix
			double[][] Hx = Matrix.outerProduct(sx, sx);
			H = Matrix.add(H, Hx);
			
			// Step 7: Calculate the error
			double d = R[u] - interpolate(I, xT + xc);
			sqrError = sqrError + d * d;
			
			
			// Step 8: update the column vector dp:
			dp = Matrix.add(dp, Matrix.multiply(d, sx));
		}
		
		IJ.log(String.format(" iteration=%d  s=%.4f t=%.4f  sqrError=%.3f", iteration, p[0]+1, p[1], sqrError));
		// Step 9: calculate the optimal parameter change:
		
		double[] qopt = Matrix.solve(H, dp);
		if (qopt == null) {
			// IJ.log("singular Hessian!");
			return null;
		}
		
		qmag = Matrix.normL2squared(qopt);
		
		// Step 10: Update and return the warp parameters p + qopt:
		return Matrix.add(p, qopt);
	}

	private double[] makeGradient(double[] I) {
		int M = I.length;	// I is assumed 0 outside
		double[] G = new double[M];
		G[0] = 0.5 * I[1];
		for (int i = 1; i < M - 1; i++) {
			G[i] = 0.5 * (I[i+1] - I[i-1]);
		}
		G[M-1] = 0.5 * -I[M-2];
		return G;
	}
	
	private double T(double[] p, double u) {
		final double s = p[0] + 1;
		final double t = p[1];
		return s * u + t;
	}
	
	private double[][] getJacobianT(double[] p, double x) {
//		final double s = p[0];
//		final double t = p[1];
		return new double[][] {{x, 1}};	// degenerate case: (1 x n) matrix
	}
	
	private double interpolate(double[] A, double x) {
		int u0 = (int) Math.floor(x);
		int u1 = u0 + 1;
		double d = x - u0;
		double a0 = (u0 >= 0 && u0 < A.length) ? A[u0] : 0;
		double a1 = (u1 >= 0 && u1 < A.length) ? A[u1] : 0;
		return (1 - d) * a0 + d * a1;
	}
	
	public double getRmsError() {
		return Math.sqrt(sqrError);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.lucaskanade;

import ij.process.FloatProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.math.Matrix;

/**
 * Lucas-Kanade (forward-additive) matcher, as described in Simon Baker and Iain Matthews, 
 * "Lucas-Kanade 20 Years On: A Unifying Framework: Part 1", CMU-RI-TR-02-16 (2002)
 * Also called the "forward-additive" algorithm.
 * This version assumes that the the origin of R is at its center!
 *  @author Wilhelm Burger
 *  @version 2014/02/08
 */
public class LucasKanadeForwardMatcher extends LucasKanadeMatcher {
	
	private int n;								// number of warp parameters
	private FloatProcessor Ix, Iy;				// gradient of the search image
	private double qmag = Double.MAX_VALUE; 	// magnitude of parameter difference vector
	private double sqrError = Double.MAX_VALUE;	// squared sum of differences between I and R
	private double[][][] S;						// S[u][u][n] = the steepest descent image for dimension n at pos. u,v (same size as R)

	/**
	 * Creates a new matcher of type {@link LucasKanadeForwardMatcher}.
	 * @param I the search image (of type {@link FloatProcessor}).
	 * @param R the reference image (of type {@link FloatProcessor})
	 * @param params a parameter object of type  {@link LucasKanadeMatcher.Parameters}.
	 */
	public LucasKanadeForwardMatcher(FloatProcessor I, FloatProcessor R, Parameters params) {
		super(I, R, params);
	}
	
	public LucasKanadeForwardMatcher(FloatProcessor I, FloatProcessor R) {
		super(I, R, new Parameters());
	}

	@Override
	public boolean hasConverged() {
		return (qmag < params.tolerance);
	}

	@Override
	public double getRmsError() {
		return Math.sqrt(sqrError);
	}

	private void initializeMatch(ProjectiveMapping2D Tinit) {
//		n = Tinit.getParameters().length;
		n = getParameters(Tinit).length;
		Ix = gradientX(I);
		Iy = gradientY(I);
		iteration = 0;
	}

	@Override
	public ProjectiveMapping2D iterateOnce(ProjectiveMapping2D Tp) {
		if (iteration < 0) {
			initializeMatch(Tp);
		}
		iteration = iteration + 1;
		double[][] H = new double[n][n];	// n x n cumulated Hessian matrix
		double[] dp = new double[n];		// n-dim vector \delta_p = 0
		sqrError = 0;

		if (params.showSteepestDescentImages) {
			S = new double[wR][hR][];		// S[u][v] holds a double vector of length n
		}
		
		// for all positions (u,v) in R do
		for (int u = 0; u < wR; u++) {
			for (int v = 0; v < hR; v++) {
				Pnt2d x = PntDouble.from(u - xc, v - yc);		// position w.r.t. the center of R
				Pnt2d xT = Tp.applyTo(x);		// warp x -> x'

				double gx = Ix.getInterpolatedValue(xT.getX(), xT.getY());	// interpolate the gradient at pos. xw
				double gy = Iy.getInterpolatedValue(xT.getX(), xT.getY());
				
				double[] G = new double[] {gx, gy};				// interpolated gradient vector

				// Step 4: Evaluate the Jacobian of the warp W at position x
				double[][] J = Tp.getJacobian(x);

				// Step 5: compute the steepest descent image:
				double[] sx = Matrix.multiply(G, J); // I_steepest = gradI(xy) * dW/dp(xy)  is a n-dim vector

				if (params.showSteepestDescentImages && iteration == 1) {
					S[u][v] = sx;
				}

				// Step 6: Update the Hessian matrix
				double[][] Hx = Matrix.outerProduct(sx, sx);
				H = Matrix.add(H, Hx);

				// Step 7: Compute sum_x [gradI*dW/dp]^T (R(x)-I(W(x;p))] = Isteepest^T * Exy]
				double d = R.getf(u, v) - I.getInterpolatedValue(xT.getX(), xT.getY());
				sqrError = sqrError + d * d;

				dp = Matrix.add(dp, Matrix.multiply(d, sx));
			}
		}

		if (params.showHessians && iteration == 1) {
			IjUtils.createImage("H", H).show();
			IjUtils.createImage("Hi", Matrix.inverse(H)).show();
		}

		// Step 8: Compute delta_p using Equation 10
		//			double[][] Hi = Matrix.invert(H);
		//			if (Hi == null) {
		//				IJ.log("could not invert Hessian matrix!");
		//				return null;
		//			}
		// Step 9: update the parameter vector
		//			double[] dp = Matrix.multiply(Hi, S);

		// Step 8/9 (alternative)
		double[] qopt = Matrix.solve(H, dp);
		if (qopt == null) {	// this should not happen
			throw new RuntimeException(this.getClass().getName() + ": Encountered singular Hessian matrix!");
			//return null;
		}
		
//		double[] p = Matrix.add(Tp.getParameters(), qopt);
		double[] p = Matrix.add(getParameters(Tp), qopt);
		qmag = Matrix.normL2squared(qopt);

		if (params.showSteepestDescentImages && iteration == 1) {
			showSteepestDescentImages(S);
		}

//		return ProjectiveMapping2D.fromParameters(p);
		return toProjectiveMap(p);
	}
}

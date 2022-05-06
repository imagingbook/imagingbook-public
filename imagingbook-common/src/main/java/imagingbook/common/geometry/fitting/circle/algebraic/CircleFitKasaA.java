/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;

/**
 * This is an implementation of the algebraic circle fitting algorithm described in [1].
 * This algorithm is among the oldest and simplest algebraic circle fitting methods.
 * This implementation closely follows the original paper. The only difference is the
 * use of a numerical solver (as compared to using the inverse of matrix M) for
 * solving the 3x3 linear system.
 * Its performance is sufficient if points are sampled over a large part of the circle.
 * It shows significant bias (estimated circles are too small) when sample points are 
 * confined to a small segment of the circle. It fails when matrix M becomes singular.
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * No data centering (which should improve numerical stability) is used.
 * 
 * <p>
 * [1] I. Kåsa. "A circle fitting procedure and its error analysis",
 * <em>IEEE Transactions on Instrumentation and Measurement</em> <strong>25</strong>(1), 
 * 8–14 (1976).
 * </p>
 * @author WB
 * 
 */
public class CircleFitKasaA extends CircleFitAlgebraic {

	private final double[] q;	// q = (B,C,D) algebraic circle parameters
	
	public CircleFitKasaA(Pnt2d[] points) {
		this.q = fit(points);
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {1, q[0], q[1], q[2]};
	}
	
	// WB's version: Original from Kasa's paper
	public double[] fit(Pnt2d[] pts) {
		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}

		// calculate sums:
		double sx = 0, sy = 0;
		double sxy = 0, sxx = 0, syy = 0;
		double sxxx = 0, syyy = 0;
		for (int i = 0; i < n; i++) {
			final double x = pts[i].getX();
			final double y = pts[i].getY();
			final double x2 = sqr(x);
			final double y2 = sqr(y);
			sx   += x;
			sy   += y;
			sxx  += x2;
			syy  += y2;
			sxy  += x * y;
			sxxx += x2 * x + y2 * x;	// = x^3 + x * y^2
			syyy += y2 * y + x2 * y;	// = y^3 + x^2 * y
		}
		
		double sz = sxx + syy;
		
		double[][] M = {				// = D in the paper
				{ 2 * sx,  2 * sy,  n},
				{ 2 * sxx, 2 * sxy, sx},
				{ 2 * sxy, 2 * syy, sy}};
	    
		double[] b = {sz, sxxx, syyy};	 // RHS vector (= E in the paper)
		
		// find exact solution to 3x3 system M * q = b (using a numerical solver):
		double[] q = Matrix.solve(M, b); // = Q in the paper, q = (B,C,D)
		
		double B = -2 * q[0];
		double C = -2 * q[1];
		double D = -q[2];
		
		return new double[] {B, C, D};
	}

}

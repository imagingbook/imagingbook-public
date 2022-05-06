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
 * This is an implementation of the modified Kåsa [1] circle fitting algorithm described in 
 * [2, Sec. 5.1]. A description of the concrete algorithm can be found in [3, Alg. 11.1].
 * See {@link CircleFitKasaA} for the original version.
 * <p>
 * Compared to the original Kåsa algorithm, this variant also solves a 3x3 linear
 * system but uses a slightly different setup of the scatter matrix (using only
 * powers of 2 instead of 3). A numerical solver is used for this purpose.
 * The algorithm is fast but shares the same numerical instabilities and bias when 
 * sample points are taken from a small circle segment.
 * It fails when matrix M becomes singular.
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * No data centering (which should improve numerical stability) is used.
 * </p>
 * <p>
 * [1] I. Kåsa. "A circle fitting procedure and its error analysis",
 * <em>IEEE Transactions on Instrumentation and Measurement</em> <strong>25</strong>(1), 
 * 8–14 (1976).
 * <br>
 * [2] N. Chernov. "Circular and Linear Regression: Fitting Circles and
 * Lines by Least Squares". Monographs on Statistics and Applied Probability.
 * Taylor & Francis (2011).
 * <br>
 * [3] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class CircleFitKasaB extends CircleFitAlgebraic {

	private final double[] q;	// q = (B,C,D) circle parameters, A=1
	
	public CircleFitKasaB(Pnt2d[] points) {
		q = fit(points);
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {1, q[0], q[1], q[2]};
	}
	
	private double[] fit(Pnt2d[] pts) {
		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}

		// calculate elements of scatter matrix
		double sx = 0, sy = 0, sz = 0;
		double sxy = 0, sxx = 0, syy = 0, sxz = 0, syz = 0;
		for (int i = 0; i < n; i++) {
			final double x = pts[i].getX();
			final double y = pts[i].getY();
			final double x2 = sqr(x);
			final double y2 = sqr(y);
			final double z = x2 + y2;
			sx  += x;
			sy  += y;
			sz  += z;
			sxx += x2;
			syy += y2;
			sxy += x * y;	
			sxz += x * z;
			syz += y * z;
		}
		
		double[][] M = {				// scatter matrix M
				{sxx, sxy, sx},
				{sxy, syy, sy},
				{ sx,  sy,  n}};
	    
		double[] b = {-sxz, -syz, -sz};	 // RHS vector
		double[] q = Matrix.solve(M, b); // solve M * q = b (exact), for parameter vector q = (B, C, D)
		if (q == null) {
			return null;	// M is singular, no solution
		}
		else {
			return q;
		}
	}

}

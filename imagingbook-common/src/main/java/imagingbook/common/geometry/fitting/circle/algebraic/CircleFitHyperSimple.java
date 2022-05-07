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

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eigen.EigenvalueDecomposition;
import imagingbook.common.util.SortingOrder;

/**
 * <p>
 * This is an implementation of the "hyperaccurate" algebraic circle fit proposed
 * by Al-Sharadqah and Chernov in [1], also described in [2] (Sec. 7.5, Eq. 744).
 * This method combines the Pratt and Taubin fits to eliminate the essential bias.
 * This version is "optimized for speed, not for stability"
 * (see https://people.cas.uab.edu/~mosya/cl/Hyper.m), also referred to 
 * as "simple" version. 
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * Note that is works without data centering (by using x/y/z means internally).
 * The algorithm may fail on certain critical point constellations, see 
 * {@link CircleFitHyperStable} for a superior implementation.
 * </p>
 * <p>
 * [1] A. Al-Sharadqah and N. Chernov. "Error analysis for circle fitting algorithms".
 * <em>Electronic Journal of Statistics</em>, 3:886–911 (2009).
 * <br>
 * [2] N. Chernov. "Circular and Linear Regression: Fitting Circles and
 * Lines by Least Squares". Monographs on Statistics and Applied Probability.
 * Taylor & Francis (2011).
 * </p>
 * 
 * @author WB
 *
 */
public class CircleFitHyperSimple extends CircleFitAlgebraic {

	private final double[] q;	// q = (A,B,C,D) circle parameters

	public CircleFitHyperSimple(Pnt2d[] points) {
		this.q = fit(points);
	}

	@Override
	public double[] getParameters() {
		return this.q;
	}

	// -------------------------------------------------------------------------

	private double[] fit(Pnt2d[] pts) {
		PrintPrecision.set(3);

		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}

		double[][] Xa = new double[n][];
		double xs = 0;	// sum of x_i
		double ys = 0;	// sum of y_i
		double zs = 0;	// sum of z_i
		for (int i = 0; i < n; i++) {
			double x = pts[i].getX();
			double y = pts[i].getY();
			double z = sqr(x) + sqr(y);
			Xa[i] = new double[] {z, x, y, 1};
			xs = xs + x;
			ys = ys + y;
			zs = zs + z;
		}

		double xm = xs / n;	// mean of x_i
		double ym = ys / n;	// mean of y_i
		double zm = zs / n;	// mean of z_i

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
		RealMatrix M = (X.transpose()).multiply(X);

		RealMatrix N = MatrixUtils.createRealMatrix(new double[][]
				{{ 8 * zm, 4 * xm, 4 * ym, 2 },
				 { 4 * xm, 1,      0,      0 },
				 { 4 * ym, 0,      1,      0 },
				 { 2,      0,      0,      0 }});

		RealMatrix NM = MatrixUtils.inverse(N).multiply(M);
		EigenvalueDecomposition ed = new EigenvalueDecomposition(NM);
		double[] evals = ed.getRealEigenvalues();
		
//		int k = getSmallestPositiveIdx(evals);
		int k = new SortingOrder(evals).getIndex(1);	// index of the 2nd-smallest eigenvalue
		
		RealVector p = ed.getEigenvector(k);

		return p.toArray();			// p = (A, B, C, D)
	}


//	private int getSmallestPositiveIdx(double[] x) {
//		double minval = Double.POSITIVE_INFINITY;
//		int minidx = -1;
//		for (int i = 0; i < x.length; i++) {
//			if (x[i] >= 0 && x[i] < minval) {
//				minval = x[i];
//				minidx = i;
//			}
//		}
//		return minidx;
//	}
}

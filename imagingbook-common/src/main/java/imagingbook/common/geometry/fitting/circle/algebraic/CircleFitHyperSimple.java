/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;


import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eigen.EigenDecompositionJama;
import imagingbook.common.util.PrimitiveSortMap;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * This is an implementation of the "hyperaccurate" algebraic circle fit proposed
 * by Al-Sharadqah and Chernov in [1], also described in [2] (Sec. 7.5, Eq. 744).
 * This method combines the Pratt and Taubin fits to eliminate the essential bias.
 * This version is "optimized for speed, not for stability"
 * (see https://people.cas.uab.edu/~mosya/cl/Hyper.m), also referred to 
 * as "simple" version. 
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * Data centering is used to improve numerical stability (alternatively, a reference
 * point can be specified).
 * Note that this algorithm may fail on certain critical point constellations, see 
 * {@link CircleFitHyperStable} for a superior implementation.
 * </p>
 * <p>
 * [1] A. Al-Sharadqah and N. Chernov. "Error analysis for circle fitting algorithms".
 * <em>Electronic Journal of Statistics</em>, 3:886–911 (2009).
 * <br>
 * [2] N. Chernov. "Circular and Linear Regression: Fitting Circles and
 * Lines by Least Squares". Monographs on Statistics and Applied Probability.
 * Taylor &amp; Francis (2011).
 * </p>
 * 
 * @author WB
 *
 */
public class CircleFitHyperSimple implements CircleFitAlgebraic {

	private final double[] q;	// q = (A,B,C,D) circle parameters

	/**
	 * Constructor.
	 * The centroid of the sample points is used as the reference point.
	 * 
	 * @param points sample points
	 */
	public CircleFitHyperSimple(Pnt2d[] points) {
		this(points, null);
	}
	
	/**
	 * Constructor.
	 * The centroid of the sample points is used as the reference point for data
	 * centering if {@code null} is passed for {@code xref}.
	 * 
	 * @param points sample points
	 * @param xref reference point or {@code null}
	 */
	public CircleFitHyperSimple(Pnt2d[] points, Pnt2d xref) {
		this.q = fit(points, xref);
	}

	@Override
	public double[] getParameters() {
		return this.q;
	}

	// -------------------------------------------------------------------------

	private double[] fit(Pnt2d[] pts, Pnt2d xref) {
		PrintPrecision.set(3);

		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}
		
		if (xref == null) {
			xref = PntUtils.centroid(pts);
		}
		final double xr = xref.getX();
		final double yr = xref.getY();

		double[][] Xa = new double[n][];
		double xs = 0;	// sum of x_i
		double ys = 0;	// sum of y_i
		double zs = 0;	// sum of z_i
		for (int i = 0; i < n; i++) {
			double x = pts[i].getX() - xr;
			double y = pts[i].getY() - yr;
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
		RealMatrix D = (X.transpose()).multiply(X);

		// data-dependent constraint matrix:
//		RealMatrix C = MatrixUtils.createRealMatrix(new double[][]
//				{{ 8 * zm, 4 * xm, 4 * ym, 2 },
//				 { 4 * xm, 1,      0,      0 },
//				 { 4 * ym, 0,      1,      0 },
//				 { 2,      0,      0,      0 }});
//		RealMatrix Ci = MatrixUtils.inverse(C);
		
		// define the inverse constraint matrix directly:
		RealMatrix Ci = MatrixUtils.createRealMatrix(new double[][]
				{{ 0,   0, 0, 0.5 }, 
				 { 0,   1, 0, -2 * xm }, 
				 { 0,   0, 1, -2 * ym },
				 { 0.5, -2 * xm, -2 * ym, 4 * (sqr(xm) + sqr(ym)) - 2 * zm }});

		RealMatrix CiD = Ci.multiply(D);
		EigenDecompositionJama ed = new EigenDecompositionJama(CiD);
		double[] evals = ed.getRealEigenvalues();
		
		int l = new PrimitiveSortMap(evals).getIndex(1);	// index of the 2nd-smallest eigenvalue	(1st is negative)
		RealVector qq = ed.getEigenvector(l);
		
		RealMatrix M = CircleFitAlgebraic.getDecenteringMatrix(xr, yr);
		return M.operate(qq).toArray();	// q = (A, B, C, D)		
	}

}

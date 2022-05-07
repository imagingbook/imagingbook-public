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

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eigen.EigenvalueDecomposition;
import imagingbook.common.util.SortingOrder;

/**
 * <p>
 * This is an implementation of the "hyperaccurate" algebraic circle fit proposed
 * by Al-Sharadqah and Chernov in [1], also described in [2] (Sec. 7.5, Eq. 744).
 * This method combines the Pratt and Taubin fits to eliminate the essential bias.
 * This version is "optimized for stability, not for speed"
 * (see https://people.cas.uab.edu/~mosya/cl/HyperSVD.m).
 * Singular-value decomposition is used for testing singularity of the data matrix
 * and finding a solution in the singular case.
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * Data centering is used to improve numerical stability (alternatively, a reference
 * point can be specified).
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
public class CircleFitHyperStable extends CircleFitAlgebraic {
	
	private final double[] q;	// q = (A,B,C,D) circle parameters
	
	public CircleFitHyperStable(Pnt2d[] points, Pnt2d xref) {
		this.q = fit(points, xref);
	}
	
	public CircleFitHyperStable(Pnt2d[] points) {
		this.q = fit(points, null);
	}
	
	@Override
	public double[] getParameters() {
		return this.q;
	}
	
	// -------------------------------------------------------------------------
	
	private double[] fit(Pnt2d[] pts, Pnt2d xref) {
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
		double xs = 0;
		double ys = 0;
		double zs = 0;
		for (int i = 0; i < n; i++) {
			double x = pts[i].getX() - xr;
			double y = pts[i].getY() - yr;
			double z = sqr(x) + sqr(y);
			Xa[i] = new double[] {z, x, y, 1};
			xs = xs + x;
			ys = ys + y;
			zs = zs + z;
		}

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		RealMatrix S = svd.getS();	
		RealMatrix V = svd.getV();	
		double[] svals = svd.getSingularValues(); 	// note: singular values are all positive
		
		int k = Matrix.idxMin(svals);
		double smin = svals[k];
		double smax = svals[Matrix.idxMax(svals)];
		double icond = smin / smax;			// inverse condition number of X	
		RealVector p = null;				// solution vector (circle parameters)

		if (icond < 1e-12) { 				// singular case		
			p = V.getColumnVector(k);		// take the vector for smallest singular value as the solution
		} 
		else {								// regular (non-singular) case
			double xm = xs / n;
			double ym = ys / n;			
			double zm = zs / n;
			// Eigenvalue calculation appears to be numerically very sensitive in Apache Commons Math implementation:
//			RealMatrix C = MatrixUtils.createRealMatrix(new double[][]
//				{{ 8 * zm, 4 * xm, 4 * ym, 2 },
//				 { 4 * xm, 1,      0,      0 },
//				 { 4 * ym, 0,      1,      0 },
//				 { 2,      0,      0,      0 }});
//			RealMatrix Ci = MatrixUtils.inverse(C);
			
			RealMatrix Ci = MatrixUtils.createRealMatrix(new double[][]
					{{ 0,   0, 0, 0.5 }, 
					 { 0,   1, 0, -2 * xm }, 
					 { 0,   0, 1, -2 * ym },
					 { 0.5, -2 * xm, -2 * ym, 4 * (sqr(xm) + sqr(ym)) - 2 * zm }});

			RealMatrix W = V.multiply(S).multiply(V.transpose());
			RealMatrix Z = W.multiply(Ci).multiply(W);

			EigenvalueDecomposition ed = new EigenvalueDecomposition(Z);
			double[] evals = ed.getRealEigenvalues();
			
//			int l = getSmallestPositiveIdx(evals);	
			int l = new SortingOrder(evals).getIndex(1);	// index of the 2nd-smallest eigenvalue
			RealVector el = ed.getEigenvector(l);
			
			p = Matrix.solve(W, el);
//			p = MatrixUtils.inverse(Y).operate(el);	// alternatively p = Y^-1 * el			// alternative 1
//			p = V.multiply(MatrixUtils.inverse(S)).multiply(V.transpose()).operate(el);		// alternative 2 (S is diagonal!)
		}
		
		// re-adjust circle parameters for data centering:
		RealMatrix MM = MatrixUtils.createRealMatrix(new double[][] 
			{{ 1, 0, 0, 0 },
			 {-2*xr, 1, 0, 0 },
			 {-2*yr, 0, 1, 0 },
			 {sqr(xr) + sqr(yr), -xr, -yr, 1}});		
		double[] q = MM.operate(p).toArray();
		return q;		// q = (A, B, C, D)	
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

/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.eigen.EigenDecompositionJama;
import imagingbook.common.util.SortMap;


/**
 * This is an implementation of the algebraic circle fitting algorithm by Pratt [1],
 * as described in [2] (Sec. 5.5-5.6). The algorithm uses singular-value decomposition
 * (SVD) and eigen-decomposition. See [3, Alg. 11.2] for additional details.
 * <p>
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * Data centering is used to improve numerical stability (alternatively, a reference
 * point can be specified).
 * </p>
 * <p>
 * [1] V. Pratt. "Direct least-squares fitting of algebraic surfaces". <em>ACM
 * SIGGRAPH Computer Graphics</em> <strong>21</strong>(4), 145–152 (July 1987).
 * <br>
 * [2] N. Chernov. "Circular and Linear Regression: Fitting Circles and
 * Lines by Least Squares". Monographs on Statistics and Applied Probability.
 * Taylor &amp; Francis (2011).
 * <br>
 * [3] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class CircleFitPratt implements CircleFitAlgebraic {
	
	private static final RealMatrix Ci =	// inverse of constraint matrix C
			MatrixUtils.createRealMatrix(new double[][] { 
				{  0,   0, 0, -0.5 },
				{  0,   1, 0,  0 },
				{  0,   0, 1,  0 },
				{ -0.5, 0, 0,  0 }});
	
	private final double[] q;	// q = (A,B,C,D) circle parameters
	
	@Override
	public double[] getParameters() {
		return this.q;
	}
	
	/**
	 * Constructor.
	 * The centroid of the sample points is used as the reference point.
	 * 
	 * @param points sample points
	 */
	public CircleFitPratt(Pnt2d[] points) {
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
	public CircleFitPratt(Pnt2d[] points, Pnt2d xref) {
		this.q = fit(points, xref);
	}

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

		double[][] Xa = new double[Math.max(n, 4)][4];	// Xa must have at least 4 rows!
		for (int i = 0; i < pts.length; i++) {
			double x = pts[i].getX() - xr;		// = x_i
			double y = pts[i].getY() - yr;		// = y_i
			Xa[i][0] = sqr(x) + sqr(y);			// = z_i
			Xa[i][1] = x;
			Xa[i][2] = y;
			Xa[i][3] = 1;
		}
		// if nPoints = 3 (special case) the last row of the
		// 4x4 matrix contains all zeros (make X singular)!

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);

		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		RealMatrix S = svd.getS();	
		RealMatrix V = svd.getV();
		double[] svals = svd.getSingularValues(); 	// note: singular values are all positive (>= 0)
		
		int k = Matrix.idxMin(svals);
		double smin = svals[k];
		double smax = Matrix.max(svals); 
		
		RealVector qq = null;		// = \dot{q} solution vector (algebraic circle parameters)

		double icond = smin / smax;
		if (icond < 1e-12) { 			// smin/smax = inverse condition number of X, 1e-12
			// singular case (X is rank deficient)
			qq = V.getColumnVector(k);
		} else {
			// regular (non-singular) case
		
			// Version1 (seems to create smaller roundoff errors, better matrix symmetry):
			RealMatrix Y = V.multiply(S).multiply(V.transpose());
			RealMatrix Z = Y.multiply(Ci).multiply(Y); // = Y * Ci * Y
			
			// Version2:
//			RealMatrix Y = V.multiply(S);
//			RealMatrix Z = Y.transpose().multiply(Ci).multiply(Y); // = Y^T * Ci * Y
			
			EigenDecompositionJama ed = new EigenDecompositionJama(Z); 
			double[] evals = ed.getRealEigenvalues();
			int l = new SortMap(evals).getIndex(1);	// index of the 2nd-smallest eigenvalue			
			RealVector el = ed.getEigenvector(l);
			
			// Version1 ---------------------------------------------------
//			qq = Matrix.solve(S.multiply(svd.getVT()), el);		// solve S * V^T * p = el
			
			// Version2 ---------------------------------------------------
			qq = V.operate(MatrixUtils.inverse(S).operate(el));	// simpler since S is diagonal (i.e., easy to invert)
		}

		RealMatrix M = CircleFitAlgebraic.getDecenteringMatrix(xr, yr);
		return M.operate(qq).toArray();  // q = (A,B,C,D)
	}
		
}
/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.algebraic;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.eigen.EigenDecompositionJama;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * Algebraic ellipse fit using Fitzgibbon's original method [1], based on simple
 * matrix inversion. See [2, Sec. 11.2.1] for a detailed description.
 * Note: This implementation does not use data centering nor accepts a specific
 * reference point. With exactly 5 input points (generally sufficient for
 * ellipse fitting) the scatter matrix X is singular and in this case matrix S
 * has no inverse. Thus at least 6 distinct input points are required (i.e., no
 * duplicate points are allowed).
 * </p>
 * <p>
 * [1] A. W. Fitzgibbon, M. Pilu, and R. B. Fisher. Direct least- squares
 * fitting of ellipses. IEEE Transactions on Pattern Analysis and Machine
 * Intelligence 21(5), 476-480 (1999).<br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/17
 */
public class EllipseFitFitzgibbonNaive implements EllipseFitAlgebraic {
	
	// constraint matrix
	private static final RealMatrix C = Matrix.makeRealMatrix(6, 6,
			  	0, 0, 2, 0, 0, 0,
				0,-1, 0, 0, 0, 0,
				2, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0 );
	
	private final double[] q;	// = (A,B,C,D,E,F) ellipse parameters
	
	public EllipseFitFitzgibbonNaive(Pnt2d[] points) {
		if (points.length < 6) {
			throw new IllegalArgumentException("fitter requires at least 6 sample points instead of " + points.length);
		}
		this.q = fit(points);
	}

	@Override
	public double[] getParameters() {
		return this.q;
	}
	
	private double[] fit(Pnt2d[] points) {
		final int n = points.length;

		// create design matrix X:
		RealMatrix X = MatrixUtils.createRealMatrix(n, 6);
		for (int i = 0; i < n; i++) {
			double x = points[i].getX();
			double y = points[i].getY();
			X.setEntry(i, 0, sqr(x));
			X.setEntry(i, 1, x * y);
			X.setEntry(i, 2, sqr(y));
			X.setEntry(i, 3, x);
			X.setEntry(i, 4, y);
			X.setEntry(i, 5, 1);
		}
		
		// scatter matrix S:
		RealMatrix S = X.transpose().multiply(X);	
		RealMatrix Si = MatrixUtils.inverse(S, 1e-15);
		
//		EigenDecomposition ed = new EigenDecomposition(Si.multiply(C));
		EigenDecompositionJama ed = new EigenDecompositionJama(Si.multiply(C));

		double[] evals = ed.getRealEigenvalues();
		int k = Matrix.idxMax(evals);				// index of the largest eigenvalue
		return ed.getEigenvector(k).toArray();
	}
	
}

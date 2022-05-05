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
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * This is an improved implementation of the Kåsa [1] circle fitting algorithm described in 
 * [2, Sec. 5.2, eq. 5.12], based on the Moore-Penrose pseudo-inverse applied to the full data
 * matrix (i.e, no 3x3 scatter matrix is mounted).
 * See also [3, Sec. 11.1.2] and {@link CircleFitKasaOrig} for the original version.
 * <p>
 * This algorithm is assumed to be numerically more stable than solutions based on solving
 * a 3x3 system. The pseudo-inverse is obtained by singular-value decomposition (SVD).
 * However, the significant bias on points sampled from a small circle
 * segment remains.
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

	private final double[] q;	// p = (B,C,D) circle parameters, A=1
	
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
		
		final double[] z = new double[n];
		final double[][] Xa = new double[n][];
		for (int i = 0; i < n; i++) {
			final double x = pts[i].getX();
			final double y = pts[i].getY();
			Xa[i] = new double[] {x, y, 1};
			z[i] = -(sqr(x) + sqr(y));
		}

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
		
		RealMatrix Xi = null;
		try {
			SingularValueDecomposition svd = new SingularValueDecomposition(X);
			Xi = svd.getSolver().getInverse();		// get (3,N) pseudoinverse of X
//			IJ.log("solver = " + svd.getSolver());
//			IJ.log("rank X = " + svd.getRank());
		} catch (SingularMatrixException e) { }
		
		if (Xi == null) {
			return null;
		}
		else {
			double[] q = Xi.operate(z);	// solution vector q = X^-1 * z = (B, C, D)
			return q;
		}
	}

}

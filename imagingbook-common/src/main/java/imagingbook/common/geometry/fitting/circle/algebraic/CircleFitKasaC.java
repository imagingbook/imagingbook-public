/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;

import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.SingularMatrixException;
import org.apache.commons.math4.legacy.linear.SingularValueDecomposition;

// import org.apache.commons.math3.linear.MatrixUtils;
// import org.apache.commons.math3.linear.RealMatrix;
// import org.apache.commons.math3.linear.SingularMatrixException;
// import org.apache.commons.math3.linear.SingularValueDecomposition;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * This is an improved implementation of the Kåsa [1] circle fitting algorithm described in [2, Sec. 5.2] (Eq. 5.12). It
 * is based on the Moore-Penrose pseudo-inverse which is applied to the full data matrix (i.e, no 3x3 scatter matrix is
 * mounted). See also [3, Sec. 11.1.2] and {@link CircleFitKasaA} for the original version.
 * <p>
 * This algorithm is assumed to be numerically more stable than solutions based on solving a 3x3 system. The
 * pseudo-inverse is obtained by singular-value decomposition (SVD). However, the significant bias on points sampled
 * from a small circle segment remains. Fits to exactly 3 (non-collinear) points are handled properly. No data centering
 * (which should improve numerical stability) is used.
 * </p>
 * <p>
 * [1] I. Kåsa. "A circle fitting procedure and its error analysis", <em>IEEE Transactions on Instrumentation and
 * Measurement</em> <strong>25</strong>(1), 8–14 (1976).
 * <br>
 * [2] N. Chernov. "Circular and Linear Regression: Fitting Circles and Lines by Least Squares". Monographs on
 * Statistics and Applied Probability. Taylor &amp; Francis (2011).
 * <br>
 * [3] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class CircleFitKasaC implements CircleFitAlgebraic {

	private final double[] q;	// q = (B,C,D) circle parameters, A=1

	/**
	 * Constructor. The centroid of the sample points is used as the reference point.
	 *
	 * @param points sample points
	 */
	public CircleFitKasaC(Pnt2d[] points) {
		this(points, null);
	}

	/**
	 * Constructor. The centroid of the sample points is used as the reference point for data centering if {@code null}
	 * is passed for {@code xref}.
	 *
	 * @param points sample points
	 * @param xref reference point or {@code null}
	 */
	public CircleFitKasaC(Pnt2d[] points, Pnt2d xref) {
		this.q = fit(points, xref);
	}
	
	@Override
	public double[] getParameters() {
		return q;
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

		
		final double[] z = new double[n];
		final double[][] Xa = new double[n][];
		for (int i = 0; i < n; i++) {
			final double x = pts[i].getX() - xr;
			final double y = pts[i].getY() - yr;
			Xa[i] = new double[] {x, y, 1};
			z[i] = -(sqr(x) + sqr(y));
		}

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
		
		RealMatrix Xi = null;
		try {
			SingularValueDecomposition svd = new SingularValueDecomposition(X);
			Xi = svd.getSolver().getInverse();		// get (3,N) pseudoinverse of X
		} catch (SingularMatrixException e) { }
		
		if (Xi == null) {
			return null;
		}
		else {
			double[] qq = Xi.operate(z);	// solution vector qq = X^-1 * z = (B, C, D)	
			// re-adjust for data centering
			RealMatrix M = CircleFitAlgebraic.getDecenteringMatrix(xr, yr);		
			return M.operate(new double[] {1, qq[0], qq[1], qq[2]});	// q = (A,B,C,D)
		}
	}

}

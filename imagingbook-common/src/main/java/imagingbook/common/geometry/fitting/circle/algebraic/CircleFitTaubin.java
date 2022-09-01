/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Matrix;

/**
 * This is an implementation of the algebraic circle fitting algorithm by Taubin [1],
 * following the description in [2] (Sec. 5.9-5.10). 
 * <p>
 * The algorithm uses singular-value decomposition (SVD).
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * Data centering is used to improve numerical stability (alternatively, a reference
 * point can be specified).
 * </p>
 * <p>
 * [1] G. Taubin. "Estimation of planar curves, surfaces, and nonplanar
 * space curves defined by implicit equations with applications to edge
 * and range image segmentation". <em>IEEE Transactions on Pattern Analysis
 * and Machine Intelligence</em> <strong>13</strong>(11), 1115–1138 (1991).
 * <br>
 * [2] N. Chernov. "Circular and Linear Regression: Fitting Circles and
 * Lines by Least Squares". Monographs on Statistics and Applied Probability.
 * Taylor &amp; Francis (2011).
 * </p>
 * 
 * @author WB
 *
 */
public class CircleFitTaubin implements CircleFitAlgebraic {
	
	private final double[] q;	// p = (A,B,C,D) circle parameters
	
	/**
	 * Constructor.
	 * The centroid of the sample points is used as the reference point.
	 * 
	 * @param points sample points
	 */
	public CircleFitTaubin(Pnt2d[] points) {
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
	public CircleFitTaubin(Pnt2d[] points, Pnt2d xref) {
		this.q = fit(points, xref);
	}
	
	private double[] fit(Pnt2d[] pts, Pnt2d xref) {
		int n = pts.length;
		if (n < 3)
			throw new IllegalArgumentException("at least 3 points are required");

		if (xref == null) {
			xref = PntUtils.centroid(pts);
		}
		double xr = xref.getX();
		double yr = xref.getY();

		double[][] Xa = new double[n][3];	// Xa[i] = (z, x, y)
		double zs = 0;
		for (int i = 0; i < n; i++) {
			double x = pts[i].getX() - xr;	// = x_i
			double y = pts[i].getY() - yr;	// = y_i
			double z = sqr(x) + sqr(y);		// = z_i
			zs = zs + z;
			Xa[i][0] = z;
			Xa[i][1] = x;
			Xa[i][2] = y;
		}
		
		double zm = zs / n;
		double zmr = Math.sqrt(zm);
		
		// normalize z to zero mean, unit variance:
		for (int i = 0; i < n; i++) {
			Xa[i][0] = (Xa[i][0] - zm) / (2 * zmr);
		}
		
		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		RealMatrix V = svd.getV();
		
		// get the column vector of V associated with the smallest singular value:
		double[] svals = svd.getSingularValues();
		int minIdx = Matrix.idxMin(svals);
		double[] a = V.getColumn(minIdx);

		double[] qq = new double[4];
		qq[0] =  a[0] / (2 * zmr);
		qq[1] =  a[1];
		qq[2] =  a[2];
		qq[3] = -a[0] * zmr / 2;
				
		// re-adjust for data centering:
		RealMatrix M = CircleFitAlgebraic.getDecenteringMatrix(xr, yr);
		return M.operate(qq);		// q = (A,B,C,D)
	}
	
	@Override
	public double[] getParameters() {
		return this.q;
	}

}
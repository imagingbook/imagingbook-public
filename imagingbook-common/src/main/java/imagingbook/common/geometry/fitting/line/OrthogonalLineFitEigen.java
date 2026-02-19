/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.eigen.EigenDecompositionJama;
import imagingbook.common.util.PrimitiveSortMap;

import org.apache.commons.math4.legacy.linear.MatrixUtils;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * This class implements line fitting by orthogonal regression to a set of 2D points using eigendecomposition. See Sec.
 * 10.2.2 (Alg. 10.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/29
 * @see OrthogonalLineFitSvd
 * @see OrthogonalLineFitHomogeneous
 */
public class OrthogonalLineFitEigen implements LineFit {
	
	private final int n;
	private double[] params;	// algebraic line parameters A,B,C
	private double err;	// the sum of the squared point distances

	/**
	 * Constructor, performs a orthogonal regression fit to the specified points. At least two different points are
	 * required.
	 *
	 * @param points an array with at least 2 points
	 */
	public OrthogonalLineFitEigen(Pnt2d[] points) {
		if (points.length < 2) {
			throw new IllegalArgumentException("line fit requires at least 2 points");
		}
		this.n = points.length;
		// this.p = doFit(points);
		doFit(points);
	}
	
	@Override
	public int getSize() {
		return n;
	}

	@Override
	public double[] getLineParameters() {
		return params;
	}

	/**
	 * Returns the sum of squared orthogonal point distances from the fitted line.
	 * For {@link OrthogonalLineFitEigen} this quantity is obtained as the smallest
	 * eigenvalue of the scatter matrix, i.e., without any additional calculations.
	 * @return the fitting error as the sum of squared orthogonal point distances
	 */
	public double getError() {
		return err;
	}

	private void doFit(Pnt2d[] points) {
		final int n = points.length;
	
		double Sx = 0, Sy = 0, Sxx = 0, Syy = 0, Sxy = 0;
		
		for (int i = 0; i < n; i++) {
			final double x = points[i].getX();
			final double y = points[i].getY();
			Sx += x;
			Sy += y;
			Sxx += sqr(x);
			Syy += sqr(y);
			Sxy += x * y;
		}
		
		double sxx = Sxx - sqr(Sx) / n;
		double syy = Syy - sqr(Sy) / n;
		double sxy = Sxy - Sx * Sy / n;
		
		double[][] S = {
				{sxx, sxy},
				{sxy, syy} 
		};
		
		
		EigenDecompositionJama es = new EigenDecompositionJama(MatrixUtils.createRealMatrix(S));	// Jama-derived local implementation
//		EigenDecomposition es = new EigenDecomposition(MatrixUtils.createRealMatrix(S));	// Apache Commons Maths
		double[] eVals = es.getRealEigenvalues();
		int k = PrimitiveSortMap.getNthSmallestIndex(eVals, 0);	// index of smallest eigenvalue
		double eVal = eVals[k];		//  smallest eigenvalue = suared error
		double[] eVec = es.getEigenvector(k).toArray();
		
		double A = eVec[0];
		double B = eVec[1];
		double C = -(A * Sx + B * Sy) / n;
		
		// return new double[] {A, B, C};
		this.params = new double[] {A, B, C};
		this.err = eVal;
	}
}

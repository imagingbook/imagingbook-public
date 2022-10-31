/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Matrix;

/**
 * <p>
 * This class implements line fitting by orthogonal regression to a set of 2D
 * points using singular-value decomposition (SVD). See Sec. 10.2.2 (Alg. 10.2)
 * of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/29
 * 
 * @see OrthogonalLineFitEigen
 * @see OrthogonalLineFitHomogeneous
 */
public class OrthogonalLineFitSvd implements LineFit {
	
	private final int n;
	private final double[] p;	// algebraic line parameters A,B,C
	
	/**
	 * Constructor, performs a linear regression fit to the specified points.
	 * At least two different points are required.
	 * 
	 * @param points an array with at least 2 points
	 */
	public OrthogonalLineFitSvd(Pnt2d[] points) {
		if (points.length < 2) {
			throw new IllegalArgumentException("line fit requires at least 2 points");
		}
		this.n = points.length;
		this.p = fit(points);
	}
	
	@Override
	public int getSize() {
		return n;
	}

	@Override
	public double[] getLineParameters() {
		return p;
	}
	
	private double[] fit(Pnt2d[] points) {
		final int n = points.length;
		final double[][] X = new double[n][2];
		Pnt2d ctr = PntUtils.centroid(points);
		final double xc = ctr.getX();
		final double yc = ctr.getY();
		for (int i = 0; i < n; i++) {
			X[i][0] = points[i].getX() - xc;
			X[i][1] = points[i].getY() - yc;
		}
		
		SingularValueDecomposition svd =  
				new SingularValueDecomposition(MatrixUtils.createRealMatrix(X));
		
		RealMatrix V = svd.getV();
		double[] s = svd.getSingularValues();
		int k = Matrix.idxMin(s);
		double[] e = V.getColumn(k);

		double A = e[0];
		double B = e[1];
		double C = -A * xc - B * yc;
		return new double[] {A,B,C};
	}
	
}

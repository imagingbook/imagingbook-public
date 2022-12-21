/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * <p>
 * This class implements line fitting by orthogonal regression to a set of 2D points by solving a homogeneous linear
 * system. See Sec. 6.7.1 of [1] and Sec. 10.2.2 (Alg. 10.2) of [2] for additional details.
 * </p>
 * <p>
 * [1] W. Gander, M. J. Gander, and F. Kwok. "Scientific Computing – An Introduction using Maple and MATLAB". Springer
 * (2014). <br> [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd
 * ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/29
 * @see OrthogonalLineFitEigen
 * @see OrthogonalLineFitSvd
 */
public class OrthogonalLineFitHomogeneous implements LineFit {
	
	private final Pnt2d[] points;
	private final double[] p;	// algebraic line parameters A,B,C

	/**
	 * Constructor, performs a orthogonal regression fit to the specified points. At least two different points are
	 * required.
	 *
	 * @param points an array with at least 2 points
	 */
	public OrthogonalLineFitHomogeneous(Pnt2d[] points) {
		if (points.length < 2) {
			throw new IllegalArgumentException("line fit requires at least 2 points");
		}
		this.points = points;
		this.p = fit();
	}
	
	@Override
	public int getSize() {
		return points.length;
	}
	
	@Override
	public double[] getLineParameters() {
		return p;
	}
	
	private double[] fit() {
		final int n = points.length; //.max(3, pts.length);
		final double[][] X = new double[n][3];
		
		for (int i = 0; i < points.length; i++) {
			X[i][0] = 1;
			X[i][1] = points[i].getX();
			X[i][2] = points[i].getY();
		}
		
		QRDecomposition qr = new QRDecomposition(MatrixUtils.createRealMatrix(X));
		RealMatrix R = qr.getR();
		
//		RealMatrix RR = R.getSubMatrix(1, 2, 1, 2);
		RealMatrix RR = new Array2DRowRealMatrix(2,2);
		RR.setEntry(0, 0, R.getEntry(1, 1));
		RR.setEntry(0, 1, R.getEntry(1, 2));
		if (R.getRowDimension() >= 3) {
			RR.setEntry(1, 1, R.getEntry(2, 2));
		}
	
		SingularValueDecomposition svd = new SingularValueDecomposition(RR);
		
		RealMatrix V = svd.getV();
		double[] s = svd.getSingularValues();
		int k = Matrix.idxMin(s);
		double[] e = V.getColumn(k);
		
		double R01 = R.getEntry(0, 1);
		double R02 = R.getEntry(0, 2);
		double R00 = R.getEntry(0, 0);

		double A = e[0];
		double B = e[1];
		double C = ( -A * R01 - B * R02) / R00;
		return new double[] {A,B,C};
	}

}

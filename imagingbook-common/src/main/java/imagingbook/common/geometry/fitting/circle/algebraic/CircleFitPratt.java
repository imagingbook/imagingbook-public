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


/**
 *
 */
public class CircleFitPratt extends CircleFitAlgebraic {
	
	private static final RealMatrix Bi =	// inverse of constraint matrix B
			MatrixUtils.createRealMatrix(new double[][] { 
				{  0,   0, 0, -0.5 },
				{  0,   1, 0,  0 },
				{  0,   0, 1,  0 },
				{ -0.5, 0, 0,  0 }});
	
	private final double[] q;	// p = (A,B,C,D) circle parameters
	
	public CircleFitPratt(Pnt2d[] points) {
		this.q = fit(points);
	}
	
//	@Override
//	public GeometricCircle getGeometricCircle() {
//		if (isZero(p[0])) {			// (abs(2 * A / s) < (1 / Rmax))
//			return null;			// return a straight line
//		}
//		else {
//			return GeometricCircle.from(new AlgebraicCircle(p));
//		}
//	}

	// enforce constraint B^2 + C^2 - 4 A D = 1 :
	@SuppressWarnings("unused")
	private double[] normalizeP(double[] q) {
		final double A = q[0];
		final double B = q[1];
		final double C = q[2];
		final double D = q[3];
		double s  = Math.sqrt(sqr(B) + sqr(C) - 4 * A * D);
		return new double[] {A/s, B/s, C/s, D/s};
	}
	

	private double[] fit(Pnt2d[] pts) {	// Version in Chernov (Table 5.1) - this version also works for the HyperFitter!
//		IJ.log(this.getClass().getSimpleName() + " -- Version B");
		final int nPoints = pts.length;
		if (nPoints < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}
		
//		IJ.log("n = " + nPoints);
		Pnt2d ctr = PntUtils.centroid(pts);
		final double xr = ctr.getX();
		final double yr = ctr.getY();

		double[][] Xa = new double[Math.max(nPoints, 4)][4];	// Xa must have at least 4 rows!
		for (int i = 0; i < pts.length; i++) {
			double x = pts[i].getX() - xr;
			double y = pts[i].getY() - yr;
			Xa[i][0] = sqr(x) + sqr(y);
			Xa[i][1] = x;
			Xa[i][2] = y;
			Xa[i][3] = 1;
		}
		// if nPoints = 3 (special case) the last row of the
		// 4x4 matrix contains all zeros to make X singular!

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
//		IJ.log("X = \n" + Matrix.toString(X.getData()));

		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		RealMatrix S = svd.getS();	
		RealMatrix V = svd.getV();
		double[] svals = svd.getSingularValues(); 	// note: singular values are all positive (or zero)
		
		int minIdx = Matrix.idxMin(svals);
		double smin = svals[minIdx];
		int maxIdx = Matrix.idxMax(svals);
		double smax = svals[maxIdx];
		
		RealVector qq = null;		// = \dot{q} solution vector (algebraic circle parameters)

		double icond = smin / smax;
//		IJ.log("Pratt fitter: icond = " + icond);
		
		if (icond < 1e-12) { 			// smin/smax = inverse condition number of X, 1e-12
			// singular case (X is rank deficient)
			qq = V.getColumnVector(minIdx);
		} else {
			// regular (non-singular) case
		
			// Version1 ---------------------------------------------------
//			RealMatrix Y = V.multiply(S).multiply(V.transpose());
//			RealMatrix Z = Y.multiply(Bi).multiply(Y); // = Y * Bi * Y
			// Version2 ---------------------------------------------------
			RealMatrix Y = V.multiply(S);
			RealMatrix Z = Y.transpose().multiply(Bi).multiply(Y); // = Y^T * Bi * Y
			// ------------------------------------------------------------

			EigenDecomposition ed = new EigenDecomposition(Z);
			
			double[] evals = ed.getRealEigenvalues();
			int k = getSmallestPositiveIdx(evals);

//			IJ.log("l = " + l);
			RealVector ek = ed.getEigenvector(k);
			
			// Version1 ---------------------------------------------------
//			qq = Matrix.solve(S.multiply(svd.getVT()), ek);		// solve S * V^T * p = ek
			// Version2 ---------------------------------------------------
			qq = V.operate(MatrixUtils.inverse(S).operate(ek));	// simpler since S is diagonal
			// ------------------------------------------------------------
		}
		
		double[][] M = 
			{{ 1, 0, 0, 0 },
			 {-2*xr, 1, 0, 0 },
			 {-2*yr, 0, 1, 0 },
			 {sqr(xr) + sqr(yr), -xr, -yr, 1}};
		
		RealMatrix MM = MatrixUtils.createRealMatrix(M);
		
		double[] q = MM.operate(qq).toArray();
		return q;
	}
	
	private int getSmallestPositiveIdx(double[] x) {
		double minval = Double.POSITIVE_INFINITY;
		int minidx = -1;
		for (int i = 0; i < x.length; i++) {
			// ignore complex eigenvalues (x[i] == NaN)
			if (Double.isFinite(x[i]) && x[i] >= 0 && x[i] < minval) {
				minval = x[i];
				minidx = i;
			}
		}
		return minidx;
	}

	@Override
	public double[] getParameters() {
		return this.q;
	}
		
}

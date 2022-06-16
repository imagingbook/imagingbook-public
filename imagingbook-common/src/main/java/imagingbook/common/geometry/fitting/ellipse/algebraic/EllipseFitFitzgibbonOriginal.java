/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import ij.IJ;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eigen.GeneralizedSymmetricEigenDecomposition;

/**
 * 
 * Algebraic ellipse fit based on Fitzgibbon's original method [1], 
 * as described in Halir and Flusser [2] (WITHOUT their numerical
 * improvements). Based on generalized eigenproblem solution.
 * 
 * This implementation does not use data centering nor accepts a specific reference point.
 * 
 * <p>
 * Note: With exactly 5 input points (generally sufficient for ellipse fitting) the scatter matrix
 * X is singular and thus the Cholesky decomposition used by the {@link GeneralizedSymmetricEigenDecomposition}
 * cannot be applied. At least 6 distinct input points are required (i.e., no duplicate points are
 * allowed).
 * </p>
 * 
 * <p>
 * [1] A. W. Fitzgibbon, M. Pilu, and R. B. Fisher. Direct least-
 * squares fitting of ellipses. IEEE Transactions on Pattern Analysis
 * and Machine Intelligence 21(5), 476-480 (1999).
 * <br>
 * [2] R. Halíř and J. Flusser. Numerically stable direct least squares
 * fitting of ellipses. In "Proceedings of the 6th International
 * Conference in Central Europe on Computer Graphics and Visualization
 * (WSCG’98)", pp. 125-132, Plzeň, CZ (February 1998).
 * </p>
 * @author WB
 *
 */
public class EllipseFitFitzgibbonOriginal implements EllipseFitAlgebraic {
	
	private final double[] p;	// = (A,B,C,D,E,F) algebraic ellipse parameters
	
	public EllipseFitFitzgibbonOriginal(Pnt2d[] points) {
		this.p = fit(points);
	}

	@Override
	public double[] getParameters() {
		return this.p;
	}
	
	private double[] fit(Pnt2d[] points) {
		PrintPrecision.set(3);
		IJ.log("**** " + this.getClass().getSimpleName());
		final int n = points.length;

		// design matrix X:
		RealMatrix X = MatrixUtils.createRealMatrix(n, 6);
		
		for (int i = 0; i < n; i++) {
			double x = points[i].getX();
			double y = points[i].getY();
			double[] ri = {sqr(x), x * y, sqr(y), x, y, 1};
			X.setRow(i, ri);
		}
		
		// scatter matrix S:
		RealMatrix S = X.transpose().multiply(X);
//		System.out.println("S = \n" + Matrix.toString(S));
//		System.out.println("S nonsingular: " + Matrix.isNonSingular(S));
//		System.out.println("S pos. definite: " + Matrix.isPositiveDefinite(S));
//		System.out.println("S determinant = " + Matrix.determinant(S.getData()));
		
//		SingularValueDecomposition svdS = new SingularValueDecomposition(S);
//		System.out.println("   rank(S) = " + svdS.getRank());
//		System.out.println("   singular values = "  + Matrix.toString(svdS.getSingularValues()));
//		System.out.println("   condition no = " + svdS.getConditionNumber());
		
		// constraint matrix C:
		RealMatrix C = MatrixUtils.createRealMatrix(6, 6);
		C.setEntry(0, 2, 2);
		C.setEntry(1, 1, -1);
		C.setEntry(2, 0, 2);
		
		// solve C*p = lambda*S*p  which is equiv. to 
		// A*x = lambda*B*x (A, B symmetric, B positive definite)
		GeneralizedSymmetricEigenDecomposition eigen = new GeneralizedSymmetricEigenDecomposition(C, S, 1e-15, 1e-15); 
				// low ABSOLUTE_POSITIVITY_THRESHOLD (last argument) is important!
		
		double[] evals = eigen.getRealEigenvalues(); 
		int k = getLargestPositiveIdx(evals);
		double[] p = eigen.getEigenvector(k).toArray();
		
		return Matrix.normalize(p);		
	}
	
	// ------------------------------
	
	private int getLargestPositiveIdx(double[] x) {
		double maxval = Double.NEGATIVE_INFINITY;
		int maxidx = -1;
		for (int i = 0; i < x.length; i++) {
			if (Double.isFinite(x[i]) && x[i] >= 0 && x[i] > maxval) {
				maxval = x[i];
				maxidx = i;
			}
		}
		return maxidx;
	}
	
	// -------------------------------------------------
	
	public static void main(String[] args) {
		Pnt2d[] points = {
				Pnt2d.from(40, 53),
				Pnt2d.from(107, 20),
				Pnt2d.from(170, 26),
				Pnt2d.from(186, 55),
				Pnt2d.from(135, 103),
//				Pnt2d.from(135, 113)
				};
		
		EllipseFitAlgebraic fit = new EllipseFitFitzgibbonOriginal(points);
		System.out.println("fit parameters = " + Matrix.toString(fit.getParameters()));
		System.out.println("fit ellipse = " + fit.getEllipse());
		System.out.println("fit ellipse = " +  Matrix.toString(fit.getEllipse().getParameters()));
		
		// create random 5-point sets and try to fit ellipses, counting null results:
//		Random rg = new Random(17);
//		int N = 1000;
//		int nullCnt = 0;
//		for (int k = 0; k < N; k++) {
//			for (int i = 0; i < points.length; i++) {
//				double x = rg.nextInt(200);
//				double y = rg.nextInt(200);
//				points[i] = Pnt2d.from(x, y);
//			}
//			fit = new EllipseFit5Points(points);
//			if (fit.getEllipse() == null) {
//				nullCnt++;
//			}	
//		}
//		
//		System.out.println(nullCnt + " null results out of " + N);
	}
	
	// fit ellipse = AlgebraicEllipse {0.317325319, 0.332954818, 0.875173557, -89.442594143, -150.574265066, 7886.192568730}
	
}
package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.eigen.GeneralizedSymmetricEigenSolver;

/**
 * Algebraic ellipse fit based on Taubin's method [1].
 * Version 1 uses the full scatter and constraint matrix (6x6).
 * The constraint matrix (C) is not positive definite.
 * The solution is found by a generalized symmetric eigenproblem.
 * Performs data centering or alternatively accepts a specific reference point.
 * 
 * @author WB
 * @version 2021/11/09
 */
public class EllipseFitTaubin1 implements EllipseFitAlgebraic {
	
	private final double[] p;	// p = (A,B,C,D,E,F) ellipse parameters
	
	public EllipseFitTaubin1(Pnt2d[] points, Pnt2d xref) {
		if (points.length < 5) {
			throw new IllegalArgumentException("at least 5 points must be supplied for ellipse fitting");
		}
		this.p = fit(points, xref);
		
	}
	
	public EllipseFitTaubin1(Pnt2d[] points) {
		this(points, PntUtils.centroid(points));
	}

	@Override
	public double[] getParameters() {
		return this.p;
	}
	
	// --------------------------------------------------------------------------
	
	private double[] fit(Pnt2d[] points, Pnt2d xref) {
		final int n = points.length;
		
		// reference point
		final double xr = xref.getX();
		final double yr = xref.getY();

		// design matrix (same as Fitzgibbon):
		RealMatrix X = MatrixUtils.createRealMatrix(n, 6);
		for (int i = 0; i < n; i++) {
			final double x = points[i].getX() - xr;	// center data set
			final double y = points[i].getY() - yr;
			double[] fi = {sqr(x), x*y, sqr(y), x, y, 1};
			X.setRow(i, fi);
		}
		
		// scatter matrix S (normalized by 1/n)
		RealMatrix S = X.transpose().multiply(X).scalarMultiply(1.0/n);
//		IJ.log("M = \n" + imagingbook.lib.math.Matrix.toString(S.getData()));
			
		double a = S.getEntry(0, 5);
		double b = S.getEntry(2, 5);
		double c = S.getEntry(1, 5);	//2*s[1][5];
		double d = S.getEntry(3, 5);
		double e = S.getEntry(4, 5);
//		IJ.log("d = " + d);
//		IJ.log("e = " + e);
		
		RealMatrix C = Matrix.makeRealMatrix(6, 6, 
				4*a, 2*c, 0,   2*d, 0,   0 , 
				2*c, a+b, 2*c, e,   d,   0 , 
				0,   2*c, 4*b, 0,   2*e, 0 , 
				2*d, e,   0,   1,   0,   0 ,
				0,   d,   2*e, 0,   1,   0 , 
				0,   0,   0,   0,   0,   0 );
		
//		IJ.log("S is positive definite: " + isPositiveDefinite(S));
//		IJ.log("C is positive definite: " + isPositiveDefinite(C));
		
		// solve C*p = lambda*S*p
		GeneralizedSymmetricEigenSolver eigen = 
				new GeneralizedSymmetricEigenSolver(C, S, 1e-15, 1e-15);	// S, C reversed, since C is not positive definite
		
		double[] evals = eigen.getRealEigenvalues();
//		IJ.log("evals = " + Arrays.toString(evals));
		
		int k = Matrix.idxMax(evals);	// index of largest eigenvalue
		double[] p0 = eigen.getEigenVector(k).toArray();
		
		RealMatrix U = getDataOffsetCorrectionMatrix(xr, yr);
		double[] p = U.operate(p0);
		
		return Matrix.normalize(p);
	}
	
	private static double TOLERANCE = 1e-16;
	
	@SuppressWarnings("unused")
	private boolean isPositiveDefinite(RealMatrix A) {	
		try {
			new CholeskyDecomposition(A, TOLERANCE, TOLERANCE);
			return true;
		} catch (NonPositiveDefiniteMatrixException e) {};
		
		return false;
	}


}

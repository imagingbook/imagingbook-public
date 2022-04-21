package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.eigen.GeneralizedSymmetricEigenSolver;

/**
 * Algebraic ellipse fit based on Taubin's method [1].
 * Version 2 uses a reduced scatter and constraint matrix (5x5).
 * solution is found by a generalized symmetric eigenproblem.
 * Performs data centering or alternatively accepts a specific reference point.
 * 
 * @author WB
 * @version 2021/11/09
 */
public class EllipseFitTaubin2 implements EllipseFitAlgebraic {
	
	private final double[] p;	// p = (A,B,C,D,E,F) ellipse parameters
	
	public EllipseFitTaubin2(Pnt2d[] points, Pnt2d xref) {
		if (points.length < 5) {
			throw new IllegalArgumentException("at least 5 points must be supplied for ellipse fitting");
		}
		this.p = fit(points, xref);
		
	}
	
	public EllipseFitTaubin2(Pnt2d[] points) {
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
		
		RealMatrix S1 = S.getSubMatrix(0, 4, 0, 4);
//		IJ.log("S1 = \n" + imagingbook.lib.math.Matrix.toString(S1.getData()));
		
		RealVector s5 = S.getColumnVector(5).getSubVector(0, 5);
//		IJ.log("s5 = " + imagingbook.lib.math.Matrix.toString(s5.toArray()));

		RealMatrix P = S1.subtract(s5.outerProduct(s5));
//		IJ.log("P = \n" + imagingbook.lib.math.Matrix.toString(P.getData()));
	
		double a = S.getEntry(0, 5);
		double b = S.getEntry(2, 5);
		double c = S.getEntry(1, 5);
		double d = S.getEntry(3, 5);
		double e = S.getEntry(4, 5);
		
		// constraint matrix:
		RealMatrix Q = Matrix.makeRealMatrix(5, 5, 
				4*a, 2*c, 0,   2*d, 0,
				2*c, a+b, 2*c, e,   d, 
				0,   2*c, 4*b, 0,   2*e,
				2*d, e,   0,   1,   0,
				0,   d,   2*e, 0,   1);
		
		GeneralizedSymmetricEigenSolver eigen = new GeneralizedSymmetricEigenSolver(P, Q);
		
		double[] evals = eigen.getRealEigenvalues();
//		IJ.log("evals = " + Arrays.toString(evals));
		
		int k = getSmallestPositiveIdx(evals);
		RealVector q0 = eigen.getEigenVector(k);
//		IJ.log("pInit = " + Matrix.toString(pInit.toArray()));
		
		double f = -s5.dotProduct(q0);
		RealVector qopt = q0.append(f);
		
		RealMatrix U = getDataOffsetCorrectionMatrix(xr, yr);
		double[] q = U.operate(qopt.toArray());
		return Matrix.normalize(q);
	}
	
	// --------------------------------------------------------------------
	
	private int getSmallestPositiveIdx(double[] x) {
		double minval = Double.POSITIVE_INFINITY;
		int minidx = -1;
		for (int i = 0; i < x.length; i++) {
			if (x[i] >= 0 && x[i] < minval) {
				minval = x[i];
				minidx = i;
			}
		}
		return minidx;
	}
	
	private static double TOLERANCE = 1e-6;
	
	@SuppressWarnings("unused")
	private boolean isPositiveDefinite(RealMatrix A) {	
		try {
			new CholeskyDecomposition(A, TOLERANCE, TOLERANCE);
			return true;
		} catch (NonPositiveDefiniteMatrixException e) {};
		
		return false;
	}
	


}

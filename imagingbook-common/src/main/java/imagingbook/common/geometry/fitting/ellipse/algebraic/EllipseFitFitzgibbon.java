package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import ij.IJ;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.eigen.GeneralizedSymmetricEigenSolver;

/**
 * 
 * Algebraic ellipse fit based on Fitzgibbon's original method [1], 
 * as described in Halir and Flusser [2] (WITHOUT their numerical
 * improvements). Based on generalized eigenproblem solution.
 * 
 * Does not use data centering nor accepts a specific reference point.
 * 
 * [1] A. W. Fitzgibbon, M. Pilu, and R. B. Fisher. Direct least-
 * squares fitting of ellipses. IEEE Transactions on Pattern Analysis
 * and Machine Intelligence 21(5), 476-480 (1999).
 * 
 * [2] R. Halíř and J. Flusser. Numerically stable direct least squares
 * fitting of ellipses. In "Proceedings of the 6th International
 * Conference in Central Europe on Computer Graphics and Visualization
 * (WSCG’98)", pp. 125-132, Plzeň, CZ (February 1998).
 * 
 * @author WB
 *
 */
public class EllipseFitFitzgibbon implements EllipseFitAlgebraic {
	
	private final double[] p;	// = (A,B,C,D,E,F) algebraic ellipse parameters
	
	public EllipseFitFitzgibbon(Pnt2d[] points) {
		this.p = fit(points);
	}

	@Override
	public double[] getParameters() {
		return this.p;
	}
	
	private double[] fit(Pnt2d[] points) {
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
		
		// constraint matrix C:
		RealMatrix C = MatrixUtils.createRealMatrix(6, 6);
		C.setEntry(0, 2, 2);
		C.setEntry(1, 1, -1);
		C.setEntry(2, 0, 2);
		
		// solve C*p = lambda*S*p  which is equiv. to 
		// A*x = lambda*B*x (A, B symmetric, B positive definite)
		GeneralizedSymmetricEigenSolver eigen = 
				new GeneralizedSymmetricEigenSolver(C, S, 1e-15, 1e-15); 
				// low ABSOLUTE_POSITIVITY_THRESHOLD (last argument) is important!
		
		double[] evals = eigen.getRealEigenvalues(); 
		int k = getLargestPositiveIdx(evals);
		double[] p = eigen.getEigenVector(k).toArray();
		
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
	
}
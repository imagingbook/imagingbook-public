package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * Works by matrix inversion  (naive).
 * FAILS on PERFECT FIT!
 * 
 * Algebraic ellipse fit based on Fitzgibbon's original method [1], 
 * as described in Halir and Flusser [2] (WITHOUT their numerical
 * improvements).
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
public class EllipseFitFitzgibbonNaive implements EllipseFitAlgebraic {
	
	private final double[] p;	// p = (A,B,C,D,E,F) ellipse parameters
	
	public EllipseFitFitzgibbonNaive(Pnt2d[] points) {
		this.p = fit(points);
	}

	@Override
	public double[] getParameters() {
		return this.p;
	}
	
	private double[] fit(Pnt2d[] points) {
		final int n = points.length;

		// create design matrix X:
		RealMatrix X = MatrixUtils.createRealMatrix(n, 6);
		for (int i = 0; i < n; i++) {
			double x = points[i].getX();
			double y = points[i].getY();
			X.setEntry(i, 0, sqr(x));
			X.setEntry(i, 1, x * y);
			X.setEntry(i, 2, sqr(y));
			X.setEntry(i, 3, x);
			X.setEntry(i, 4, y);
			X.setEntry(i, 5, 1);
		}
		
		RealMatrix S = X.transpose().multiply(X);
//		IJ.log("S = \n" + Matrix.toString(S.getData()));
		
//		SingularValueDecomposition svdS = new SingularValueDecomposition(S);
//		IJ.log("rank(S) = " + svdS.getRank());
//		IJ.log("   singular values = "  + Matrix.toString(svdS.getSingularValues()));
//		IJ.log("   condition no = " + svdS.getConditionNumber());
		
		RealMatrix C = MatrixUtils.createRealMatrix(6, 6);
		C.setEntry(0, 2, 2);
		C.setEntry(1, 1, -1);
		C.setEntry(2, 0, 2);
		
		RealMatrix Si = MatrixUtils.inverse(S, 1e-15);
//		IJ.log("Si = \n" + Matrix.toString(Si.getData()));
		
		RealMatrix SiB = Si.multiply(C);
		
//		SingularValueDecomposition svdSiB = new SingularValueDecomposition(SiB);
//		IJ.log("rank(SiB) = " + svdSiB.getRank());
//		IJ.log("  singular values = "  + Matrix.toString(svdSiB.getSingularValues()));
//		IJ.log("  condition no = " + svdSiB.getConditionNumber());
		
		
		EigenDecomposition ed = new EigenDecomposition(SiB);
//		IJ.log("nonsingular = " + ed.getSolver().isNonSingular());
		
		
//		IJ.log("det(SiB) = " + ed.getDeterminant());
//		PrintPrecision.set(10);
//		IJ.log("eigenvalues = "  + Matrix.toString(ed.getRealEigenvalues()));
		
		double[] evals = ed.getRealEigenvalues();
		int k = getLargestPositiveIdx(evals);
//		IJ.log("idx k = " + k);
//		IJ.log("eval(k) = " + evals[k]);
		RealVector p = ed.getEigenvector(k);
		
		//p.mapDivideToSelf(p.getNorm());
		return p.toArray();
	}
	
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
	
//	private int getSmallestPositiveIdx(double[] x) {
//		double minval = Double.POSITIVE_INFINITY;
//		int minidx = -1;
//		for (int i = 0; i < x.length; i++) {
//			if (x[i] > 1e-9 && x[i] < minval) {
//				minval = x[i];
//				minidx = i;
//			}
//		}
//		return minidx;
//	}
	
}

package imagingbook.common.geometry.fitting.circle.algebraic;


import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;

public class CircleFitHyper extends CircleFitAlgebraic {
	
	private final double[] p;	// p = (A,B,C,D) circle parameters
	
	public CircleFitHyper(Pnt2d[] points) {
		this.p = fit(points);
	}
	
	@Override
	public double[] getParameters() {
		return this.p;
	}
	
	// -------------------------------------------------------------------------
	
	private double[] fit(Pnt2d[] pts) {
//		IJ.log(this.getClass().getSimpleName());
		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}
		
		double[][] Xa = new double[n][];
		double xSum = 0;
		double ySum = 0;
		double zSum = 0;
		for (int i = 0; i < n; i++) {
			double x = pts[i].getX();
			double y = pts[i].getY();
			double z = sqr(x) + sqr(y);
			Xa[i] = new double[] {z, x, y, 1};
			xSum = xSum + x;
			ySum = ySum + y;
			zSum = zSum + z;
		}
		
		double xMean = xSum / n;	// mean of x_i
		double yMean = ySum / n;	// mean of y_i
		double zMean = zSum / n;	// mean of z_i

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		RealMatrix S = svd.getS();	
		RealMatrix V = svd.getV();	
		double[] svals = svd.getSingularValues(); 	// note: singular values are all positive
		
		int k = Matrix.idxMin(svals);
		double smin = svals[k];
		double smax = svals[Matrix.idxMax(svals)];
//		IJ.log("Pratt fitter: svals = " + Matrix.toString(svals));
//		IJ.log("Pratt fitter: rank(X) = " + svd.getRank());
		
		RealVector p = null;				// solution vector (circle parameters)

		double icond = smin / smax;
//		IJ.log("Hyper fitter: icond = " + icond);	
		if (icond < 1e-12) { 			// smin/smax = inverse condition number of X
//			IJ.log("Hyper fitter: singular"); // singular case (X is rank deficient)
			p = V.getColumnVector(k);
		} 
		else {	// regular (non-singular) case
//			IJ.log("Hyper fitter: non-singular");
						
			RealMatrix N = MatrixUtils.createRealMatrix(new double[][]
					{{ 8 * zMean, 4 * xMean, 4 * yMean, 2 },
					 { 4 * xMean,    1  ,   0   , 0 },
					 { 4 * yMean,    0  ,   1   , 0 },
					 {   2   ,    0  ,   0   , 0 }});
//			IJ.log("N = \n" + Matrix.toString(N.getData()));
			
			RealMatrix Ni = MatrixUtils.inverse(N);
//			IJ.log("Ni = \n" + Matrix.toString(Ni.getData()));
			
			RealMatrix Y = V.multiply(S).multiply(V.transpose());
			RealMatrix Z = Y.multiply(Ni).multiply(Y);

			EigenDecomposition ed = new EigenDecomposition(Z);
			double[] evals = ed.getRealEigenvalues();
//			IJ.log("evals = " + Matrix.toString(evals));
//			CircleFit.printEigenVectors(ed);
			
			int l = getSmallestPositiveIdx(evals);
//			IJ.log("l = " + l);
			
			RealVector el = ed.getEigenvector(l);
//			IJ.log("el = " + Matrix.toString(el.toArray()));
			
			p = Matrix.solve(Y, el);	// alternatively p = Y^-1 * el
			
//			RealMatrix Yi = MatrixUtils.inverse(Y);
//			p = Yi.operate(el);

		}
		
		return p.toArray();			// p = (A, B, C, D)
	}
	
	
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
}

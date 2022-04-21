package imagingbook.common.geometry.fitting;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;


/**
 * Implements a 2-dimensional Procrustes fit, using the algorithm described in 
 * Shinji Umeyama, "Least-squares estimation of transformation parameters 
 * between two point patterns", IEEE Transactions on Pattern Analysis and 
 * Machine Intelligence 13.4 (Apr. 1991), pp. 376–380.
 * Usage example (also see the {@code main()} method of this class):
 * <pre>
 * Point[] P = ... // create sequence of 2D source points
 * Point[] Q = ... // create sequence of 2D target points
 * ProcrustesFit pf = new ProcrustesFit(P, Q);
 * double err = pf.getError();
 * RealMatrix R = pf.getR();
 * RealVector t = pf.getT();
 * double s = pf.getScale();
 * double err = pf.getError();
 * RealMatrix A = pf.getTransformationMatrix();
 * </pre>
 * 
 * @author W. Burger
 * @version 2021/11/27
 */
public class ProcrustesFit implements LinearFit2D {
	
	private final RealMatrix R;					// orthogonal (rotation) matrix
	private final RealVector t;					// translation vector
	private final double s;						// uniform scale
	
	private final RealMatrix A;					// resulting transformation matrix
	private final double err;					// RMS fitting error
	
	// --------------------------------------------------------------
	
	/**
	 * Convenience constructor, with
	 * parameters {@code allowTranslation}, {@code allowScaling} and {@code forceRotation}
	 * set to {@code true}.
	 * @param P the source points
	 * @param Q the target points
	 */
	public ProcrustesFit(Pnt2d[] P, Pnt2d[] Q) {
		this(P, Q, true, true, true);
	}
	
	/**
	 * Full constructor.
	 * @param P the first point sequence
	 * @param Q the second point sequence
	 * @param allowTranslation if {@code true}, translation (t) between point sets is considered, 
	 * 		otherwise zero translation is assumed
	 * @param allowScaling if {@code true}, scaling (s) between point sets is considered, 
	 * 		otherwise unit scale assumed
	 * @param forceRotation if {@code true}, the orthogonal part of the transformation (Q)
	 * 		is forced to a true rotation and no reflection is allowed
	 */
	public ProcrustesFit(Pnt2d[] P, Pnt2d[] Q, boolean allowTranslation, boolean allowScaling, boolean forceRotation) {
		checkSize(P, Q);
		
		double[] meanP = null;
		double[] meanY = null;
		
		if (allowTranslation) {
			meanP = getMeanVec(P);
			meanY = getMeanVec(Q);
		}
		
		RealMatrix vP = makeDataMatrix(P, meanP);
		RealMatrix vQ = makeDataMatrix(Q, meanY);
		MatrixUtils.checkAdditionCompatible(vP, vQ);	// P, Q of same dimensions?
		
		RealMatrix QPt = vQ.multiply(vP.transpose());
		SingularValueDecomposition svd = new SingularValueDecomposition(QPt);
		
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
			
		double d = (svd.getRank() >= 2) ? det(QPt) : det(U) * det(V);
		
		RealMatrix D = MatrixUtils.createRealIdentityMatrix(2);
		if (d < 0 && forceRotation)
			D.setEntry(1, 1, -1);
		
		R = U.multiply(D).multiply(V.transpose());
		
		double normP = vP.getFrobeniusNorm();
		double normQ = vQ.getFrobeniusNorm();
		
		s = (allowScaling) ? 
				S.multiply(D).getTrace() / sqr(normP) : 1.0;
		
		if (allowTranslation) {
			RealVector ma = MatrixUtils.createRealVector(meanP);
			RealVector mb = MatrixUtils.createRealVector(meanY);
			t = mb.subtract(R.scalarMultiply(s).operate(ma));
		}
		else {
			t = new ArrayRealVector(2);	// zero vector
		}
		
		// make the transformation matrix A
		RealMatrix cR = R.scalarMultiply(s);
		A = MatrixUtils.createRealMatrix(2, 3);
		A.setSubMatrix(cR.getData(), 0, 0);
		A.setColumnVector(2, t);
		
		// calculate the fitting error:
		err = Math.sqrt(sqr(normQ) - sqr(S.multiply(D).getTrace() / normP));	
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Retrieves the estimated scale.
	 * @return The estimated scale (or 1 if {@code allowscaling = false}).
	 */
	public double getScale() {
		return s;
	}
	
	/**
	 * Retrieves the estimated orthogonal (rotation) matrix.
	 * @return The estimated rotation matrix.
	 */
	public double[][] getR() {
		return R.getData();
	}
	
	/**
	 * Retrieves the estimated translation vector.
	 * @return The estimated translation vector.
	 */
	public double[] getT() {
		return t.toArray();
	}
	
	// --------------------------------------------------------
	
	@Override
	public double[][] getTransformationMatrix() {
		return A.getData();
//		return A;
	}
	
	@Override
	public double getError() {
		return err;
	}
	
	/**
	 * Calculates the total error for the estimated fit as
	 * the sum of the squared Euclidean distances between the 
	 * transformed point set X and the reference set Y.
	 * This method is provided for testing as an alternative to
	 * the quicker {@link getError} method.
	 * @param P Sequence of n-dimensional points.
	 * @param Q Sequence of n-dimensional points (reference).
	 * @return The total error for the estimated fit.
	 */
	private double getEuclideanError(Pnt2d[] P, Pnt2d[] Q) {
		int m = Math.min(P.length,  Q.length);
		RealMatrix sR = R.scalarMultiply(s);
		double errSum = 0;
		for (int i = 0; i < m; i++) {
			RealVector p = new ArrayRealVector(P[i].toDoubleArray());
			RealVector q = new ArrayRealVector(Q[i].toDoubleArray());
			RealVector pp = sR.operate(p).add(t);
			//System.out.format("p=%s, q=%s, pp=%s\n", p.toString(), q.toString(), pp.toString());
			double e = pp.subtract(q).getNorm();
			errSum = errSum + e * e;
		}
		return Math.sqrt(errSum);	// correct!
	}
	
	// -----------------------------------------------------------------
	
	private double det(RealMatrix M) {
		return new LUDecomposition(M).getDeterminant();
	}
	
	private double[] getMeanVec(Pnt2d[] points) {
		double sumX = 0;
		double sumY = 0;
		for (Pnt2d p : points) {
			sumX = sumX + p.getX();
			sumY = sumY + p.getY();
		}
		return new double[] {sumX / points.length, sumY / points.length};
	}
	
	private RealMatrix makeDataMatrix(Pnt2d[] points, double[] meanX) {
		RealMatrix M = MatrixUtils.createRealMatrix(2, points.length);
		RealVector mean = MatrixUtils.createRealVector(meanX);
		int i = 0;
		for (Pnt2d p : points) {
			RealVector cv = p.toRealVector();
//			RealVector cv = MatrixUtils.createRealVector(p.toDoubleArray());
			if (meanX != null) {
				cv = cv.subtract(mean);
			}
			M.setColumnVector(i, cv);
			i++;
		}
		return M;
	}
	
//	private void printSVD(SingularValueDecomposition svd) {
//		RealMatrix U = svd.getU();
//		RealMatrix S = svd.getS();
//		RealMatrix V = svd.getV();
//		System.out.println("------ SVD ---------------");
//		System.out.println("U = " + Matrix.toString(U.getData()));
//		System.out.println("S = " + Matrix.toString(S.getData()));
//		System.out.println("V = " + Matrix.toString(V.getData()));
//		System.out.println("--------------------------");
//	}
	
	
	private void checkSize(Pnt2d[] P, Pnt2d[] Q) {
		if (P.length < 3 || Q.length < 3) {
			throw new IllegalArgumentException("At least 3 point pairs are required to calculate this fit");
		}
	}

	// --------------------------------------------------------------------------------
	
	private static double roundToDigits(double x, int ndigits) {
		int d = (int) Math.pow(10, ndigits);
		return Math.rint(x * d) / d;
	}

	public static void main(String[] args) {
		PrintPrecision.set(6);
		int NDIGITS = 1;
		
		boolean allowTranslation = true;
		boolean allowScaling = true;
		boolean forceRotation = true;
		
		double a = 0.6;
		double[][] R0data =
			{{ Math.cos(a), -Math.sin(a) },
			 { Math.sin(a),  Math.cos(a) }};
		
		RealMatrix R0 = MatrixUtils.createRealMatrix(R0data);
		double[] t0 = {4, -3};
		double s = 3.5;
		
		System.out.format("original alpha: a = %.6f\n", a);
		System.out.println("original rotation: R = \n" + Matrix.toString(R0.getData()));
		System.out.println("original translation: t = " + Matrix.toString(t0));
		System.out.format("original scale: s = %.6f\n", s);
		System.out.println();
		
		Pnt2d[] P = {
				PntInt.from(2, 5),
				PntInt.from(7, 3),
				PntInt.from(0, 9),
				PntInt.from(5, 4)
		};
		
		Pnt2d[] Q = new Pnt2d[P.length];
		
		for (int i = 0; i < P.length; i++) {
			Pnt2d q = PntDouble.from(R0.operate(P[i].toDoubleArray()));
			// noise!
			double qx = roundToDigits(s * q.getX() + t0[0], NDIGITS);
			double qy = roundToDigits(s * q.getY() + t0[1], NDIGITS);
			Q[i] = Pnt2d.PntDouble.from(qx, qy);
		}
		
		//P[0] = Point.create(2, 0);	// to provoke a large error
		
		ProcrustesFit pf = new ProcrustesFit(P, Q, allowTranslation, allowScaling, forceRotation);

		double[][] R = pf.getR();
		System.out.format("estimated alpha: a = %.6f\n", Math.acos(R[0][0]));
		System.out.println("estimated rotation: R = \n" + Matrix.toString(R));
		double[] T = pf.getT();
		System.out.println("estimated translation: t = " + Matrix.toString(T));
		System.out.format("estimated scale: s = %.6f\n", pf.getScale());
		
		System.out.println();
		System.out.format("RMS fitting error = %.6f\n", pf.getError());
		System.out.format("euclidean error (test) = %.6f\n", pf.getEuclideanError(P, Q));
		
		double[][] A = pf.getTransformationMatrix();
		System.out.println("transformation matrix: A = \n" + Matrix.toString(A));
	}

	/*
	original alpha: a = 0.600000
	original rotation: R = 
	{{0.825336, -0.564642}, 
	{0.564642, 0.825336}}
	original translation: t = {4.000000, -3.000000}
	original scale: s = 3.500000
	
	estimated alpha: a = 0.599589
	estimated rotation: R = 
	{{0.825568, -0.564303}, 
	{0.564303, 0.825568}}
	estimated translation: t = {3.980905, -3.011055}
	estimated scale: s = 3.500560
	
	fitting error = 0.048079
	euclidean error (test) = 0.048079
	transformation matrix: A = 
	{{2.889950, -1.975377, 3.980905}, 
	{1.975377, 2.889950, -3.011055}}
	 */
}


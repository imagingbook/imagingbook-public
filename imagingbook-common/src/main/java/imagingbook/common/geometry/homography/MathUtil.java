package imagingbook.common.geometry.homography;

import java.util.Locale;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;

/**
 * Static utility methods used for camera calibration.
 * @author WB
 *
 */
public class MathUtil {

	static {
		Locale.setDefault(Locale.US);
	}

	public static void print(String name, RealMatrix M) {
		System.out.println(name);
		for (int r = 0; r < M.getRowDimension(); r++) {
			RealVector row = M.getRowVector(r);
			System.out.println("  " + r + " " + row.toString());
		}
	}

	public static void print(String name, RealVector v) {
		System.out.println(name + v.toString());
	}

	// -------------------------------------------------------

	public static RealVector[] getColumnVectors(RealMatrix M) {
		final int ncols = M.getColumnDimension();
		RealVector[] colVecs = new  RealVector[ncols];
		for (int col = 0; col < ncols; col++) {
			colVecs[col] = M.getColumnVector(col);
		}
		return colVecs;
	}

	public static RealVector crossProduct3x3(RealVector A, RealVector B) {
		final double[] a = A.toArray();
		final double[] b = B.toArray();
		final double[] c = {
				a[1] * b[2] - b[1] * a[2],
				a[2] * b[0] - b[2] * a[0],
				a[0] * b[1] - b[0] * a[1]
		};
		return MatrixUtils.createRealVector(c);
	}

	public static double det(RealMatrix M) {
		return new LUDecomposition(M).getDeterminant();
	}

	public static RealVector getRowPackedVector(RealMatrix A) {
		double[][] AA = A.getData();
		double[] V = new double[AA.length * AA[0].length];
		int k = 0;
		for (int i = 0; i < AA.length; i++) {
			for (int j = 0; j < AA[0].length; j++) {
				V[k++] = AA[i][j];
			}
		}
		return MatrixUtils.createRealVector(V);
	}

	public static RealMatrix fromRowPackedVector(RealVector V, int rows, int columns) {
		double[][] AA = new double[rows][columns];
		double[] data = V.toArray();
		int k = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				AA[i][j] = data[k++];
			}
		}
		return MatrixUtils.createRealMatrix(AA);
	}


	//	/**               
	//	 * Finds a nontrivial solution (x) to the homogeneous linear system M . x = 0.
	//	 * @param M	
	//	 * @param fromRight
	//	 * @return
	//	 */
	//	@Deprecated 	// use the simpler version below
	//	public static RealVector solveHomogeneousSystemOLD(RealMatrix M, boolean fromRight) {
	//		SingularValueDecomposition svd = new SingularValueDecomposition(M);
	//		RealMatrix U = svd.getU();
	//		RealMatrix V = svd.getV();
	//		
	//		// dimensions of the original (decomposed) matrix;
	//		int svdnumRows = U.getRowDimension();
	//		int svdnumCols = V.getColumnDimension();
	//		double[] s = svd.getSingularValues();
	//		
	//		RealMatrix A = fromRight ? svd.getVT() /*V is transposed!*/ : svd.getU();
	//		      
	//		// find the row/column index of the smallest singular value (diagonal)
	//		int minIndex = -1;
	//		if (fromRight && svdnumCols > svdnumRows)
	//			minIndex = svdnumCols - 1;
	//		else if (!fromRight && svdnumCols < svdnumRows)
	//			minIndex = svdnumRows - 1;
	//		else {
	//			// find the index of the smallest singular value
	//			double minValue = Double.MAX_VALUE;
	//			for (int i = 0; i < s.length; i++) {
	//				if (s[i] < minValue) {
	//					minValue = s[i];
	//					minIndex = i;
	//				}
	//			}
	//		}
	//		//System.out.println("nullspace: smallestIndex = " + minIndex);
	//		
	//		RealVector nullVec = fromRight ? A.getRowVector(minIndex) : A.getColumnVector(minIndex);
	//		return nullVec;
	//	}


	/**               
	 * Finds a nontrivial solution (x) to the homogeneous linear system M . x = 0
	 * by singular-value decomposition. If M has more rows than columns, the
	 * system of equations is overdetermined. In this case the returned solution
	 * minimizes the residual ||M . x|| in the least-squares sense.
	 * 
	 * @param A	the original matrix.
	 * @return the solution vector x.
	 */
	public static RealVector solveHomogeneousSystem(RealMatrix A) {
		SingularValueDecomposition svd = new SingularValueDecomposition(A);
		RealMatrix V = svd.getV();
		RealVector x = V.getColumnVector(V.getColumnDimension() - 1);
		return x;
	}

	public static double[] toHomogeneous(double[] cvec) {
		double[] hvec = new double[cvec.length + 1];
		for (int i = 0; i < cvec.length; i++) {
			hvec[i] = cvec[i];
			hvec[hvec.length - 1] = 1;
		}
		return hvec;
	}

	public static double[] toCartesian(double[] hvec) {
		double[] cvec = new double[hvec.length - 1];
		final double s = 1 / hvec[hvec.length - 1];	// TODO: check for zero factor
		for (int i = 0; i < hvec.length - 1; i++) {
			cvec[i] = s * hvec[i];
		}
		return cvec;
	}

	//	/**  
	//	 * implements a left (pre-) matrix-vector multiplication:  A . x -> y
	//	 * Matrix A is of size (m,n), column vector x is of length n.
	//	 * The result y is a vector of length m.
	//	 * non-destructive
	//	 */
	//	public static double[] multiply(final double[][] A, final double[] x) {
	//		double[] y = new double[A.length];
	//		multiplyD(A, x, y);
	//		return y;
	//	}

	//	// destructive
	//	public static void multiplyD(final double[][] A, final double[] x, double[] y) {
	//		final int m = A.length;
	//		final int n = A[0].length;
	//		if (x.length != n || y.length != m) 
	//			throw new IllegalArgumentException("incompatible matrix-vector dimensions");
	//		for (int i = 0; i < m; i++) {
	//			double s = 0;
	//			for (int j = 0; j < n; j++) {
	//				s = s + A[i][j] * x[j];
	//			}
	//			y[i] = s;
	//		}
	//	}

	public static String getInfo(double[][] A) {
		return String.format("Matrix: rows=%d, columns=%d", A.length, A[0].length);
	}

	public static String getInfo(double[] x) {
		return String.format("Vector: dimension=%d", x.length);
	}

	public static double mean(double[] x) {
		final int n = x.length;
		if (n == 0) 
			return 0;
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i];
		}
		return sum / n;
	}

	/**
	 * 
	 * @param x a sequence of real values 
	 * @return the variance of the values in x (sigma^2)
	 */
	public static double variance(double[] x) {
		final int n = x.length;
		if (n == 0) 
			return 0;
		double sum = 0;
		double sum2 = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i];
			sum2 = sum2 + x[i] * x[i];
		}
		return (sum2 - (sum * sum) / n) / n;
	}

	public static RealMatrix getNormalisationMatrix(Pnt2d[] pnts) {
		final int N = pnts.length;
		double[] x = new double[N];
		double[] y = new double[N];

		for (int i = 0; i < N; i++) {
			x[i] = pnts[i].getX();
			y[i] = pnts[i].getY();
		}

		// calculate the means in x/y
		double meanx = MathUtil.mean(x);
		double meany = MathUtil.mean(y);

		// calculate the variances in x/y
		double varx = MathUtil.variance(x);
		double vary = MathUtil.variance(y);

		double sx = Math.sqrt(2 / varx);
		double sy = Math.sqrt(2 / vary);

		RealMatrix matrixA = MatrixUtils.createRealMatrix(new double[][] {
			{ sx,  0, -sx * meanx},
			{  0, sy, -sy * meany},
			{  0,  0,           1 }});

		return matrixA;
	}


	// Utility method
	public static double[] transform(double[] p, RealMatrix M3x3) {
		if (p.length != 2) {
			throw new IllegalArgumentException("transform(): vector p must be of length 2 but is " + p.length);
		}
		double[] pA = MathUtil.toHomogeneous(p);
		double[] pAt = M3x3.operate(pA);
		return MathUtil.toCartesian(pAt); // need to de-homogenize, since pAt[2] == 1?
	}

	// Conversions between Double[] and double[]

	//	// We want to pass a 'Deque' instance, thus not just 'List' parameter!
	//	public static double[] toArray(Collection<Double> c) {
	//		double[] model = new double[c.size()];
	//		int i = 0;
	//		for (Double x : c) {
	//			System.out.format("Param %d = %.6f\n", i, x);
	//			if (x != null) {
	//				model[i] = x.doubleValue();
	//				i++;
	//			}
	//		}
	//		return model;
	//	}

	//	public static Deque<Double> toList(double[] a) {
	//		Deque<Double> A = new ArrayDeque<Double>(a.length);
	//		for (int i = 0; i < a.length; i++) {
	//			A.add(a[i]);
	//		}
	//		return A;
	//	}

	// ---------------------------------------------------------------

	public static Quaternion toQuaternion(Rotation r) {
		return new Quaternion(r.getQ0(), r.getQ1(), r.getQ2(), r.getQ3());
	}

	public static Rotation toRotation(Quaternion q) {
		return new Rotation(q.getQ0(), q.getQ1(), q.getQ2(), q.getQ3(), true);
	}

	// ---------------------------------------------------------------
	
	public static void main(String[] args) {
		System.out.println("Solving A.x = 0 (for A being n x 3, exact and least squares)");
		{
			double[][] A = {{1, 2, 3}, {4, 5, 6}, {9, 8, 0}};
			RealVector x = solveHomogeneousSystem(MatrixUtils.createRealMatrix(A));
			System.out.println("\nA = \n" + Matrix.toString(A));
			System.out.println("Solution = " + x.toString());
			// Solution = {0.649964237; -0.7338780288; 0.1974070146}
		}
		{
			double[][] A = {{1, 2, 3}, {4, 5, 6}, {9, 8, 0}, {-3, 7, 2}};
			RealVector x = solveHomogeneousSystem(MatrixUtils.createRealMatrix(A));
			System.out.println("\nA = \n" + Matrix.toString(A));
			System.out.println("Solution = " + x.toString());
			// Solution = {0.2311474408; -0.5001889485; 0.8344949828}
		}

	}

}

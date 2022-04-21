/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.sqr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import imagingbook.common.math.Arithmetic.DivideByZeroException;
import imagingbook.common.math.eigen.RealEigensolver;

/**
 * <p>This class defines a set of static methods for calculations
 * with vectors and matrices using native Java arrays without any enclosing 
 * objects structures. 
 * Matrices are simply two-dimensional arrays A[r][c], where r is the row index
 * and c is the column index (as common in linear algebra). This means that
 * matrices are really vectors of row vectors.
 * Only arrays of type {@code float} and {@code double} are supported.
 * All matrices are assumed to be rectangular (i.e., all rows are of equal length).</p>
 * 
 * <p>Methods named with a trailing 'D' (e.g., {@link #multiplyD(double, double[])}) 
 * operate destructively, i.e., modify one of the passed arguments.</p>
 * 
 * <p>Most methods are self-explanatory and are therefore left undocumented.</p>
 * 
 * @see RealEigensolver
 * @author W. Burger
 * @version 2021/10/10
 */
public abstract class Matrix {
	
	/** Locale used for printing decimal numbers. */
	public static Locale PrintLocale = Locale.US;
	
	/** Character used to separate successive vector and matrix elements. */
	public static char SeparationChar = ',';
	
	/** Leading delimiter used for lists of vector and matrix elements. */
	public static char LeftDelimitChar = '{';
	
	/** Trailing delimiter used for lists of vector and matrix elements. */
	public static char RightDelimitChar = '}';
	
	// ----  Matrix creation -----------------------------
	
	public static double[][] makeDoubleMatrix(final int rows, final int cols, final double... values) {
		final double[][] A = new double[rows][cols];
		if (values == null || values.length == 0) {
			return A;
		}
		else if (values.length != rows * cols) {
			throw new IllegalArgumentException("wrong number of matrix values: " 
					+ values.length + " instead of " + rows*cols);
		}
		
		for (int i = 0, r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				A[r][c] = values[i];
				i++;
			}
		}		
		return A;
	}
	
	public static float[][] makeFloatMatrix(final int rows, final int cols, final float... values) {
		final float[][] A = new float[rows][cols];
		if (values == null || values.length == 0) {
			return A;
		}
		else if (values.length != rows * cols) {
			throw new IllegalArgumentException("wrong number of matrix values: " 
					+ values.length + " instead of " + rows*cols);
		}	
		
		for (int i = 0, r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				A[r][c] = values[i];
				i++;
			}
		}		
		return A;
	}
	
	public static RealMatrix makeRealMatrix(final int rows, final int cols, final double... values) {
		return MatrixUtils.createRealMatrix(makeDoubleMatrix(rows, cols, values));
	}
	
	public static double[] makeDoubleVector(double... values) {
		return values;
	}
	
	public static float[] makeFloatVector(float... values) {
		return values;
	}
	
	public static RealVector makeRealVector(double... values) {
		//return MatrixUtils.createRealVector(values);
		return new ArrayRealVector(values);
	}
	
	// Specific vector/matrix creation:
	
	/**
	 * Creates and returns a new zero-valued {@code double} vector of the 
	 * specified length.
	 * @param length the length of the vector
	 * @return a vector with zero values
	 */
	public static double[] zeroVector(int length) {
		if (length < 1)
			throw new IllegalArgumentException("vector size cannot be < 1");
		return new double[length];
	}
	
	/**
	 * Creates and returns a new identity matrix of the 
	 * specified size.
	 * @param size the size of the matrix
	 * @return an identity matrix
	 */
	public static double[][] idMatrix(int size) {
		if (size < 1)
			throw new IllegalArgumentException("matrix size cannot be < 1");
		double[][] A = new double[size][size];
		for (int i = 0; i < size; i++) {
			A[i][i] = 1;
		}
		return A;
	}
	
	// Matrix properties -------------------------------------

	public static int getNumberOfRows(double[][] A) {
		return A.length;
	}
	
	public static int getNumberOfColumns(double[][] A) {
		return A[0].length;
	}
	
	public static int getNumberOfRows(float[][] A) {
		return A.length;
	}
	
	public static int getNumberOfColumns(float[][] A) {
		return A[0].length;
	}
	
	// Extract rows or columns
	
	public static double[] getRow(double[][] A, int r) {
		return A[r].clone();
	}
	
	public static double[] getColumn(double[][] A, int c) {
		final int rows = A.length;
		double[] col = new double[rows];
		for (int r = 0; r < rows; r++) {
			col[r] = A[r][c];
		}
		return col;
	}
	
	// Checking vector/matrix dimensions
	
	/**
	 * Checks if all rows of the given matrix have the same length.
	 * @param A a matrix
	 * @return true iff the matrix is rectangular
	 */
	public static boolean isRectangular(float[][] A) {
		final int nCols = A[0].length;
		for (int i = 1; i < A.length; i++) {
			if (A[i].length != nCols) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all rows of the given matrix have the same length.
	 * @param A a matrix
	 * @return true iff the matrix is rectangular
	 */
	public static boolean isRectangular(double[][] A) {
		final int nCols = A[0].length;
		for (int i = 1; i < A.length; i++) {
			if (A[i].length != nCols) {
				return false;
			}
		}
		return true;
	}
	
	/** Checks it the given matrix has the same number of rows
	 * and columns.
	 * @param A a matrix
	 * @return true iff the matrix is square
	 */
	public static boolean isSquare(double[][] A) {
		return A.length > 0 && A.length == A[0].length;
	}
	
	/** Checks it the given matrix has the same number of rows
	 * and columns.
	 * @param A a matrix
	 * @return true iff the matrix is square
	 */
	public static boolean isSquare(float[][] A) {
		return A.length > 0 && A.length == A[0].length;
	}
	
	/**
	 * Checks is the given square matrix is non-singular.
	 * @param A a square matrix
	 * @return true if the matrix is non-singular
	 */
	public static boolean isNonSingular(double[][] A) throws NonsquareMatrixException {
		if (!Matrix.isSquare(A)) {
			throw new NonsquareMatrixException();
		}		
		DecompositionSolver solver = new LUDecomposition(new Array2DRowRealMatrix(A)).getSolver();		
		return solver.isNonSingular();
	}
	
	/**
	 * Checks if the given vectors have the same length.
	 * @param a first vector
	 * @param b second vector
	 * @return true iff both vectors have the same length
	 */
	public static boolean sameSize(double[] a, double[] b) {
		return a.length == b.length;
	}
	
	/**
	 * Checks if the given vectors have the same length.
	 * @param a first vector
	 * @param b second vector
	 * @return true iff both vectors have the same length
	 */
	public static boolean sameSize(float[] a, float[] b) {
		return a.length == b.length;
	}
	
	/**
	 * Checks if the given matrices have the same size.
	 * @param A first matrix
	 * @param B second matrix
	 * @return true iff both matrices have the same size
	 */
	public static boolean sameSize(double[][] A, double[][] B) {
		return (A.length == B.length) && (A[0].length == B[0].length);
	}
	
	/**
	 * Checks if the given matrices have the same size.
	 * @param A first matrix
	 * @param B second matrix
	 * @return true iff both matrices have the same size
	 */
	public static boolean sameSize(float[][] A, float[][] B) {
		return (A.length == B.length) && (A[0].length == B[0].length);
	}

	// Matrix and vector duplication ------------------------------

	public static double[] duplicate(final double[] A) {
		return A.clone();
	}
	
	public static float[] duplicate(final float[] A) {
		return A.clone();
	}

	public static double[][] duplicate(final double[][] A) {
		final int m = A.length;
		final double[][] B = new double[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	public static float[][] duplicate(final float[][] A) {
		final int m = A.length;
		float[][] B = new float[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	// ----- double <-> float conversions -----------------
	
	public static float[] toFloat(final double[] A) {
		final int m = A.length;
		final float[] B = new float[m];
		for (int i = 0; i < m; i++) {
			B[i] = (float) A[i];
		}
		return B;
	}
	
	public static float[][] toFloat(final double[][] A) {
		final int m = A.length;
		final int n = A[0].length;
		final float[][] B = new float[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = (float) A[i][j];
			}
		}
		return B;
	}
	
	public static double[] toDouble(final float[] A) {
		final int m = A.length;
		final double[] B = new double[m];
		for (int i = 0; i < m; i++) {
			B[i] = A[i];
		}
		return B;
	}
	
	public static double[][] toDouble(final float[][] A) {
		final int m = A.length;
		final int n = A[0].length;
		final double[][] B = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = A[i][j];
			}
		}
		return B;
	}
	
	// ----------- fill operations (destructive) ------------------------
	
	public static void fillD(final float[] A, float val) {
		Arrays.fill(A, val);
	}
	
	public static void fillD(final float[][] A, float val) {
		for (int i = 0; i < A.length; i++) { 
			Arrays.fill(A[i], val);
		}
	}
	
	public static void fillD(final double[] A, double val) {
		Arrays.fill(A, val);
	}
	
	public static void fillD(final double[][] A, double val) {
		for (int i = 0; i < A.length; i++) { 
			Arrays.fill(A[i], val);
		}
	}
	
	// Element-wise arithmetic -------------------------------
	
	// non-destructive
	public static double[] add(final double[] a, final double[] b) throws IncompatibleDimensionsException {
		double[] c = b.clone();
		addD(a, c);
		return c;
	}
	
	// destructive, b is modified
	public static void addD(final double[] a, final double[] b) throws IncompatibleDimensionsException {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i] + b[i];
		}
	}
	
	// non-destructive
	public static float[] add(final float[] a, final float[] b) throws IncompatibleDimensionsException {
		float[] c = b.clone();
		addD(a, c);
		return c;
	}
	
	// destructive, b is modified
	public static void addD(final float[] a, final float[] b) throws IncompatibleDimensionsException {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i] + b[i];
		}
	}

	// non-destructive
	public static double[][] add(final double[][] A, final double[][] B) throws IncompatibleDimensionsException {
		double[][] C = duplicate(B);
		addD(A, C);
		return C;
	}
	
	// destructive, B is modified
	public static void addD(final double[][] A, final double[][] B) throws IncompatibleDimensionsException {
		if (!sameSize(A, B))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				B[i][j] = A[i][j] + B[i][j];
			}
		}
	}
	
	// non-destructive
	public static float[][] add(final float[][] A, final float[][] B) throws IncompatibleDimensionsException {
		float[][] C = duplicate(B);
		addD(A, C);
		return C;
	}
	
	// destructive, B is modified
	public static void addD(final float[][] A, final float[][] B) throws IncompatibleDimensionsException {
		if (!sameSize(A, B))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				B[i][j] = A[i][j] + B[i][j];
			}
		}
	}
	
	// ------------------------------------------------
	
	// non-destructive a - b
	public static double[] subtract(final double[] a, final double[] b) throws IncompatibleDimensionsException {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		final int n = a.length;
		double[] c = new double[n];
		for (int i = 0; i < n; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}
	
	// non-destructive a - b
	public static float[] subtract(final float[] a, final float[] b) throws IncompatibleDimensionsException {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		final int n = a.length;
		float[] c = new float[n];
		for (int i = 0; i < n; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}

	// Scalar multiplications -------------------------------

	// non-destructive
	public static double[] multiply(final double s, final double[] a) {
		double[] b = a.clone();
		multiplyD(s, b);
		return b;
	}
	
	// destructive
	public static void multiplyD(final double s, final double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = a[i] * s;
		}
	}
	

	// non-destructive
	public static double[][] multiply(final double s, final double[][] A) {
		double[][] B = duplicate(A);
		multiplyD(s, B);
		return B;
	}
	
	public static void multiplyD(final double s, final double[][] A) {
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = A[i][j] * s;
			}
		}
	}
	
	// non-destructive
	public static float[] multiply(final float s, final float[] A) {
		float[] B = duplicate(A);
		multiplyD(s, B);
		return B;
	}
	
	// destructive
	public static void multiplyD(final float s, final float[] A) {
		for (int i = 0; i < A.length; i++) {
			A[i] = A[i] * s;
		}
	}

	// non-destructive
	public static float[][] multiply(final float s, final float[][] A) {
		float[][] B = duplicate(A);
		multiplyD(s, B);
		return B;
	}
	
	// destructive
	public static void multiplyD(final float s, final float[][] A) {
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = A[i][j] * s;
			}
		}
	}
	
	// matrix-vector multiplications ----------------------------------------

	/**
	 * Multiplies a vector x with a matrix A from the right, i.e., y = x * A,
	 * where x is treated as a row vector and the result y is also a row vector.
	 * @param x a (row) vector of length m
	 * @param A a matrix of size (m,n)
	 * @return a (row) vector of length n
	 */
	public static double[] multiply(final double[] x, final double[][] A) {
		double[] y = new double[getNumberOfColumns(A)];
		multiplyD(x, A, y);
		return y;
	}
	
	/**
	 * Destructive version of {@link #multiply(double[], double[][])}.
	 * @param x a (row) vector of length m
	 * @param A matrix of size (m,n)
	 * @param y a (row) vector of length n
	 */
	public static void multiplyD(final double[] x, final double[][] A, double[] y) throws SameSourceTargetException {
		if (x == y) 
			throw new SameSourceTargetException();
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != m || y.length != n) 
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < n; i++) {
			double s = 0;
			for (int j = 0; j < m; j++) {
				s = s + x[j] * A[j][i];
			}
			y[i] = s;
		}
	}

	/**
	 * Multiplies a matrix A with a vector x from the right, i.e., y = A * x,
	 * where x is treated as a column vector and the result y is also a column vector.
	 * @param x a (column) vector of length n
	 * @param A a matrix of size (m,n)
	 * @return a (column) vector of length m
	 */
	public static double[] multiply(final double[][] A, final double[] x) {
		double[] y = new double[getNumberOfRows(A)];
		multiplyD(A, x, y);
		return y;
	}
	
	/**
	 * Destructive version of {@link #multiply(double[][], double[])}.
	 * @param A matrix of size (m,n)
	 * @param x a (column) vector of length n
	 * @param y a (column) vector of length m
	 */
	public static void multiplyD(final double[][] A, final double[] x, double[] y) throws SameSourceTargetException, IncompatibleDimensionsException {
		if (x == y) 
			throw new SameSourceTargetException();
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != n || y.length != m) 
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < m; i++) {
			double s = 0;
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * x[j];
			}
			y[i] = s;
		}
	}
	
	/**
	 * Multiplies a matrix A with a vector x from the right, i.e., y = A * x,
	 * where x is treated as a column vector and the result y is also a column vector.
	 * @param x a (column) vector of length n
	 * @param A a matrix of size (m,n)
	 * @return a (column) vector of length m
	 */
	public static float[] multiply(final float[][] A, final float[] x) {
		float[] y = new float[getNumberOfRows(A)];
		multiplyD(A, x, y);
		return y;
	}
	
	/**
	 * Destructive version of {@link #multiply(float[][], float[])}.
	 * @param A matrix of size (m,n)
	 * @param x a (column) vector of length n
	 * @param y a (column) vector of length m
	 */
	public static void multiplyD(final float[][] A, final float[] x, float[] y) throws SameSourceTargetException, IncompatibleDimensionsException {
		if (x == y) 
			throw new SameSourceTargetException();
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != n || y.length != m) 
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < m; i++) {
			double s = 0;
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * x[j];
			}
			y[i] = (float) s;
		}
	}
	

	
	// Matrix-matrix products ---------------------------------------
	
	// returns A * B (non-destructive)
	public static double[][] multiply(final double[][] A, final double[][] B) {
		int m = getNumberOfRows(A);
		int q = getNumberOfColumns(B);
		double[][] C = makeDoubleMatrix(m, q);
		multiplyD(A, B, C);
		return C;
	}
	
	// A * B -> C (destructive)
	public static void multiplyD(final double[][] A, final double[][] B, final double[][] C) throws SameSourceTargetException, IncompatibleDimensionsException {
		if (A == C || B == C) 
			throw new SameSourceTargetException();
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		final int nB = getNumberOfColumns(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < mA; i++) {
			for (int j = 0; j < nB; j++) {
				double s = 0;
				for (int k = 0; k < nA; k++) {
					s = s + A[i][k] * B[k][j];
				}
				C[i][j] = s;
			}
		}
	}
	
	// returns A * B (non-destructive)
	public static float[][] multiply(final float[][] A, final float[][] B) {
		// TODO: also check nA = mB
		final int mA = getNumberOfRows(A);
		final int nB = getNumberOfColumns(B);
		float[][] C = makeFloatMatrix(mA, nB);
		multiplyD(A, B, C);
		return C;
	}

	// A * B -> C (destructive)
	public static void multiplyD(final float[][] A, final float[][] B, final float[][] C) throws SameSourceTargetException, IncompatibleDimensionsException {
		if (A == C || B == C) 
			throw new SameSourceTargetException();
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		final int nB = getNumberOfColumns(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < mA; i++) {
			for (int j = 0; j < nB; j++) {
				float s = 0;
				for (int k = 0; k < nA; k++) {
					s = s + A[i][k] * B[k][j];
				}
				C[i][j] = s;
			}
		}
	}
	
	// Vector-vector products ---------------------------------------
	
	/**
	 * Calculates and returns the dot (inner or scalar) product of two vectors,
	 * which must have the same length.
	 * @param x first vector
	 * @param y second vector
	 * @return the dot product
	 */
	public static double dotProduct(final double[] x, final double[] y) throws IncompatibleDimensionsException {
		if (!sameSize(x, y))
			throw new IncompatibleDimensionsException();
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i] * y[i];
		}
		return sum;
	}
	
	// A is considered a column vector, B is a row vector, of length m, n, resp.
	// returns a matrix M of size (m,n).
	/**
	 * Calculates and returns the outer product of two vectors, which is a
	 * matrix of size (m,n), where m is the length of the first vector and
	 * m is the length of the second vector.
	 * @param x first vector (of length m)
	 * @param y second vector (of length n)
	 * @return the outer product (matrix)
	 */
	public static double[][] outerProduct(final double[] x, final double[] y) {
		final int m = x.length;
		final int n = y.length;
		final double[][] M = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				M[i][j] = x[i] * y[j];
			}
		}
		return M;
	}

	//  Vector norms ---------------------------------------------------

	/**
	 * Calculates and returns the L1 norm of the given vector.
	 * @param x a vector
	 * @return the L1 norm of the vector
	 */
	public static double normL1(final double[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + Math.abs(val);
		}
		return sum;
	}

	/**
	 * Calculates and returns the L2 norm of the given vector.
	 * @param x a vector
	 * @return the L2 norm of the vector
	 */
	public static double normL2(final double[] x) {
		return Math.sqrt(normL2squared(x));
	}

	/**
	 * Calculates and returns the squared L2 norm of the given vector.
	 * The squared norm is less costly to calculate (no square root
	 * needed) than the L2 norm and is thus often used for efficiency. 
	 * @param x a vector
	 * @return the squared L2 norm of the vector
	 */
	public static double normL2squared(final double[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + (val * val);
		}
		return sum;
	}
	
	/**
	 * Calculates and returns the L1 norm of the given vector.
	 * @param x a vector
	 * @return the L1 norm of the vector
	 */
	public static float normL1(final float[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + Math.abs(val);
		}
		return (float) sum;
	}

	/**
	 * Calculates and returns the L2 norm of the given vector.
	 * @param x a vector
	 * @return the L2 norm of the vector
	 */
	public static float normL2(final float[] x) {
		return (float) Math.sqrt(normL2squared(x));
	}

	/**
	 * Calculates and returns the squared L2 norm of the given vector.
	 * The squared norm is less costly to calculate (no square root
	 * needed) than the L2 norm and is thus often used for efficiency. 
	 * @param x a vector
	 * @return the squared L2 norm of the vector
	 */
	public static float normL2squared(final float[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + (val * val);
		}
		return (float) sum;
	}
	
	// Normalize vectors
	
	public static double[] normalize(final double[] x) {
		double[] xx = duplicate(x);
		normalizeD(xx);
		return xx;
	}
	
	public static void normalizeD(final double[] x) {
		multiplyD(1.0 / normL2(x), x);
	}
	
	// Distance between vectors ---------------------------------------
	
	/** 
	 * Calculates the L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param x first vector
	 * @param y second vector
	 * @return the distance
	 */
	public static double distL2(double[] x, double[] y) {
		return Math.sqrt(distL2squared(x, y));
	}
	
	/** 
	 * Calculates the squared L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param x first vector
	 * @param y second vector
	 * @return the squared distance
	 */
	public static double distL2squared(double[] x, double[] y) {
		double sum = 0;
		for  (int i = 0; i < x.length; i++) {
			sum = sum + sqr(x[i] - y[i]);
		}
		return sum;
	}
	
	/** 
	 * Calculates the L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param x first vector
	 * @param y second vector
	 * @return the distance
	 */
	public static float distL2(float[] x, float[] y) {
		return (float) Math.sqrt(distL2squared(x, y));
	}
	
	/** 
	 * Calculates the squared L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param x first vector
	 * @param y second vector
	 * @return the squared distance
	 */
	public static float distL2squared(float[] x, float[] y) {
		double sum = 0;
		for  (int i = 0; i < x.length; i++) {
			sum = sum + sqr(x[i] - y[i]);
		}
		return (float) sum;
	}
	
	// Matrix (Froebenius) norm ---------------------------------------
	
	/**
	 * Calculates and returns the Froebenius norm of the given matrix.
	 * @param A a matrix
	 * @return the norm of the matrix
	 */
	public static double norm(final double[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		double s = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * A[i][j];
			}
		}
		return Math.sqrt(s);
	}

	// Summation --------------------------------------------------

	public static double sum(final double[] x) {
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i];
		}
		return sum;
	}
	
	public static double sum(final double[][] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				sum = sum + A[i][j];
			}
		}
		return sum;
	}
	
	public static float sum(final float[] x) {
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i];
		}
		return (float) sum;
	}
	
	public static double sum(final float[][] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				sum = sum + A[i][j];
			}
		}
		return sum;
	}
	
	// --------------------------------------------------
	
	/**
	 * Calculates the sum of the elements in the specified matrix row.
	 * @param A a matrix
	 * @param row the row index
	 * @return the sum of the row's elements
	 */
	public static double sumRow(final double[][] A, final int row) {
		return sum(A[row]);
	}
	
	/**
	 * Calculates the sum of the elements in the specified matrix row.
	 * @param A a matrix
	 * @param row the row index
	 * @return the sum of the row's elements
	 */
	public static float sumRow(final float[][] A, final int row) {
		return sum(A[row]);
	}
	

	/**
	 * Calculates the sum of the elements in the specified matrix column.
	 * @param A a matrix
	 * @param col the column index
	 * @return the sum of the column's elements
	 */
	public static double sumColumn(final double[][] A, final int col) {
		double sum = 0;
		for (int r = 0; r < A.length; r++) {
			sum = sum + A[r][col];
		}
		return sum;
	}
	
	/**
	 * Calculates the sum of the elements in the specified matrix column.
	 * @param A a matrix
	 * @param col the column index
	 * @return the sum of the column's elements
	 */
	public static float sumColumn(final float[][] A, final int col) {
		double sum = 0;
		for (int r = 0; r < A.length; r++) {
			sum = sum + A[r][col];
		}
		return (float) sum;
	}
	
	/**
	 * Calculates the sums of all matrix rows and returns 
	 * them as a vector with one element per row.
	 * @param A a matrix of size (M,N)
	 * @return a vector of length M containing the sums of all matrix columns
	 */
	public static double[] sumRows(final double[][] A) {
		double[] sumVec = new double[getNumberOfRows(A)];
		for (int i = 0; i < sumVec.length; i++) {
			double sum = 0;
			for (int j = 0; j < A[i].length; j++) {
				sum = sum + A[i][j];
			}
			sumVec[i] = sum;
		}
		return sumVec;
	}
	
	/**
	 * Calculates the sums of all matrix rows and returns 
	 * them as a vector with one element per row.
	 * @param A a matrix of size (M,N)
	 * @return a vector of length M containing the sums of all matrix columns
	 */
	public static float[] sumRows(final float[][] A) {
		float[] sumVec = new float[getNumberOfRows(A)];
		for (int i = 0; i < sumVec.length; i++) {
			double sum = 0;
			for (int j = 0; j < A[i].length; j++) {
				sum = sum + A[i][j];
			}
			sumVec[i] = (float) sum;
		}
		return sumVec;
	}
	
	/**
	 * Calculates the sums of all matrix columns and returns
	 * them as a vector with one element per column.
	 * @param A a matrix of size (M,N)
	 * @return a vector of length N containing the sums of all matrix rows
	 */
	public static double[] sumColumns(final double[][] A) {
		double[] sumVec = new double[getNumberOfColumns(A)];
		for (int c = 0; c < sumVec.length; c++) {
			double sum = 0;
			for (int r = 0; r < A.length; r++) {
				sum = sum + A[r][c];
			}
			sumVec[c] = sum;
		}
		return sumVec;
	}
	
	/**
	 * Calculates the sums of all matrix columns and returns
	 * them as a vector with one element per column.
	 * @param A a matrix of size (M,N)
	 * @return a vector of length N containing the sums of all matrix rows
	 */
	public static float[] sumColumns(final float[][] A) {
		float[] sumVec = new float[getNumberOfColumns(A)];
		for (int c = 0; c < sumVec.length; c++) {
			double sum = 0;
			for (int r = 0; r < A.length; r++) {
				sum = sum + A[r][c];
			}
			sumVec[c] = (float) sum;
		}
		return sumVec;
	}
	
	// min/max of vectors ------------------------
	
	// TODO:  testing,  add float version
	/**
	 * Returns the index of the smallest element in the
	 * specified vector. If the smallest value is
	 * not unique, the lowest index is returned.
	 * An exception is thrown if the vector has zero length.
	 * 
	 * @param x a vector
	 * @return the index of the smallest value
	 */
	public static int idxMin(double[] x) {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		double minval = x[0];
		int minidx = 0;
		for (int i = 1; i < x.length; i++) {
			if (x[i] < minval) {
				minval = x[i];
				minidx = i;
			}
		}
		return minidx;
	}
	
	// TODO: testing, add float version
	/**
	 * Returns the index of the largest element in the
	 * specified vector. If the largest value is
	 * not unique, the lowest index is returned.
	 * An exception is thrown if the vector has zero length.
	 * 
	 * @param x a vector
	 * @return the index of the largest value
	 */
	public static int idxMax(double[] x) {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		double maxval = x[0];
		int maxidx = 0;
		for (int i = 1; i < x.length; i++) {
			if (x[i] > maxval) {
				maxval = x[i];
				maxidx = i;
			}
		}
		return maxidx;
	}
	
	
	public static float min(final float[] x) throws ZeroLengthVectorException {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		float minval = Float.POSITIVE_INFINITY;
		for (float val : x) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	
	public static double min(final double[] x) throws ZeroLengthVectorException {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		double minval = Double.POSITIVE_INFINITY;
		for (double val : x) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	

	
	public static float max(final float[] x) throws ZeroLengthVectorException {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		float maxval = Float.NEGATIVE_INFINITY;
		for (float val : x) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}
	
	public static double max(final double[] x) throws ZeroLengthVectorException {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		double maxval = Double.NEGATIVE_INFINITY;
		for (double val : x) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}
	
	public static double max(double[][] A) {
		double maxval = Double.NEGATIVE_INFINITY;
		for (int r = 0; r < A.length; r++) {
			for (double val : A[r]) {
				if (val > maxval) {
					maxval = val;
				}
			}
		}
		return maxval;
	}
	
	public static float max(float[][] A) {
		float maxval = Float.NEGATIVE_INFINITY;
		for (int r = 0; r < A.length; r++) {
			for (float val : A[r]) {
				if (val > maxval) {
					maxval = val;
				}
			}
		}
		return maxval;
	}
	
	// Vector concatenation -----------------------
	
	/**
	 * Joins (concatenates) a sequence of vectors into a single vector.
	 * @param xs a sequence of vectors (at least one vector)
	 * @return a vector containing all elements of the input vectors
	 */
	public static float[] join(float[]... xs) {
		int n = 0;
		for (float[] x : xs) {
			n = n + x.length;
		}
		float[] va = new float[n];
		int j = 0;
		for (float[] x : xs) {
			for (int i = 0; i < x.length; i++) {
				va[j] = x[i];
				j++;
			}
		}		
		return va;
	}

	/**
	 * Joins (concatenates) a sequence of vectors into a single vector.
	 * @param xs a sequence of vectors (at least one vector)
	 * @return a vector containing all elements of the input vectors
	 */
	public static double[] join(double[]... xs) {
		int n = 0;
		for (double[] x : xs) {
			n = n + x.length;
		}
		double[] va = new double[n];	
		int j = 0;
		for (double[] x : xs) {
			for (int i = 0; i < x.length; i++) {
				va[j] = x[i];
				j++;
			}
		}		
		return va;
	}
	
	// Vector linear interpolation ---------------------------------
	
	/**
	 * Performs linear interpolation between two vectors {@code a} and {@code b}, 
	 * which must have the same length.
	 * Returns a new vector {@code c = a + t * (b - a)}.
	 * @param a first vector (to be interpolated from)
	 * @param b second vector (to be interpolated to)
	 * @param t interpolation coefficient, expected to be in [0,1]
	 * @return the interpolated vector
	 */
	public static float[] lerp(float[] a, float[] b, final float t) {
		float[] c = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] + t * (b[i] - a[i]);
		}
		return c;
	}
	
	/**
	 * Performs linear interpolation between two vectors {@code a} and {@code b}, 
	 * which must have the same length.
	 * Returns a new vector {@code c = a + t * (b - a)}.
	 * @param a first vector (to be interpolated from)
	 * @param b second vector (to be interpolated to)
	 * @param t interpolation coefficient, expected to be in [0,1]
	 * @return the interpolated vector
	 */
	public static double[] lerp(double[] a, double[] b, final double t) {
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] + t * (b[i] - a[i]);
		}
		return c;
	}
	
	
	// Homogeneous coordinates ---------------------------------
	
	/**
	 * Converts a Cartesian vector to an equivalent homogeneous
	 * vector, which contains an additional 1-element.
	 * See also {@link #toCartesian(double[])}.
	 * @param xc a Cartesian vector
	 * @return an equivalent homogeneous vector
	 */
	public static double[] toHomogeneous(double[] xc) {
		double[] xh = new double[xc.length + 1];
		for (int i = 0; i < xc.length; i++) {
			xh[i] = xc[i];
			xh[xh.length - 1] = 1;
		}
		return xh;
	}
	
	/**
	 * Converts a homogeneous vector to its equivalent Cartesian
	 * vector, which is one element shorter.
	 * See also {@link #toHomogeneous(double[])}.
	 * @param xh a homogeneous vector
	 * @return the equivalent Cartesian vector
	 */
	public static double[] toCartesian(double[] xh) throws DivideByZeroException {
		double[] xc = new double[xh.length - 1];
		final double s = 1 / xh[xh.length - 1];
		if (!Double.isFinite(s))	// isZero(s)
			throw new DivideByZeroException();
		for (int i = 0; i < xh.length - 1; i++) {
			xc[i] = s * xh[i];
		}
		return xc;
	}
	
	// Determinants --------------------------------------------
	
	public static float determinant2x2(final float[][] A) throws IncompatibleDimensionsException {
		if (A.length != 2 || A[0].length != 2)
			throw new IncompatibleDimensionsException();
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	public static double determinant2x2(final double[][] A) throws IncompatibleDimensionsException {
		if (A.length != 2 || A[0].length != 2)
			throw new IncompatibleDimensionsException();
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	public static float determinant3x3(final float[][] A) throws IncompatibleDimensionsException {
		if (A.length != 3 || A[0].length != 3)
			throw new IncompatibleDimensionsException();
		return
			A[0][0] * A[1][1] * A[2][2] +
			A[0][1] * A[1][2] * A[2][0] +
			A[0][2] * A[1][0] * A[2][1] -
			A[2][0] * A[1][1] * A[0][2] -
			A[2][1] * A[1][2] * A[0][0] -
			A[2][2] * A[1][0] * A[0][1] ;
	}

	public static double determinant3x3(final double[][] A) throws IncompatibleDimensionsException {
		if (A.length != 3 || A[0].length != 3)
			throw new IncompatibleDimensionsException();
		return 
			A[0][0] * A[1][1] * A[2][2] + 
			A[0][1] * A[1][2] * A[2][0]	+ 
			A[0][2] * A[1][0] * A[2][1] - 
			A[2][0] * A[1][1] * A[0][2] - 
			A[2][1] * A[1][2] * A[0][0] - 
			A[2][2] * A[1][0] * A[0][1] ;
	}
	
	public static double determinant(final double[][] A) throws NonsquareMatrixException {
		if (!isSquare(A))
			throw new NonsquareMatrixException();
		RealMatrix M = MatrixUtils.createRealMatrix(A);
		return new LUDecomposition(M).getDeterminant();
	}
	
	// Matrix trace ---------------------------------------
	
	public static double trace(final double[][] A) throws NonsquareMatrixException {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (m != n) 
			throw new NonsquareMatrixException();
		double s = 0;
		for (int i = 0; i < m; i++) {
				s = s + A[i][i];
		}
		return s;
	}
	
	// Matrix transposition ---------------------------------------
	
	public static float[][] transpose(float[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		float[][] At = new float[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				At[j][i] = A[i][j];
			}
		}
		return At;
	}
	
	public static double[][] transpose(double[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		double[][] At = new double[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				At[j][i] = A[i][j];
			}
		}
		return At;
	}
	
	// Checking vectors for all zero values  ------------------------------
	
	public static boolean isZero(double[] x, double tolerance) {
		for (int i = 0; i < x.length; i++) {
			if (!Arithmetic.isZero(x[i], tolerance)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isZero(double[] x) {
		return isZero(x, Arithmetic.EPSILON_DOUBLE);
	}
	
	public static boolean isZero(float[] x, float tolerance) {
		for (int i = 0; i < x.length; i++) {
			if (!Arithmetic.isZero(x[i], tolerance)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isZero(float[] x) {
		return isZero(x, Arithmetic.EPSILON_FLOAT);
	}
	
	// Matrix inversion ---------------------------------------
	
	/**
	 * Calculates and returns the inverse of the given matrix, which
	 * must be square. Exceptions are n if the supplied matrix is
	 * not square or ill-conditioned (singular).
	 * @param A a square matrix
	 * @return the inverse matrix
	 */
	public static double[][] inverse(final double[][] A) throws NonsquareMatrixException {
		if (!isSquare(A))
			throw new NonsquareMatrixException();
		RealMatrix M = MatrixUtils.createRealMatrix(A);
		return MatrixUtils.inverse(M).getData();
	}
	
	/**
	 * Calculates and returns the inverse of the given matrix, which
	 * must be square. Exceptions are thrown if the supplied matrix is
	 * not square or ill-conditioned (singular).
	 * @param A a square matrix
	 * @return the inverse matrix
	 */
	public static float[][] inverse(final float[][] A) throws NonsquareMatrixException {
		if (!isSquare(A))
			throw new NonsquareMatrixException();
		double[][] Ad = toDouble(A);
		return toFloat(inverse(Ad));
	}

	// ------------------------------------------------------------------------
	
	/**
	 * Finds the exact solution x for the linear system of equations
	 * A * x = b. Returns the solution vector x or {@code null}
	 * if the supplied matrix is ill-conditioned (i.e., singular).
	 * Exceptions are thrown if A is not square or dimensions are incompatible.
	 * Uses {@link LUDecomposition} from the Apache Commons Math library.
	 * 
	 * @param A a square matrix of size n x n
	 * @param b a vector of length n
	 * @return the solution vector (x) of length n or {@code null} if no solution possible
	 */
	
	public static double[] solve(final double[][] A, double[] b) {
		RealVector x = solve(MatrixUtils.createRealMatrix(A), MatrixUtils.createRealVector(b));
		if (!Matrix.isSquare(A)) {
			throw new RuntimeException("matrix A must be square");
		}
		if (x == null) {
			return null;
		}
		else {
			return x.toArray();
		}
	}
	
	/**
	 * Finds the exact solution x for the linear system of equations
	 * A * x = b. Returns the solution vector x or {@code null}
	 * if the supplied matrix is ill-conditioned (i.e., singular).
	 * Exceptions are thrown if A is not square or dimensions are incompatible.
	 * Uses {@link LUDecomposition} from the Apache Commons Math library
	 * (with singularity threshold set to zero).
	 * 
	 * @param A a square matrix of size n x n
	 * @param b a vector of length n
	 * @return the solution vector (x) of length n or {@code null} if no solution possible
	 */
	public static RealVector solve(RealMatrix A, RealVector b) {
		if (!A.isSquare()) {
			throw new RuntimeException("matrix A must be square");
		}
		if (A.getRowDimension() != b.getDimension()) {
			throw new IncompatibleDimensionsException();
		}
		DecompositionSolver solver = new LUDecomposition(A, 0.0).getSolver();
		RealVector x = null;
		try {
			x = solver.solve(b);
		} catch (SingularMatrixException e) {}
		return x;
	}
	
	// Output to strings and streams ------------------------------------------
	
	public static String toString(double[] x) {
		if (x == null) {
			return String.valueOf(x);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(x, strm);
		return bas.toString();
	}
	
	public static String toString(float[] x) {
		if (x == null) {
			return String.valueOf(x);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(x, strm);
		return bas.toString();
	}
	
	public static String toString(double[][] A) {
		if (A == null) {
			return String.valueOf(A);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static String toString(float[][] A) {
		if (A == null) {
			return String.valueOf(A);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static String toString(RealVector x) {
		if (x == null) {
			return String.valueOf(x);
		}
		else {
			return toString(x.toArray());
		}
	}
	
	public static String toString(RealMatrix A) {
		if (A == null) {
			return String.valueOf(A);
		}
		else {
			return toString(A.getData());
		}
	}
	
	// --------------------
	
	public static void printToStream(double[] x, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i = 0; i < x.length; i++) {
			if (i > 0)
				strm.format("%c ", SeparationChar);
			strm.format(PrintLocale, fStr, x[i]);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	public static void printToStream(double[][] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("%c", LeftDelimitChar);
			else
				strm.format("%c \n%c", SeparationChar, LeftDelimitChar);
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					strm.format(PrintLocale, fStr, A[i][j]);
				else
					strm.format(PrintLocale, "%c " + fStr, SeparationChar, A[i][j]);
			}
			strm.format("%c", RightDelimitChar);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	public static void printToStream(float[] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				strm.format("%c ", SeparationChar);
			strm.format(PrintLocale, fStr, A[i]);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	public static void printToStream(float[][] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("%c", LeftDelimitChar);
			else
				strm.format("%c \n%c", SeparationChar, LeftDelimitChar);
			for (int j = 0; j < A[i].length; j++) {
				if (j == 0) 
					strm.format(PrintLocale, fStr, A[i][j]);
				else
					strm.format(PrintLocale, "%c " + fStr, SeparationChar, A[i][j]);
			}
			strm.format("%c", RightDelimitChar);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}

	// Exceptions ----------------------------------------------------------------
	
	public static class IncompatibleDimensionsException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public IncompatibleDimensionsException() {
			super("incompatible matrix-vector dimensions");
		}
	}
	
	public static class NonsquareMatrixException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public NonsquareMatrixException() {
			super("square matrix expected");
		}
	}
	
	public static class SameSourceTargetException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public SameSourceTargetException() {
			super("source and target must not be the same");
		}
	}
	
	public static class ZeroLengthVectorException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public ZeroLengthVectorException() {
			super("vector length must be greater that 0");
		}
	}
	
	// ------------------------------------------------------------------------
	
	public static void main(String[] args) {
		double s = Double.NaN;
		s = -1.0 / 1E-200; // / 1E-200;
		System.out.println(Double.isFinite(s));
		System.out.println((double[]) null);
		
		double[][] A = makeDoubleMatrix(5, 4,
				1,2,3,4,
				5,6,7,8,
				9,10,11,12,
				13,14,15,16,
				17,18,19,20);
		System.out.println("A = \n" + toString(A));
		
		RealMatrix B = makeRealMatrix(5, 4);
		System.out.println("B = \n" + toString(B.getData()));

	}

}

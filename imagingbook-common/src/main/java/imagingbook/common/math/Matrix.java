/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.sqr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import imagingbook.common.math.exception.DivideByZeroException;

/**
 * <p>This class defines a set of static methods for calculations
 * with vectors and matrices using native Java arrays without any enclosing 
 * object structures. 
 * Matrices are simple two-dimensional arrays {@code A[r][c]}, where {@code r} is the 
 * (vertical) <strong>row</strong> index
 * and {@code c} is the (horizontal) <strong>column</strong> index (as common in linear algebra). 
 * This means that matrices are really vectors of row vectors.
 * Only arrays of type {@code float} and {@code double} are supported.
 * All matrices are assumed to be rectangular (i.e., all rows are of equal length).
 * </p>
 * <p>
 * Note: Methods named with a trailing '{@code D}' (e.g., {@link #multiplyD(double, double[])}) 
 * operate destructively, i.e., modify one of the supplied arguments.
 * </p>
 * 
 * @author WB
 * @version 2022/08/29
 */
@SuppressWarnings("serial")
public abstract class Matrix {
	
	private Matrix() {}
	
	// TODO: check if these variables should be public and non-final
	
	/** Locale used for printing decimal numbers by {@code toString()} methods. */
	public static Locale PrintLocale = Locale.US;
	
	/** Character used to separate successive vector and matrix elements by {@code toString()} methods. */
	public static char SeparationChar = ',';
	
	/** Leading delimiter used for lists of vector and matrix elements by {@code toString()} methods. */
	public static char LeftDelimitChar = '{';
	
	/** Trailing delimiter used for lists of vector and matrix elements by {@code toString()} methods. */
	public static char RightDelimitChar = '}';
	
	// ----  Matrix creation -----------------------------
	
	/**
	 * <p>Creates and returns a {@code double[][]} matrix containing the specified values.
	 * Throws an {@link IllegalArgumentException} if the number of supplied values does
	 * not exactly match the matrix size. Example for creating a 2x3 matrix:
	 * </p>
	 * <pre>
	 * double[][] A = makeDoubleMatrix(2, 3, 
	 * 		1.0, 2.0, 3.0,
	 * 		4.0, 5.0, 6.0 );</pre>
	 * <p>
	 * See also {@link #flatten(double[][])}.
	 * </p>
	 * 
	 * @param rows the number of matrix rows
	 * @param cols the number of matrix columns
	 * @param values a sequence of matrix values in row-major order (may also be passed as a {@code double[]})
	 * @return a {@code double[][]} matrix
	 */
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
	
	/**
	 * <p>
	 * Creates and returns a {@code float[][]} matrix containing the specified values.
	 * Throws an {@link IllegalArgumentException} if the number of supplied values does
	 * not exactly match the matrix size. Example for creating a 2x3 matrix:
	 * </p>
	 * <pre>
	 * float[][] A = makeFloatMatrix(2, 3, 
	 * 		1.0f, 2.0f, 3.0f,
	 * 		4.0f, 5.0f, 6.0f );</pre>
	 * <p>
	 * See also {@link #flatten(float[][])}.
	 * </p>
	 * 
	 * @param rows the number of matrix rows
	 * @param cols the number of matrix columns
	 * @param values a sequence of matrix values in row-major order (may also be passed as a {@code float[]})
	 * @return a {@code float[][]} matrix
	 */
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
	
	/**
	 * <p>
	 * Creates and returns a {@link RealMatrix} containing the specified {@code double} values.
	 * Calls {@link #makeDoubleMatrix(int, int, double...)}. Example for creating a 2x3 matrix:
	 * </p>
	 * <pre>
	 * RealMatrix A = makeRealMatrix(2, 3, 
	 * 		1.0, 2.0, 3.0,
	 * 		4.0, 5.0, 6.0 );</pre>
	 * <p>
	 * See also {@link #flatten(RealMatrix)}.
	 * </p>
	 * @param rows the number of matrix rows
	 * @param cols the number of matrix columns
	 * @param values a sequence of matrix values in row-major order (may also be passed as a {@code double[]})
	 * @return a {@link RealMatrix} 
	 */
	public static RealMatrix makeRealMatrix(final int rows, final int cols, final double... values) {
		return MatrixUtils.createRealMatrix(makeDoubleMatrix(rows, cols, values));
	}
	

	/**
	 * Returns the values of the specified matrix as a {@code double[]}
	 * with elements arranged in row-major order.
	 * The matrix must be fully rectangular (all rows of same length).
	 * See also {@link #makeDoubleMatrix(int, int, double...)}.
	 *  
	 * @param A a matrix
	 * @return a {@code double[]} with the matrix elements
	 */
	public static double[] flatten(double[][] A) {
		final int rows = A.length;
		final int cols = A[0].length;
		final double[] vals = new double[rows * cols];
		int i = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				vals[i] = A[r][c];
				i++;
			}
		}
		return vals;
	}
	
	/**
	 * Returns the values of the specified matrix as a {@code float[]}
	 * with elements arranged in row-major order.
	 * The matrix must be fully rectangular (all rows of same length).
	 * See also {@link #makeFloatMatrix(int, int, float...)}.
	 *  
	 * @param A a matrix
	 * @return a {@code float[]} with the matrix elements
	 */
	public static float[] flatten(float[][] A) {
		final int rows = A.length;
		final int cols = A[0].length;
		final float[] vals = new float[rows * cols];
		int i = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				vals[i] = A[r][c];
				i++;
			}
		}
		return vals;
	}
	
	/**
	 * Returns the values of the specified matrix as a {@code double[]}
	 * with elements arranged in row-major order.
	 * See also {@link #makeRealMatrix(int, int, double...)}.
	 *  
	 * @param A a matrix
	 * @return a {@code double[]} with the matrix elements
	 */
	public static double[] flatten(RealMatrix A) {
		return flatten(A.getData());
	}
	
	// --------------------------------------------------------------------------
	
	/**
	 * Creates and returns a {@code double[]} vector from the
	 * specified values.
	 * 
	 * @param values a sequence of vector values (may also be passed as a single {@code double[]})
	 * @return a {@code double[]}
	 */
	public static double[] makeDoubleVector(double... values) {
		return values;
	}
	
	/**
	 * Creates and returns a {@code float[]} vector from the
	 * specified values.
	 * 
	 * @param values a sequence of vector values (may also be passed as a single {@code float[]})
	 * @return a {@code float[]}
	 */
	public static float[] makeFloatVector(float... values) {
		return values;
	}
	
	/**
	 * Creates and returns a {@link RealVector} from the
	 * specified {@code double} values.
	 * 
	 * @param values a sequence of vector values (may also be passed as a single {@code double[]})
	 * @return a {@link RealVector} 
	 */
	public static RealVector makeRealVector(double... values) {
		return MatrixUtils.createRealVector(values);
//		return new ArrayRealVector(values);
	}
	
	// Specific vector/matrix creation:
	
	/**
	 * Creates and returns a new zero-valued {@code double[]} vector of the 
	 * specified length.
	 * Throws an exception if the length is less than 1.
	 * @param length the length of the vector
	 * @return a {@code double[]} with zero values
	 */
	public static double[] zeroVector(int length) {
		if (length < 1)
			throw new IllegalArgumentException("vector size cannot be < 1");
		return new double[length];
	}
	
	/**
	 * Creates and returns a new identity matrix of the 
	 * specified size.
	 * Throws an exception if the size is less than 1.
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

	/**
	 * Returns the number of rows of the specified {@code double} matrix.
	 * @param A a {@code double[][]} matrix
	 * @return the number of rows
	 */
	public static int getNumberOfRows(double[][] A) {
		return A.length;
	}
	
	
	/**
	 * Returns the number of columns of the specified {@code double} matrix.
	 * @param A a {@code double[][]} matrix
	 * @return the number of columns
	 */
	public static int getNumberOfColumns(double[][] A) {
		return A[0].length;
	}
	
	/**
	 * Returns the number of rows of the specified {@code float} matrix.
	 * @param A a {@code float[][]} matrix
	 * @return the number of rows
	 */
	public static int getNumberOfRows(float[][] A) {
		return A.length;
	}
	
	/**
	 * Returns the number of columns of the specified {@code float} matrix.
	 * @param A a {@code float[][]} matrix
	 * @return the number of columns
	 */
	public static int getNumberOfColumns(float[][] A) {
		return A[0].length;
	}
	
	/**
	 * Returns the number of rows of the specified {@link RealMatrix}.
	 * @param A a {@link RealMatrix}
	 * @return the number of rows
	 */
	public static int getNumberOfRows(RealMatrix A) {
		return A.getRowDimension();
	}
	
	/**
	 * Returns the number of columns of the specified {@link RealMatrix}.
	 * @param A a {@link RealMatrix}
	 * @return the number of columns
	 */
	public static int getNumberOfColumns(RealMatrix A) {
		return A.getColumnDimension();
	}
	
	// Extract rows or columns
	
	/**
	 * Returns a particular row of the specified {@code double} matrix 
	 * as a {@code double} vector.
	 * @param A a {@code double[][]} matrix
	 * @param r the row index (starting with 0)
	 * @return a {@code double} vector
	 */
	public static double[] getRow(double[][] A, int r) {
		return A[r].clone();
	}
	
	/**
	 * Returns a particular row of the specified {@code float} matrix 
	 * as a {@code float} vector.
	 * @param A a {@code float[][]} matrix
	 * @param r the row index (starting with 0)
	 * @return a {@code float} vector
	 */
	public static float[] getRow(float[][] A, int r) {
		return A[r].clone();
	}
	
	/**
	 * Returns a particular column of the specified {@code double} matrix 
	 * as a {@code double} vector.
	 * @param A a {@code double[][]} matrix
	 * @param c the column index (starting with 0)
	 * @return a {@code double} vector
	 */
	public static double[] getColumn(double[][] A, int c) {
		final int rows = A.length;
		double[] col = new double[rows];
		for (int r = 0; r < rows; r++) {
			col[r] = A[r][c];
		}
		return col;
	}
	
	/**
	 * Returns a particular column of the specified {@code float} matrix 
	 * as a {@code float} vector.
	 * @param A a {@code float[][]} matrix
	 * @param c the column index (starting with 0)
	 * @return a {@code float} vector
	 */
	public static float[] getColumn(float[][] A, int c) {
		final int rows = A.length;
		float[] col = new float[rows];
		for (int r = 0; r < rows; r++) {
			col[r] = A[r][c];
		}
		return col;
	}
	
	// Checking vector/matrix dimensions
	
	/**
	 * Checks if all rows of the given matrix have the same length.
	 * @param A a {@code float[][]} matrix
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
	 * @param A a {@code double[][]} matrix
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
	 * @param A a {@code double[][]} matrix
	 * @return true iff the matrix is square
	 */
	public static boolean isSquare(double[][] A) {
		return A.length > 0 && A.length == A[0].length;
	}
	
	/** Checks it the given matrix has the same number of rows
	 * and columns.
	 * @param A a {@code float[][]} matrix
	 * @return true iff the matrix is square
	 */
	public static boolean isSquare(float[][] A) {
		return A.length > 0 && A.length == A[0].length;
	}
	
	/**
	 * Returns a vector with the diagonal elements of the specified matrix.
	 * @param A a square {@code double[][]} matrix
	 * @return the vector of diagonal elements
	 */
	public static double[] getDiagonal(double[][] A) {
		if (!isSquare(A)) {
			throw new NonsquareMatrixException();
		}
		final int n = A.length;
		double[] diag = new double[n];
		for (int i = 0; i < n; i++) {
			diag[i] = A[i][i];
		}
		return diag;
	}
	
	/**
	 * Returns a vector with the diagonal elements of the specified matrix.
	 * @param A a square {@code float[][]} matrix
	 * @return the vector of diagonal elements
	 */
	public static float[] getDiagonal(float[][] A) {
		if (!isSquare(A)) {
			throw new NonsquareMatrixException();
		}
		final int n = A.length;
		float[] diag = new float[n];
		for (int i = 0; i < n; i++) {
			diag[i] = A[i][i];
		}
		return diag;
	}
	
	/**
	 * Returns a vector with the diagonal elements of the specified matrix.
	 * @param A a square {@link RealMatrix}
	 * @return the {@link RealVector} of diagonal elements
	 */
	public static RealVector getDiagonal(RealMatrix A) {
		return MatrixUtils.createRealVector(getDiagonal(A.getData()));
	}
	
	/**
	 * Creates and returns a diagonal matrix from the specified 
	 * vector. See also {@link #getDiagonal(double[][])}.
	 * 
	 * @param d vector of diagonal matrix elements
	 * @return a diagonal matrix
	 */
	public double[][] diagMatrix(double[] d) {
		return MatrixUtils.createRealDiagonalMatrix(d).getData();
	}
	
	/**
	 * Checks is the given square {@code double[][]} matrix is non-singular.
	 * @param A a square {@code double[][]} matrix
	 * @return true if the matrix is singular
	 */
	public static boolean isSingular(double[][] A) throws NonsquareMatrixException {
		if (!Matrix.isSquare(A)) {
			throw new NonsquareMatrixException();
		}	
		return isSingular(new Array2DRowRealMatrix(A));
	}
	
	/**
	 * Checks is the given square matrix is non-singular.
	 * @param A a square {@link RealMatrix}
	 * @return true if the matrix is singular
	 */
	public static boolean isSingular(RealMatrix A) throws NonsquareMatrixException {
		if (!A.isSquare()) {
			throw new NonsquareMatrixException();
		}		
		DecompositionSolver solver = new LUDecomposition(A).getSolver();
		return !solver.isNonSingular();
	}
	
	/** Default matrix symmetry tolerance. */
	public static final double DefaultSymmetryTolerance = 1e-12;
	
	/**
	 * Checks is the given square matrix is symmetric
	 * using the specified relative tolerance.
	 * @param A a square matrix
	 * @param relTolerance relative symmetry tolerance
	 * @return true if the matrix is symmetric
	 */
	public static boolean isSymmetric(RealMatrix A, double relTolerance) {
		return MatrixUtils.isSymmetric(A, relTolerance);
	}
	
	/**
	 * Checks is the given square matrix is symmetric,
	 * using the default tolerance value ({@link Arithmetic#EPSILON_DOUBLE}).
	 * @param A a square matrix
	 * @return true if the matrix is symmetric
	 */
	public static boolean isSymmetric(RealMatrix A) {
		return isSymmetric(A, DefaultSymmetryTolerance);
	}
	
	/**
	 * Checks is the given square matrix is symmetric
	 * using the specified relative tolerance.
	 * @param A a square matrix
	 * @param relTolerance relative symmetry tolerance
	 * @return true if the matrix is symmetric
	 */
	public static boolean isSymmetric(double[][] A, double relTolerance) {
		return MatrixUtils.isSymmetric(MatrixUtils.createRealMatrix(A), relTolerance);
	}
	
	/**
	 * Checks is the given square matrix is symmetric,
	 * using the default tolerance value ({@link Arithmetic#EPSILON_DOUBLE}).
	 * @param A a square matrix
	 * @return true if the matrix is symmetric
	 */
	public static boolean isSymmetric(double[][] A) {
		return isSymmetric(A, DefaultSymmetryTolerance);
	}
	
	/**
	 * Checks is the given square matrix is positive definite,
	 * using the specified symmetry tolerance value.
	 * 
	 * @param A a square matrix
	 * @param tolerance the absolute positivity and relative symmetry tolerance
	 * @return true if the matrix is positive definite
	 */
	public static boolean isPositiveDefinite(RealMatrix A, double tolerance) { //TODO: split tolerance in two parameters
		try {
			new CholeskyDecomposition(A, tolerance, tolerance);
			return true;
		} catch (NonPositiveDefiniteMatrixException e) {};
		return false;
	}
	
	/**
	 * Checks is the given square matrix is positive definite.
	 * @param A a square matrix
	 * @return true if the matrix is positive definite
	 */
	public static boolean isPositiveDefinite(RealMatrix A) {
		return isPositiveDefinite(A, Arithmetic.EPSILON_DOUBLE);
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

	/**
	 * Returns a copy of the given {@code double[]} vector. 
	 * @param a a {@code double[]} vector
	 * @return a copy of the vector
	 */
	public static double[] duplicate(final double[] a) {
		return a.clone();
	}
	
	/**
	 * Returns a copy of the given {@code float[]} vector. 
	 * @param a a {@code float[]} vector
	 * @return a copy of the vector
	 */
	public static float[] duplicate(final float[] a) {
		return a.clone();
	}

	/**
	 * Returns a copy of the given {@code double[][]} matrix. 
	 * @param A a {@code double[][]} matrix
	 * @return a copy of the matrix
	 */
	public static double[][] duplicate(final double[][] A) {
		final int m = A.length;
		final double[][] B = new double[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	/**
	 * Returns a copy of the given {@code float[][]} matrix. 
	 * @param A a {@code float[][]} matrix
	 * @return a copy of the matrix
	 */
	public static float[][] duplicate(final float[][] A) {
		final int m = A.length;
		float[][] B = new float[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	// vector copying ------------------------------
	
	/**
	 * Copy data from one {@code float[]} vector to another.
	 * The two vectors must have the same length (not checked).
	 * @param source the source vector (unmodified)
	 * @param target the target vector (modified)
	 */
	public static void copyD(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}
	
	/**
	 * Copy data from one {@code double[]} vector to another.
	 * The two vectors must have the same length (not checked).
	 * @param source the source vector (unmodified)
	 * @param target the target vector (modified)
	 */
	public static void copyD(double[] source, double[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}
	
	// ----- double <-> float conversions -----------------
	
	/**
	 * Converts a {@code double[]} to a {@code float[]}.
	 * @param a the original {@code double[]} array
	 * @return a copy of the array of type {@code float[]}
	 */
	public static float[] toFloat(final double[] a) {
		final int m = a.length;
		final float[] b = new float[m];
		for (int i = 0; i < m; i++) {
			b[i] = (float) a[i];
		}
		return b;
	}
	
	/**
	 * Converts a {@code double[][]} to a {@code float[][]}.
	 * @param A the original {@code double[][]} array
	 * @return a copy of the array of type {@code float[][]}
	 */
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
	
	/**
	 * Converts a {@code float[]} to a {@code double[]}.
	 * @param a the original {@code float[]} array
	 * @return a copy of the array of type {@code double[]}
	 */
	public static double[] toDouble(final float[] a) {
		final int m = a.length;
		final double[] B = new double[m];
		for (int i = 0; i < m; i++) {
			B[i] = a[i];
		}
		return B;
	}
	
	/**
	 * Converts a {@code float[][]} to a {@code double[][]}.
	 * @param A the original {@code float[][]} array
	 * @return a copy of the array of type {@code double[][]}
	 */
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
	
	/**
	 * Fills the given {@code double} vector with the specified value (destructively).
	 * @param x a vector (which is modified)
	 * @param val the fill value
	 */
	public static void fillD(final double[] x, double val) {
		Arrays.fill(x, val);
	}
	
	/**
	 * Fills the given {@code float} vector with the specified value (destructively).
	 * @param x a vector (which is modified)
	 * @param val the fill value
	 */
	public static void fillD(final float[] x, float val) {
		Arrays.fill(x, val);
	}
	
	/**
	 * Fills the given {@code double} matrix with the specified value (destructively).
	 * @param A a matrix (which is modified)
	 * @param val the fill value
	 */
	public static void fillD(final double[][] A, double val) {
		for (int i = 0; i < A.length; i++) { 
			Arrays.fill(A[i], val);
		}
	}
	
	/**
	 * Fills the given {@code float} matrix with the specified value (destructively).
	 * @param A a matrix (which is modified)
	 * @param val the fill value
	 */
	public static void fillD(final float[][] A, float val) {
		for (int i = 0; i < A.length; i++) { 
			Arrays.fill(A[i], val);
		}
	}
	

	// Element-wise arithmetic -------------------------------
	
	/**
	 * Calculates and returns the sum of the specified {@code double} vectors
	 * (non-destructively). An exception is thrown if the vectors are of
	 * different lengths.
	 * None of the arguments is modified.
	 * @param a the first vector
	 * @param b the second vector
	 * @return a new {@code double} vector
	 */
	public static double[] add(final double[] a, final double[] b) {
		double[] c = b.clone();
		addD(a, c);
		return c;
	}
	
	/**
	 * Adds the elements of the first {@code double} vector
	 * to the second vector (destructively).
	 * An exception is thrown if the vectors are of
	 * different lengths.
	 * The second vector is modified.
	 * @param a the first vector
	 * @param b the second vector
	 */
	public static void addD(final double[] a, final double[] b) {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i] + b[i];
		}
	}
	
	/**
	 * Calculates and returns the sum of the specified {@code float} vectors
	 * (non-destructively). An exception is thrown if the vectors are of
	 * different lengths.
	 * None of the arguments is modified.
	 * @param a the first vector
	 * @param b the second vector
	 * @return a new {@code float} vector
	 */
	public static float[] add(final float[] a, final float[] b) {
		float[] c = b.clone();
		addD(a, c);
		return c;
	}
	
	/**
	 * Adds the elements of the first {@code float} vector
	 * to the second vector (destructively).
	 * An exception is thrown if the vectors are of different lengths.
	 * The second vector is modified.
	 * @param a the first vector
	 * @param b the second vector
	 */
	public static void addD(final float[] a, final float[] b) {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i] + b[i];
		}
	}

	/**
	 * Calculates and returns the sum of the specified {@code double} matrix
	 * (non-destructively). An exception is thrown if the matrices are of
	 * different size.
	 * None of the arguments is modified.
	 * @param A the first matrix
	 * @param B the second matrix
	 * @return a new {@code double} matrix
	 */
	public static double[][] add(final double[][] A, final double[][] B) {
		double[][] C = duplicate(B);
		addD(A, C);
		return C;
	}
	
	/**
	 * Adds the elements of the first {@code double} matrix
	 * to the second vector (destructively).
	 * An exception is thrown if the matrices are of
	 * different size.
	 * The second matrix is modified.
	 * @param A the first matrix
	 * @param B the second matrix
	 */
	public static void addD(final double[][] A, final double[][] B) {
		if (!sameSize(A, B))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				B[i][j] = A[i][j] + B[i][j];
			}
		}
	}
	
	/**
	 * Calculates and returns the sum of the specified {@code float} matrix
	 * (non-destructively). An exception is thrown if the matrices are of
	 * different size.
	 * None of the arguments is modified.
	 * @param A the first matrix
	 * @param B the second matrix
	 * @return a new {@code float} matrix
	 */
	public static float[][] add(final float[][] A, final float[][] B) {
		float[][] C = duplicate(B);
		addD(A, C);
		return C;
	}
	
	/**
	 * Adds the elements of the first {@code float} matrix
	 * to the second vector (destructively).
	 * An exception is thrown if the matrices are of
	 * different size.
	 * The second matrix is modified.
	 * @param A the first matrix
	 * @param B the second matrix
	 */
	public static void addD(final float[][] A, final float[][] B) {
		if (!sameSize(A, B))
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				B[i][j] = A[i][j] + B[i][j];
			}
		}
	}
	
	// ------------------------------------------------
	
	/**
	 * Calculates and returns the difference (a - b) of the specified {@code double} vectors
	 * (non-destructively). An exception is thrown if the vectors are of
	 * different lengths.
	 * None of the arguments is modified.
	 * @param a the first vector
	 * @param b the second vector
	 * @return a new {@code double} vector
	 */
	public static double[] subtract(final double[] a, final double[] b) {
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
	/**
	 * Calculates and returns the difference (a - b) of the specified {@code float} vectors
	 * (non-destructively). An exception is thrown if the vectors are of
	 * different lengths.
	 * None of the arguments is modified.
	 * @param a the first vector
	 * @param b the second vector
	 * @return a new {@code float} vector
	 */
	public static float[] subtract(final float[] a, final float[] b) {
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
	/**
	 * Multiplies a {@code double[]} vector by a scalar and returns a new
	 * {@code double[]} vector (non-destructive).
	 * @param s a scalar
	 * @param x a vector
	 * @return a new vector
	 */
	public static double[] multiply(final double s, final double[] x) {
		double[] b = x.clone();
		multiplyD(s, b);
		return b;
	}
	
	// destructive
	/**
	 * Multiplies a {@code double[]} vector by a scalar.
	 * Destructive, i.e., the specified vector is modified.
	 * @param s a scalar
	 * @param x a vector
	 */
	public static void multiplyD(final double s, final double[] x) {
		for (int i = 0; i < x.length; i++) {
			x[i] = x[i] * s;
		}
	}
	
	// non-destructive
	/**
	 * Multiplies a {@code double[][]} matrix by a scalar and returns a new
	 * {@code double[][]} matrix (non-destructive).
	 * @param s a scalar
	 * @param A a matrix
	 * @return a new matrix
	 */
	public static double[][] multiply(final double s, final double[][] A) {
		double[][] B = duplicate(A);
		multiplyD(s, B);
		return B;
	}
	
	/**
	 * Multiplies a {@code double[][]} matrix by a scalar.
	 * Destructive, i.e., the specified matrix is modified.
	 * @param s a scalar
	 * @param A a matrix
	 */
	public static void multiplyD(final double s, final double[][] A) {
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = A[i][j] * s;
			}
		}
	}
	
	// non-destructive
	/**
	 * Multiplies a {@code float[]} vector by a scalar and returns a new
	 * {@code float[]} vector (non-destructive).
	 * @param s a scalar
	 * @param x a vector
	 * @return a new vector
	 */
	public static float[] multiply(final float s, final float[] x) {
		float[] B = duplicate(x);
		multiplyD(s, B);
		return B;
	}
	
	// destructive
	/**
	 * Multiplies a {@code float[]} vector by a scalar.
	 * Destructive, i.e., the specified vector is modified.
	 * @param s a scalar
	 * @param x a matrix
	 */
	public static void multiplyD(final float s, final float[] x) {
		for (int i = 0; i < x.length; i++) {
			x[i] = x[i] * s;
		}
	}

	// non-destructive
	/**
	 * Multiplies a {@code float[][]} matrix by a scalar and returns a new
	 * {@code float[][]} matrix (non-destructive).
	 * @param s a scalar
	 * @param A a matrix
	 * @return a new matrix
	 */
	public static float[][] multiply(final float s, final float[][] A) {
		float[][] B = duplicate(A);
		multiplyD(s, B);
		return B;
	}
	
	// destructive
	/**
	 * Multiplies a {@code float[][]} matrix by a scalar.
	 * Destructive, i.e., the specified matrix is modified.
	 * @param s a scalar
	 * @param A a matrix
	 */
	public static void multiplyD(final float s, final float[][] A) {
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = A[i][j] * s;
			}
		}
	}
	
	// matrix-vector multiplications ----------------------------------------

	/**
	 * Multiplies a vector x with a matrix A "from the left", i.e., y = x * A,
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
	public static void multiplyD(final double[] x, final double[][] A, double[] y) {
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
	 * Multiplies a matrix A with a vector x "from the right", i.e., y = A * x,
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
	 * Multiplies a matrix A with a vector x "from the right", i.e., y = A * x,
	 * where x is treated as a column vector and the result y is also a column vector.
	 * The result is placed in the vector passed as the last argument, 
	 * which is modified.
	 * Destructive version of {@link #multiply(double[][], double[])}.
	 * An exception is thrown if any matrix or vector dimensions do not match
	 * or if the two vectors are the same.
	 * @param A matrix of size (m,n)
	 * @param x a (column) vector of length n
	 * @param y a (column) vector of length m
	 */
	public static void multiplyD(final double[][] A, final double[] x, double[] y) {
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
	 * Multiplies a matrix A with a vector x "from the right", i.e., y = A * x,
	 * where x is treated as a column vector and the result y is also a column vector.
	 * The result is placed in the vector passed as the last argument, 
	 * which is modified.
	 * Destructive version of {@link #multiply(float[][], float[])}.
	 * An exception is thrown if any matrix or vector dimensions do not match
	 * or if the two vectors are the same.
	 * @param A matrix of size (m,n)
	 * @param x a (column) vector of length n
	 * @param y a (column) vector of length m
	 */
	public static void multiplyD(final float[][] A, final float[] x, float[] y) {
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
	/**
	 * Returns the product of two {@code double[][]} matrices A, B
	 * as a new {@code double[][]} matrix.
	 * Non-destructive, i.e., none of the arguments is modified.
	 * @param A first matrix
	 * @param B second matrix
	 * @return the matrix product A * B
	 */
	public static double[][] multiply(final double[][] A, final double[][] B) {
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();	// check size of A, B
		int ma = getNumberOfRows(A);
		int nb = getNumberOfColumns(B);
		double[][] C = makeDoubleMatrix(ma, nb);
		multiplyD(A, B, C);
		return C;
	}
	
	// A * B -> C (destructive)
	/**
	 * Calculates the product of two {@code double[][]} matrices A, B
	 * and places the results in the third {@code double[][]} matrix C,
	 * which is modified (destructively).
	 * An exception is thrown if any matrix dimensions do not match
	 * or if the target matrix is the same as one of the source matrices.
	 * @param A first matrix
	 * @param B second matrix
	 * @param C the result matrix
	 */
	public static void multiplyD(final double[][] A, final double[][] B, final double[][] C) 
			throws SameSourceTargetException, IncompatibleDimensionsException {
		if (A == C || B == C) 
			throw new SameSourceTargetException();
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		final int nB = getNumberOfColumns(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();	// check size of A, B
		if (mA != getNumberOfRows(C) || nB != getNumberOfColumns(C))
			throw new IncompatibleDimensionsException();	// check size of C
		
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
	/**
	 * Returns the product of two {@code float[][]} matrices A, B
	 * as a new {@code float[][]} matrix.
	 * Non-destructive, i.e., none of the arguments is modified.
	 * @param A first matrix
	 * @param B second matrix
	 * @return the matrix product A * B
	 */
	public static float[][] multiply(final float[][] A, final float[][] B) {
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();	// check size of A, B
		final int mA = getNumberOfRows(A);
		final int nB = getNumberOfColumns(B);
		float[][] C = makeFloatMatrix(mA, nB);
		multiplyD(A, B, C);
		return C;
	}

	// A * B -> C (destructive)
	/**
	 * Calculates the product of two {@code float[][]} matrices A, B
	 * and places the results in the third {@code float[][]} matrix C,
	 * which is modified (destructively).
	 * An exception is thrown if any matrix dimensions do not match
	 * or if the target matrix is the same as one of the source matrices.
	 * @param A first matrix
	 * @param B second matrix
	 * @param C the result matrix
	 */
	public static void multiplyD(final float[][] A, final float[][] B, final float[][] C) {
		if (A == C || B == C) 
			throw new SameSourceTargetException();
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		final int nB = getNumberOfColumns(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();	// check size of A,B
		if (mA != getNumberOfRows(C) || nB != getNumberOfColumns(C))
			throw new IncompatibleDimensionsException();	// check size of C
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
	 * Throws an exceptions if vector dimensions do not match.
	 * @param a first vector
	 * @param b second vector
	 * @return the dot product
	 */
	public static double dotProduct(final double[] a, final double[] b) {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum = sum + a[i] * b[i];
		}
		return sum;
	}
	
	// a is considered a column vector, b is a row vector, of length m, n, resp.
	// returns a matrix M of size (m,n).
	/**
	 * Calculates and returns the outer product of two vectors, which is a
	 * matrix of size (m,n), where m is the length of the first vector and
	 * m is the length of the second vector.
	 * @param a first (column) vector (of length m)
	 * @param b second (row) vector (of length n)
	 * @return the outer product (matrix)
	 */
	public static double[][] outerProduct(final double[] a, final double[] b) {
		final int m = a.length;
		final int n = b.length;
		final double[][] M = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				M[i][j] = a[i] * b[j];
			}
		}
		return M;
	}

	//  Vector norms ---------------------------------------------------

	/**
	 * Calculates and returns the L1 norm of the given vector.
	 * @param a a vector
	 * @return the L1 norm of the vector
	 */
	public static double normL1(final double[] a) {
		double sum = 0;
		for (double val : a) {
			sum = sum + Math.abs(val);
		}
		return sum;
	}
	
	/**
	 * Calculates and returns the L1 norm of the given vector.
	 * @param a a vector
	 * @return the L1 norm of the vector
	 */
	public static float normL1(final float[] a) {
		double sum = 0;
		for (double val : a) {
			sum = sum + Math.abs(val);
		}
		return (float) sum;
	}

	/**
	 * Calculates and returns the L2 norm of the given vector.
	 * @param a a vector
	 * @return the L2 norm of the vector
	 */
	public static double normL2(final double[] a) {
		return Math.sqrt(normL2squared(a));
	}

	/**
	 * Calculates and returns the squared L2 norm of the given vector.
	 * The squared norm is less costly to calculate (no square root
	 * needed) than the L2 norm and is thus often used for efficiency. 
	 * @param a a vector
	 * @return the squared L2 norm of the vector
	 */
	public static double normL2squared(final double[] a) {
		double sum = 0;
		for (double val : a) {
			sum = sum + sqr(val);
		}
		return sum;
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
	 * @param a a vector
	 * @return the squared L2 norm of the vector
	 */
	public static double normL2squared(final float[] a) {
		double sum = 0;
		for (double val : a) {
			sum = sum + sqr(val);
		}
		return sum;
	}
	
	// Normalize vectors
	
	/**
	 * Normalizes the specified {@code double[]} vector to unit
	 * (L2) norm and returns the result as a new {@code double[]} vector.
	 * @param a a vector
	 * @return the normalized vector
	 */
	public static double[] normalize(final double[] a) {
		double[] xx = duplicate(a);
		normalizeD(xx);
		return xx;
	}
	
	/**
	 * Normalizes the specified {@code double[]} vector to unit
	 * (L2) norm. The vector is modified (destructively).
	 * @param a a vector
	 */
	public static void normalizeD(final double[] a) {
		double normx = normL2(a);
		if (Arithmetic.isZero(normx))
			throw new IllegalArgumentException("cannot normalize zero-norm vector " + Matrix.toString(a));
		multiplyD(1.0 / normx, a);
	}
	
	/**
	 * Normalizes the specified {@code float[]} vector to unit
	 * (L2) norm and returns the result as a new {@code float[]} vector.
	 * @param a a vector
	 * @return the normalized vector
	 */
	public static float[] normalize(final float[] a) {
		float[] xx = duplicate(a);
		normalizeD(xx);
		return xx;
	}
	
	/**
	 * Normalizes the specified {@code float[]} vector to unit
	 * (L2) norm. The vector is modified (destructively).
	 * @param a a vector
	 */
	public static void normalizeD(final float[] a) {
		double normx = normL2(a);
		if (Arithmetic.isZero(normx))
			throw new IllegalArgumentException("cannot normalize zero-norm vector");
		multiplyD((float) (1.0 / normx), a);
	}
	
	// Distance between vectors ---------------------------------------
	
	/** 
	 * Calculates the L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance
	 */
	public static double distL2(double[] a, double[] b) {
		return Math.sqrt(distL2squared(a, b));
	}
	
	/** 
	 * Calculates the squared L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param a first vector
	 * @param b second vector
	 * @return the squared distance
	 */
	public static double distL2squared(double[] a, double[] b) {
		double sum = 0;
		for  (int i = 0; i < a.length; i++) {
			sum = sum + sqr(a[i] - b[i]);
		}
		return sum;
	}
	
	/** 
	 * Calculates the L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param a first vector
	 * @param b second vector
	 * @return the distance
	 */
	public static float distL2(float[] a, float[] b) {
		return (float) Math.sqrt(distL2squared(a, b));
	}
	
	/** 
	 * Calculates the squared L2 distance between two vectors (points)
	 * in n-dimensional space. Both vectors must have the same 
	 * number of elements.
	 * @param a first vector
	 * @param b second vector
	 * @return the squared distance
	 */
	public static float distL2squared(float[] a, float[] b) {
		double sum = 0;
		for  (int i = 0; i < a.length; i++) {
			sum = sum + sqr(a[i] - b[i]);
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
				s = s + sqr(A[i][j]);
			}
		}
		return Math.sqrt(s);
	}

	// Summation --------------------------------------------------

	/**
	 * Calculates and returns the sum of the elements in the specified 
	 * {@code double[]} vector.
	 * @param a a vector
	 * @return the sum of vector elements
	 */
	public static double sum(final double[] a) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum = sum + a[i];
		}
		return sum;
	}
	
	/**
	 * Calculates and returns the sum of the elements in the specified 
	 * {@code double[][]} matrix.
	 * @param A a matrix
	 * @return the sum of matrix elements
	 */
	public static double sum(final double[][] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				sum = sum + A[i][j];
			}
		}
		return sum;
	}
	
	/**
	 * Calculates and returns the sum of the elements in the specified 
	 * {@code float[]} vector.
	 * @param a a vector
	 * @return the sum of vector elements
	 */
	public static double sum(final float[] a) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum = sum + a[i];
		}
		return sum;
	}
	
	/**
	 * Calculates and returns the sum of the elements in the specified 
	 * {@code float[][]} matrix.
	 * @param A a matrix
	 * @return the sum of matrix elements
	 */
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
	public static double sumRow(final float[][] A, final int row) {
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
	public static double sumColumn(final float[][] A, final int col) {
		double sum = 0;
		for (int r = 0; r < A.length; r++) {
			sum = sum + A[r][col];
		}
		return sum;
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
	
	/**
	 * Returns the index of the smallest element in the
	 * specified vector. If the smallest value is
	 * not unique, the lowest index is returned.
	 * An exception is thrown if the vector has zero length.
	 * 
	 * @param a a vector
	 * @return the index of the smallest value
	 */
	public static int idxMin(double[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		double minval = a[0];
		int minidx = 0;
		for (int i = 1; i < a.length; i++) {
			if (a[i] < minval) {
				minval = a[i];
				minidx = i;
			}
		}
		return minidx;
	}
	
	/**
	 * Returns the index of the smallest element in the
	 * specified vector. If the smallest value is
	 * not unique, the lowest index is returned.
	 * An exception is thrown if the vector has zero length.
	 * 
	 * @param a a vector
	 * @return the index of the smallest value
	 */
	public static int idxMin(float[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		float minval = a[0];
		int minidx = 0;
		for (int i = 1; i < a.length; i++) {
			if (a[i] < minval) {
				minval = a[i];
				minidx = i;
			}
		}
		return minidx;
	}
	
	
	/**
	 * Returns the index of the largest element in the
	 * specified vector. If the largest value is
	 * not unique, the lowest index is returned.
	 * An exception is thrown if the vector has zero length.
	 * 
	 * @param a a vector
	 * @return the index of the largest value
	 */
	public static int idxMax(double[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		double maxval = a[0];
		int maxidx = 0;
		for (int i = 1; i < a.length; i++) {
			if (a[i] > maxval) {
				maxval = a[i];
				maxidx = i;
			}
		}
		return maxidx;
	}
	
	/**
	 * Returns the index of the largest element in the
	 * specified vector. If the largest value is
	 * not unique, the lowest index is returned.
	 * An exception is thrown if the vector has zero length.
	 * 
	 * @param a a vector
	 * @return the index of the largest value
	 */
	public static int idxMax(float[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		float maxval = a[0];
		int maxidx = 0;
		for (int i = 1; i < a.length; i++) {
			if (a[i] > maxval) {
				maxval = a[i];
				maxidx = i;
			}
		}
		return maxidx;
	}
	
	
	/**
	 * Returns the smallest value in the
	 * specified vector. 
	 * An exception is thrown if the vector has zero length.
	 * @param a a vector
	 * @return the largest value
	 */
	public static float min(final float[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		float minval = Float.POSITIVE_INFINITY;
		for (float val : a) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	
	/**
	 * Returns the smallest value in the
	 * specified vector. 
	 * An exception is thrown if the vector has zero length.
	 * @param a a vector
	 * @return the largest value
	 */
	public static double min(final double[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		double minval = Double.POSITIVE_INFINITY;
		for (double val : a) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	
	/**
	 * Returns the largest value in the specified {@code double[]} vector. 
	 * An exception is thrown if the vector has zero length.
	 * @param a a vector
	 * @return the largest value
	 */
	public static double max(final double[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		double maxval = Double.NEGATIVE_INFINITY;
		for (double val : a) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}

	/**
	 * Returns the largest value in the specified {@code float[]} vector. 
	 * An exception is thrown if the vector has zero length.
	 * @param a a vector
	 * @return the largest value
	 */
	public static float max(final float[] a) {
		if (a.length == 0)
			throw new ZeroLengthVectorException();
		float maxval = Float.NEGATIVE_INFINITY;
		for (float val : a) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}
	
	/**
	 * Returns the largest value in the specified {@code double[][]} matrix. 
	 * @param A a matrix
	 * @return the largest matrix value
	 */
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
	
	/**
	 * Returns the largest value in the specified {@code float[][]} matrix. 
	 * @param A a matrix
	 * @return the largest matrix value
	 */
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
	 * @param as a sequence of vectors (at least one vector)
	 * @return a vector containing all elements of the input vectors
	 */
	public static float[] join(float[]... as) {
		int n = 0;
		for (float[] x : as) {
			n = n + x.length;
		}
		float[] va = new float[n];
		int j = 0;
		for (float[] x : as) {
			for (int i = 0; i < x.length; i++) {
				va[j] = x[i];
				j++;
			}
		}		
		return va;
	}

	/**
	 * Joins (concatenates) a sequence of vectors into a single vector.
	 * @param as a sequence of vectors (at least one vector)
	 * @return a vector containing all elements of the input vectors
	 */
	public static double[] join(double[]... as) {
		int n = 0;
		for (double[] x : as) {
			n = n + x.length;
		}
		double[] va = new double[n];	
		int j = 0;
		for (double[] x : as) {
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
		final float[] c = new float[a.length];
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
		final double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] + t * (b[i] - a[i]);
		}
		return c;
	}
	
	
	// Homogeneous coordinates ---------------------------------
	
	/**
	 * Converts a Cartesian vector to an equivalent homogeneous
	 * vector by attaching an additional 1-element.
	 * The resulting homogeneous vector is one element longer than the 
	 * specified Cartesian vector.
	 * See also {@link #toCartesian(double[])}.
	 * @param ac a Cartesian vector
	 * @return an equivalent homogeneous vector
	 */
	public static double[] toHomogeneous(double[] ac) {
		double[] xh = new double[ac.length + 1];
		for (int i = 0; i < ac.length; i++) {
			xh[i] = ac[i];
			xh[xh.length - 1] = 1;
		}
		return xh;
	}
	
	/**
	 * Converts a homogeneous vector to its equivalent Cartesian
	 * vector, which is one element shorter.
	 * See also {@link #toHomogeneous(double[])}.
	 * @param ah a homogeneous vector
	 * @return the equivalent Cartesian vector
	 */
	public static double[] toCartesian(double[] ah) throws DivideByZeroException {
		double[] xc = new double[ah.length - 1];
		final double s = 1 / ah[ah.length - 1];
		if (!Double.isFinite(s))	// isZero(s)
			throw new DivideByZeroException();
		for (int i = 0; i < ah.length - 1; i++) {
			xc[i] = s * ah[i];
		}
		return xc;
	}
	
	// Determinants --------------------------------------------
	
	/**
	 * Calculates and returns the determinant of the given {@link RealMatrix}.
	 * Throws an exception if the matrix is non-square.
	 * @param A a square matrix
	 * @return the determinant
	 */
	public static double determinant(RealMatrix A) {
		if (!A.isSquare())
			throw new NonsquareMatrixException();
		return new LUDecomposition(A).getDeterminant();
	}
	
	/**
	 * Calculates and returns the determinant of the given {@code double[][]} matrix.
	 * Throws an exception if the matrix is non-square.
	 * @param A a square matrix
	 * @return the determinant
	 */
	public static double determinant(final double[][] A) {
		return determinant(MatrixUtils.createRealMatrix(A));
	}
	
	/**
	 * Calculates and returns the determinant of the given 2x2 {@code double[][]} matrix.
	 * This method is hard-coded for the specific matrix size for better performance
	 * than the general method ({@link #determinant(double[][])}).
	 * Throws an exception if the matrix is not 2x2.
	 * @param A a 2x2 matrix
	 * @return the determinant
	 */
	public static double determinant2x2(final double[][] A) {
		if (A.length != 2 || A[0].length != 2)
			throw new IncompatibleDimensionsException();
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	/**
	 * Calculates and returns the determinant of the given 2x2 {@code float[][]} matrix.
	 * Throws an exception if the matrix is not 2x2.
	 * @param A a 2x2 matrix
	 * @return the determinant
	 */
	public static float determinant2x2(final float[][] A) {
		if (A.length != 2 || A[0].length != 2)
			throw new IncompatibleDimensionsException();
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	

	/**
	 * Calculates and returns the determinant of the given 3x3 {@code double[][]} matrix.
	 * This method is hard-coded for the specific matrix size for better performance
	 * than the general method ({@link #determinant(double[][])}).
	 * Throws an exception if the matrix is not 3x3.
	 * @param A a 3x3 matrix
	 * @return the determinant
	 */
	public static double determinant3x3(final double[][] A) {
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
	
	/**
	 * Calculates and returns the determinant of the given 3x3 {@code float[][]} matrix.
	 * Throws an exception if the matrix is not 3x3.
	 * @param A a 3x3 matrix
	 * @return the determinant
	 */
	public static float determinant3x3(final float[][] A) {
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


	
	// Matrix trace ---------------------------------------
	
	/**
	 * Calculates and returns the trace of the given {@code double[][]} matrix.
	 * Throws an exception if the matrix is non-square.
	 * @param A a square matrix
	 * @return the trace 
	 */
	public static double trace(final double[][] A) {
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
	
	/**
	 * Calculates and returns the trace of the given {@code float[][]} matrix.
	 * Throws an exception if the matrix is non-square.
	 * @param A a square matrix
	 * @return the trace 
	 */
	public static float trace(final float[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (m != n) 
			throw new NonsquareMatrixException();
		double s = 0;
		for (int i = 0; i < m; i++) {
				s = s + A[i][i];
		}
		return (float) s;
	}
	
	// Matrix transposition ---------------------------------------
	
	/**
	 * Returns the transpose of the given {@code double[][]} matrix.
	 * The original matrix is not modified.
	 * @param A a matrix
	 * @return the transpose of the matrix
	 */
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
	
	/**
	 * Returns the transpose of the given {@code float[][]} matrix.
	 * The original matrix is not modified.
	 * @param A a matrix
	 * @return the transpose of the matrix
	 */
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
	
	
	// Checking vectors for all zero values  ------------------------------
	
	/**
	 * Checks if all elements of the specified {@code double[]} vector are zero
	 * using the specified tolerance value.
	 * @param a a vector
	 * @param tolerance the tolerance value
	 * @return true if all vector elements are smaller than the tolerance
	 */
	public static boolean isZero(double[] a, double tolerance) {
		for (int i = 0; i < a.length; i++) {
			if (!Arithmetic.isZero(a[i], tolerance)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all elements of the specified {@code double[]} vector are zero
	 * using the default tolerance value ({@link Arithmetic#EPSILON_DOUBLE}).
	 * @param a a vector
	 * @return true if all vector elements are smaller than the tolerance
	 */
	public static boolean isZero(double[] a) {
		return isZero(a, Arithmetic.EPSILON_DOUBLE);
	}
	
	/**
	 * Checks if all elements of the specified {@code float[]} vector are zero
	 * using the specified tolerance value.
	 * @param a a vector
	 * @param tolerance the tolerance value
	 * @return true if all vector elements are smaller than the tolerance
	 */
	public static boolean isZero(float[] a, float tolerance) {
		for (int i = 0; i < a.length; i++) {
			if (!Arithmetic.isZero(a[i], tolerance)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all elements of the specified {@code float[]} vector are zero
	 * using the default tolerance value ({@link Arithmetic#EPSILON_DOUBLE}).
	 * @param a a vector
	 * @return true if all vector elements are smaller than the tolerance
	 */
	public static boolean isZero(float[] a) {
		return isZero(a, Arithmetic.EPSILON_FLOAT);
	}
	
	// Checking matrices for all zero values  ------------------------------
	
	/**
	 * Checks if all elements of the specified {@code double[][]} matrix are zero
	 * using the specified tolerance value.
	 * @param A a matrix
	 * @param tolerance the tolerance value
	 * @return true if all matrix elements are smaller than the tolerance
	 */
	public static boolean isZero(double[][] A, double tolerance) {
		for (int i = 0; i < A.length; i++) {	// for each matrix row i
			if (!Matrix.isZero(A[i], tolerance)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all elements of the specified {@code double[][]} matrix are zero
	 * using default tolerance value ({@link Arithmetic#EPSILON_DOUBLE}).
	 * @param A a matrix
	 * @return true if all matrix elements are smaller than the tolerance
	 */
	public static boolean isZero(double[][] A) {
		return isZero(A, Arithmetic.EPSILON_DOUBLE);
	}
	
	// Sorting vectors (non-destructively)  ------------------------------
	
	/**
	 * Returns a sorted copy of the given {@code double[]} vector.
	 * Elements are sorted by increasing value (smallest first).
	 * 
	 * @param a a {@code double[]} vector
	 * @return a sorted copy of the vector
	 */
	public static double[] sort(double[] a) {
		double[] y = Arrays.copyOf(a, a.length);
		Arrays.sort(y);
		return y;
	}
	
	/**
	 * Returns a sorted copy of the given {@code float[]} vector.
	 * Elements are sorted by increasing value (smallest first).
	 * 
	 * @param a a {@code float[]} vector
	 * @return a sorted copy of the vector
	 */
	public static float[] sort(float[] a) {
		float[] y = Arrays.copyOf(a, a.length);
		Arrays.sort(y);
		return y;
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
	 * Calls {@link #solve(RealMatrix, RealVector)}.
	 * 
	 * @param A a square matrix of size n x n
	 * @param b a vector of length n
	 * @return the solution vector (x) of length n or {@code null} if no solution possible
	 */
	
	public static double[] solve(final double[][] A, double[] b) {
		RealVector x = solve(MatrixUtils.createRealMatrix(A), MatrixUtils.createRealVector(b));
		return (x == null) ? null : x.toArray();
	}
	
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
	public static RealVector solve(RealMatrix A, RealVector b) {
		if (!A.isSquare()) {
			throw new NonsquareMatrixException();
		}
		if (A.getRowDimension() != b.getDimension()) {
			throw new IncompatibleDimensionsException();
		}
//		DecompositionSolver solver = new LUDecomposition(A, 0.0).getSolver();
		DecompositionSolver solver = new LUDecomposition(A).getSolver();
		RealVector x = null;
		try {
			x = solver.solve(b);
		} catch (SingularMatrixException e) {}
		return x;
	}
	
	// Output to strings and streams ------------------------------------------
	
	/**
	 * Returns a string representation of the specified vector.
	 * @param a a vector
	 * @return the string representation
	 */
	public static String toString(double[] a) {
		if (a == null) {
			return String.valueOf(a);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(a, strm);
		return bas.toString();
	}
	
	/**
	 * Returns a string representation of the specified vector.
	 * @param a a vector
	 * @return the string representation
	 */
	public static String toString(float[] a) {
		if (a == null) {
			return String.valueOf(a);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(a, strm);
		return bas.toString();
	}
	
	/**
	 * Returns a string representation of the specified vector.
	 * @param a a vector
	 * @return the string representation
	 */
	public static String toString(RealVector a) {
		if (a == null) {
			return String.valueOf(a);
		}
		else {
			return toString(a.toArray());
		}
	}
	
	/**
	 * Returns a string representation of the specified matrix.
	 * @param A a matrix
	 * @return the string representation
	 */
	public static String toString(double[][] A) {
		if (A == null) {
			return String.valueOf(A);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	/**
	 * Returns a string representation of the specified matrix.
	 * @param A a matrix
	 * @return the string representation
	 */
	public static String toString(float[][] A) {
		if (A == null) {
			return String.valueOf(A);
		}
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	/**
	 * Returns a string representation of the specified matrix.
	 * @param A a matrix
	 * @return the string representation
	 */
	public static String toString(RealMatrix A) {
		if (A == null) {
			return String.valueOf(A);
		}
		else {
			return toString(A.getData());
		}
	}
	
	// --------------------
	
	/**
	 * Outputs a string representation of the given vector to
	 * the specified output stream.
	 * @param a the vector
	 * @param strm the output stream
	 * @see #toString(double[])
	 */
	public static void printToStream(double[] a, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i = 0; i < a.length; i++) {
			if (i > 0)
				strm.format("%c ", SeparationChar);
			strm.format(PrintLocale, fStr, a[i]);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	/**
	 * Outputs a string representation of the given matrix to
	 * the specified output stream.
	 * @param A the matrix
	 * @param strm the output stream
	 * @see #toString(double[][])
	 */
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
	
	/**
	 * Outputs a string representation of the given vector to
	 * the specified output stream.
	 * @param a the vector
	 * @param strm the output stream
	 * @see #toString(float[])
	 */
	public static void printToStream(float[] a, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i = 0; i < a.length; i++) {
			if (i > 0)
				strm.format("%c ", SeparationChar);
			strm.format(PrintLocale, fStr, a[i]);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	/**
	 * Outputs a string representation of the given matrix to
	 * the specified output stream.
	 * @param A the matrix
	 * @param strm the output stream
	 * @see #toString(float[][])
	 */
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
	
	/** Thrown when the dimensions of matrix/vector arguments do not match. */
	public static class IncompatibleDimensionsException extends RuntimeException {		
		public IncompatibleDimensionsException() {
			super("incompatible matrix-vector dimensions");
		}
		public IncompatibleDimensionsException(String msg) {
			super(msg);
		}
	}
	
	/** Thrown when a non-square matrix is encountered where a square matrix is assumed. */
	public static class NonsquareMatrixException extends RuntimeException {
		public NonsquareMatrixException() {
			super("square matrix expected");
		}
		public NonsquareMatrixException(String msg) {
			super(msg);
		}
	}
	
	/** Thrown when source and target objects are identical but shouldn't. */
	public static class SameSourceTargetException extends RuntimeException {
		public SameSourceTargetException() {
			super("source and target must not be the same");
		}
		public SameSourceTargetException(String msg) {
			super(msg);
		}
	}
	
	/** Thrown when the length of some vector is zero. */
	public static class ZeroLengthVectorException extends RuntimeException {	
		public ZeroLengthVectorException() {
			super("vector length must be at least 1");
		}
		public ZeroLengthVectorException(String msg) {
			super(msg);
		}
	}
	
	// ------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		double s = Double.NaN;
//		s = -1.0 / 1E-200; // / 1E-200;
//		System.out.println(Double.isFinite(s));
//		System.out.println((double[]) null);
//		
//		double[][] A = makeDoubleMatrix(5, 4,
//				1,2,3,4,
//				5,6,7,8,
//				9,10,11,12,
//				13,14,15,16,
//				17,18,19,20);
//		System.out.println("A = \n" + toString(A));
//		
//		RealMatrix B = makeRealMatrix(5, 4);
//		System.out.println("B = \n" + toString(B.getData()));
//	}

}

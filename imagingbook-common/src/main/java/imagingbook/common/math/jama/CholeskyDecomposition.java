/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math.jama;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Matrix;


@Deprecated 
public class CholeskyDecomposition {

    public static final double DEFAULT_RELATIVE_SYMMETRY_THRESHOLD = 1.0e-15;
    public static final double DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD = 1.0e-10;

	private final double[][] L;				// array for internal storage of decomposition
	private final int n;					// row and column dimension (square matrix)
	private final boolean isPosDefinite;	// symmetric and positive definite flag

	/**
	 * 
	 * @param matrix a square, symmetric matrix to be decomposed
	 * @param relativeSymmetryThreshold 
	 * 		threshold above which off-diagonal elements are considered too different and matrix not symmetric
	 * @param absolutePositivityThreshold
	 * 		threshold below which diagonal elements are considered null and matrix not positive definite
	 * @throws NonSquareMatrixException if the matrix is not square
	 */
    public CholeskyDecomposition(RealMatrix matrix, double relativeSymmetryThreshold, double absolutePositivityThreshold) {
		if (!matrix.isSquare()) {
			throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
		}

		final double[][] A = matrix.getData();
		this.n = matrix.getRowDimension();
		this.L = new double[n][n];

		boolean posDef = true;
		for (int j = 0; j < n; j++) {
			final double[] Lrowj = L[j];
			double d = 0.0;
			for (int k = 0; k < j; k++) {
				if (Arithmetic.isZero(L[k][k])) {
					throw new RuntimeException("encountered zero diagonal value at pos. " + k);
				}
				final double[] Lrowk = L[k];
				double s = 0.0;
				for (int i = 0; i < k; i++) {
					s += Lrowk[i] * Lrowj[i];
				}
//				Lrowj[k] = s = (A[j][k] - s) / L[k][k];
				s = (A[j][k] - s) / L[k][k];
				Lrowj[k] = s;
				d = d + s * s;
				posDef = posDef & Arithmetic.equals(A[k][j], A[j][k], relativeSymmetryThreshold); // (A[k][j] == A[j][k]);
			}
			d = A[j][j] - d;
			posDef = posDef & (d > absolutePositivityThreshold);
			L[j][j] = Math.sqrt(Math.max(d, 0.0));
			for (int k = j + 1; k < n; k++) {
				L[j][k] = 0.0;
			}
		}

		this.isPosDefinite = posDef;
    }
    
    
	/**
	 * Constructor.
	 * 
	 * @param matrix a square, symmetric matrix
	 * @throws NonSquareMatrixException if the matrix is not square
	 */
	public CholeskyDecomposition(RealMatrix matrix) {
		this(matrix, DEFAULT_RELATIVE_SYMMETRY_THRESHOLD, DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD);
	}


	/**
	 * Checks if the matrix symmetric and positive definite.
	 * @return true if the decomposed matrix is symmetric and positive definite
	 */
	public boolean isPositiveDefinite() {
		return isPosDefinite;
	}

	/**
	 * Returns the triangular factor matrix.
	 * @return the matrix L
	 */
	public RealMatrix getL() {
		return MatrixUtils.createRealMatrix(L);
	}
	
	
	

//	/**
//	 * Solve A*X = B
//	 *
//	 * @param B a matrix with as many rows as A and any number of columns.
//	 * @return X so that L*L'*X = B
//	 * @exception IllegalArgumentException Matrix row dimensions must agree.
//	 * @exception RuntimeException Matrix is not symmetric positive definite.
//	 */
//
//	public RealMatrix solve(RealMatrix B) {
//		if (B.getRowDimension() != n) {
//			throw new IllegalArgumentException("Matrix row dimensions must agree.");
//		}
//		if (!isspd) {
//			throw new RuntimeException("Matrix is not symmetric positive definite.");
//		}
//
//		// Copy right hand side.
////		final double[][] X = B.getArrayCopy();
//		final double[][] X = B.getData();
//		final int nx = B.getColumnDimension();
//
//		// Solve L*Y = B;
//		for (int k = 0; k < n; k++) {
//			for (int j = 0; j < nx; j++) {
//				for (int i = 0; i < k; i++) {
//					X[k][j] -= X[i][j] * L[k][i];
//				}
//				X[k][j] /= L[k][k];
//			}
//		}
//
//		// Solve L'*X = Y;
//		for (int k = n - 1; k >= 0; k--) {
//			for (int j = 0; j < nx; j++) {
//				for (int i = k + 1; i < n; i++) {
//					X[k][j] -= X[i][j] * L[i][k];
//				}
//				X[k][j] /= L[k][k];
//			}
//		}
//
////		return new Matrix(X, n, nx);
//		return MatrixUtils.createRealMatrix(X);
//	}
	
	
	
	// ---------------------------------------------------------------------------------
	
	public static void main(String[] args) {
			
		/**
		 * This symmetric 6x6 has only rank 5 and cannot be handled by Apache Common Maths's
		 * Cholesky implementation:
		 */
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			{2497883442.0, 762966565.0, 326612325.0, 15097274.0, 4845135.0, 94770.0}, 
			{762966565.0, 326612325.0, 188262895.0, 4845135.0, 2264945.0, 32815.0}, 
			{326612325.0, 188262895.0, 130208963.0, 2264945.0, 1433555.0, 17519.0}, 
			{15097274.0, 4845135.0, 2264945.0, 94770.0, 32815.0, 638.0}, 
			{4845135.0, 2264945.0, 1433555.0, 32815.0, 17519.0, 257.0}, 
			{94770.0, 32815.0, 17519.0, 638.0, 257.0, 5.0}});
		
		{
			System.out.println("B = \n" + Matrix.toString(B));
			CholeskyDecomposition cd = new CholeskyDecomposition(B);
			System.out.println("isPositiveDefinite = " + cd.isPositiveDefinite());
			RealMatrix L = cd.getL();
			System.out.println("L = \n" + Matrix.toString(L));
			System.out.println("L * LT = \n" + Matrix.toString(L.multiply(L.transpose())));
		}
		
//		{
//			System.out.println("B = \n" + Matrix.toString(B));
//			org.apache.commons.math3.linear.CholeskyDecomposition cd = new org.apache.commons.math3.linear.CholeskyDecomposition(B);
//			RealMatrix L = cd.getL();
//			System.out.println("L = \n" + Matrix.toString(L));
//			System.out.println("L * LT = \n" + Matrix.toString(L.multiply(L.transpose())));
//		}
		
		
	}
}

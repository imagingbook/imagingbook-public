/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.eigen;

import imagingbook.common.math.Matrix;
import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;

import static imagingbook.common.math.Matrix.isSquare;
import static imagingbook.common.math.Matrix.sameSize;
import static imagingbook.common.math.eigen.eispack.QZHES.qzhes;
import static imagingbook.common.math.eigen.eispack.QZIT.qzit;
import static imagingbook.common.math.eigen.eispack.QZVAL.qzval;
import static imagingbook.common.math.eigen.eispack.QZVEC.qzvec;

/**
 * <p>
 * Solves the generalized eigenproblem of the form A x = &lambda; B x, for square matrices A, B.
 * </p>
 * <p>
 * This implementation was ported from original EISPACK code [1] using a finite state machine concept [2] to untangle
 * Fortran's GOTO statements (which are not available in Java), with some inspirations from this <a href=
 * "https://github.com/accord-net/framework/blob/development/Sources/Accord.Math/Decompositions/GeneralizedEigenvalueDecomposition.cs">
 * C# implementation</a>. See
 * <a href="https://mipav.cit.nih.gov/">mipav.cit.nih.gov</a> (file
 * {@code gov.nih.mipav.model.structures.jama.GeneralizedEigenvalue.java}) for another Java implementation based on
 * EISPACK.
 * </p>
 * <p>
 * Note: Results have limited accuracy. For some reason the first eigenvalue/-vector (k=0) is reasonably accurate, but
 * the remaining ones are not. If matrices A, B are symmetric and B is positive definite, better use
 * {@link GeneralizedSymmetricEigenDecomposition} instead, which is more accurate.
 * </p>
 * <p>
 * [1] http://www.netlib.no/netlib/eispack/ <br> [2] D. E. Knuth, "Structured Programming with Goto Statements",
 * Computing Surveys, Vol. 6, No. 4 (1974).
 *
 * @author WB
 * @version 2022/12/22
 * @see GeneralizedSymmetricEigenDecomposition
 */
public class GeneralizedEigenDecomposition implements RealEigenDecomposition {
	// TODO: Check complex-valued eigenvectors.

	public static boolean VERBOSE = false;

	private final int n;
	private final double[] alphaR;
	private final double[] alphaI;
	private final double[] beta;
	private final double[][] Z;

	/**
	 * Constructor, solves the generalized eigenproblem A x = &lambda; B x, for square matrices A, B.
	 *
	 * @param A square matrix A
	 * @param B square matrix B
	 */
	public GeneralizedEigenDecomposition(RealMatrix A, RealMatrix B) {
		this(A.getData(), B.getData());
	}

	/**
	 * Constructor, solves the generalized eigenproblem A x = &lambda; B x, for square matrices A, B.
	 *
	 * @param A square matrix A
	 * @param B square matrix B
	 */
	public GeneralizedEigenDecomposition(double[][] A, double[][] B) {
		if (!isSquare(A) || !isSquare(B) || !sameSize(A, B)) {
			throw new IllegalArgumentException("matrices A, B must be square and of same size");
		}
		this.n = A.length;
		this.alphaR = new double[n];
		this.alphaI = new double[n];
		this.beta = new double[n];
		this.Z = new double[n][n];

		// a, b are modified by EISPACK routines:
		double[][] a = Matrix.duplicate(A);
		double[][] b = Matrix.duplicate(B);
		boolean matz = true;

		qzhes(a, b, matz, Z);
		int ierr = qzit(a, b, 0, matz, Z);
		if (ierr >= 0) {
			throw new RuntimeException("limit of 30*n iterations exhausted for eigenvalue " + ierr);
		}
		qzval(a, b, alphaR, alphaI, beta, matz, Z);
		qzvec(a, b, alphaR, alphaI, beta, Z);
	}

	/**
	 * Returns a vector with the real parts of the eigenvalues.
	 *
	 * @return the real parts of the eigenvalues
	 */
	@Override
	public double[] getRealEigenvalues() {
		double[] eval = new double[n];
		for (int i = 0; i < n; i++) {
			eval[i] = alphaR[i] / beta[i];
		}
		return eval;
	}

	@Override
	public double getRealEigenvalue(int k) {
		return alphaR[k] / beta[k];
	}

	/**
	 * Returns a vector with the imaginary parts of the eigenvalues.
	 *
	 * @return the imaginary parts of the eigenvalues
	 */
	public double[] getImagEigenvalues() {
		double[] eval = new double[n];
		for (int i = 0; i < n; i++)
			eval[i] = alphaI[i] / beta[i];
		return eval;
	}

	/**
	 * Return the matrix of eigenvectors, which are its column vectors.
	 *
	 * @return the matrix of eigenvectors
	 */
	@Override
	public RealMatrix getV() {	// TODO: not implemented/checked for complex eigenvalues yet!
		return MatrixUtils.createRealMatrix(Z);
	}

	/**
	 * Returns the specified eigenvector.
	 *
	 * @param k index
	 * @return the kth eigenvector
	 */
	@Override
	public RealVector getEigenvector(int k) {	// TODO: not implemented for complex eigenvalues yet!
		return MatrixUtils.createRealVector(Matrix.getColumn(Z, k));
	}

	/**
	 * Returns whether the calculated eigenvalues are complex or real. The method performs a zero check on each element
	 * of the {@link #getImagEigenvalues()} array and returns {@code true} if any element is not equal to zero.
	 *
	 * @return {@code true} if any of the eigenvalues is complex, {@code false} otherwise
	 */
	@Override
	public boolean hasComplexEigenvalues() {
		for (int i = 0; i < n; i++) {
			if (alphaI[i] != 0.0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the block diagonal eigenvalue matrix D.
	 *
	 * @return the block diagonal eigenvalue matrix
	 */
	@Override
	public RealMatrix getD() {	// TODO: check complex case!
		double[][] x = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++)
				x[i][j] = 0.0;

			x[i][i] = alphaR[i] / beta[i];
			if (alphaI[i] > 0)
				x[i][i + 1] = alphaI[i] / beta[i];
			else if (alphaI[i] < 0)
				x[i][i - 1] = alphaI[i] / beta[i];
		}

		return MatrixUtils.createRealMatrix(x);
	}

	// ----------------------------------------------------------

	/**
	 * This method checks if any of the generated betas is zero. It does not says that the problem is singular, but only
	 * that one of the matrices A, B is singular.
	 *
	 * @return true if A or B is singular
	 */
	public boolean isSingular() {
		for (int i = 0; i < n; i++) {
			if (beta[i] == 0)
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the eigenvalue problem is degenerate (i.e., ill-posed).
	 *
	 * @return true if degenerate
	 */
	public boolean IsDegenerate() {
		for (int i = 0; i < n; i++) {
			if (beta[i] == 0 && alphaR[i] == 0)
				return true;
		}
		return false;
	}

}
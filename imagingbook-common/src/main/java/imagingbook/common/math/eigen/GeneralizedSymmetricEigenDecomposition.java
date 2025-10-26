/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.eigen;


import org.apache.commons.math4.legacy.linear.CholeskyDecomposition;
import org.apache.commons.math4.legacy.linear.DecompositionSolver;
import org.apache.commons.math4.legacy.linear.EigenDecomposition;
import org.apache.commons.math4.legacy.linear.LUDecomposition;
import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;

// import org.apache.commons.math3.linear.CholeskyDecomposition;
// import org.apache.commons.math3.linear.DecompositionSolver;
// import org.apache.commons.math3.linear.EigenDecomposition;
// import org.apache.commons.math3.linear.LUDecomposition;
// import org.apache.commons.math3.linear.MatrixUtils;
// import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
// import org.apache.commons.math3.linear.RealMatrix;
// import org.apache.commons.math3.linear.RealVector;

import static org.apache.commons.math3.linear.CholeskyDecomposition.DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD;
import static org.apache.commons.math3.linear.CholeskyDecomposition.DEFAULT_RELATIVE_SYMMETRY_THRESHOLD;

/**
 * <p>
 * Solves the generalized symmetric eigenproblem of the form A x = &lambda; B x, where matrices A, B are symmetric and B
 * is positive definite (see Sec. 11.0.5. of [1]). See Appendix Sec. B.5.2 of [2] for more details. The methods defined
 * by this class are analogous to the conventional eigendecomposition (see {@link EigenDecomposition}).
 * </p>
 * <p>
 * [1] Press, Teukolsky, Vetterling, Flannery: "Numerical Recipes". Cambridge University Press, 3rd ed. (2007). <br> [2]
 * W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/06/11
 * @see GeneralizedEigenDecomposition
 * @see EigenDecompositionJama
 */
public class GeneralizedSymmetricEigenDecomposition implements RealEigenDecomposition {

	private final EigenDecomposition eigendecompY;
	private final DecompositionSolver solverLT;

	/**
	 * Constructor, using specific parameters. Solves the generalized symmetric eigenproblem of the form A x = &lambda;
	 * B x, where matrices A, B are symmetric and B is positive definite. An exception is thrown if A is not symmetric
	 * and the Cholesky decomposition throws an exception if B is either not symmetric, not positive definite or
	 * singular.
	 *
	 * @param A real symmetric matrix
	 * @param B real symmetric and positive definite matrix
	 * @param rsth relative symmetry threshold
	 * @param apth absolute positivity threshold
	 */
	public GeneralizedSymmetricEigenDecomposition(RealMatrix A, RealMatrix B, double rsth, double apth) {
		if (!MatrixUtils.isSymmetric(A, rsth)) {
			throw new IllegalArgumentException("matrix A must be symmetric");
		}

		if (!MatrixUtils.isSymmetric(B, rsth)) {
			throw new IllegalArgumentException("matrix B must be symmetric");
		}

		CholeskyDecomposition cd = null;
		try {
			cd = new CholeskyDecomposition(B, rsth, apth);
		} catch (NonPositiveDefiniteMatrixException e) {
			throw new IllegalArgumentException("matrix B must be positive definite");
		}

		RealMatrix L = cd.getL();

		// find Q, such that Q * LT = A or equivalently L * QT = AT = A (since A is symmetric)
		DecompositionSolver sL = new LUDecomposition(L).getSolver();
		RealMatrix Q = sL.solve(A);

		// find Y, such that L * Y = QT
		RealMatrix Y = sL.solve(Q.transpose());

		// Y has the same eigenvalues as the original system and eigenvectors v_k
		this.eigendecompY = new EigenDecomposition(Y);    // use EigenDecompositionJama instead?

		// the eigenvectors x_k of the original system are related
		// to the eigenvectors v_k of Y as x_k = (LT)^(-1) * v_k = (L^(-1))^T * v_k
		// or found by solving L^T * x_k = y_k, using the following solver:
		this.solverLT = new LUDecomposition(cd.getLT()).getSolver();
	}

	/**
	 * Constructor, using default parameters. Solves the generalized symmetric eigenproblem of the form A x = &lambda; B
	 * x, where matrices A, B * are symmetric and B is positive definite
	 *
	 * @param A real symmetric matrix
	 * @param B real symmetric and positive definite matrix
	 */
	public GeneralizedSymmetricEigenDecomposition(RealMatrix A, RealMatrix B) {
		this(A, B,
				DEFAULT_RELATIVE_SYMMETRY_THRESHOLD,
				DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD);
	}

	// ---------------------------------------------------------------------

	@Override
	public double[] getRealEigenvalues() {
		return eigendecompY.getRealEigenvalues();
	}

	@Override
	public double getRealEigenvalue(int k) {
		return eigendecompY.getRealEigenvalue(k);
	}

	public double[] getImagEigenvalues() {
		return eigendecompY.getImagEigenvalues();
	}

	@Override
	public boolean hasComplexEigenvalues() {
		return eigendecompY.hasComplexEigenvalues();
	}

	@Override
	public RealMatrix getD() {
		return eigendecompY.getD();
	}

	@Override
	public RealVector getEigenvector(int k) {
//		return LiT.operate(ed.getEigenvector(k));
		// solve LT * x_k = v_k
		RealVector vk = eigendecompY.getEigenvector(k);
		return solverLT.solve(vk);
	}

	@Override
	public RealMatrix getV() {
//		return LiT.multiply(ed.getV());
		// solve LT * X = V
		return solverLT.solve(eigendecompY.getV());
	}

}


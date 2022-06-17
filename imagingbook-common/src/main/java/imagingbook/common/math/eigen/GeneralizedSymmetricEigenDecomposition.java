/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math.eigen;

import static org.apache.commons.math3.linear.CholeskyDecomposition.DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD;
import static org.apache.commons.math3.linear.CholeskyDecomposition.DEFAULT_RELATIVE_SYMMETRY_THRESHOLD;

import java.util.Arrays;

import org.apache.commons.math3.linear.CholeskyDecomposition;
//import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

//import imagingbook.common.math.CholeskyDecomposition;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

/**
 * Solves the generalized symmetric eigenproblem of the form A x = &lambda; B x,
 * where matrices A, B are symmetric and B is positive definite
 * (see Sec. 11.0.5. of [1]).
 * The methods defined by this class are analogous to the conventional
 * eigendecomposition (see {@link EigenDecomposition}).
 * 
 * <p>
 * [1] Press, Teukolsky, Vetterling, Flannery:
 * "Numerical Recipes". Cambridge University Press, 3rd ed. (2007).
 * </p>
 * @see EigenDecomposition
 * @author WB
 * @version 2022/06/11
 */
public class GeneralizedSymmetricEigenDecomposition {
	
	private final EigenDecomposition eigendecompY;
	private final DecompositionSolver solverLT;
	
	/**
	 * Constructor.
	 * An exception is thrown if A is not symmetric and
	 * the Cholesky decomposition throws an exception if B is either not symmetric,
	 * not positive definite or singular.
	 * 
	 * @param A real symmetric matrix
	 * @param B real symmetric and positive definite matrix
	 * @param rsth	relative symmetry threshold
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
		this.eigendecompY = new EigenDecomposition(Y);
		
		// the eigenvectors x_k of the original system are related
		// to the eigenvectors v_k of Y as x_k = (LT)^(-1) * v_k = (L^(-1))^T * v_k
		// or found by solving L^T * x_k = y_k, using the following solver:
		this.solverLT = new LUDecomposition(cd.getLT()).getSolver();
	}
	
	/**
	 * Constructor.
	 * See {@link #GeneralizedSymmetricEigenSolver(RealMatrix, RealMatrix, double, double)}.
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
	
	public double[] getRealEigenvalues() {
		return eigendecompY.getRealEigenvalues();
	}
	
	public double[] getImagEigenvalues() {
		return eigendecompY.getImagEigenvalues();
	}
	
	public boolean hasComplexEigenvalues() {
		return eigendecompY.hasComplexEigenvalues();
	}
	
	public RealMatrix getD() {
		return eigendecompY.getD();
	}
	
	public RealVector getEigenvector(int k) {
//		return LiT.operate(ed.getEigenvector(k));
		// solve LT * x_k = v_k
		RealVector vk = eigendecompY.getEigenvector(k);
		return solverLT.solve(vk);
	}

	public RealMatrix getV() {
//		return LiT.multiply(ed.getV());
		// solve LT * X = V
		return solverLT.solve(eigendecompY.getV());
	}

	// ---------------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(9);
		
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
			{ 3,  -1,  5},
			{ -1,  -2, 7},
			{ 5,  7,  0}});
		
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			{ 10, 2,  7},
			{  2, 12, 3},
			{  7, 3, 15}});
		
		GeneralizedSymmetricEigenDecomposition solver = new GeneralizedSymmetricEigenDecomposition(A, B);
		
		System.out.println("has complex eigenvalues = " + solver.hasComplexEigenvalues());
		double[] evals = solver.getRealEigenvalues();
		System.out.println("evals = " + Arrays.toString(evals));
		
		for (int k = 0; k < evals.length; k++) {
			double lambda = evals[k];
			RealVector evec = solver.getEigenvector(k);
			RealVector evecn = normalize(evec);
			System.out.println("k = " + k);
			System.out.println("  eval = " + lambda);
			System.out.println("  evec = " + Matrix.toString(evec));
			System.out.println("  evecn = " + Matrix.toString(evecn));
			
			RealVector L = A.operate(evecn);
			System.out.println("L = "+ Arrays.toString(L.toArray()));
			
			RealVector R = B.operate(evecn).mapMultiply(lambda);
			System.out.println("R = "+ Arrays.toString(R.toArray()));
			
			RealVector res = L.subtract(R);
			//System.out.println("res = "+ Arrays.toString(res.toArray()));	// L - R must be 0
			System.out.println("  res = 0? "+  Matrix.isZero(res.toArray(), 1e-6));
		}
		
		RealMatrix V = solver.getV();
		System.out.println("V = \n" + Matrix.toString(V.getData()));
		RealMatrix D = solver.getD();
		System.out.println("D = \n" + Matrix.toString(D.getData()));
		
		// check A*V = B*V*D
		RealMatrix AV = A.multiply(V);
		System.out.println("AV = \n" + Matrix.toString(AV.getData()));
		RealMatrix BVD = B.multiply(V).multiply(D);
		System.out.println("BVD = \n" + Matrix.toString(BVD.getData()));
		
		// normalize V:
		// each eigenvector is normalized so that the modulus of its largest component is 1.0 .
		for (int k = 0; k < evals.length; k++) {
			V.setColumn(k, normalize(V.getColumn(k)));
		}
		System.out.println("V normalized = \n" + Matrix.toString(V.getData()));
		
	}
	
	static RealVector normalize(RealVector x) {
		return MatrixUtils.createRealVector(normalize(x.toArray()));
	}
	
	static double[] normalize(double[] x) {
		int n = x.length;
		double[] y = new double[n];
		double maxval = -1;
		for (int i = 0; i < n; i++) {
			maxval = Math.max(maxval, Math.abs(x[i]));
		}
		for (int i = 0; i < n; i++) {
			y[i] = x[i] / maxval;
		}
		return y;
	}
}
/*
has complex eigenvalues = false
evals = [0.39652279397140217, 0.2884669048273067, -1.2739949344008035]
i = 0
  eval = 0.39652279397140217
  evec = [0.1763871132237779, 0.06192779664186667, 0.12646136674589745]
  res = 0? true
i = 1
  eval = 0.2884669048273067
  evec = [-0.2690030812171035, 0.22022575242852008, 0.12691709905459791]
  res = 0? true
i = 2
  eval = -1.2739949344008035
  evec = [-0.2138681562789338, -0.1892041258097452, 0.2629091348298518]
  res = 0? true
V = 
{{0.176, -0.269, -0.214}, 
{0.062, 0.220, -0.189}, 
{0.126, 0.127, 0.263}}
D = 
{{0.397, 0.000, 0.000}, 
{0.000, 0.288, 0.000}, 
{0.000, 0.000, -1.274}}
AV = 
{{1.100, -0.393, 0.862}, 
{0.585, 0.717, 2.433}, 
{1.315, 0.197, -2.394}}
BVD = 
{{1.100, -0.393, 0.862}, 
{0.585, 0.717, 2.433}, 
{1.315, 0.197, -2.394}}
*/

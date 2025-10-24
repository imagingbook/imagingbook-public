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
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * Implements an efficient, closed form algorithm for calculating the real eigenvalues (&lambda;) and eigenvectors (x)
 * of a 2x2 matrix of the form
 * </p>
 * <pre>
 *   | a b |
 *   | c d |
 * </pre>
 * <p>
 * There are typically (but not always) two pairs of real-valued solutions &lang;&lambda;<sub>0</sub>,
 * x<sub>0</sub>&rang;, &lang;&lambda;<sub>1</sub>, x<sub>1</sub>&rang; such that A&middot;x<sub>k</sub> =
 * &lambda;<sub>k</sub>&middot;x<sub>k</sub>. The resulting eigensystems are ordered such that &lambda;<sub>0</sub> &ge;
 * &lambda;<sub>1</sub>. Eigenvectors are not normalized, i.e., no unit vectors (any scalar multiple of an Eigenvector
 * is an Eigenvector too). Non-real eigenvalues are not handled. Clients should call method
 * {@link #hasComplexEigenvalues()} to check if the eigenvalue calculation was successful.
 * </p>
 * <p>
 * This implementation is inspired by Ch. 5 ("Consider the Lowly 2x2 Matrix") of [1]. Note that Blinn uses the notation
 * x&middot;A = &lambda;&middot;x for the matrix-vector product (as common in computer graphics), while this
 * implementation uses A&middot;x = &lambda;&middot;x. Thus x is treated as a column vector and matrix A is transposed
 * (elements b/c are exchanged). See Appendix Sec. B.5 of [2] for more details.
 * </p>
 * <p>
 * This implementation is considerably faster (ca. factor 5) than the general solution (e.g.,
 * {@link EigenDecompositionJama}) for 2x2 matrices.
 * </p>
 * <p>
 * [1] Blinn, Jim: "Jim Blinn's Corner: Notation, Notation, Notation", Morgan Kaufmann (2002). <br> [2] W. Burger, M.J.
 * Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/02/18
 * @see EigenDecompositionJama
 * @see EigenDecompositionApache
 */
public class Eigensolver2x2 implements RealEigenDecomposition { // to check: http://www.akiti.ca/Eig2Solv.html
	
	private final boolean isReal;
	private final double[] eVals = {Double.NaN, Double.NaN};
	private final double[][] eVecs = new double[2][];
	
	/**
	 * Constructor, takes a 2x2 matrix.
	 * @param A a 2x2 matrix
	 */
	public Eigensolver2x2(double[][] A) {
		this(A[0][0], A[0][1], A[1][0], A[1][1]);
		if (Matrix.getNumberOfRows(A) != 2 || Matrix.getNumberOfColumns(A) != 2) {
			throw new IllegalArgumentException("matrix not of size 2x2");
		}
	}
	
	/**
	 * Constructor, takes the individual elements of a 2x2 matrix A:
	 * <pre>
	 *   | a b |
	 *   | c d | </pre>
	 * @param a matrix element A[0,0]
	 * @param b matrix element A[0,1]
	 * @param c matrix element A[1,0]
	 * @param d matrix element A[1,1]
	 */
	public Eigensolver2x2(double a, double b, double c, double d) {
		isReal = solve(a, b, c, d);
	}
	
	private boolean solve(final double a, final double b, final double c, final double d) {
		final double R = (a + d) / 2;
		final double S = (a - d) / 2;
		final double rho = sqr(S) + b * c;
		
		if (rho < 0) {	
			return false;		// no real-valued eigenvalues
		}
		
		final double T = Math.sqrt(rho);
		final double lambda0 = R + T;	// eigenvalue 0
		final double lambda1 = R - T;	// eigenvalue 1
		final double[] x0, x1;			// eigenvectors 0, 1
		
		if (a - d > 0) {
//			System.out.println("Case 1");
			x0 = new double[] {S + T, c};
			x1 = new double[] {b, -(S + T)};
		}
		else if (a - d < 0) {
//			System.out.println("Case 2");
			x0 = new double[] {b, -S + T};
			x1 = new double[] {S - T, c};
		}
		else {		// (A - D) == 0
//			System.out.println("Case 3");
			final double bA = Math.abs(b);
			final double cA = Math.abs(c);
			final double bcR = Math.sqrt(b * c);
			if (bA < cA) {							// |b| < |c|
//				System.out.println("Case 3a");
				x0 = new double[] { bcR, c};
				x1 = new double[] {-bcR, c};
			}
			else if (bA > cA) { 					// |b| > |c|
//				System.out.println("Case 3b");
				x0 = new double[] {b,  bcR};
				x1 = new double[] {b, -bcR};
			}
			else { 	// |B| == |C| and B,C must have the same sign
//				System.out.println("Case 3c");
				if (cA > 0) {	// 
					x0 = new double[] { c, c};
					x1 = new double[] {-c, c};
				}
				else { // B = C = 0; any vector is an eigenvector (we don't return trivial zero vectors)
					x0 = new double[] { 0, 1};	// pick 2 arbitrary, orthogonal vectors
					x1 = new double[] { 1, 0};
				}
			}
		}
		
		eVals[0] = lambda0;
		eVals[1] = lambda1;
		eVecs[0] = x0;
		eVecs[1] = x1;
		
		// lambda0 >= lambda1, no need to sort by magnitude
		
//		if (Math.abs(lambda0) >= Math.abs(lambda1)) {	// order eigenvalues by magnitude
//			eVals[0] = lambda0;
//			eVals[1] = lambda1;
//			eVecs[0] = x0;
//			eVecs[1] = x1;
//		}
//		else {
//			eVals[0] = lambda1;
//			eVals[1] = lambda0;
//			eVecs[0] = x1;
//			eVecs[1] = x0;
//		}
		return true;	// real eigenvalues
	}
	
	
//	public boolean isReal() {
//		return isReal;
//	}
	
	@Override
	public boolean hasComplexEigenvalues() {
		return !isReal;
	}
	
	@Override
	public double[] getRealEigenvalues() {
		return eVals;
	}
	
	@Override
	public double getRealEigenvalue(int k) {
		return eVals[k];
	}
	
	@Override
	public RealMatrix getV() {
		return MatrixUtils.createRealMatrix(Matrix.transpose(eVecs));
	}
	
	@Override
	public RealVector getEigenvector(int k) {
		return MatrixUtils.createRealVector(eVecs[k]);
	}
	
	@Override
	public String toString() {
		if (this.isReal) {
			return String.format("<%.4f, %.4f, %s, %s>", 
				eVals[0], eVals[1], Matrix.toString(eVecs[0]), Matrix.toString(eVecs[1]));
		}
		else {
			return "<not real>";
		}
	}

		
}

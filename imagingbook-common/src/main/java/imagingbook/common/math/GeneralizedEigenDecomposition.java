package imagingbook.common.math;

import static imagingbook.common.math.Matrix.isSquare;
import static imagingbook.common.math.Matrix.sameSize;

import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.common.math.eispack.QZHES;
import imagingbook.common.math.eispack.QZIT;
import imagingbook.common.math.eispack.QZVAL;
import imagingbook.common.math.eispack.QZVEC;

/**
 * <p>
 * Solves the generalized eigenproblem of the form A x = &lambda; B x,
 * for square matrices A, B.
 * </p>
 * <p>
 * This implementation was ported from original EISPACK code [1] using
 * a finite state machine concept [2] to untangle Fortran's GOTO
 * statements (which are not available in Java), with some inspirations from
 * <a href="https://github.com/accord-net/framework/blob/development/Sources/Accord.Math/Decompositions/GeneralizedEigenvalueDecomposition.cs">
 * this implementation</a>.
 * See 
 * <a href="https://mipav.cit.nih.gov/">mipav.cit.nih.gov</a> 
 * (file {@code gov.nih.mipav.model.structures.jama.GeneralizedEigenvalue.java}) 
 * for another Java implementation based on EISPACK.
 * </p>
 * 
 * <p>
 * Note: Results have limited accuracy. For some reason the first eigenvalue/-vector (k=0) is reasonably
 * accurate, but the remaining ones are not. 
 * If matrices A, B are symmetric and B is positive definite, better use 
 * {@link GeneralizedSymmetricEigenDecomposition} instead, which is more accurate.
 * 
 * TODO: Check complex-valued eigenvectors.
 * </p>
 * 
 * <p>
 * [1] http://www.netlib.no/netlib/eispack/
 * <br>
 * [2] D. E. Knuth, "Structured Programming with Goto Statements", Computing Surveys, Vol. 6, No. 4 (1974).
 *
 * @author WB
 * @version 2022/06/21
 * @see GeneralizedSymmetricEigenDecomposition
 */
public class GeneralizedEigenDecomposition {
	
	static boolean VERBOSE = false;

	private int n;
	private double[] alphaR;
	private double[] alphaI;
	private double[] beta;
	private double[][] Z;

	public GeneralizedEigenDecomposition(RealMatrix A, RealMatrix B) {
		this(A.getData(), B.getData());
	}

	/**
	 * Constructor.
	 * @param A first matrix
	 * @param B second matrix
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

		QZHES.qzhes(a, b, matz, Z);
		int ierr = QZIT.qzit(a, b, 0, matz, Z);
		if (ierr >= 0) {
			throw new RuntimeException("limit of 30*n iterations exhausted for eigenvalue " + ierr);
		}
		QZVAL.qzval(a, b, alphaR, alphaI, beta, matz, Z);
		QZVEC.qzvec(a, b, alphaR, alphaI, beta, Z);
	}

	/**
	 * Returns a vector with the real parts of the eigenvalues.
	 * 
	 * @return the real parts of the eigenvalues
	 */
	public double[] getRealEigenvalues() {
		double[] eval = new double[n];
		for (int i = 0; i < n; i++) {
			eval[i] = alphaR[i] / beta[i];
		}
		return eval;
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
	 * Return the matrix of eigenvectors, which are its column
	 * vectors.
	 * TODO: not implemented/checked for complex eigenvalues yet!
	 * 
	 * @return the matrix of eigenvectors
	 */
	public RealMatrix getV() {
		return MatrixUtils.createRealMatrix(Z);
	}

	/**
	 * Returns the specified eigenvector.
	 * TODO: not implemented for complex eigenvalues yet!
	 * 
	 * @param k index
	 * @return the kth eigenvector
	 */
	public RealVector getEigenvector(int k) {
		return MatrixUtils.createRealVector(Matrix.getColumn(Z, k));
	}
	
	/**
     * Returns whether the calculated eigenvalues are complex or real.
     * The method performs a zero check on each element of the
     * {@link #getImagEigenvalues()} array and returns {@code true} if any
     * element is not equal to zero.
     *
     * @return {@code true} if any of the eigenvalues is complex, {@code false} otherwise
     */
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
	 * @return the block diagonal eigenvalue matrix
	 */
	public RealMatrix getD() {
		double[][] x = new double[n][n];

		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
				x[i][j] = 0.0;

			x[i][i] = alphaR[i] / beta[i];
			if (alphaI[i] > 0)
				x[i][ i + 1] = alphaI[i] / beta[i];
			else if (alphaI[i] < 0)
				x[i][ i - 1] = alphaI[i] / beta[i];
		}

		return MatrixUtils.createRealMatrix(x);
	}
	
	// ----------------------------------------------------------
	
	// unused methods from 
	// https://github.com/accord-net/framework/blob/development/Sources/Accord.Math/Decompositions/GeneralizedEigenvalueDecomposition.cs
	
	/**
	 * This method checks if any of the generated betas is zero. It does not says
	 * that the problem is singular, but only that one of the matrices 
	 * A, B is singular.
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

	// ----------------------------------------------------------


	public static void main(String[] args) {

		PrintPrecision.set(9);

		// real eigenvals:
		double[][] a = new double[][] {
			{ 3,  -1,  5},
			{ -1,  -2, 7},
			{ 5,  7,  0}};

		double[][] b = new double[][] {
			{ 10, 2,  7},
			{  2, 12, 3},
			{  7, 3, 15}};
		
		
		// complex eigenvals:
//		double[][] a = new double[][] {
//			{ 3,  -1,  4},
//			{ -1,  -2, 7},
//			{ 5,  7,  0}};
//
//		double[][] b = new double[][] {
//			{ 10, 2,  7},
//			{  2, 12, 3},
//			{  7, 3, 15}};

		int n = a.length;
		RealMatrix A = MatrixUtils.createRealMatrix(a);
		RealMatrix B = MatrixUtils.createRealMatrix(b);

		GeneralizedEigenDecomposition ges = new GeneralizedEigenDecomposition(a, b);
		
		System.out.println("a = \n" + Matrix.toString(a));
		System.out.println("b = \n" + Matrix.toString(b));
		

		double[] evals = ges.getRealEigenvalues();
		System.out.println("evals Re = " + Matrix.toString(ges.getRealEigenvalues()));
		System.out.println("evals Im = " + Matrix.toString(ges.getImagEigenvalues()));
		System.out.println("has complex eigenvals = " + ges.hasComplexEigenvalues());

		RealMatrix evecs = ges.getV();
		System.out.println("evecs = \n" + Matrix.toString(evecs));

		// check A x_k = lambda_k B x_k
		for (int k = 0; k < n; k++) {
			double lambda = evals[k];
			RealVector evec = ges.getEigenvector(k);
			System.out.println("k = " + k);
			System.out.println("  eval = " + lambda);
			System.out.println("  evec = " + Matrix.toString(evec));
			
			RealVector L = A.operate(evec);
			System.out.println("L = "+ Arrays.toString(L.toArray()));
			
			RealVector R = B.operate(evec).mapMultiply(lambda);
			System.out.println("R = "+ Arrays.toString(R.toArray()));
			
			RealVector res = L.subtract(R);
			System.out.println("res = "+ Arrays.toString(res.toArray()));	// L - R must be 0
			System.out.println("  res = 0? "+  Matrix.isZero(res.toArray(), 1e-4));
		}
	}
	
}

/*
a = 
{{3.000000000, -1.000000000, 5.000000000}, 
{-1.000000000, -2.000000000, 7.000000000}, 
{5.000000000, 7.000000000, 0.000000000}}
b = 
{{10.000000000, 2.000000000, 7.000000000}, 
{2.000000000, 12.000000000, 3.000000000}, 
{7.000000000, 3.000000000, 15.000000000}}
evals Re = {-1.273994934, 0.396523621, 0.288466905}
evals Im = {0.000000000, 0.000000000, 0.000000000}
has complex eigenvals = false
evecs = 
{{0.813467955, -1.000000000, -1.000000000}, 
{0.719655960, -0.351090320, 0.818689996}, 
{-1.000000000, -0.716953313, 0.471822517}}
k = 0
  eval = -1.2739949344008035
  evec = {0.813467955, 0.719655960, -1.000000000}
L = [-3.2792520947595136, -9.252779875000275, 9.104931495104088]
R = [-3.279252094759509, -9.252779875000273, 9.104931495104092]
res = [-4.440892098500626E-15, -1.7763568394002505E-15, -3.552713678800501E-15]
  res = 0? true
k = 1
  eval = 0.39652362080285425
  evec = {-1.000000000, -0.351090320, -0.716953313}
L = [-6.23367624486253, -3.3164925507790857, -7.457632240331995]
R = [-6.23368988316817, -3.31650127164484, -7.457646014575417]
res = [1.3638305639496195E-5, 8.720865754430207E-6, 1.3774243422304266E-5]
  res = 0? true
k = 2
  eval = 0.2884669048273069
  evec = {-1.000000000, 0.818689996, 0.471822517}
L = [-1.4595774121577358, 2.6653776252566264, 0.7308299734296764]
R = [-1.4596028424741732, 2.665361364118212, 0.7308042896403361]
res = [2.543031643731375E-5, 1.626113841446397E-5, 2.56837893403139E-5]
  res = 0? true
 */
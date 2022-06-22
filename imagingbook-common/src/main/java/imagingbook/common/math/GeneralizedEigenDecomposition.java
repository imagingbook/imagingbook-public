package imagingbook.common.math;

import static imagingbook.common.math.Matrix.isSquare;
import static imagingbook.common.math.Matrix.sameSize;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

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

	/**
	 * Constructor.
	 * @param A first matrix
	 * @param B second matrix
	 */
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
	 * TODO: check complex case!
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


//	public static void main(String[] args) {
//
//		PrintPrecision.set(9);
//
//		// real eigenvals:
//		double[][] a = new double[][] {
//			{ 3,  -1,  5},
//			{ -1,  -2, 7},
//			{ 5,  7,  0}};
//
//		double[][] b = new double[][] {
//			{ 10, 2,  7},
//			{  2, 12, 3},
//			{  7, 3, 15}};
//		
//		
//		// complex eigenvals:
////		double[][] a = new double[][] {
////			{ 3,  -1,  4},
////			{ -1,  -2, 7},
////			{ 5,  7,  0}};
////
////		double[][] b = new double[][] {
////			{ 10, 2,  7},
////			{  2, 12, 3},
////			{  7, 3, 15}};
//
//		int n = a.length;
//		RealMatrix A = MatrixUtils.createRealMatrix(a);
//		RealMatrix B = MatrixUtils.createRealMatrix(b);
//
//		GeneralizedEigenDecomposition ges = new GeneralizedEigenDecomposition(a, b);
//		
//		System.out.println("a = \n" + Matrix.toString(a));
//		System.out.println("b = \n" + Matrix.toString(b));
//		
//
//		double[] evals = ges.getRealEigenvalues();
//		System.out.println("evals Re = " + Matrix.toString(ges.getRealEigenvalues()));
//		System.out.println("evals Im = " + Matrix.toString(ges.getImagEigenvalues()));
//		System.out.println("has complex eigenvals = " + ges.hasComplexEigenvalues());
//
//		RealMatrix evecs = ges.getV();
//		System.out.println("evecs = \n" + Matrix.toString(evecs));
//
//		// check A x_k = lambda_k B x_k
//		for (int k = 0; k < n; k++) {
//			double lambda = evals[k];
//			RealVector evec = ges.getEigenvector(k);
//			System.out.println("k = " + k);
//			System.out.println("  eval = " + lambda);
//			System.out.println("  evec = " + Matrix.toString(evec));
//			
//			RealVector L = A.operate(evec);
//			System.out.println("L = "+ Arrays.toString(L.toArray()));
//			
//			RealVector R = B.operate(evec).mapMultiply(lambda);
//			System.out.println("R = "+ Arrays.toString(R.toArray()));
//			
//			RealVector res = L.subtract(R);
//			System.out.println("res = "+ Arrays.toString(res.toArray()));	// L - R must be 0
//			System.out.println("  res = 0? "+  Matrix.isZero(res.toArray(), 1e-4));
//		}
//	}
	
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


// ----------------------------------------------------------------------------

abstract class QZHES {
	
	// EISPACK Routines, see http://www.netlib.org/eispack/
	
	/**
	 * <p>
	 * This subroutine is the first step of the qz algorithm for solving generalized
	 * matrix eigenvalue problems, SIAM J. Numer. Anal. 10, 241-256 (1973) by Moler
	 * and Stewart. This description has been adapted from the original version
	 * (dated August 1983).
	 * </p>
	 * <p>
	 * This subroutine accepts a pair of real general matrices and reduces one of
	 * them to upper Hessenberg form and the other to upper triangular form using
	 * orthogonal transformations. It is usually followed by qzit, qzval and,
	 * possibly, qzvec.
	 * </p>
	 * <p>
	 * On output:
	 * </p>
	 * <ul>
	 * <li><strong>a</strong> has been reduced to upper hessenberg form. The
	 * elements below the first subdiagonal have been set to zero.</li>
	 * 
	 * <li><strong>b</strong> has been reduced to upper triangular form. The
	 * elements below the main diagonal have been set to zero.</li>
	 * 
	 * <li><strong>z</strong> contains the product of the right hand transformations
	 * if matz has been set to true. Otherwise, <strong>z</strong> is not
	 * referenced.</li>
	 * </ul>
	 * 
	 * 
	 * @param a    contains a real general matrix.
	 * @param b    contains a real general matrix.
	 * @param matz should be set to true if the right hand transformations are to be
	 *             accumulated for later use in computing eigenvectors, and to false
	 *             otherwise.
	 * @param z    on output, contains the product of the right hand transformations if matz
	 *             has been set to true. Otherwise, z is not referenced.
	 */
	static void qzhes(double[][] a, double[][] b, boolean matz, double[][] z) {
		
		final int n = a.length;
		
		int i, j, k, l, nm1, nm2;
		double r, s, t;
		int l1;
		double u1, u2, v1, v2;
		int lb, nk1;
		double rho;
	
		// initialize z (with identity matrix)
		if (matz) {
			for (j = 0; j < n; j++) {
				for (i = 0; i < n; i++) {
					z[i][j] = 0.0;
				}	//	L2
				z[j][j] = 1.0;
			}	// L3
		}
	
		// L10: reduce b to upper triangular form
		if (n <= 1) {
			return;
		}
		
		nm1 = n - 1;	// TODO: check!!
		
		for (l = 0; l < nm1; l++) {
			l1 = l + 1;
			s = 0.0;
	
			for (i = l1; i < n; i++) {
				s = s + Math.abs(b[i][l]);
			}	// L20
	
			if (s != 0.0) {		
				s = s + Math.abs(b[l][l]);
				r = 0.0;
				for (i = l; i < n; i++) {
					b[i][l] = b[i][l] / s;
					r = r + b[i][l] * b[i][l];
				}	// L25
		
				r = Math.copySign(Math.sqrt(r), b[l][l]);
				b[l][l] = b[l][l] + r;
				rho = r * b[l][l];
		
				for (j = l1; j < n; j++) {
					t = 0.0;	// dot product
					for (i = l; i < n; i++) {
						t = t + b[i][l] * b[i][j];
					}	//L30
					t = -t / rho;
					
					for (i = l; i < n; i++) {
						b[i][j] = b[i][j] + t * b[i][l];
					}	// L40
					
				}	// L50
		
				for (j = 0; j < n; j++) {
					t = 0.0;	// dot product
					for (i = l; i < n; i++) {
						t = t + b[i][l] * a[i][j];
					}	// L60
					
					t = -t / rho;
					
					for (i = l; i < n; i++) {
						a[i][j] = a[i][j] + t * b[i][l];
					}	// L70
					
				}	// L80
		
				b[l][l] = -s * r;
				for (i = l1; i < n; i++) {
					b[i][l] = 0.0;
				}	// L90
			
			}	// end if		
		}	// L100
	
		// reduce a to upper Hessenberg form, while keeping b triangular
		if (n == 2) {
			return;
		}
		nm2 = n - 2;
		
		for (k = 0; k < nm2; k++) {
			nk1 = nm1 - k - 1; // = n - 2 - k;
	
			// for l=n-1 step -1 until k+1 do
			for (lb = 0; lb < nk1; lb++) {
				l = n - lb - 2;					// TODO: check!!
				l1 = l + 1;
	
				// zero a(l+1,k)
				s = Math.abs(a[l][k]) + Math.abs(a[l1][k]);
				if (s == 0.0) {
					continue;	// goto 150
				}
				u1 = a[l][k] / s;
				u2 = a[l1][k] / s;
				r = Math.copySign(Math.hypot(u1, u2), u1); 
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (j = k; j < n; j++) {
					t = a[l][j] + u2 * a[l1][j];
					a[l][j] = a[l][j] + t * v1;
					a[l1][j] = a[l1][j] + t * v2;
				}	// L110
	
				a[l1][k] = 0.0;
	
				for (j = l; j < n; j++) {
					t = b[l][j] + u2 * b[l1][j];
					b[l][j] = b[l][j] + t * v1;
					b[l1][j] = b[l1][j] + t * v2;
				}	// L120
	
				// zero b(l+1,l)
				s = Math.abs(b[l1][l1]) + Math.abs(b[l1][l]);
				if (s == 0.0) {
					continue;	// go to 150
				}
				u1 = b[l1][l1] / s;
				u2 = b[l1][l] / s;
				r = Math.copySign(Math.hypot(u1, u2), u1);
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (i = 0; i <= l1; i++) {
					t = b[i][l1] + u2 * b[i][l];
					b[i][l1] = b[i][l1] + t * v1;
					b[i][l] = b[i][l] + t * v2;
				}	// L130
	
				b[l1][l] = 0.0;
	
				for (i = 0; i < n; i++) {
					t = a[i][l1] + u2 * a[i][l];
					a[i][l1] = a[i][l1] + t * v1;
					a[i][l] = a[i][l] + t * v2;
				}	// L140
	
				if (matz) {
					for (i = 0; i < n; i++) {
						t = z[i][l1] + u2 * z[i][l];
						z[i][l1] = z[i][l1] + t * v1;
						z[i][l] = z[i][l] + t * v2;
					}	// L145
				}
			}	// L150
		}	// L160
	
	} // end of qzhes

}

//----------------------------------------------------------------------------

abstract class QZIT {
	
	// EISPACK Routines, see http://www.netlib.org/eispack/

	/**
	 * <p>
	 * This subroutine is the second step of the qz algorithm for solving
	 * generalized matrix eigenvalue problems, Siam J. Numer. anal. 10,
	 * 241-256(1973) by Moler and Stewart, as modified in technical note NASA TN
	 * D-7305(1973) by Ward. 
	 * This description has been adapted from the original version
	 * (dated August 1983).
	 * </p>
	 * <p>
	 * This subroutine accepts a pair of real matrices, one of them in upper
	 * Hessenberg form and the other in upper triangular form. It reduces the
	 * Hessenberg matrix to quasi-triangular form using orthogonal transformations
	 * while maintaining the triangular form of the other matrix. It is usually
	 * preceded by qzhes and followed by qzval and, possibly, qzvec.
	 * </p>
	 * <p>
	 * On output:
	 * </p>
	 * <ul>
	 * <li><strong>a</strong> has been reduced to quasi-triangular form. The elements 
	 * below the first subdiagonal are still zero and no two consecutive subdiagonal
	 * elements are nonzero.</li>
	 * 
	 * <li><strong>b</strong> is still in upper triangular form, although its elements have been altered.
	 * the location b(n,1) is used to store eps1 times the norm of b for later use
	 * by qzval and qzvec.
	 * 
	 * <li><strong>z</strong> contains the product of the right hand transformations (for both steps) if
	 * matz has been set to true.</li>
	 * </ul>
	 * 
	 * 
	 * @param a    contains a real upper hessenberg matrix.
	 * @param b    contains a real upper triangular matrix.
	 * @param eps1 is a tolerance used to determine negligible elements. eps1 = 0.0
	 *             (or negative) may be input, in which case an element will be
	 *             neglected only if it is less than roundoff error times the norm
	 *             of its matrix. If the input eps1 is positive, then an element
	 *             will be considered negligible if it is less than eps1 times the
	 *             norm of its matrix. A positive value of eps1 may result in faster
	 *             execution, but less accurate results.
	 * @param matz should be set to true if the right hand transformations are to be
	 *             accumulated for later use in computing eigenvectors, and to false
	 *             otherwise.
	 * @param z    contains, if matz has been set to true, the transformation matrix
	 *             produced in the reduction by qzhes, if performed, or else the
	 *             identity matrix. If matz has been set to false, z is not
	 *             referenced.
	 * @return -1 for normal return, j if the limit of 30*n iterations is exhausted
	 *         while the j-th eigenvalue is being sought.
	 */
	static int qzit(double[][] a, double[][] b, double eps1, boolean matz, double[][] z) {

		final int n = a.length;
		
		int i, j, k, l = 0;
		double r, s, t, a1 = 0, a2 = 0, a3 = 0;
		int k1, k2, l1 = 0, ll;
		double u1, u2, u3;
		double v1, v2, v3;
		double a11 = 0, a12, a21 = 0, a22, a33 = 0, a34 = 0, a43 = 0, a44 = 0;
		double b11 = 0, b12, b22 = 0, b33, b34 = 0, b44;
		int na = 0, en, ld = 0;
		double ep;
		double sh = 0;
		int km1, lm1 = 0;
		double ani, bni;
		int ish = 0, itn, its = 0, enm2 = 0, lor1;
		double epsa, epsb, anorm, bnorm;
		int enorn;
		boolean notlas;
	
		int ierr = -1;		// no error 
	
		// compute epsa and epsb
		anorm = 0.0;
		bnorm = 0.0;
			      
		for (i = 0; i < n; i++) {
			ani = 0.0;
			if (i != 0) {
				ani = (Math.abs(a[i][(i - 1)]));
			}
			bni = 0.0;
			
			for (j = i; j < n; j++) {
				ani += Math.abs(a[i][j]);
				bni += Math.abs(b[i][j]);
			}
	
			if (ani > anorm) {
				anorm = ani;
			}
			if (bni > bnorm) {
				bnorm = bni;
			}
		}
	
		if (anorm == 0.0) {
			anorm = 1.0;
		}
		if (bnorm == 0.0) {
			bnorm = 1.0;
		}
	
//		ep = eps1;
//		if (ep == 0.0) {
//			// use round-off level if eps1 is zero
//			ep = epsilon(1.0);
//		}
		ep = (eps1 > 0) ? eps1 : epslon(1.0);
		epsa = ep * anorm;
		epsb = ep * bnorm;
	
		// rReduce a to quasi-triangular form, while keeping b triangular
		lor1 = 0;
		enorn = n;
		en = n - 1;
		itn = n * 30;
		
		// ------------------------------------------
	
		State state = State.L60;
		StateLoop: while (state != State.Final) {
			switch (state) {
	
			case L60: 
				// Begin QZ step
				if (en <= 1) {
					state = State.L1001;
					break;
				}
				if (!matz) {
					enorn = en + 1;
				}
				its = 0;
				na = en - 1;
				enm2 = na;
				state = State.L70;
				break;
	
			case L70:
				ish = 2;
				// check for convergence or reducibility.
				for (ll = 0; ll <= en; ++ll) {
					lm1 = en - ll - 1;
					l = lm1 + 1;
					if (l == 0) {
						state = State.L95;
						continue StateLoop;		// check again!!
					}
					if (Math.abs(a[l][lm1]) <= epsa) {
						state = State.L90;
						break;
					}
				}
				state = State.L90;
				break;
	
			case L90:
				a[l][lm1] = 0.0;
				if (l < na) {
					state = State.L95;
					break;
				}
				// 1-by-1 or 2-by-2 block isolated
				en = lm1;
				state = State.L60;
				break;
	
			case L95: 
				// check for small top of b
				ld = l;
				state = State.L100;
				break;
	
			case L100:
				l1 = l + 1;
				b11 = b[l][l];
	
				if (Math.abs(b11) > epsb) {
					state = State.L120;
					break;
				}
	
				b[l][l] = 0.0;
				s = Math.abs(a[l][l]) + Math.abs(a[l1][l]);
				u1 = a[l][l] / s;
				u2 = a[l1][l] / s;
//				r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
				r = Math.copySign(Math.hypot(u1, u2), u1);
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (j = l; j < enorn; j++) {
					t = a[l][j] + u2 * a[l1][j];
					a[l][j] = a[l][j] + t * v1;
					a[l1][j] = a[l1][j] + t * v2;
	
					t = b[l][j] + u2 * b[l1][j];
					b[l][j] = b[l][j] + t * v1;
					b[l1][j] = b[l1][j] + t * v2;
				}
	
				if (l != 0) {
					a[l][lm1] = -a[l][lm1];
				}
				lm1 = l;
				l = l1;
				state = State.L90;
				break;
	
			case L120:
				a11 = a[l][l] / b11;
				a21 = a[l1][l] / b11;
				if (ish == 1) {
					state = State.L140;
					break;
				}
				// iteration strategy
				if (itn == 0) {
					state = State.L1000;
					break;
				}
				if (its == 10) {
					state = State.L155;
					break;
				}
	
				// determine type of shift
				b22 = b[l1][l1];
				if (Math.abs(b22) < epsb) {
					b22 = epsb;
				}
				b33 = b[na][na];
				if (Math.abs(b33) < epsb) {
					b33 = epsb;
				}
				b44 = b[en][en];
				if (Math.abs(b44) < epsb)
					b44 = epsb;
				a33 = a[na][na] / b33;
				a34 = a[na][en] / b44;
				a43 = a[en][na] / b33;
				a44 = a[en][en] / b44;
				b34 = b[na][en] / b44;
				t = 0.5 * (a43 * b34 - a33 - a44);
				r = t * t + a34 * a43 - a33 * a44;
				if (r < 0.0) {
					state = State.L150;
					break;
				}
	
				// determine single shift zero-th column of a
				ish = 1;
				r = Math.sqrt(r);
				sh = -t + r;
				s = -t - r;
				if (Math.abs(s - a44) < Math.abs(sh - a44)) {
					sh = s;
				}
	
				// look for two consecutive small sub-diagonal elements of a.
				for (ll = ld; ll < enm2; ll++) {
					l = enm2 + ld - ll - 1;
					if (l == ld) {
						state = State.L140;
						//break loop130;
						continue StateLoop;
					}
					lm1 = l - 1;
					l1 = l + 1;
//					t = a[l + 1][l + 1];	// CHECK THIS!!
					t = a[l][l];
	
					if (Math.abs(b[l][l]) > epsb) {
						t = t - sh * b[l][l];
					}
	
					if (Math.abs(a[l][lm1]) <= Math.abs(t / a[l1][l]) * epsa) {
						state = State.L100;
						continue StateLoop;
					}
				}
				state = State.L140;
				break;
	
			case L140:
				a1 = a11 - sh;
				a2 = a21;
				if (l != ld) {
					a[l][lm1] = -a[l][lm1];
				}
				state = State.L160;
				break;
	
			case L150: 
				// determine double shift zero-th column of a
				a12 = a[l][l1] / b22;
				a22 = a[l1][l1] / b22;
				b12 = b[l][l1] / b22;
				a1 = ((a33 - a11) * (a44 - a11) - a34 * a43 + a43 * b34 * a11) / a21 + a12 - a11 * b12;
				a2 = a22 - a11 - a21 * b12 - (a33 - a11) - (a44 - a11) + a43 * b34;
				a3 = a[l1 + 1][l1] / b22;
				state = State.L160;
				break;
	
			case L155: 
				// ad hoc shift
				a1 = 0.0;
				a2 = 1.0;
				a3 = 1.1605;			// magic!
				state = State.L160;
				break;
	
			case L160:
				its = its + 1;
			    itn = itn - 1;
				if (!matz) {
					lor1 = ld;
				}
	
				mainloop: for (k = l; k <= na; k++) {
					notlas = (k != na && ish == 2);
					k1 = k + 1;
					k2 = k + 2;
					km1 = Math.max(k, l + 1) - 1;
					ll = Math.min(en, k1 + ish);
	
					if (!notlas) {
						// zero a(k+1,k-1)
						if (k != l) {
							a1 = a[k][km1];
							a2 = a[k1][km1];
						}
	
						s = Math.abs(a1) + Math.abs(a2);
						if (s == 0.0) {
							state = State.L70;
							continue StateLoop;
						}
						u1 = a1 / s;
						u2 = a2 / s;
						r = Math.copySign(Math.hypot(u1, u2), u1);
						v1 = -(u1 + r) / r;
						v2 = -u2 / r;
						u2 = v2 / v1;
	
						for (j = km1; j < enorn; j++) {
							t = a[k][j] + u2 * a[k1][j];
							a[k][j] = a[k][j] + t * v1;
							a[k1][j] = a[k1][j] + t * v2;
	
							t = b[k][j] + u2 * b[k1][j];
							b[k][j] = b[k][j] + t * v1;
							b[k1][j] = b[k1][j] + t * v2;
						}
	
						if (k != l) {
							a[k1][km1] = 0.0;
						}
						// goto L240;
					}
	
					else {
						// zero a(k+1,k-1) and a(k+2,k-1)
						//L190:
						if (k != l) {
							a1 = a[k][km1];
							a2 = a[k1][km1];
							a3 = a[k2][km1];
						}
						//L200:
						s = Math.abs(a1) + Math.abs(a2) + Math.abs(a3);
						if (s == 0.0) {
							continue mainloop;	// goto L260;
						}
						u1 = a1 / s;
						u2 = a2 / s;
						u3 = a3 / s;
						r = Math.copySign(Math.sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
						v1 = -(u1 + r) / r;
						v2 = -u2 / r;
						v3 = -u3 / r;
						u2 = v2 / v1;
						u3 = v3 / v1;
	
						for (j = km1; j < enorn; j++) {
							t = a[k][j] + u2 * a[k1][j] + u3 * a[k2][j];
							a[k][j] = a[k][j] + t * v1;
							a[k1][j] = a[k1][j] + t * v2;
							a[k2][j] = a[k2][j] + t * v3;
	
							t = b[k][j] + u2 * b[k1][j] + u3 * b[k2][j];
							b[k][j] = b[k][j] + t * v1;
							b[k1][j] = b[k1][j] + t * v2;
							b[k2][j] = b[k2][j] + t * v3;
						}
	
						if (k != l) {
							a[k1][km1] = 0.0;
							a[k2][km1] = 0.0;
						}
	
						// zero b(k+2,k+1) and b(k+2,k)
						s = Math.abs(b[k2][k2]) + Math.abs(b[k2][k1]) + Math.abs(b[k2][k]);
						if (s != 0.0) {
							u1 = b[k2][k2] / s;
							u2 = b[k2][k1] / s;
							u3 = b[k2][k] / s;
							r = Math.copySign(Math.sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
							v1 = -(u1 + r) / r;
							v2 = -u2 / r;
							v3 = -u3 / r;
							u2 = v2 / v1;
							u3 = v3 / v1;
	
							for (i = lor1; i < ll + 1; i++) {
								t = a[i][k2] + u2 * a[i][k1] + u3 * a[i][k];
								a[i][k2] = a[i][k2] + t * v1;
								a[i][k1] = a[i][k1] + t * v2;
								a[i][k] = a[i][k] + t * v3;
	
								t = b[i][k2] + u2 * b[i][k1] + u3 * b[i][k];
								b[i][k2] = b[i][k2] + t * v1;
								b[i][k1] = b[i][k1] + t * v2;
								b[i][k] = b[i][k] + t * v3;
							}
	
							b[k2][k] = 0.0;
							b[k2][k1] = 0.0;
	
							if (matz) {
								for (i = 0; i < n; i++) {
									t = z[i][k2] + u2 * z[i][k1] + u3 * z[i][k];
									z[i][k2] = z[i][k2] + t * v1;
									z[i][k1] = z[i][k1] + t * v2;
									z[i][k] = z[i][k] + t * v3;
								}
							}
						}
					}
	
					// L240:
					// zero b(k+1,k)
					s = (Math.abs(b[k1][k1])) + (Math.abs(b[k1][k]));
					if (s == 0.0) {
						continue;	// goto L260;
					}
					u1 = b[k1][k1] / s;
					u2 = b[k1][k] / s;
					r = Math.copySign(Math.hypot(u1, u2), u1);
					v1 = -(u1 + r) / r;
					v2 = -u2 / r;
					u2 = v2 / v1;
	
					for (i = lor1; i <= ll; i++) {
						t = a[i][k1] + u2 * a[i][k];
						a[i][k1] = a[i][k1] + t * v1;
						a[i][k] = a[i][k] + t * v2;
	
						t = b[i][k1] + u2 * b[i][k];
						b[i][k1] = b[i][k1] + t * v1;
						b[i][k] = b[i][k] + t * v2;
					}
	
					b[k1][k] = 0.0;
	
					if (matz) {
						for (i = 0; i < n; i++) {
							t = z[i][k1] + u2 * z[i][k];
							z[i][k1] = z[i][k1] + t * v1;
							z[i][k] = z[i][k] + t * v2;
						}
					}
					// L260: ;
				}
	
				state = State.L70; // end qz step
				break;
	
			case L1000: 
				// set error -- all eigenvalues have not converged after 30*n iterations
				ierr = en + 1;
				state = State.L1001;
				break;
	
			case L1001: 
				// save epsb for use by qzval and qzvec
				if (n > 1) {
					b[n - 1][0] = epsb;
				}
				state = State.Final;
				break;
	
			case Final:
				throw new RuntimeException("this should never happen!");
			}
		}
	
		return ierr;	// return eigenvalue index if iteration count exceeded
	} // end of qzit()
	

	private enum State {
		L60, L70, L90, L95, L100, L120, L140, L150, L155, L160, L1000, L1001, Final;
	}

	private static double EpsDouble;
	static {
		double eps;
		double a = 4.0 / 3.0;
		do {
			double b = a - 1.0;
			double c = b + b + b;
			eps = Math.abs(c - 1.0);
		} while (eps == 0);
		EpsDouble = eps;
	}
	
	private static float EpsFloat;
	static {
		float eps;
		float a = 4.0f / 3.0f;
		do {
			float b = a - 1.0f;
			float c = b + b + b;
			eps = Math.abs(c - 1.0f);
		} while (eps == 0);
		EpsFloat = eps;
	}
 
	/**
	 * Estimates unit round-off in quantities of size x.
	 * This is a port of the epsilon function from EISPACK.
	 * See also
	 * https://www.researchgate.net/publication/2860253_A_Comment_on_the_Eispack_Machine_Epsilon_Routine
	 * 
	 * @param x some quantity
	 * @return returns the smallest number e of the same kind as x such that 1 + e > 1.
	 */
	private static double epslon(double x) {
//		double eps;
//		double a = 4.0 / 3.0;
//		do {
//			double b = a - 1.0;
//			double c = b + b + b;
//			eps = Math.abs(c - 1.0);
//		} while (eps == 0);
		return EpsDouble * Math.abs(x);
	}
	
	private static float epslon(float x) {
		return EpsFloat * Math.abs(x);
		
	}
	
/* original FORTRAN code:
	   a = 4.0d0/3.0d0
	10 b = a - 1.0d0
	   c = b + b + b
	   eps = dabs(c-1.0d0)
	   if (eps .eq. 0.0d0) go to 10
	   epslon = eps*dabs(x)
	   return
	   end
*/
	
/*
program test_epsilon
    real :: x = 3.143
    real(8) :: y = 2.33
    print *, epsilon(x)
    print *, epsilon(y)
end program test_epsilon
 */
	
	public static void main(String[] args) {
		double x = 3.143;
		System.out.println(epslon(x));
		float y = 2.33f;
		System.out.println(epslon(y));
		
	}

}

//----------------------------------------------------------------------------

abstract class QZVAL {

	// EISPACK Routines, see http://www.netlib.org/eispack/

	/**
	 * <p>
	 * This subroutine is the third step of the qz algorithm for solving generalized
	 * matrix eigenvalue problems, Siam J. Numer. Anal. 10, 241-256 (1973) by Moler
	 * and Stewart. This description has been adapted from the original version
	 * (dated August 1983).
	 * </p>
	 * <p>
	 * This subroutine accepts a pair of real matrices, one of them in
	 * quasi-triangular form and the other in upper triangular form. it reduces the
	 * quasi-triangular matrix further, so that any remaining 2-by-2 blocks
	 * correspond to pairs of complex Eigenvalues, and returns quantities whose
	 * ratios give the generalized eigenvalues. it is usually preceded by qzhes and
	 * qzit and may be followed by qzvec.
	 * </p>
	 * <p>
	 * On output:
	 * </p>
	 * <ul>
	 * <li><strong>a</strong> has been reduced further to a quasi-triangular matrix
	 * in which all nonzero subdiagonal elements correspond to pairs of complex
	 * eigenvalues.</li>
	 * 
	 * <li><strong>b</strong> is still in upper triangular form, although its
	 * elements have been altered. b[n-1][0] is unaltered.</li>
	 * 
	 * <li><strong>alfr</strong> and <strong>alfi</strong> contain the real and
	 * imaginary parts of the diagonal elements of the triangular matrix that would
	 * be obtained if a were reduced completely to triangular form by unitary
	 * transformations. Non-zero values of <strong>alfi</strong> occur in pairs, the
	 * first member positive and the second negative.</li>
	 * 
	 * <li><strong>beta</strong> contains the diagonal elements of the corresponding
	 * <strong>b</strong>, normalized to be real and non-negative. The generalized
	 * eigenvalues are then the ratios ((alfr + i * alfi) / beta).</li>
	 * 
	 * <li><strong>z</strong> contains the product of the right hand transformations
	 * (for all three steps) if <strong>matz</strong> has been set to true.</li>
	 * </ul>
	 * 
	 * @param a    contains a real upper quasi-triangular matrix.
	 * @param b    contains a real upper triangular matrix. In addition, location
	 *             b[n-1][0] contains the tolerance quantity (epsb) computed and
	 *             saved in qzit.
	 * @param alfr and alfi contain the real and imaginary parts of the diagonal
	 *             elements of the triangular matrix that would be obtained if a
	 *             were reduced completely to triangular form by unitary
	 *             transformations. Non-zero values of <strong>alfi</strong> occur
	 *             in pairs, the first member positive and the second negative.
	 * @param alfi see description for <strong>alfr</strong>.
	 * @param beta contains the diagonal elements of the corresponding
	 *             <strong>b</strong>, normalized to be real and non-negative. The
	 *             generalized eigenvalues are then the ratios ((alfr + i * alfi) / beta).
	 * @param matz should be set to true. if the right hand transformations are to
	 *             be accumulated for later use in computing eigenvectors, and to
	 *             false otherwise.
	 * @param z    contains, if <strong>matz</strong> has been set to true, the
	 *             transformation matrix produced in the reductions by qzhes and
	 *             qzit, if performed, or else the identity matrix. If
	 *             <strong>matz</strong> has been set to false, z is not referenced.
	 */
	@SuppressWarnings("unused")
	static void qzval(double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, boolean matz,
			double[][] z) {

		final int n = a.length;
		
		int i = 0, j;
		int na = 0, en = 0, nn;
		double c = 0, d = 0, e = 0;
		double r, s, t;
		double a1 = 0, a2 = 0, u1, u2, v1, v2;
		double a11 = 0, a12 = 0, a21 = 0, a22 = 0;
		double b11 = 0, b12 = 0, b22 = 0;
		double di = 0, ei = 0;
		double an = 0, bn = 0;
		double cq = 0, dr = 0;
		double cz = 0, ti = 0, tr = 0;
		double a1i = 0, a2i = 0, a11i, a12i, a22i, a11r, a12r, a22r;
		double sqi = 0, ssi = 0, sqr = 0, szi = 0, ssr = 0, szr = 0;

		double epsb = b[n - 1][0];
		int isw = 1;

		// find eigenvalues of quasi-triangular matrices.
		for (nn = 0; nn < n; nn++) {
			
			State state = State.Linit;
			StateLoop: while (state != State.Lfinal) {
				
				switch (state) {
				case Linit:
					en = n - 1 - nn;	// TODO: check!!
					na = en - 1;
					if (isw == 2) {
						state = State.L505;
						break;
					}
					if (en == 0) {
						state = State.L410;
						break;
					}
					if (a[en][na] != 0.0) {
						state = State.L420;
						break;
					}
					state = State.L410;
					break;

				case L410:
					// 1-by-1 block, one real root
					alfr[en] = a[en][en];
					if (b[en][en] < 0.0) {
						alfr[en] = -alfr[en];
					}
					beta[en] = (Math.abs(b[en][en]));
					alfi[en] = 0.0;
					state = State.L510;
					break;

				case L420:
					// 2-by-2 block
					if (Math.abs(b[na][na]) <= epsb) {
						state = State.L455;
						break;
					}
					if (Math.abs(b[en][en]) > epsb) {
						state = State.L430;
						break;
					}
					a1 = a[en][en];
					a2 = a[en][na];
					bn = 0.0;
					state = State.L435;
					break;

				case L430:
					an = Math.abs(a[na][na]) + Math.abs(a[na][en]) + Math.abs(a[en][na]) + Math.abs(a[en][en]);
					bn = Math.abs(b[na][na]) + Math.abs(b[na][en]) + Math.abs(b[en][en]);
					a11 = a[na][na] / an;
					a12 = a[na][en] / an;
					a21 = a[en][na] / an;
					a22 = a[en][en] / an;
					b11 = b[na][na] / bn;
					b12 = b[na][en] / bn;
					b22 = b[en][en] / bn;
					e = a11 / b11;
					ei = a22 / b22;
					s = a21 / (b11 * b22);
					t = (a22 - e * b22) / b22;

					if (Math.abs(e) > Math.abs(ei)) {
						e = ei;
						t = (a11 - e * b11) / b11;
					}

					c = 0.5 * (t - s * b12);
					d = c * c + s * (a12 - e * b12);

					if (d < 0.0) {
						state = State.L480;
						break;
					}

					// two real roots. zero both a(en,na) and b(en,na)
					e = e + c + Math.copySign(Math.sqrt(d), c);
					a11 -= e * b11;
					a12 -= e * b12;
					a22 -= e * b22;

					if (Math.abs(a11) + Math.abs(a12) < Math.abs(a21) + Math.abs(a22)) {
						a1 = a22;
						a2 = a21;
					} else {
						a1 = a12;
						a2 = a11;
					}
					state = State.L435;
					break;

				case L435:
					// choose and apply real z
					s = Math.abs(a1) + Math.abs(a2);
					u1 = a1 / s;
					u2 = a2 / s;
					r = Math.copySign(Math.hypot(u1, u2), u1);
					v1 = -(u1 + r) / r;
					v2 = -u2 / r;
					u2 = v2 / v1;

					for (i = 0; i <= en; i++) {		// TODO: check!
						t = a[i][en] + u2 * a[i][na];
						a[i][en] = a[i][en] + t * v1;
						a[i][na] = a[i][na] + t * v2;

						t = b[i][en] + u2 * b[i][na];
						b[i][en] = b[i][en] + t * v1;
						b[i][na] = b[i][na] + t * v2;
					}	// L440

					if (matz) {
						for (i = 0; i < n; i++) {
							t = z[i][en] + u2 * z[i][na];
							z[i][en] = z[i][en] + t * v1;
							z[i][na] = z[i][na] + t * v2;
						}	// L445
					}

					// L450
					if (bn == 0.0) {
						state = State.L475;
						break;
					}
					if (an < Math.abs(e) * bn) {
						state = State.L455;
						break;
					}
					a1 = b[na][na];
					a2 = b[en][na];
					state = State.L460;
					break;

				case L455:
					a1 = a[na][na];
					a2 = a[en][na];
					state = State.L475;
					break;

				case L460:
					// choose and apply real q
					s = Math.abs(a1) + Math.abs(a2);
					if (s == 0.0) {
						state = State.L475;
						break;
					}
					u1 = a1 / s;
					u2 = a2 / s;
					r = Math.copySign(Math.hypot(u1, u2), u1);
					v1 = -(u1 + r) / r;
					v2 = -u2 / r;
					u2 = v2 / v1;

					for (j = na; j < n; j++) {
						t = a[na][j] + u2 * a[en][j];
						a[na][j] = a[na][j] + t * v1;
						a[en][j] = a[en][j] + t * v2;

						t = b[na][j] + u2 * b[en][j];
						b[na][j] = b[na][j] + t * v1;
						b[en][j] = b[en][j] + t * v2;
					}	// L470
					
					state = State.L475;
					break;

				case L475:
					a[en][na] = 0.0;
					b[en][na] = 0.0;
					alfr[na] = a[na][na];
					alfr[en] = a[en][en];

					if (b[na][na] < 0.0)
						alfr[na] = -alfr[na];

					if (b[en][en] < 0.0)
						alfr[en] = -alfr[en];

					beta[na] = (Math.abs(b[na][na]));
					beta[en] = (Math.abs(b[en][en]));
					alfi[en] = 0.0;
					alfi[na] = 0.0;
					state = State.L505;
					break;

				case L480:
					// two complex roots
					e = e + c;
					ei = Math.sqrt(-d);
					a11r = a11 - e * b11;
					a11i = ei * b11;
					a12r = a12 - e * b12;
					a12i = ei * b12;
					a22r = a22 - e * b22;
					a22i = ei * b22;

					if (Math.abs(a11r) + Math.abs(a11i) + Math.abs(a12r) + Math.abs(a12i) < 
						Math.abs(a21) + Math.abs(a22r) + Math.abs(a22i)) {
						// L482;
						a1 = a22r;
						a1i = a22i;
						a2 = -a21;
						a2i = 0.0;
					} else {
						a1 = a12r;
						a1i = a12i;
						a2 = -a11r;
						a2i = -a11i;
					}
					// goto L485;
					state = State.L485;
					break;

				case L485:
					// choose complex z
					cz = Math.hypot(a1, a1i);
					if (cz == 0.0) {
						// L487
						szr = 1.0;
						szi = 0.0;
					} else {
						szr = (a1 * a2 + a1i * a2i) / cz;
						szi = (a1 * a2i - a1i * a2) / cz;
						r = Math.sqrt(cz * cz + szr * szr + szi * szi);
						cz = cz / r;
						szr = szr / r;
						szi = szi / r;
					}

					// L490:
					if (an < (Math.abs(e) + ei) * bn) {
						// L492
						a1 = cz * a11 + szr * a12;
						a1i = szi * a12;
						a2 = cz * a21 + szr * a22;
						a2i = szi * a22;
					} else {
						a1 = cz * b11 + szr * b12;
						a1i = szi * b12;
						a2 = szr * b22;
						a2i = szi * b22;
					}

					// L495: choose complex q
					cq = Math.hypot(a1, a1i);
					if (cq == 0.0) {
						sqr = 1.0;
						sqi = 0.0;
					} else {
						sqr = (a1 * a2 + a1i * a2i) / cq;
						sqi = (a1 * a2i - a1i * a2) / cq;
						r = Math.sqrt(cq * cq + sqr * sqr + sqi * sqi);
						cq = cq / r;
						sqr = sqr / r;
						sqi = sqi / r;
					}

					// L500: compute diagonal elements that would result if transformations were applied
					ssr = sqr * szr + sqi * szi;
					ssi = sqr * szi - sqi * szr;
					i = 0;
					tr = cq * cz * a11 + cq * szr * a12 + sqr * cz * a21 + ssr * a22;
					ti = cq * szi * a12 - sqi * cz * a21 + ssi * a22;
					dr = cq * cz * b11 + cq * szr * b12 + ssr * b22;
					di = cq * szi * b12 + ssi * b22;
					state = State.L503;
					break;

				case L502:
					i = 1;
					tr = ssr * a11 - sqr * cz * a12 - cq * szr * a21 + cq * cz * a22;
					ti = -ssi * a11 - sqi * cz * a12 + cq * szi * a21;
					dr = ssr * b11 - sqr * cz * b12 + cq * cz * b22;
					di = -ssi * b11 - sqi * cz * b12;
					state = State.L503;
					break;

				case L503:
					t = ti * dr - tr * di;
					j = na;
					if (t < 0.0) {
						j = en;
					}
					r = Math.hypot(dr, di);
					beta[j] = bn * r;
					alfr[j] = an * (tr * dr + ti * di) / r;
					alfi[j] = an * t / r;
					if (i == 0) {
						// goto L502;
						state = State.L502;
						break;
					}
					state = State.L505;
					break;

				case L505:
					isw = 3 - isw;
					state = State.L510;
					break;

				case L510:
					state = State.Lfinal;
					break;

				case Lfinal:
					throw new RuntimeException("this should never happen!");
					//					break StateLoop;
				}
			}	// end StateLoop: while (state

		}	// L510 end for (nn ..

		b[n - 1][0] = epsb;
		
	} // end of qzval()

	private enum State {
		Linit, Lfinal, L505, L410, L420, L455, L430, L435, L480, L475, L485, L503, L502, L460, L510
	}

}

//----------------------------------------------------------------------------

abstract class QZVEC {
	
	public static boolean VERBOSE = false;

	// EISPACK Routines, see http://www.netlib.org/eispack/

	/**
	 * <p>
	 * This subroutine is the optional fourth step of the qz algorithm for solving
	 * generalized matrix eigenvalue problems, Siam J. Numer. Anal. 10, 241-256
	 * (1973) by Moler and Stewart. This description has been adapted from the
	 * original version (dated August 1983).
	 * </p>
	 * <p>
	 * This subroutine accepts a pair of real matrices, one of them in
	 * quasi-triangular form (in which each 2-by-2 block corresponds to a pair of
	 * complex eigenvalues) and the other in upper triangular form. It computes the
	 * eigenvectors of the triangular problem and transforms the results back to the
	 * original coordinate system. It is usually preceded by qzhes, qzit, and qzval.
	 * </p>
	 * <p>
	 * On output:
	 * </p>
	 * <ul>
	 * <li><strong>a</strong> is unaltered. Its subdiagonal elements provide
	 * information about the storage of the complex eigenvectors.</li>
	 * 
	 * <li><strong>b</strong> has been destroyed.</li>
	 * 
	 * <li><strong>alfr</strong>, <strong>alfi</strong> and <strong>beta</strong>
	 * are unaltered.</li>
	 * 
	 * <li><strong>z</strong> contains the real and imaginary parts of the
	 * eigenvectors. If alfi(i) = 0.0, the i-th eigenvalue is real and the i-th
	 * column of z contains its eigenvector. if alfi(i) &ne; 0.0, the i-th
	 * eigenvalue is complex. if alfi(i) &gt; 0.0, the eigenvalue is the first of a
	 * complex pair and the i-th and (i+1)-th columns of z contain its eigenvector.
	 * if alfi(i) &lt; 0.0, the eigenvalue is the second of a complex pair and the
	 * (i-1)-th and i-th columns of z contain the conjugate of its eigenvector. Each
	 * eigenvector is normalized so that the modulus of its largest component is
	 * 1.0.</li>
	 * </ul>
	 * 
	 * @param a    contains a real upper quasi-triangular matrix.
	 * @param b    contains a real upper triangular matrix. In addition, location
	 *             b[n-1][0] contains the tolerance quantity (epsb) computed and
	 *             saved in qzit.
	 * @param alfr , <strong>alfi</strong>, and <strong>beta</strong> are vectors
	 *             with components whose ratios ((alfr + i * alfi) / beta) are the
	 *             generalized eigenvalues. They are usually obtained from qzval.
	 * @param alfi see <strong>alfr</strong>
	 * @param beta see <strong>alfr</strong>
	 * @param z    contains the transformation matrix produced in the reductions by
	 *             qzhes, qzit, and qzval, if performed. If the eigenvectors of the
	 *             triangular problem are desired, z must contain the identity
	 *             matrix.
	 */
	@SuppressWarnings("unused")
	static void qzvec(double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, double[][] z) {

		final int n = a.length;

		int i = 0, j, k, m;
		int na = 0, ii, en = 0, jj, nn, enm2;
		double d = 0, q;
		double r = 0, s = 0, t = 0, w = 0, x = 0, y = 0, t1 = 0, t2 = 0, w1 = 0, x1 = 0, z1 = 0, di = 0;
		double ra = 0, dr = 0, sa = 0;
		double ti = 0, rr, tr = 0, zz = 0;
		double alfm, almi, betm, almr;

		double epsb = b[n - 1][0];
		int isw = 1;

		if (VERBOSE) System.out.println("QZVEC: starting Loop A ***********************");
		LoopA: for (nn = 0; nn < n; nn++) {

			StateA stateA = StateA.Ainit;
			StateLoopA: while (stateA != StateA.Afinal) {

				switch(stateA) {
				case Ainit :
					en = n - 1 - nn;	// TODO: check!!
					na = en - 1;
					if (isw == 2) {
						stateA = StateA.A795;
						break;
					}
					if (alfi[en] != 0.0) {
						stateA = StateA.A710;
						break;
					}

					// Real vector
					m = en;
					b[en][en] = 1.0;
					if (na == -1) {
						stateA = StateA.A800;
						break;
					}
					alfm = alfr[m];
					betm = beta[m];

					if (VERBOSE) System.out.println("QZVEC: starting Loop B ***********************");
					
					// for i=en-1 step -1 until 1 do --
					LoopB: for (ii = 0; ii <= na; ii++) {
						
						StateB stateB = StateB.Binit;
						StateLoopB: while (stateB != StateB.Bfinal) {
							switch (stateB) {
							case Binit :
								i = en - ii - 1;
								w = betm * a[i][i] - alfm * b[i][i];
								r = 0.0;

								for (j = m; j <= en; j++) {
									r = r +(betm * a[i][j] - alfm * b[i][j]) * b[j][en];
								}	// L610

								if (i == 0 || isw == 2) {
									stateB = StateB.B630;
									break;
								}
								if (betm * a[i][i - 1] == 0.0) {
									stateB = StateB.B630;
									break;
								}
								zz = w;
								s = r;
								stateB = StateB.B690;
								break;

							case B630 :
								m = i;
								if (isw == 2) {
									stateB = StateB.B640;
									break;
								}
								// real 1-by-1 block
								t = w;
								if (w == 0.0) {
									t = epsb;
								}
								b[i][en] = -r / t;
								stateB = StateB.B700;
								break;

							case B640 :
								// real 2-by-2 block
								x = betm * a[i][i + 1] - alfm * b[i][i + 1];
								y = betm * a[i + 1][i];
								q = w * zz - x * y;
								t = (x * s - zz * r) / q;
								b[i][en] = t;
								if (Math.abs(x) <= Math.abs(zz)) {
									stateB = StateB.B650;
									break;
								}
								b[i + 1][en] = (-r - w * t) / x;
								stateB = StateB.B690;
								break;

							case B650 :
								b[i + 1][en] = (-s - y * t) / zz;
								stateB = StateB.B690;
								break;

							case B690 :
								isw = 3 - isw;
								stateB = StateB.B700;
								break;

							case B700 :
								stateB = StateB.Bfinal;
								break;

							case Bfinal :
								throw new RuntimeException("this should never happen!");

							}	// end switch (stateB)	
						}	// end StateLoopB    			
					}	// L700: end of LoopB
					if (VERBOSE) System.out.println("QZVEC: finished Loop B ***********************");
					// end real vector
					stateA = StateA.A800;
					break;

				case A710:
					// complex vector
					m = na;
					almr = alfr[m];
					almi = alfi[m];
					betm = beta[m];
					// last vector component chosen imaginary so that eigenvector matrix is triangular
					y = betm * a[en][na];
					b[na][na] = -almi * b[en][en] / y;
					b[na][en] = (almr * b[en][en] - betm * a[en][en]) / y;
					b[en][na] = 0.0;
					b[en][en] = 1.0;
					enm2 = na;			// TODO: check!
					if (enm2 == 0) {
						stateA = StateA.A795;
						break;
					}

					if (VERBOSE) System.out.println("QZVEC: starting Loop C, enm2 = " + enm2);
					LoopC: for (ii = 0; ii < enm2; ii++) {
						if (VERBOSE) System.out.println("    ii = " + ii);
						StateC stateC = StateC.Cinit;
						StateLoopC: while (stateC != StateC.Cfinal) {
							if (VERBOSE) System.out.println("       StateLoopC: " + stateC);
							switch (stateC) {
							
							case Cinit :
								i = na - ii - 1;
								w = betm * a[i][i] - almr * b[i][i];
								w1 = -almi * b[i][i];
								ra = 0.0;
								sa = 0.0;

								for (j = m; j <= en; j++)
								{
									x = betm * a[i][j] - almr * b[i][j];
									x1 = -almi * b[i][j];
									ra = ra + x * b[j][na] - x1 * b[j][en];
									sa = sa + x * b[j][en] + x1 * b[j][na];
								}	// L760

								if (i == 0 || isw == 2) {
									stateC = StateC.C770;
									break;
								}
								if (betm * a[i][i - 1] == 0.0) {
									stateC = StateC.C770;
									break;
								}
								zz = w;
								z1 = w1;
								r = ra;
								s = sa;
								isw = 2;
								stateC = StateC.C790;
								break;

							case C770 :
								m = i;
								if (isw == 2) {
									stateC = StateC.C780;
									break;
								}
								// complex 1-by-1 block 
								tr = -ra;
								ti = -sa;
								stateC = StateC.C773;
								break;

							case C773:
								dr = w;
								di = w1;
								stateC = StateC.C775;
								break;

							case C775:
								// complex divide (t1,t2) = (tr,ti) / (dr,di)
								if (Math.abs(di) > Math.abs(dr)) {
									stateC = StateC.C777;
									break;
								}							
								rr = di / dr;
								d = dr + di * rr;
								t1 = (tr + ti * rr) / d;
								t2 = (ti - tr * rr) / d;

								switch (isw) {	// go to (787,782), isw
								case 1: 
									stateC = StateC.C787;
									continue StateLoopC;
									//break;
								case 2:
									stateC = StateC.C782;
									continue StateLoopC;
									//break;
								default: 
									throw new RuntimeException("illegal state isw = " + isw);
								}
								//stateC = StateC.C777;
								//break;

							case C777:
								rr = dr / di;
								d = dr * rr + di;
								t1 = (tr * rr + ti) / d;
								t2 = (ti * rr - tr) / d;
								switch (isw) {	// go to (787,782), isw
								case 1: 
									stateC = StateC.C787;
									continue StateLoopC;
									//break;
								case 2: 
									stateC = StateC.C782;
									continue StateLoopC;
									//break;
								default: 
									throw new RuntimeException("illegal state isw = " + isw);
								}
								//stateC = StateC.C780;
								//break;

							case C780:
								// complex 2-by-2 block 
								x = betm * a[i][i + 1] - almr * b[i][i + 1];
								x1 = -almi * b[i][i + 1];
								y = betm * a[i + 1][i];
								tr = y * ra - w * r + w1 * s;
								ti = y * sa - w * s - w1 * r;
								dr = w * zz - w1 * z1 - x * y;
								di = w * z1 + w1 * zz - x1 * y;
								if (dr == 0.0 && di == 0.0) {
									dr = epsb;
								}
								stateC = StateC.C775;
								break;

							case C782:
								b[i + 1][na] = t1;
								b[i + 1][en] = t2;
								isw = 1;
								if (Math.abs(y) > Math.abs(w) + Math.abs(w1)) {
									stateC = StateC.C785;
									break;
								}
								tr = -ra - x * b[(i + 1)][na] + x1 * b[(i + 1)][en];
								ti = -sa - x * b[(i + 1)][en] - x1 * b[(i + 1)][na];
								stateC = StateC.C773;
								break;

							case C785:
								t1 = (-r - zz * b[(i + 1)][na] + z1 * b[(i + 1)][en]) / y;
								t2 = (-s - zz * b[(i + 1)][en] - z1 * b[(i + 1)][na]) / y;
								stateC = StateC.C787;
								break;

							case C787:
								b[i][na] = t1;
								b[i][en] = t2;
								stateC = StateC.C790;
								break;

							case C790:
								stateC = StateC.Cfinal;
								break;

							case Cfinal:
								throw new RuntimeException("this should never happen!");

							}	// end switch(stateC)
						}	// end StateLoopC

					}	// L790: end of LoopC:

					if (VERBOSE) System.out.println("QZVEC: finished Loop C ***********************");
					stateA = StateA.A795;
					break;
					// end complex vector

				case A795:
					isw = 3 - isw;
					stateA = StateA.A800;
					break;

				case A800:
					stateA = StateA.Afinal;
					break;
					
				case Afinal:
					throw new RuntimeException("this should never happen!");
				}	// end switch(stateA)

			}	// end StateLoopA 
		}	// L800: end LoopA
		if (VERBOSE) System.out.println("QZVEC: finished Loop A ***********************");

		// end back substitution. transform to original coordinate system.
		for (jj = 0; jj < n; jj++) {
			j = n - jj - 1;

			for (i = 0; i < n; i++) {
				zz = 0.0;
				for (k = 0; k <= j; k++) {
					zz = zz + z[i][k] * b[k][j];
				}	// L860
				z[i][j] = zz;
			}
		} 	// L880: end for (jj ...

		if (VERBOSE) System.out.println("QZVEC: starting Loop D ***********************");
		// normalize so that modulus of largest component of each vector is 1.
		// (isw is 1 initially from before)
		LoopD: for (j = 0; j < n; j++) {
			
			StateD stateD = StateD.Dinit;
			StateLoopD: while (stateD != StateD.Dfinal) {

				switch (stateD) {
				case Dinit :
					d = 0.0;
					if (isw == 2) {
						stateD = StateD.D920;
						break;
					}
					if (alfi[j] != 0.0) {
						stateD = StateD.D945;
						break;
					}

					for (i = 0; i < n; i++) {
						//	if ((Math.abs(z[i][j])) > d) d = (Math.abs(z[i][j]));
						d = Math.max(d, Math.abs(z[i][j]));
					} // L890

					for (i = 0; i < n; i++) {
						z[i][j] = z[i][j] / d;
					}	// L900

					stateD = StateD.D950;
					break;

				case D920:
					for (i = 0; i < n; i++) {
						r = Math.abs(z[i][j - 1]) + Math.abs(z[i][j]);
						if (r != 0.0) {
							// r = r * dsqrt((z(i,j-1)/r)**2 + (z(i,j)/r)**2)
							r = r * Math.hypot(z[i][j - 1] / r, z[i][j] / r);
						}
						if (r > d) {
							d = r;
						}
					}	// L930

					for (i = 0; i < n; i++) {
						z[i][j - 1] = z[i][j - 1] / d;
						z[i][j] = z[i][j] / d;
					}	// L940

					stateD = StateD.D945;
					break;

				case D945:
					isw = 3 - isw;
					stateD = StateD.D950;
					break;

				case D950:
					stateD = StateD.Dfinal;
					break;

				case Dfinal:
					throw new RuntimeException("this should never happen!");

				}	// end switch(stateD)
			}	// end StateLoopD

		}	// L950: end LoopD
		if (VERBOSE) System.out.println("QZVEC: finished Loop D ***********************");

	}	// end of qzvec()


	private enum StateA {
		Ainit, Afinal, A795, A710, A800
	}

	private enum StateB {
		Binit, Bfinal, B630, B690, B640, B700, B650
	}

	private enum StateC {
		Cinit, Cfinal, C770, C790, C780, C773, C775, C777, C787, C782, C785
	}

	private enum StateD {
		Dinit, Dfinal, D920, D945, D950
	}

}
package imagingbook.common.math.eigen.accord;

import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eispack.QZHES;
import imagingbook.common.math.eispack.QZIT;
import imagingbook.common.math.eispack.QZVAL;
import imagingbook.common.math.eispack.QZVEC;

/**
 * Ported from https://github.com/accord-net/framework/blob/development/Sources/Accord.Math/Decompositions/GeneralizedEigenvalueDecomposition.cs
 * @author WB
 *
 */
public class GeneralzedEigenSolverAccord {

	// TODO: 
	//		look at http://www.netlib.no/netlib/eispack/qzhes.f
	//				http://www.netlib.no/netlib/eispack/qzit.f
	//	also 
	//		https://github.com/johannesgerer/jburkardt-f/blob/master/eispack/eispack.f90
	//		https://github.com/johannesgerer/jburkardt-c/blob/master/eispack/eispack.c	( C port!)
	// https://github.com/gurkangokdemir/eigenvalue-vector/blob/master/eispack.c


	private int n;
	private double[] ar;
	private double[] ai;
	private double[] beta;
	private double[][] Z;


	/// <summary>
	///   Constructs a new generalized eigenvalue decomposition.
	/// </summary>
	/// 
	/// <param name="a">The first matrix of the (A,B) matrix pencil.</param>
	/// <param name="b">The second matrix of the (A,B) matrix pencil.</param>
	/// <param name="sort">
	///   Pass <see langword="true"/> to sort the eigenvalues and eigenvectors at the end
	///   of the decomposition.</param>
	///
	public GeneralzedEigenSolverAccord(double[][] a, double[][] b, boolean sort) // boolean sort = false
	{
		if (a == null)
			throw new IllegalArgumentException("Matrix A cannot be null.");

		if (b == null)
			throw new IllegalArgumentException("Matrix B cannot be null.");

		if (a.length != a[0].length)
			throw new IllegalArgumentException("Matrix is not a square matrix.");

		if (b.length != b[0].length)
			throw new IllegalArgumentException("Matrix is not a square matrix.");

		if (a.length != b.length || a[0].length!= b[0].length)
			throw new IllegalArgumentException("Matrix dimensions do not match");


		n = a.length;
		double[][] A = Matrix.duplicate(a);
		double[][] B = Matrix.duplicate(b);
		ar = new double[n];
		ai = new double[n];
		beta = new double[n];
		Z = new double[n][n];
		boolean matz = true;

		// reduces A to upper Hessenberg form and B to upper
		// triangular form using orthogonal transformations
		QZHES.qzhes(A, B, matz, Z);

		// reduces the Hessenberg matrix A to quasi-triangular form
		// using orthogonal transformations while maintaining the
		// triangular form of the B matrix.
		double eps1 = 0.0;
		int ierr = QZIT.qzit(A, B, eps1 , matz, Z);
		//  System.out.println("Z = \n" + Matrix.toString(Z));
		
		if (ierr >= 0) {
			throw new RuntimeException("limit of 30*n iterations was exhausted for eigenvalue " + ierr);
		}

		// reduces the quasi-triangular matrix further, so that any
		// remaining 2-by-2 blocks correspond to pairs of complex
		// eigenvalues, and returns quantities whose ratios give the
		// generalized eigenvalues.
		QZVAL.qzval(A, B, ar, ai, beta, matz, Z);
		//            System.out.println("Z = \n" + Matrix.toString(Z));

		// computes the eigenvectors of the triangular problem and
		// transforms the results back to the original coordinate system.
		QZVEC.qzvec(A, B, ar, ai, beta, Z);
		//            System.out.println("Z = \n" + Matrix.toString(Z));

		//            if (sort)
		//            {
		//                // Sort eigenvalues and vectors in descending order
		//                var idx = Vector.Range(n);
		//                Array.Sort(idx, (i, j) =>
		//                {
		//                    if (Math.Abs(ar[i]) == Math.Abs(ar[j]))
		//                        return -Math.Abs(ai[i]).CompareTo(Math.Abs(ai[j]));
		//                    return -Math.Abs(ar[i]).CompareTo(Math.Abs(ar[j]));
		//                });
		//
		//                this.ar = this.ar.Get(idx);
		//                this.ai = this.ai.Get(idx);
		//                this.beta = this.beta.Get(idx);
		//                this.Z = this.Z.Get(null, idx);
		//            }
	}


	/// <summary>Returns the real parts of the alpha values.</summary>
	public double[] RealAlphas()
	{
		return ar;
	}

	/// <summary>Returns the imaginary parts of the alpha values.</summary>
	public double[] ImaginaryAlphas()
	{
		return ai;
	}

	/// <summary>Returns the beta values.</summary>
	public double[] Betas()
	{
		return beta;
	}

	/// <summary>
	///   Returns true if matrix B is singular.
	/// </summary>
	/// <remarks>
	///   This method checks if any of the generated betas is zero. It
	///   does not says that the problem is singular, but only that one
	///   of the matrices of the pencil (A,B) is singular.
	/// </remarks>
	public boolean IsSingular()
	{
		for (int i = 0; i < n; i++) {
			if (beta[i] == 0)
				return true;
		}
		return false;
	}

	/// <summary>
	///   Returns true if the eigenvalue problem is degenerate (ill-posed).
	/// </summary>
	public boolean IsDegenerate()
	{
		for (int i = 0; i < n; i++) {
			if (beta[i] == 0 && ar[i] == 0)
				return true;
		}
		return false;
	}

	/// <summary>Returns the real parts of the eigenvalues.</summary>
	/// <remarks>
	///   The eigenvalues are computed using the ratio alpha[i]/beta[i],
	///   which can lead to valid, but infinite eigenvalues.
	/// </remarks>
	public double[] RealEigenvalues()
	{
		// ((alfr+i*alfi)/beta)
		double[] eval = new double[n];
		for (int i = 0; i < n; i++) {
			eval[i] = ar[i] / beta[i];
		}
		return eval;
	}

	/// <summary>Returns the imaginary parts of the eigenvalues.</summary>	
	/// <remarks>
	///   The eigenvalues are computed using the ratio alpha[i]/beta[i],
	///   which can lead to valid, but infinite eigenvalues.
	/// </remarks>
	public double[] ImaginaryEigenvalues()
	{
		// ((alfr+i*alfi)/beta)
		double[] eval = new double[n];
		for (int i = 0; i < n; i++)
			eval[i] = ai[i] / beta[i];
		return eval;
	}

	/// <summary>Returns the eigenvector matrix.</summary>
	public double[][] Eigenvectors()
	{
		return Z;
	}

	public double[] getEigenvector(int k) {
		RealMatrix V = MatrixUtils.createRealMatrix(Z);
		return V.getColumn(k);
	}

	/// <summary>Returns the block diagonal eigenvalue matrix.</summary>
	public double[][] DiagonalMatrix()
	{
		double[][] x = new double[n][n];

		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
				x[i][j] = 0.0;

			x[i][i] = ar[i] / beta[i];
			if (ai[i] > 0)
				x[i][ i + 1] = ai[i] / beta[i];
			else if (ai[i] < 0)
				x[i][ i - 1] = ai[i] / beta[i];
		}

		return x;
	}

	// ----------------------------------------------------------


	public static void main(String[] args) {

		PrintPrecision.set(15);

		double[][] a = new double[][] {
			{ 3,  -1,  5},
			{ -1,  -2, 7},
			{ 5,  7,  0}};

		double[][] b = new double[][] {
			{ 10, 2,  7},
			{  2, 12, 3},
			{  7, 3, 15}};

		int n = a.length;
		RealMatrix A = MatrixUtils.createRealMatrix(a);
		RealMatrix B = MatrixUtils.createRealMatrix(b);

		GeneralzedEigenSolverAccord ges = new GeneralzedEigenSolverAccord(a, b, false);
		
		System.out.println("a = \n" + Matrix.toString(a));
		System.out.println("b = \n" + Matrix.toString(b));
		

		double[] evals = ges.RealEigenvalues();
		System.out.println("evals Re = " + Matrix.toString(ges.RealEigenvalues()));
		System.out.println("evals Im = " + Matrix.toString(ges.ImaginaryEigenvalues()));

		double[][] evecs = ges.Eigenvectors();
		System.out.println("evecs = \n" + Matrix.toString(evecs));

		// check A x_k = lambda_k B x_k
		for (int k = 0; k < n; k++) {
			double lambda = evals[k];
			RealVector evec = MatrixUtils.createRealVector(ges.getEigenvector(k));
			RealVector evecn = normalize(evec);
			System.out.println("k = " + k);
			System.out.println("  eval = " + lambda);
			System.out.println("  evec = " + Matrix.toString(evec));
//			System.out.println("  evecn = " + Matrix.toString(evecn));
			
			RealVector L = A.operate(evec);
			System.out.println("L = "+ Arrays.toString(L.toArray()));
			
			RealVector R = B.operate(evec).mapMultiply(lambda);
			System.out.println("R = "+ Arrays.toString(R.toArray()));
			
			RealVector res = L.subtract(R);
			System.out.println("res = "+ Arrays.toString(res.toArray()));	// L - R must be 0
			System.out.println("  res = 0? "+  Matrix.isZero(res.toArray(), 1e-4));
		}
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
evals = [0.39652279397140217, 0.2884669048273067, -1.2739949344008035]
 V = 
{{0.176, -0.269, -0.214}, 
{0.062, 0.220, -0.189}, 
{0.126, 0.127, 0.263}}

V normalized = 
{{1.000, -1.000, -0.813}, 
{0.351, 0.819, -0.720}, 
{0.717, 0.472, 1.000}}
 */
package imagingbook.common.math.eigen.accord;

import imagingbook.common.math.Arithmetic;

/**
 * Ported from https://github.com/accord-net/framework/blob/development/Sources/Accord.Math/Decompositions/GeneralizedEigenvalueDecomposition.cs
 * @author WB
 *
 */
public class GeneralzedEigenSolverAccord {
	
	// TODO: 
	//		look at http://www.netlib.no/netlib/eispack/qzhes.f
	//				http://www.netlib.no/netlib/eispack/qzit.f
	//	also https://github.com/johannesgerer/jburkardt-f/blob/master/eispack/eispack.f90

	
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
            ar = new double[n];
            ai = new double[n];
            beta = new double[n];
            Z = new double[n][n];
            double[][] A = a.clone();
            double[][] B = b.clone();

            boolean matz = true;
            int ierr = 0;


            // reduces A to upper Hessenberg form and B to upper
            // triangular form using orthogonal transformations
            QZHES.qzhes(n, A, B, matz, Z);

            // reduces the Hessenberg matrix A to quasi-triangular form
            // using orthogonal transformations while maintaining the
            // triangular form of the B matrix.
            QZIT.qzit(n, A, B, Arithmetic.EPSILON_DOUBLE, matz, Z, ierr);		// ref ierr

            // reduces the quasi-triangular matrix further, so that any
            // remaining 2-by-2 blocks correspond to pairs of complex
            // eigenvalues, and returns quantities whose ratios give the
            // generalized eigenvalues.
            QZVAL.qzval(n, A, B, ar, ai, beta, matz, Z);

            // computes the eigenvectors of the triangular problem and
            // transforms the results back to the original coordinate system.
            QZVEC.qzvec(n, A, B, ar, ai, beta, Z);

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
}

package imagingbook.common.math.eigen;

import static org.apache.commons.math3.linear.CholeskyDecomposition.DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD;
import static org.apache.commons.math3.linear.CholeskyDecomposition.DEFAULT_RELATIVE_SYMMETRY_THRESHOLD;

import java.util.Arrays;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.common.math.Matrix;

/**
 * Solver for generalized symmetric eigenproblems.
 * @author WB
 * @version 2021/11/11
 */
public class GeneralizedSymmetricEigenSolver {
	
	private final RealMatrix LiT;
	private final EigenDecomposition ed;
	
	/**
	 * Solves the generalized symmetric eigenproblem of the form
	 *       A x = lambda B x,
	 * where matrices A, B are symmetric and B is positive definite.
	 * See Sec. 11.0.5. of Press, Teukolsky, Vetterling, Flannery. 
	 * "Numerical Recipes". Cambridge University Press, third ed. (2007).
	 * 
	 * An exception is thrown if A is not symmetric and
	 * the Cholesky decomposition throws an exception if B is not symmetric
	 * and positive definite.
	 * 
	 * @param A real symmetric matrix
	 * @param B real symmetric and positive definite matrix
	 * @param rsth	relative symmetry threshold
	 * @param apth absolute positivity threshold
	 */
	public GeneralizedSymmetricEigenSolver(RealMatrix A, RealMatrix B, double rsth, double apth) {
		if (!MatrixUtils.isSymmetric(A, rsth)) {
			throw new RuntimeException("matrix A must be symmetric");
		}
		final RealMatrix L = new CholeskyDecomposition(B, rsth, apth).getL();
		final RealMatrix Li = MatrixUtils.inverse(L);
		this.LiT = Li.transpose();
		
		final DecompositionSolver ds = new LUDecomposition(L).getSolver();
		final RealMatrix Q = ds.solve(A);		// solve L * Q = A
		final RealMatrix Y = ds.solve(Q.transpose()); // solve L * Y = Q^T
		// alternatively:
		//RealMatrix Y = Li.multiply(A).multiply(LiT);
		
		this.ed =  new EigenDecomposition(Y);
	}
	
	/**
	 * See {@link #GeneralizedSymmetricEigenSolver(RealMatrix, RealMatrix, double, double)}.
	 * 
	 * @param A real symmetric matrix
	 * @param B real symmetric and positive definite matrix
	 */
	public GeneralizedSymmetricEigenSolver(RealMatrix A, RealMatrix B) {
		this(A, B, 
				DEFAULT_RELATIVE_SYMMETRY_THRESHOLD, 
				DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD);
	}
	
	// ---------------------------------------------------------------------
	
	public RealVector getEigenVector(int i) {
		return LiT.operate(ed.getEigenvector(i));
	}
	
	public double[] getRealEigenvalues() {
		return ed.getRealEigenvalues();
	}
	
	public double[] getImagEigenvalues() {
		return ed.getImagEigenvalues();
	}
	
	public boolean hasComplexEigenvalues() {
		return ed.hasComplexEigenvalues();
	}
	
	public RealMatrix getD() {
		return ed.getD();
	}

	
	public RealMatrix getV() {
		return LiT.multiply(ed.getV());
	}

	// ---------------------------------------------------------------------
	
	public static void main(String[] args) {
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
			{ 3,  -1,  5},
			{ -1,  -2, 7},
			{ 5,  7, 0}});
		
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			{ 10, 2,  7},
			{  2, 12, 3},
			{  7, 3, 15}});
		
		GeneralizedSymmetricEigenSolver decomp =
				new GeneralizedSymmetricEigenSolver(A, B);
		
		double[] evals = decomp.getRealEigenvalues();
		System.out.println("evals = " + Arrays.toString(evals));
		
		RealMatrix V = decomp.getV();
		System.out.println("V = \n" + Matrix.toString(V.getData()));
		RealMatrix D = decomp.getD();
		System.out.println("D = \n" + Matrix.toString(D.getData()));
		
		for (int i = 0; i < evals.length; i++) {
			double lambda = evals[i];
			RealVector evec = decomp.getEigenVector(i);
			System.out.println("evec = " + Arrays.toString(evec.toArray()));
			
			RealVector L = A.operate(evec);
//			System.out.println("L = "+ Arrays.toString(L.toArray()));
			RealVector R = B.operate(evec).mapMultiply(lambda);
//			System.out.println("R = "+ Arrays.toString(R.toArray()));
			RealVector res = L.subtract(R);
			//System.out.println("res = "+ Arrays.toString(res.toArray()));	// L - R must be 0
			System.out.println("res = 0? "+  Matrix.isZero(res.toArray(), 1e-6));
			
			// check A*V = B*V*D
			RealMatrix AV = A.multiply(V);
			System.out.println("AV = \n" + Matrix.toString(AV.getData()));
			RealMatrix BVD = B.multiply(V).multiply(D);
			System.out.println("BVD = \n" + Matrix.toString(BVD.getData()));
		}
		
	}
}

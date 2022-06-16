package imagingbook.common.math.eigen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import imagingbook.common.math.Matrix;

public class GeneralizedSymmetricEigenSolverTest {
	
	static double TOL = 1e-6;

	/* 
	 * Find solutions for A x = lambda B x,
	 * where matrices A, B are symmetric and B is positive definite.
	 */

	@Test
	public void test1() {
		
		// A is symmetric but not necessarily positive definite (this one is not)
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
			{  3, -1, 5},
			{ -1, -2, 7},
			{  5,  7, 0}});
		
		// B is symmetric and positive definite
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			{ 10,  2,  7},
			{  2, 12,  3},
			{  7,  3, 15}});
		
		assertTrue("matrix A must be symmetric", MatrixUtils.isSymmetric(A, TOL));
		assertTrue("matrix B must be symmetric", MatrixUtils.isSymmetric(B, TOL));
		
		assertTrue("matrix A may be positive definite or not", !Matrix.isPositiveDefinite(A, TOL));
		assertTrue("matrix B must be positive definite", Matrix.isPositiveDefinite(B, TOL));
		
		GeneralizedSymmetricEigenSolver solver = new GeneralizedSymmetricEigenSolver(A, B);
		
		assertFalse("should not have complex eigenvalues", solver.hasComplexEigenvalues());
		
		double[] evals = solver.getRealEigenvalues();
		assertNotNull("evals are null", evals);
//		System.out.println("evals = " + Arrays.toString(evals));
		
		RealMatrix V = solver.getV();
		assertNotNull("matrix V is null", V);
//		System.out.println("V = \n" + Matrix.toString(V.getData()));
		
		RealMatrix D = solver.getD();
		assertNotNull("matrix D is null",D);
//		System.out.println("D = \n" + Matrix.toString(D.getData()));
		
		// check A * x_i = lambda_i * B * x_i for all (lambda_i, x_i) pairs:
		
		for (int i = 0; i < evals.length; i++) {
			double lambda = evals[i];
			RealVector x = solver.getEigenvector(i);
//			System.out.println("x = " + Arrays.toString(x.toArray()));
			
			RealVector L = A.operate(x);						// L = A * x
//			System.out.println("L = "+ Arrays.toString(L.toArray()));
			RealVector R = B.operate(x).mapMultiply(lambda);	// R = lambda * B * x
//			System.out.println("R = "+ Arrays.toString(R.toArray()));		
			assertArrayEquals("A * x != lambda * B * x", L.toArray(), R.toArray(), 1e-6);	// check L == R
			
//			RealVector res = L.subtract(R);
//			System.out.println("res = "+ Arrays.toString(res.toArray()));	// L - R must be 0
//			System.out.println("res = 0? "+  Matrix.isZero(res.toArray(), 1e-6));	
		}
		
		// check A*V == B*V*D:
		RealMatrix AV = A.multiply(V);
//		System.out.println("AV = \n" + Matrix.toString(AV.getData()));
		RealMatrix BVD = B.multiply(V).multiply(D);
//		System.out.println("BVD = \n" + Matrix.toString(BVD.getData()));
		assertTrue("A*V != B*V*D", Matrix.isZero(AV.subtract(BVD).getData(), 1e-6));
	}

}

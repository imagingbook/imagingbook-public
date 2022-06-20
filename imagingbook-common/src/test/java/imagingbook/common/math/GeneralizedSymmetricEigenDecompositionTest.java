package imagingbook.common.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import imagingbook.testutils.NumericTestUtils;

public class GeneralizedSymmetricEigenDecompositionTest {

	@Test
	public void test1() {
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
			{ 3,  -1,  5},
			{ -1,  -2, 7},
			{ 5,  7,  0}});
		
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			{ 10, 2,  7},
			{  2, 12, 3},
			{  7, 3, 15}});
		
		double[] evalsAB = {0.39652279397140217, 0.2884669048273067, -1.2739949344008035};
		
		GeneralizedSymmetricEigenDecomposition ed = new GeneralizedSymmetricEigenDecomposition(A, B);
		
		assertFalse(ed.hasComplexEigenvalues());
		
		double[] evals = ed.getRealEigenvalues();
		assertArrayEquals(evalsAB, evals, 1e-6);
		
		// check A * x_k = lambda_k * B * x_k, for all k
		for (int k = 0; k < evals.length; k++) {
			double lambda = evals[k];
			RealVector evec = ed.getEigenvector(k);
			
			RealVector lhs = A.operate(evec);
//			System.out.println("lhs = "+ Arrays.toString(lhs.toArray()));	
			RealVector rhs = B.operate(evec).mapMultiply(lambda);
//			System.out.println("rhs = "+ Arrays.toString(rhs.toArray()));
			
			assertArrayEquals(lhs.toArray(), rhs.toArray(), 1e-6);
		}
		
		// check A*V = B*V*D
		RealMatrix V = ed.getV();
		RealMatrix D = ed.getD();
		RealMatrix AV = A.multiply(V);
//		System.out.println("AV = \n" + Matrix.toString(AV.getData()));
		RealMatrix BVD = B.multiply(V).multiply(D);
//		System.out.println("BVD = \n" + Matrix.toString(BVD.getData()));
		NumericTestUtils.assertArrayEquals(AV.getData(), BVD.getData(), 1e-6);
	}

}

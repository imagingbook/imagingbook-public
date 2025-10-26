/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.eigen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;
// import org.apache.commons.math3.linear.MatrixUtils;
// import org.apache.commons.math3.linear.RealMatrix;
// import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import imagingbook.testutils.NumericTestUtils;

public class GeneralizedEigenDecompositionTest {

	@Test
	public void test1() {	// real eigenvalues only
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
			{ 3,  -1,  5},
			{ -1,  -2, 7},
			{ 5,  7,  0}});
		
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			{ 10, 2,  7},
			{  2, 12, 3},
			{  7, 3, 15}});
		
		double[] evalsAB = {-1.273994934400804, 0.396523620802854, 0.288466904827307};
		
		runRealValuedChecks(A, B, evalsAB);
	}
	
	@Test
	public void test2() {	// real eigenvalues only
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
			{ 1, 2, 3},
		    { 8, 1, 4},
		    { 3, 2, 3}});
		
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			 {5, 1, 1},
			 { 1, 5, 1},
			 { 1, 1, 5}});
		
		double[] evalsAB = {1.138686667119456, -0.748168231839396, -0.104804149565775};
		
		runRealValuedChecks(A, B, evalsAB);
	}
	
	
	private void runRealValuedChecks(RealMatrix A, RealMatrix B, double[] evalsAB) {	// real eigenvalues only
		GeneralizedEigenDecomposition ed = new GeneralizedEigenDecomposition(A, B);
		assertFalse(ed.hasComplexEigenvalues());
		
		double[] evals = ed.getRealEigenvalues();
//		PrintPrecision.set(15);
//		System.out.println("evals = "+ Matrix.toString(evals));
		assertArrayEquals(evalsAB, evals, 1e-6);
		
		// check A * x_k = lambda_k * B * x_k, for all k
		for (int k = 0; k < evals.length; k++) {
			double lambda = evals[k];
			RealVector evec = ed.getEigenvector(k);
			
			RealVector lhs = A.operate(evec);
//			System.out.println("lhs = "+ Arrays.toString(lhs.toArray()));	
			RealVector rhs = B.operate(evec).mapMultiply(lambda);
//			System.out.println("rhs = "+ Arrays.toString(rhs.toArray()));
			
			assertArrayEquals(lhs.toArray(), rhs.toArray(), 1e-4);
		}
		
		// check A*V = B*V*D
		RealMatrix V = ed.getV();
		RealMatrix D = ed.getD();
		RealMatrix AV = A.multiply(V);
//		System.out.println("AV = \n" + Matrix.toString(AV.getData()));
		RealMatrix BVD = B.multiply(V).multiply(D);
//		System.out.println("BVD = \n" + Matrix.toString(BVD.getData()));
		NumericTestUtils.assert2dArrayEquals(AV.getData(), BVD.getData(), 1e-4);
	}
	
	// ------------------------------------------------------------
	
	@Test
	public void test3() {	// complex eigenvalues
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
			{ 3,  -1,  4},	// only one element changed from test1!
			{ -1,  -2, 7},
			{ 5,  7,  0}});
		
		RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
			{ 10, 2,  7},
			{  2, 12, 3},
			{  7, 3, 15}});
		
		// there is (always?) one real eigenvalue and two complex conjugate eigenvals:
		double[] evalsRe = {-1.218260039403249, 0.348658815513143, 0.348658815513143};
		double[] evalsIm = {0.000000000000000, 0.014231792443601, -0.014231792443601};
		
		GeneralizedEigenDecomposition ed = new GeneralizedEigenDecomposition(A, B);
		
		assertTrue(ed.hasComplexEigenvalues());
		
		assertArrayEquals(evalsRe, ed.getRealEigenvalues(), 1e-6);
		assertArrayEquals(evalsIm, ed.getImagEigenvalues(), 1e-6);
		
		// TODO: how to extract the associated eigenvectors??
	}

}

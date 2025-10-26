/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.eigen;

import static org.junit.Assert.*;

import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
// import org.apache.commons.math3.linear.MatrixUtils;
// import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.exception.MaxIterationsExceededException;
import imagingbook.testutils.NumericTestUtils;
import imagingbook.testutils.RandomMatrixGenerator;

// unhandled cases are marked
public class EigenDecompositionApacheTest {

	// two examples which Apache's implementation does not handle (finds complex eigenvals)
	
//	@Test
//	public void test1A() {	// non-symmetric example, fails
//		RealMatrix M = MatrixUtils.createRealMatrix(new double[][]  
//				{{4455707.000000000, 16685.500000000, 17344.500000000, 142.000000000}, 
//				{-951005821.436619800, -3525507.007042253, -4059917.288732395, -33371.000000000}, 
//				{-997789920.901407700, -4059917.288732392, -3879630.838028166, -34689.000000000}, 
//				{70029373562.509700000, 297685588.322852130, 314485277.997519970, 2949430.845070420}});
//		
//		double[] evalsRe = {-753386.305449104, 115.864946503, 401745.451068664, 351524.989423574};
//		
//		checkEigenvalues(M, evalsRe);
//	}
	
//	@Test
//	public void test1B() {	// symmetric example, fails
//		RealMatrix M = MatrixUtils.createRealMatrix(new double[][]  
//			{{-635322.712034708800000, 4756.174679968795000, -265184.797553499500000, 20382.684252847448000}, 
//			{4756.174679968796000, 560670.646574945200000, 0.190328961575323, -0.016193729208773}, 
//			{-265184.797553499500000, 0.190328961575416, 74652.065423977560000, 1.470729864949432}, 
//			{20382.684252847444000, -0.016193729208773, 1.470729864949432, 0.000035786438260}});
//		
//		double[] evalsRe = {-723969.633924302, 262.920133439, 163014.536230610, 560692.177560255};		
//		checkEigenvalues(M, evalsRe);
//	}
	
	@Test
	public void test1C() {	// non-symmetric example
		RealMatrix M = MatrixUtils.createRealMatrix(new double[][]  
				{	{1, 1, 2, 0}, 
					{0, 1, 3, 0}, 
					{0, 0, 2, 2}, 
					{0, 0, 0, 1}});
		
		double[] evalsRe = {1, 1, 1, 2};		
		checkEigenvalues(M, evalsRe);
	}
	
	@Test
	public void test1D() {	// non-symmetric example, only 1 eigenvalue
		RealMatrix M = MatrixUtils.createRealMatrix(new double[][]  
				{	{2, 1, 0, 0, 0}, 
					{0, 2, 0, 0, 0}, 
					{0, 0, 2, 0, 0}, 
					{0, 0, 0, 2, 1}, 
					{0, 0, 0, 0, 2}});
		
		double[] evalsRe = {2, 2, 2, 2, 2};
	
		checkEigenvalues(M, evalsRe);
	}
	

	
	private void checkEigenvalues(RealMatrix M, double[] evalsExpected) {
		EigenDecompositionApache ed = new EigenDecompositionApache(M);
		assertFalse("", ed.hasComplexEigenvalues());
		double[] evalsRe = ed.getRealEigenvalues();
//		System.out.println(Arrays.toString(evalsRe));
		assertArrayEquals("", Matrix.sort(evalsExpected), Matrix.sort(evalsRe), 1E-6);
	}
	
	// ---------------------------------------------------------------------
	
	@Test
	public void randomMatrixTestA() {
		// only checks if no exception occurs (iteration count exceeded) on square, non-symmetric matrices
		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
		for (int i = 0; i < 1000; i++) {
			RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomSquareMatrix(6, 10));
//			System.out.println("M = \n" + Matrix.toString(M));	
			try {
				EigenDecompositionApache ed = new EigenDecompositionApache(M);
//				System.out.println("evalsRe = " + Matrix.toString(ed.getRealEigenvalues()));
//				System.out.println("evalsIm = " + Matrix.toString(ed.getImagEigenvalues()));
				assertNotNull("", ed.getRealEigenvalues());
			} catch (MaxIterationsExceededException e) {
				fail("max. number of iterations exceeded");
			}
		}
	}
	
	@Test
	public void randomMatrixTestB() {	// works only for square matrices!
		// only checks if no exception occurs (iteration count exceeded) on non-square matrices
		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
		for (int i = 0; i < 1000; i++) {
			RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomMatrix(5, 5, 10));
//			System.out.println("M = \n" + Matrix.toString(M));	
			try {
				EigenDecompositionApache ed = new EigenDecompositionApache(M);
//				System.out.println("evalsRe = " + Matrix.toString(ed.getRealEigenvalues()));
//				System.out.println("evalsIm = " + Matrix.toString(ed.getImagEigenvalues()));
				assertNotNull("", ed.getRealEigenvalues());
			} catch (MaxIterationsExceededException e) {
				fail("max. number of iterations exceeded");
			}
		}
	}
	
	@Test
	public void randomMatrixTest3() {
		// checks decomposition of random symmetric matrices, always real eigenvalues!
		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
		for (int i = 0; i < 1000; i++) {
			RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomSymmetricMatrix(6, 10));
//			System.out.println("M = \n" + Matrix.toString(M));	
			try {
				EigenDecompositionApache ed = new EigenDecompositionApache(M);
//				System.out.println("evalsRe = " + Matrix.toString(ed.getRealEigenvalues()));
//				System.out.println("evalsIm = " + Matrix.toString(ed.getImagEigenvalues()));
				assertNotNull("", ed.getRealEigenvalues());
				assertFalse("", ed.hasComplexEigenvalues());
				
				// check M*x = lambda*x
				double[] eigenvals = ed.getRealEigenvalues();
				for (int k = 0; k < eigenvals.length; k++) {
					double lambda = eigenvals[k];
					double[] x = ed.getEigenvector(k).toArray();
					assertArrayEquals(Matrix.multiply(M.getData(), x), Matrix.multiply(lambda, x), 1E-6);
				}
				
			} catch (MaxIterationsExceededException e) {
				fail("max. number of iterations exceeded");
			}
		}
	}
	
	// -------------------------------------------------------------------------------
	
	
	@Test
	public void testEigenValueVectorProduct01() {
		double[][] M = {
				{3, -2},
				{-4, 1}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct02() {
		double[][] M = {
				{-0.004710, -0.006970},
				{-0.006970, -0.029195}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct03() {
		double[][] M = {
				{0, 0},
				{0, 1}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct04() {
		double[][] M = {
				{1, 0},
				{0, 0}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct05() {
		double[][] M = {
				{1, 0},
				{-2, 1}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct06() {
		double[][] M = {
				{1, -2},
				{0, 1}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct07() {
		double[][] M = {
				{1, 2},
				{2, 1}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct08() {
		double[][] M = {
				{0, -1},
				{2, 0}};
		runEigenTest(M, false);
	}
	
	@Test
	public void testEigenValueVectorProduct09() {
		double[][] M = {
				{4, -1},
				{2, 4}};
		runEigenTest(M, false);
	}
	
	@Test
	public void testEigenValueVectorProduct10() {
		double[][] M = {
				{5, 2, 0},
				{2, 5, 0},
				{-3, 4, 6}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct11() {
		double[][] M = {		// has complex eigenvalues!
				{5, 2, 0, 1},
				{2, 5, 0, 7},
				{-3, 4, 6, 0},
				{1 , 2, 3, 4}};
		runEigenTest(M, false);
	}
	
//	@Test
//	public void testEigenValueVectorProduct12() { // fails
//		double[][] M = {
//				{4455707.000000000, 16685.500000000, 17344.500000000, 142.000000000}, 
//				{-951005821.436619800, -3525507.007042253, -4059917.288732395, -33371.000000000}, 
//				{-997789920.901407700, -4059917.288732392, -3879630.838028166, -34689.000000000}, 
//				{70029373562.509700000, 297685588.322852130, 314485277.997519970, 2949430.845070420}};
//		checkEigenValueVectorProducts(M);
//	}
	
//	@Test
//	public void testEigenValueVectorProduct13() { // fails
//		double[][] M = {
//				{-635322.712034708800000, 4756.174679968795000, -265184.797553499500000, 20382.684252847448000}, 
//				{4756.174679968796000, 560670.646574945200000, 0.190328961575323, -0.016193729208773}, 
//				{-265184.797553499500000, 0.190328961575416, 74652.065423977560000, 1.470729864949432}, 
//				{20382.684252847444000, -0.016193729208773, 1.470729864949432, 0.000035786438260}};
//		checkEigenValueVectorProducts(M);
//	}
	
	@Test
	public void testEigenValueVectorProduct14() {
		double[][] M = {
				{1, 1, 2, 0}, 
				{0, 1, 3, 0}, 
				{0, 0, 2, 2}, 
				{0, 0, 0, 1}};
		checkEigenValueVectorProducts(M);
	}
	
	@Test
	public void testEigenValueVectorProduct15() {
		double[][] M = {
				{2, 1, 0, 0, 0}, 
				{0, 2, 0, 0, 0}, 
				{0, 0, 2, 0, 0}, 
				{0, 0, 0, 2, 1}, 
				{0, 0, 0, 0, 2}};
		checkEigenValueVectorProducts(M);
	}
	
	// ---------------------------------------------------------
	
		private void checkEigenValueVectorProducts(double[][] M) {
			runEigenTest(M, true);
		}

		private void runEigenTest(double[][] M, boolean shouldBeReal) {
			EigenDecompositionApache solver = new EigenDecompositionApache(MatrixUtils.createRealMatrix(M));	

			if (shouldBeReal) {
				assertFalse(solver.hasComplexEigenvalues());
			}
			else {
				assertTrue(solver.hasComplexEigenvalues());
				return;
			}
			
			double[] eigenvals = solver.getRealEigenvalues();
			double[][] V = solver.getV().getData();
			
			for (int k = 0; k < eigenvals.length; k++) {
				if (Double.isNaN(eigenvals[k])) {
					continue;
				}
				//System.out.println("testing " + eigenvals[k]);
				double lambda = eigenvals[k];
				double[] x = solver.getEigenvector(k).toArray();
				
				// the k-th eigenvector is the k-th column of V
				assertArrayEquals(x, Matrix.getColumn(V, k), 1E-6);	
				
				// check: M * x_k = λ_k * x_k
				double[] LH = Matrix.multiply(M, x);
				double[] RH = Matrix.multiply(lambda, x);
//				System.out.println("LH = " + Matrix.toString(LH));
//				System.out.println("RH = " + Matrix.toString(RH));
//				assertArrayEquals("eigenvalue " + k, 
//						Matrix.multiply(M, x), Matrix.multiply(lambda, x), 1E-3);
				NumericTestUtils.assertArrayEqualsRelative(LH, RH, 1E-6);
			}
		}

}

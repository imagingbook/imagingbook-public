package imagingbook.common.math.eigen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

import imagingbook.common.math.EigenvalueDecomposition;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.RandomMatrixGenerator;
import imagingbook.common.math.exception.MaxIterationsExceededException;
import imagingbook.testutils.NumericTestUtils;

public class EigenvalueDecompositionTest {
	
	// two examples which Apache's implementation does not handle (finds complex eigenvals)
	
	@Test
	public void test1A() {	// non-symmetric example
		RealMatrix M = MatrixUtils.createRealMatrix(new double[][]  
				{{4455707.000000000, 16685.500000000, 17344.500000000, 142.000000000}, 
				{-951005821.436619800, -3525507.007042253, -4059917.288732395, -33371.000000000}, 
				{-997789920.901407700, -4059917.288732392, -3879630.838028166, -34689.000000000}, 
				{70029373562.509700000, 297685588.322852130, 314485277.997519970, 2949430.845070420}});
		
		double[] evalsRe = {-753386.305449104, 115.864946503, 401745.451068664, 351524.989423574};
		
		runTest1(M, evalsRe);
	}
	
	@Test
	public void test1B() {	// symmetric example
		RealMatrix M = MatrixUtils.createRealMatrix(new double[][]  
			{{-635322.712034708800000, 4756.174679968795000, -265184.797553499500000, 20382.684252847448000}, 
			{4756.174679968796000, 560670.646574945200000, 0.190328961575323, -0.016193729208773}, 
			{-265184.797553499500000, 0.190328961575416, 74652.065423977560000, 1.470729864949432}, 
			{20382.684252847444000, -0.016193729208773, 1.470729864949432, 0.000035786438260}});
		
		double[] evalsRe = {-723969.633924302, 262.920133439, 163014.536230610, 560692.177560255};
		
		runTest1(M, evalsRe);
	}
	
	private void runTest1(RealMatrix M, double[] evalsExpected) {
		EigenvalueDecomposition ed = new EigenvalueDecomposition(M);
		assertFalse("", ed.hasComplexEigenvalues());
		double[] evalsRe = ed.getRealEigenvalues();
		assertArrayEquals("", evalsExpected, evalsRe, 1E-6);
	}
	
	// ---------------------------------------------------------------------
	
	@Test
	public void test2A() {
		// only checks if no exception occurs (iteration count exceeded) on square, non-symmetric matrices
		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
		for (int i = 0; i < 1000; i++) {
			RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomSquareMatrix(6, 10));
//			System.out.println("M = \n" + Matrix.toString(M));	
			try {
				EigenvalueDecomposition ed = new EigenvalueDecomposition(M);
//				System.out.println("evalsRe = " + Matrix.toString(ed.getRealEigenvalues()));
//				System.out.println("evalsIm = " + Matrix.toString(ed.getImagEigenvalues()));
				assertNotNull("", ed.getRealEigenvalues());
			} catch (MaxIterationsExceededException e) {
				fail("max. number of iterations exceeded");
			}
		}
	}
	
	@Test
	public void test2B() {
		// only checks if no exception occurs (iteration count exceeded) on non-square matrices
		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
		for (int i = 0; i < 1000; i++) {
			RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomMatrix(6, 4, 10));
//			System.out.println("M = \n" + Matrix.toString(M));	
			try {
				EigenvalueDecomposition ed = new EigenvalueDecomposition(M);
//				System.out.println("evalsRe = " + Matrix.toString(ed.getRealEigenvalues()));
//				System.out.println("evalsIm = " + Matrix.toString(ed.getImagEigenvalues()));
				assertNotNull("", ed.getRealEigenvalues());
			} catch (MaxIterationsExceededException e) {
				fail("max. number of iterations exceeded");
			}
		}
	}
	
	@Test
	public void test3() {
		// checks decomposition of random symmetric matrices, always real eigenvalues!
		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
		for (int i = 0; i < 1000; i++) {
			RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomSymmetricMatrix(6, 10));
//			System.out.println("M = \n" + Matrix.toString(M));	
			try {
				EigenvalueDecomposition ed = new EigenvalueDecomposition(M);
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
	public void testEigensolverNxNa() {
		double[][] M = {
				{3, -2},
				{-4, 1}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNb() {
		double[][] M = {
				{-0.004710, -0.006970},
				{-0.006970, -0.029195}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNc() {
		double[][] M = {
				{0, 0},
				{0, 1}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNd() {
		double[][] M = {
				{1, 0},
				{0, 0}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNe() {
		double[][] M = {
				{1, 0},
				{-2, 1}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNf() {
		double[][] M = {
				{1, -2},
				{0, 1}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNg() {
		double[][] M = {
				{1, 2},
				{2, 1}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolver2x2H() {
		double[][] M = {
				{0, -1},
				{2, 0}};
		runEigenTest(M, false);
	}
	
	@Test
	public void testEigensolver2x2I() {
		double[][] M = {
				{4, -1},
				{2, 4}};
		runEigenTest(M, false);
	}
	
	@Test
	public void testEigensolverNxNh() {
		double[][] M = {
				{5, 2, 0},
				{2, 5, 0},
				{-3, 4, 6}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNi() {
		double[][] M = {		// has complex eigenvalues!
				{5, 2, 0, 1},
				{2, 5, 0, 7},
				{-3, 4, 6, 0},
				{1 , 2, 3, 4}};
		runEigenTest(M, false);
	}
	
	@Test
	public void testEigensolverNxNj() {
		double[][] M = {
				{4455707.000000000, 16685.500000000, 17344.500000000, 142.000000000}, 
				{-951005821.436619800, -3525507.007042253, -4059917.288732395, -33371.000000000}, 
				{-997789920.901407700, -4059917.288732392, -3879630.838028166, -34689.000000000}, 
				{70029373562.509700000, 297685588.322852130, 314485277.997519970, 2949430.845070420}};
		runEigenTest(M);
	}
	
	@Test
	public void testEigensolverNxNk() {
		double[][] M = {
				{-635322.712034708800000, 4756.174679968795000, -265184.797553499500000, 20382.684252847448000}, 
				{4756.174679968796000, 560670.646574945200000, 0.190328961575323, -0.016193729208773}, 
				{-265184.797553499500000, 0.190328961575416, 74652.065423977560000, 1.470729864949432}, 
				{20382.684252847444000, -0.016193729208773, 1.470729864949432, 0.000035786438260}};
		runEigenTest(M);
	}
	
	// ---------------------------------------------------------
	
		private void runEigenTest(double[][] M) {
			runEigenTest(M, true);
		}

		private void runEigenTest(double[][] M, boolean shouldBeReal) {
			EigenvalueDecomposition solver = new EigenvalueDecomposition(MatrixUtils.createRealMatrix(M));	

			if (shouldBeReal) {
				assertFalse(solver.hasComplexEigenvalues());
			}
			else {
				assertTrue(solver.hasComplexEigenvalues());
				return;
			}
			
			double[] eigenvals = solver.getRealEigenvalues();
			
			
			for (int k = 0; k < eigenvals.length; k++) {
				if (Double.isNaN(eigenvals[k])) {
					continue;
				}
				//System.out.println("testing " + eigenvals[k]);
				double lambda = eigenvals[k];
				double[] x = solver.getEigenvector(k).toArray();
				// check: M * x_k = λ_k * x_k
				double[] LH = Matrix.multiply(M, x);
				double[] RH = Matrix.multiply(lambda, x);
//				System.out.println("LH = " + Matrix.toString(LH));
//				System.out.println("RH = " + Matrix.toString(RH));
//				assertArrayEquals("eigenvalue " + k, 
//						Matrix.multiply(M, x), Matrix.multiply(lambda, x), 1E-3);
				NumericTestUtils.assertArrayEqualsRelative(Matrix.multiply(M, x), Matrix.multiply(lambda, x), 1E-6);
			}
		}
	
}

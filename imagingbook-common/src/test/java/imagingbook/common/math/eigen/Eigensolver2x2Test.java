package imagingbook.common.math.eigen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.math.Matrix;

public class Eigensolver2x2Test {

	@Test
	public void testEigensolver2x2A() { // λ1 = 5.0000, λ2 = -1.0000 x1 = {4.000, -4.000}, x2 = {-2.000, -4.000}
		double[][] M = {
				{3, -2},
				{-4, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2B() { // <-0,0310, -0,0029, {-0.007, -0.026}, {0.026, -0.007}>
		double[][] M = {
				{-0.004710, -0.006970},
				{-0.006970, -0.029195}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2C() { // <1,0000, 0,0000, {0.000, 1.000}, {-1.000, 0.000}>
		double[][] M = {
				{0, 0},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2D() { // <1,0000, 0,0000, {1.000, 0.000}, {0.000, -1.000}>
		double[][] M = {
				{1, 0},
				{0, 0}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2E() { // <1,0000, 1,0000, {-0.000, -2.000}, {0.000, -2.000}>
		double[][] M = {
				{1, 0},
				{-2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2F() { // <1,0000, 1,0000, {-2.000, -0.000}, {-2.000, 0.000}>
		double[][] M = {
				{1, -2},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2G() { // <3,0000, -1,0000, {2.000, 2.000}, {-2.000, 2.000}>
		double[][] M = {
				{1, 2},
				{2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2H() {	// not real
		double[][] M = {
				{0, -1},
				{2, 0}};
		runTest(M, false);
	}
	
	@Test
	public void testEigensolver2x2I() { // not real
		double[][] M = {
				{4, -1},
				{2, 4}};
		runTest(M, false);
	}
	
	@Test
	public void testEigensolver2x2J() { // identity matrix
		double[][] M = Matrix.idMatrix(2); 
		runTest(M, true);
	}
	
	@Test
	public void testRandomMatrix2x2() {
		Random RG = new Random(17);
		final int N = 1000;
		//int cnt = 0;
		for (int i = 0; i < N; i++) {
			double[][] A = makeRandomMatrix2x2(RG);
			RealEigensolver solver = new Eigensolver2x2(A);
			if (solver.isReal()) {
				//cnt++;
				// check if A * x_i = lambda_i * x_i :
				double[] eigenvals = solver.getEigenvalues();
				for (int k = 0; k < eigenvals.length; k++) {
					double lambda = solver.getEigenvalue(k); //eigenvals[k];
					double[] x = solver.getEigenvector(k);
					assertArrayEquals(Matrix.multiply(A, x), Matrix.multiply(lambda, x), 1E-6);
				}
			}
		}
		//System.out.println("real solutions: " + cnt + " out of " + N);
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	private void runTest(double[][] M) {
		runTest(M, true);
	}

	private void runTest(double[][] M, boolean shouldBeReal) {
		RealEigensolver solver = new Eigensolver2x2(M);	
		if (shouldBeReal) {
			assertTrue(solver.isReal());
		}
		else {
			assertFalse(solver.isReal());
			return;
		}
		
		double[] eigenvals = solver.getEigenvalues();
		
		
		for (int k = 0; k < eigenvals.length; k++) {
			if (Double.isNaN(eigenvals[k])) {
				continue;
			}
			//System.out.println("testing " + eigenvals[k]);
			double lambda = eigenvals[k];
			double[] x = solver.getEigenvector(k);
			// check: M * x_k = λ_k * x_k
			assertArrayEquals(Matrix.multiply(M, x), Matrix.multiply(lambda, x), 1E-6);
		}
	}
	
	static double[][] makeRandomMatrix2x2(Random RG) {
		double[][] A = new double[2][2];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = 2 * RG.nextDouble() - 1;
			}
		}
		return A;
	}

}

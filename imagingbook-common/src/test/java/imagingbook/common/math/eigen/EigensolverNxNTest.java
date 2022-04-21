package imagingbook.common.math.eigen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.math.Matrix;

public class EigensolverNxNTest {

	@Test
	public void testEigensolverNxNa() {
		double[][] M = {
				{3, -2},
				{-4, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNb() {
		double[][] M = {
				{-0.004710, -0.006970},
				{-0.006970, -0.029195}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNc() {
		double[][] M = {
				{0, 0},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNd() {
		double[][] M = {
				{1, 0},
				{0, 0}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNe() {
		double[][] M = {
				{1, 0},
				{-2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNf() {
		double[][] M = {
				{1, -2},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNg() {
		double[][] M = {
				{1, 2},
				{2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2H() {
		double[][] M = {
				{0, -1},
				{2, 0}};
		runTest(M, false);
	}
	
	@Test
	public void testEigensolver2x2I() {
		double[][] M = {
				{4, -1},
				{2, 4}};
		runTest(M, false);
	}
	
	@Test
	public void testEigensolverNxNh() {
		double[][] M = {
				{5, 2, 0},
				{2, 5, 0},
				{-3, 4, 6}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNi() {
		double[][] M = {		// has complex eigenvalues!
				{5, 2, 0, 1},
				{2, 5, 0, 7},
				{-3, 4, 6, 0},
				{1 , 2, 3, 4}};
		runTest(M, false);
	}
	
	@Test
	public void testRandomMatrix2x2() {
		Random RG = new Random(17);
		final int N = 1000;
		//int cnt = 0;
		for (int i = 0; i < N; i++) {
			double[][] A = Eigensolver2x2Test.makeRandomMatrix2x2(RG);
			RealEigensolver solver = new EigensolverNxN(A);
			if (solver.isReal()) {
				//cnt++;
				double[] eigenvals = solver.getEigenvalues();
				for (int k = 0; k < eigenvals.length; k++) {
					double lambda = eigenvals[k];
					double[] x = solver.getEigenvector(k);
					assertArrayEquals(Matrix.multiply(A, x), Matrix.multiply(lambda, x), 1E-6);
				}
			}
		}
		//System.out.println("real solutions: " + cnt + " out of " + N);
	}
	
	// ---------------------------------------------------------
	
	private void runTest(double[][] M) {
		runTest(M, true);
	}

	private void runTest(double[][] M, boolean shouldBeReal) {
		RealEigensolver solver = new EigensolverNxN(M);	

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

}

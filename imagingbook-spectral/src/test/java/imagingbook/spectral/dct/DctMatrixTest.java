/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dct;

import imagingbook.common.math.Matrix;
import imagingbook.testutils.NumericTestUtils;
import org.junit.Test;

// works for square data arrays only
public class DctMatrixTest {
	
	@Test
	public void testFloat() {
		float TOL = 1E-6f;
		float[][] A = makeDctMatrixFloat(4, 4);
		float[][] At = Matrix.transpose(A);
		
		float[][] g = {
				{1,2,3,4},
				{7,2,0,9},
				{6,5,2,5},
				{0,9,8,1}};
		
		// forward DCT by standard transformation methods:
		float[][] G1 = Matrix.duplicate(g);
		Dct2d.Float dct = new Dct2dFast.Float(G1.length, G1[0].length);
		dct.forward(G1);
		
		// forward DCT by matrix multiplication:
		float[][] g2 = Matrix.duplicate(g);
		float[][] G2 = Matrix.multiply(A, Matrix.multiply(g2, At));
		
		NumericTestUtils.assert2dArrayEquals(G1, G2, TOL);
		
		// inverse DCT by standard transformation methods:
		dct.inverse(G1);
		NumericTestUtils.assert2dArrayEquals(G1, g, TOL);
		
		// inverse DCT by matrix multiplication:
		float[][] g2r = Matrix.multiply(At, Matrix.multiply(G2, A));
		NumericTestUtils.assert2dArrayEquals(g2r, g, TOL);
	}
	
	@Test
	public void testDouble() {
		double TOL = 1E-12;
		double[][] A = makeDctMatrixDouble(4, 4);
		double[][] At = Matrix.transpose(A);
		
		double[][] g = {
				{1,2,3,4},
				{7,2,0,9},
				{6,5,2,5},
				{0,9,8,1}};
		
		// forward DCT by standard transformation methods:
		double[][] G1 = Matrix.duplicate(g);
		Dct2d.Double dct = new Dct2dFast.Double(G1.length, G1[0].length);
		dct.forward(G1);
		
		// forward DCT by matrix multiplication:
		double[][] g2 = Matrix.duplicate(g);
		double[][] G2 = Matrix.multiply(A, Matrix.multiply(g2, At));
		
		NumericTestUtils.assert2dArrayEquals(G1, G2, TOL);
		
		// inverse DCT by standard transformation methods:
		dct.inverse(G1);
		NumericTestUtils.assert2dArrayEquals(G1, g, TOL);
		
		// inverse DCT by matrix multiplication:
		double[][] g2r = Matrix.multiply(At, Matrix.multiply(G2, A));
		NumericTestUtils.assert2dArrayEquals(g2r, g, TOL);
	}

	// --------------------------------------------------------------
	
	private float[][] makeDctMatrixFloat(int M, int N) {
		float[][] A = new float[M][N];
		for (int i = 0; i < M; i++) {
			double c_i = (i == 0) ? 1.0 / Math.sqrt(2) : 1;
			for (int j = 0; j < N; j++) {
				A[i][j] = (float) (Math.sqrt(2.0/N) * c_i * Math.cos(Math.PI * (2*j + 1) * i / (2.0 * M)));
			}
		}
		return A;
	}
	
	private double[][] makeDctMatrixDouble(int M, int N) {
		double[][] A = new double[M][N];
		for (int i = 0; i < M; i++) {
			double c_i = (i == 0) ? 1.0 / Math.sqrt(2) : 1;
			for (int j = 0; j < N; j++) {
				A[i][j] = Math.sqrt(2.0/N) * c_i * Math.cos(Math.PI * (2*j + 1) * i / (2.0 * M));
			}
		}
		return A;
	}

}

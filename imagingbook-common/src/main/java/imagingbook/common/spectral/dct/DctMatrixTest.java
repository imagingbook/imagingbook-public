/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.spectral.dct;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class DctMatrixTest {


	/**
	 * Test method.
	 * @param args command arguments
	 */
	public static void main(String[] args) {
		PrintPrecision.set(5);
		float[][] A = makeDctMatrix(4, 4);
		System.out.println("A = " + Matrix.toString(A));
		System.out.println();

		float[][] At = Matrix.transpose(A);
		System.out.println("At = " + Matrix.toString(At));
		System.out.println();
		
		float[][] g = {
				{1,2,3,4},
				{7,2,0,9},
				{6,5,2,5},
				{0,9,8,1}};
		
		System.out.println("---------- original 2D signal -----------");
		System.out.println("g = " + Matrix.toString(g));
		System.out.println();
		
		
		System.out.println("---------- DCT with transformation methods -----------");
		
		float[][] G1 = Matrix.duplicate(g);
		Dct2d.Float dct = new Dct2d.Float();
		dct.forward(G1);
		System.out.println("G1 = " + Matrix.toString(G1));
		System.out.println();
		
		// inverse  DFT with transformation methods
		float[][] g1r = Matrix.duplicate(G1);
		dct.inverse(g1r);
		System.out.println("g1r = " + Matrix.toString(g1r));
		
		System.out.println();
		System.out.println("---------- DCT by matrix multiplication -----------");
		
		float[][] g2 = Matrix.duplicate(g);
		float[][] G2 = Matrix.multiply(A, Matrix.multiply(g2, At));
		System.out.println("G2 = " + Matrix.toString(G2));
		System.out.println();
		
		float[][] g2r = Matrix.multiply(At, Matrix.multiply(G2, A));
		System.out.println("g2r = " + Matrix.toString(g2r));
		
//		float[][] I1 = Matrix.multiply(At, A);
//		System.out.println("I1 = " + Matrix.toString(I1));
//		
//		float[][] I2 = Matrix.multiply(A, At);
//		System.out.println("I2 = " + Matrix.toString(I2));
	}
	
	static float[][] makeDctMatrix(int M, int N) {
		float[][] A = new float[M][N];
		for (int i = 0; i < M; i++) {
			double c_i = (i == 0) ? 1.0 / Math.sqrt(2) : 1;
			for (int j = 0; j < N; j++) {
				A[i][j] = (float)
//						\sqrt{\tfrac{2}{N}} \cdot c_i \cdot 
//						\cos\Bigl(\frac{\pi \cdot (2j + 1) \cdot i}{2M}\Bigr) ,
					(Math.sqrt(2.0/N) * c_i * Math.cos(Math.PI * (2*j + 1) * i / (2.0 * M)) );
			}
		}
		
		return A;
	}

}

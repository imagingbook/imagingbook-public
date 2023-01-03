/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.testutils;

import java.util.Random;

public class RandomMatrixGenerator {
	
	private final Random rg;
	
	public RandomMatrixGenerator(long seed) {
		this.rg = new Random(seed);
	}
	
	public RandomMatrixGenerator() {
		this.rg = new Random();
	}
	
	public RandomMatrixGenerator(Random rg) {
		this.rg = rg;
	}
	
	public double[][] makeRandomMatrix(int rows, int cols, double s) {
		double[][] A = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double x = s * 2 * (rg.nextDouble() - 0.5);
				A[i][j] = x;
			}
		}
		return A;
	}
	
	public double[][] makeRandomMatrix(int rows, int cols) {
		return makeRandomMatrix(rows, cols, 1.0);
	}
	
	/**
	 * Creates and returns a square matrix of size n x n with
	 * random values in [-s, s].
	 * @param n the matrix size
	 * @param s the value scale
	 * @return a random matrix
	 */
	public double[][] makeRandomSquareMatrix(int n, double s) {
		double[][] A = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				double x = s * 2 * (rg.nextDouble() - 0.5);
				A[i][j] = x;
			}
		}
		return A;
	}
	
	/**
	 * Creates and returns a square matrix of size n x n with
	 * random values in [-1, 1].
	 * @param n the matrix size
	 * @return a random matrix
	 */
	public double[][] makeRandomSquareMatrix(int n) {
		return makeRandomSquareMatrix(n, 1.0);
	}
	
	/**
	 * Creates and returns a square symmetric matrix of size n x n with
	 * random values in [-s, s].
	 * @param n the matrix size
	 * @param s the value scale
	 * @return a random matrix
	 */
	public double[][] makeRandomSymmetricMatrix(int n, double s) {
		double[][] A = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j <= i; j++) {
				double x = s * 2 * (rg.nextDouble() - 0.5);
				A[i][j] = x;
				if (i != j) {
					A[j][i] = x;
				}
			}
		}
		return A;
	}
	
	/**
	 * Creates and returns a square matrix symmetric of size n x n with
	 * random values in [-1, 1].
	 * @param n the matrix size
	 * @return a random matrix
	 */
	public double[][] makeRandomSymmetricMatrix(int n) {
		return makeRandomSymmetricMatrix(n, 1.0);
	}
	
	// --------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
////		RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomSymmetricMatrix(3));
//		RealMatrix M = MatrixUtils.createRealMatrix(rg.makeRandomMatrix(3 , 5,  1));
//		System.out.println("M = \n" + Matrix.toString(M));
//		System.out.println("M symmetric = " + MatrixUtils.isSymmetric(M, 1e-6));
//		
//		
//		
//		
//		RealMatrix Z = MatrixUtils.createRealMatrix(new double[][]
//				{{-640324.756071648600000, -13455.063269205853000, -271133.849253710070000, -22199.258754168728000}, 
//			{-13455.063269205852000, 565371.765546445600000, -16613.193426604714000, 58.180548072531145}, 
//			{-271133.849253710070000, -16613.193426604714000, 74939.976000059920000, 1080.126330774859300}, 
//			{-22199.258754168724000, 58.180548072531160, 1080.126330774859000, 13.014525143606523}}
//		);
//		
//		System.out.println("Z symmetric = " + Matrix.isSymmetric(Z, 1e-12));
//		
//	}

}

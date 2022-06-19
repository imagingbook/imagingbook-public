package imagingbook.common.math.testing;

import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

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

}

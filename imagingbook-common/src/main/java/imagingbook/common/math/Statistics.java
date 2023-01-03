/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.math;

//import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

/**
 * This class defines static methods for statistical calculations.
 * 
 * @author WB
 *
 */
public abstract class Statistics {
	
	private Statistics() {}

	/**
	 * Calculates the mean vector for a sequence of sample vectors.
	 * @param samples a 2D array of m-dimensional vectors ({@code }double[n][m]})
	 * @return the mean vector for the sample data (of length m)
	 */
	public static double[] meanVector(double[][] samples) {
		final int n = samples.length;
		final int m = samples[0].length;
		double[] mean = new double[m];
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < m; i++) {
				mean[i] = mean[i] + samples[k][i];
			}
		}
		for (int i = 0; i < m; i++) {
			mean[i] = mean[i] / n;
		}
		return mean;
	}

	/**
	 * Calculates the covariance matrix for a sequence of sample vectors. Takes a sequence of n data samples, each of
	 * dimension m. The data element {@code samples[i][j]} refers to the j-th component of sample i. No statistical bias
	 * correction is applied. Uses {@link Covariance} from Apache Commons Math.
	 *
	 * @param samples a 2D array of m-dimensional vectors ({@code }double[n][m]})
	 * @return the covariance matrix for the sample data (of dimension m x m)
	 * @see Covariance
	 */
	public static double[][] covarianceMatrix(double[][] samples) {
		return covarianceMatrix(samples, false);
	}

	/**
	 * Calculates the covariance matrix for a sequence of sample vectors. Takes a sequence of n data samples, each of
	 * dimension m. The data element {@code samples[i][j]} refers to the j-th component of sample i. Statistical bias
	 * correction is optionally applied. Uses {@link Covariance} from Apache Commons Math.
	 *
	 * @param samples a 2D array of m-dimensional vectors (double[n][m]).
	 * @param biasCorrect if {@code true}, statistical bias correction is applied.
	 * @return the covariance matrix for the sample data (of dimension m x m).
	 * @see Covariance
	 */
	public static double[][] covarianceMatrix(double[][] samples, boolean biasCorrect) {
		Covariance cov = new Covariance(samples, biasCorrect);
		return cov.getCovarianceMatrix().getData();
	}

	/**
	 * Conditions the supplied covariance matrix by enforcing positive eigenvalues.
	 *
	 * @param cov original covariance matrix
	 * @param minDiagVal the minimum positive value of diagonal elements
	 * @return conditioned covariance matrix
	 */
	public static double[][] conditionCovarianceMatrix(double[][] cov, double minDiagVal) {
		RealMatrix C = MatrixUtils.createRealMatrix(cov);
		return conditionCovarianceMatrix(C, minDiagVal).getData();
	}

	/**
	 * Conditions the supplied covariance matrix by enforcing positive eigenvalues.
	 *
	 * @param cov original covariance matrix
	 * @param minDiagVal the minimum positive value of diagonal elements
	 * @return conditioned covariance matrix
	 */
	public static RealMatrix conditionCovarianceMatrix(RealMatrix cov, double minDiagVal) {
		if (!cov.isSquare()) {
			throw new IllegalArgumentException("covariance matrix must be square");
		}
		EigenDecomposition ed = new EigenDecomposition(cov);  // S  ->  V . D . V^T
		RealMatrix V  = ed.getV();
		RealMatrix D  = ed.getD();	// diagonal matrix of eigenvalues
		RealMatrix VT = ed.getVT();
		for (int i = 0; i < D.getRowDimension(); i++) {
			D.setEntry(i, i, Math.max(D.getEntry(i, i), minDiagVal));	// setting eigenvalues to zero is not enough!
		}
		return V.multiply(D).multiply(VT);
	}
	
//	/** 
//	 * example from UTICS-C Appendix:
//	 * N = 4 samples
//	 * K = 3 dimensions
//	 * @param args ignored
//	 */
//	public static void main(String[] args) {
//		
//		boolean BIAS_CORRECT = false;
//		
//		// example: n = 4 samples of dimension m = 3:
//		// samples[i][j], i = column (sample index), j = row (dimension index).
//		double[][] samples = { 
//				{75, 37, 12},	// i = 0
//				{41, 27, 20},	// i = 1
//				{93, 81, 11},	// i = 2
//				{12, 48, 52}	// i = 3
//		};
//		
//		// covariance matrix Cov (3x3)
//		double[][] cov = covarianceMatrix(samples, BIAS_CORRECT);
//		System.out.println("cov = \n" + Matrix.toString(cov));
//		
//		System.out.println();
//		
//		double[][] icov = Matrix.inverse(cov);
//		System.out.println("icov = \n" + Matrix.toString(icov));
//		
//		System.out.println();
//		
//		double trace = MatrixUtils.createRealMatrix(cov).getTrace();
//		System.out.println("trace(cov) = " + trace);
//		double Fnorm = MatrixUtils.createRealMatrix(cov).getFrobeniusNorm();
//		System.out.println("Fnorm(cov) = " + Fnorm);
//	}
	
/* Results (bias-corrected):
cov = 
{{1296.250, 442.583, -627.250}, 
{442.583, 550.250, -70.917}, 
{-627.250, -70.917, 370.917}}

icov = 
{{0.024, -0.014, 0.038}, 
{-0.014, 0.011, -0.022}, 
{0.038, -0.022, 0.063}}
*/
	
/* verified with Mathematica
X1 = {75, 37, 12}; X2 = {41, 27, 20}; X3 = {93, 81, 11}; X4 = {12, 48, 52};
samples = {X1, X2, X3, X4}
N[Covariance[samples]]
-> {{1296.25, 442.583, -627.25}, {442.583, 550.25, -70.9167}, {-627.25, -70.9167, 370.917}}
*/
	
/* Results (NON bias-corrected):
cov = 
{{972.188, 331.938, -470.438}, 
{331.938, 412.688, -53.188}, 
{-470.438, -53.188, 278.188}}

icov = {{0.032, -0.019, 0.051}, 
{-0.019, 0.014, -0.030}, 
{0.051, -0.030, 0.083}}
*/

}

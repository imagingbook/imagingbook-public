/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.math;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

/**
 * <p>
 * This class implements the Mahalanobis distance using the Apache Commons Math library.
 * No statistical bias correction is used.
 * See the Appendix G (Sections G.2-G.3) of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public class MahalanobisDistance { 	//TODO: Check/reuse methods for covariance matrix etc in class 'Statistics'!

	/** The default minimum diagonal value used to condition the covariance matrix. */
	public static final double DefaultMinimumDiagonalValue = 1.0E-15;
	
	/** Used for Cholesky decomposition. 
	 * See {@link CholeskyDecomposition#DEFAULT_RELATIVE_SYMMETRY_THRESHOLD} (1.0E-15), which seems too small). */
	public static final double DefaultRelativeSymmetryThreshold = 1.0E-6;
	
	/** Used for Cholesky decomposition.
	 * See {@link CholeskyDecomposition#DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD}. */
	public static final double DefaultAbsolutePositivityThreshold = CholeskyDecomposition.DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD;
	
	private final double minimumDiagonalValue;
	private final double relativeSymmetryThreshold;
	private final double absolutePositivityThreshold;

	private final int m;					// feature dimension (length of sample vectors)
	private final double[] mean;			// the distribution's mean vector (\mu)
	private final double[][] cov;			// covariance matrix of size n x n
	private final double[][] icov;			// inverse covariance matrix of size n x n

	/**
	 * Create a new instance from an array of m-dimensional samples, e.g.,
	 * samples = {{x1,y1}, {x2,y2},...,{xn,yn}} for a vector of n two-dimensional
	 * samples. Uses {@link #DefaultMinimumDiagonalValue} to condition the 
	 * covariance matrix.
	 * @param samples a sequence of m-dimensional samples, i.e.,
	 *      samples[k][i] represents the i-th component of the k-th sample
	 */
	public MahalanobisDistance(double[][] samples) {
		this(samples, DefaultMinimumDiagonalValue, DefaultRelativeSymmetryThreshold, DefaultAbsolutePositivityThreshold);
	}
	
	/**
	 * Create a new instance from an array of m-dimensional samples, e.g.
	 * samples = {{x1,y1}, {x2,y2},...,{xn,yn}} for a vector of n two-dimensional
	 * samples.
	 * @param samples a vector of length n with m-dimensional samples, i.e.,
	 *      samples[k][i] represents the i-th component of the k-th sample
	 * @param minDiagVal the minimum diagonal value used to condition the
	 *      covariance matrix to avoid singularity (see {@link #DefaultMinimumDiagonalValue})
	 * @param relSymThr
	 * 		maximum deviation from symmetry (see {@link #DefaultRelativeSymmetryThreshold})
	 * @param absPosThr
	 * 		maximum deviation from positivity (see {@link #DefaultAbsolutePositivityThreshold})
	 */
	public MahalanobisDistance(double[][] samples, double minDiagVal, double relSymThr, double absPosThr) {
		if (samples.length < 2)
			throw new IllegalArgumentException("number of samples must be 2 or more");
		if (samples[0].length < 1)
			throw new IllegalArgumentException("sample dimension must be at least 1");

		this.minimumDiagonalValue = minDiagVal;
		this.relativeSymmetryThreshold = relSymThr;
		this.absolutePositivityThreshold = absPosThr;
		
		this.cov = makeCovarianceMatrix(samples, minimumDiagonalValue);
		this.mean = makeMeanVector(samples);
		this.m = mean.length;
		RealMatrix S = MatrixUtils.createRealMatrix(cov);
		// get the inverse covariance matrix
		this.icov = MatrixUtils.inverse(S).getData();	// not always symmetric?
	}

//	/**
//	 * Create a new instance from the m x m covariance matrix and the m-dimensional
//	 * mean vector of a distribution of m-dimensional data. The covariance matrix is 
//	 * assumed to be properly conditioned, i.e., has all-positive diagonal values.
//	 * @param cov the covariance matrix of size m x m
//	 * @param mean the mean vector of length m
//	 */
//	public MahalanobisDistance(double[][] cov, double[] mean) {
//		this.cov = cov;
//		this.mean = mean;
//		this.M = mean.length;
//		RealMatrix S = MatrixUtils.createRealMatrix(cov);
//		// get the inverse covariance matrix
//		this.icov = MatrixUtils.inverse(S).getData();	// not always symmetric?
//	}

	// 
	/**
	 * Conditions the supplied covariance matrix by enforcing
	 * positive eigenvalues on its main diagonal. 
	 * @param S original covariance matrix
	 * @param minDiagVal the minimum positive value of diagonal elements
	 * @return conditioned covariance matrix
	 */
	private static RealMatrix conditionCovarianceMatrix(RealMatrix S, double minDiagVal) {
		EigenDecomposition ed = new EigenDecomposition(S);  // S  ->  V . D . V^T
		RealMatrix V  = ed.getV();
		RealMatrix D  = ed.getD();	// diagonal matrix of eigenvalues
		RealMatrix VT = ed.getVT();
		for (int i = 0; i < D.getRowDimension(); i++) {
			D.setEntry(i, i, Math.max(D.getEntry(i, i), minDiagVal));	// setting eigenvalues to zero is not enough!
		}
		return V.multiply(D).multiply(VT);
	}

	// --------------------------------------------------------------

	private static double[] makeMeanVector(double[][] samples) {
		int n = samples.length;
		int m = samples[0].length;
		double[] mu = new double[m];
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < m; i++) {
				mu[i] = mu[i] + samples[k][i];
			}
		}
		for (int i = 0; i < m; i++) {
			mu[i] = mu[i] / n;
		}
		return mu;
	}

	/**
	 * Calculates the covariance matrix for a sequence of m-dimensional samples, e.g.,
	 * samples = {{x1,y1}, {x2,y2},...,{xn,yn}} for n two-dimensional
	 * samples. The covariance matrix is conditioned to avoid negative or zero
	 * values on its main diagonal.
	 * @param samples a sequence of m-dimensional samples, i.e.,
	 *      samples[k][i] represents the i-th component of the k-th sample
	 * @param minDiagVal the minimum positive value of covariance matrix diagonal elements
	 * 		(see {@link #DefaultMinimumDiagonalValue})
	 * 
	 * @return the covariance matrix for the supplied samples
	 */
	public static double[][] makeCovarianceMatrix(double[][] samples, double minDiagVal) {
		if (samples.length < 2)
			throw new IllegalArgumentException("number of samples must be 2 or more");
		if (samples[0].length < 1)
			throw new IllegalArgumentException("sample dimension must be at least 1");
		RealMatrix S = new Covariance(samples, false).getCovarianceMatrix();	// no bias correction!
		// condition the covariance matrix to avoid singularity:
		S = conditionCovarianceMatrix(S, minDiagVal);
		return S.getData();
	}

	/**
	 * Returns the sample dimension (M) for this instance.
	 * @return the sample dimension
	 */
	public int getDimension() {
		return m;
	}

	//------------------------------------------------------------------------------------

	/**
	 * Returns the covariance matrix used for distance calculations.
	 * @return the conditioned covariance matrix
	 */
	public double[][] getCovarianceMatrix() {
		return cov;
	}

	/**
	 * Returns the inverse of the conditioned covariance matrix.
	 * @return the inverse of the conditioned covariance matrix
	 */
	public double[][] getInverseCovarianceMatrix() {
		return icov;
	}


	public double[] getMeanVector() {
		return mean;
	}

	//------------------------------------------------------------------------------------

	/**
	 * Returns the 'root' (U) of the inverse covariance matrix S^{-1},
	 * such that S^{-1} = U^T . U
	 * This matrix can be used to pre-transform the original sample
	 * vectors X (by X &#8594; U . X) to a space where distance (in the Mahalanobis
	 * sense) can be measured with the usual Euclidean norm.
	 * The matrix U is invertible in case the reverse transformation is required.
	 * 
	 * @return the m x m matrix for pre-transforming the original (m-dimensional) sample vectors
	 */
	public double[][] getWhiteningTransformation() {
		CholeskyDecomposition cd = 
				new CholeskyDecomposition(MatrixUtils.createRealMatrix(icov),
						relativeSymmetryThreshold, absolutePositivityThreshold);
		RealMatrix U = cd.getLT();
		return U.getData();
	}
	

	//------------------------------------------------------------------------------------

	/**
	 * Returns the Mahalanobis distance between the given points.
	 * @param a first point
	 * @param b second point
	 * @return the Mahalanobis distance
	 */
	public double distance(double[] a, double[] b) {
		return Math.sqrt(distance2(a, b));
	}

	/**
	 * Returns the squared Mahalanobis distance between the given points a, b:
	 * d^2(a,b) = (a-b)^T . S^{-1} . (a-b)
	 * @param a first point
	 * @param b second point
	 * @return the squared Mahalanobis distance
	 */
	public double distance2(double[] a, double[] b) {
		if (a.length != m || b.length != m) {
			throw new IllegalArgumentException("vectors a, b must be of length " + m);
		}	
		double[] amb = Matrix.subtract(a, b);	// amb = a - b
		return Matrix.dotProduct(Matrix.multiply(amb, icov), amb);
		
	}
	
	// original version
//	public double distance2(double[] X, double[] Y) {
//		if (X.length != m || Y.length != m) {
//			throw new IllegalArgumentException("vectors must be of length " + m);
//		}
//		// d^2(X,Y) = (X-Y)^T . S^{-1} . (X-Y)
//		double[] dXY = new double[m]; // = (X-Y)
//		for (int k = 0; k < m; k++) {
//			dXY[k] = X[k] - Y[k];
//		}
//		double[] iSdXY = new double[m]; // = S^{-1} . (X-Y)
//		for (int k = 0; k < m; k++) {
//			for (int i = 0; i < m; i++) {
//				iSdXY[k] += icov[i][k] * dXY[i];
//			}
//		}
//		double d2 = 0;	// final sum
//		for (int k = 0; k < m; k++) {
//			d2 += dXY[k] * iSdXY[k];
//		}						// d = (X-Y)^T . S^{-1} . (X-Y)
//		return d2;
//	}

	/**
	 * Calculates the Mahalanobis distance between point x and
	 * the mean of the reference distribution.
	 * @param x an arbitrary m-dimensional vector
	 * @return the Mahalanobis distance between the point x and
	 * 			the mean of the reference distribution
	 */
	public double distance(double[] x) {
		return distance(x, mean);
	}

	/**
	 * Calculates the sqared Mahalanobis distance between point x and
	 * the mean of the reference distribution.
	 * @param x an arbitrary m-dimensional vector
	 * @return the squared Mahalanobis distance between the point x and
	 * 			the mean of the reference distribution
	 */
	public double distance2(double[] x) {
		return distance2(x, mean);
	}

}
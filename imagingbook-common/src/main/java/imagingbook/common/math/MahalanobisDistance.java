/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.math;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import static imagingbook.common.math.Statistics.conditionCovarianceMatrix;
import static imagingbook.common.math.Statistics.covarianceMatrix;

/**
 * <p>
 * This class implements the Mahalanobis distance using the Apache Commons Math library. No statistical bias correction
 * is used. See the Appendix G (Sections G.2-G.3) of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2023/01/01
 */
public class MahalanobisDistance {

	/**
	 * The default minimum diagonal value used to condition the covariance matrix.
	 */
	public static final double DefaultMinimumDiagonalValue = 1.0E-15;

	/**
	 * Tolerance used for validating symmetry in the Cholesky decomposition. See
	 * {@link CholeskyDecomposition#DEFAULT_RELATIVE_SYMMETRY_THRESHOLD} (1.0E-15), which seems too small).
	 */
	public static final double DefaultRelativeSymmetryThreshold = 1.0E-6;

	/**
	 * Tolerance used for validating positvity in the Cholesky decomposition. See
	 * {@link CholeskyDecomposition#DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD}.
	 */
	public static final double DefaultAbsolutePositivityThreshold =
			CholeskyDecomposition.DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD;

	private final int m;				// feature dimension (length of sample vectors)
	private final double[][] cov;		// covariance matrix of size m x m
	private final double[][] icov;		// inverse covariance matrix of size m x m

	/**
	 * Constructor, creates a new {@link MahalanobisDistance} instance from the m x m covariance matrix and the
	 * m-dimensional mean vector of a distribution of m-dimensional data. The covariance matrix is assumed to be
	 * properly conditioned, i.e., has all-positive diagonal values.
	 *
	 * @param cov the covariance matrix of size m x m
	 */
	public MahalanobisDistance(double[][] cov) {
		this.m = cov.length;
		if (!Matrix.isSquare(cov)) {
			throw new IllegalArgumentException("covariance matrix must be square");
		}
		if (!Matrix.isSymmetric(cov)) {
			throw new IllegalArgumentException("covariance matrix must be symmetric");
		}
		this.cov = cov;
		this.icov = Matrix.inverse(cov);
	}

	/**
	 * Creates a new {@link MahalanobisDistance} instance from an array of m-dimensional samples, e.g., samples = {{x1,y1},
	 * {x2,y2},...,{xn,yn}} for a vector of n two-dimensional samples. Uses {@link #DefaultMinimumDiagonalValue} to
	 * condition the covariance matrix.
	 *
	 * @param samples a sequence of m-dimensional samples, i.e., samples[k][i] represents the i-th component of the k-th
	 * sample
	 * @return a {@link MahalanobisDistance} instance
	 */
	public static MahalanobisDistance fromSamples(double[][] samples) {
		return fromSamples(samples, DefaultMinimumDiagonalValue);
	}

	/**
	 * Creates a new {@link MahalanobisDistance} instance from an array of m-dimensional samples, e.g. samples =
	 * {{x1,y1}, {x2,y2},...,{xn,yn}} for a vector of two-dimensional samples (n = 2).
	 *
	 * @param samples a vector of length n with m-dimensional samples, i.e., samples[k][i] represents the i-th component
	 * of the k-th sample
	 * @param minDiagVal the minimum diagonal value used to condition the covariance matrix to avoid singularity (see
	 * {@link #DefaultMinimumDiagonalValue})
	 * @return a {@link MahalanobisDistance} instance
	 */
	public static MahalanobisDistance fromSamples(double[][] samples, double minDiagVal) {
		if (samples.length < 2)
			throw new IllegalArgumentException("number of samples must be 2 or more");
		if (samples[0].length < 1)
			throw new IllegalArgumentException("sample dimension must be at least 1");
		double[][] cov = covarianceMatrix(samples);
		return new MahalanobisDistance(conditionCovarianceMatrix(cov, minDiagVal));
	}

	//------------------------------------------------------------------------------------

	/**
	 * Returns the sample dimension (M) for this instance.
	 * @return the sample dimension
	 */
	public int getDimension() {
		return m;
	}

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

	//------------------------------------------------------------------------------------

	/**
	 * Returns the 'root' (U) of the inverse covariance matrix S^{-1}, such that S^{-1} = U^T . U This matrix can be
	 * used to pre-transform the original sample vectors X (by X &#8594; U . X) to a space where distance (in the
	 * Mahalanobis sense) can be measured with the usual Euclidean norm. The matrix U is invertible in case the reverse
	 * transformation is required.
	 *
	 * @return the m x m matrix for pre-transforming the original (m-dimensional) sample vectors
	 */
	public double[][] getWhiteningTransformation() {
		return getWhiteningTransformation(DefaultRelativeSymmetryThreshold, DefaultAbsolutePositivityThreshold);
	}

	/**
	 * Returns the 'root' (U) of the inverse covariance matrix S^{-1}, such that S^{-1} = U^T . U This matrix can be *
	 * used to pre-transform the original sample vectors X (by X &#8594; U . X) to a space where distance (in the *
	 * Mahalanobis sense) can be measured with the usual Euclidean norm. The matrix U is invertible in case the reverse
	 * * transformation is required.
	 *
	 * @param relativeSymmetryThreshold maximum deviation from symmetry (see {@link #DefaultRelativeSymmetryThreshold})
	 * @param absolutePositivityThreshold maximum deviation from positivity (see {@link #DefaultAbsolutePositivityThreshold})
	 * @return
	 */
	public double[][] getWhiteningTransformation(double relativeSymmetryThreshold, double absolutePositivityThreshold) {
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
	 * Returns the squared Mahalanobis distance between the given points a, b: d^2(a,b) = (a-b)^T . S^{-1} . (a-b)
	 *
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

}
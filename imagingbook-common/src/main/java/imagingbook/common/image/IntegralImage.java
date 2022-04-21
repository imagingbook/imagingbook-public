/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image;

import ij.process.ByteProcessor;


/**
 * This class represents an 'integral image' or 'summed area table' [Crow, 1984], 
 * as described in the book (see 2nd English ed. 2016, Sec. 3.8).
 * Currently only implemented for images of type {@link ByteProcessor}.
 * 
 * @author W. Burger
 * @version 2015/11/15
 *
 */
public class IntegralImage {
	
	private final int M, N;	
	private final long[][] S1, S2;
	
	/**
	 * Creates a new integral image from pixel values in a 2D int-array.
	 * @param I pixel values
	 */
	public IntegralImage(int[][] I) {
		M = I.length;			// image width
		N = I[0].length;		// image height
		S1 = new long[M][N];
		S2 = new long[M][N];
		
		// initialize top-left corner (0,0)
		S1[0][0] = I[0][0];
		S2[0][0] = I[0][0] * I[0][0];
		// do line v = 0:
		for (int u = 1; u < M; u++) {
			S1[u][0] = S1[u-1][0] + I[u][0];
			S2[u][0] = S2[u-1][0] + I[u][0] * I[u][0];
		}
		// do lines v = 1,...,h-1
		for (int v = 1; v < N; v++) {
			S1[0][v] = S1[0][v-1] + I[0][v];
			S2[0][v] = S2[0][v-1] + I[0][v] * I[0][v];
			for (int u = 1; u < M; u++) {
				S1[u][v] = S1[u-1][v] + S1[u][v-1] - S1[u-1][v-1] + I[u][v];
				S2[u][v] = S2[u-1][v] + S2[u][v-1] - S2[u-1][v-1] + I[u][v] * I[u][v];
			}
		}
	}
	
	/**
	 * Creates a new integral image from pixel values in a {@link ByteProcessor}.
	 * @param I input image
	 */
	public IntegralImage(ByteProcessor I) {
		this(I.getIntArray());
	}
	
	// -------------------------------------------------------
	
	/**
	 * Returns the summed area table of pixel values (Sigma_1).
	 * @return Array of Sigma_1 values
	 */
	public long[][] getS1() {
		return S1;
	}
	
	/**
	 * Returns the summed area table of squared pixel values (Sigma_2).
	 * @return Array of Sigma_2 values
	 */
	public long[][] getS2() {
		return S2;
	}
	
	// -------------------------------------------------------
	
	/**
	 * Calculates the sum of the pixel values in the rectangle
	 * R, specified by the corner points a = (ua, va) and b = (b1, vb).
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R
	 * @param vb bottom position in R
	 * @return the first-order block sum (S1(R)) inside the specified rectangle 
	 * or zero if the rectangle is empty.
	 */
	public long getBlockSum1(int ua, int va, int ub, int vb) {
		if (ub < ua || vb < va) 
			return 0;
		final long saa = (ua > 0  && va > 0)  ? S1[ua - 1][va - 1] : 0;
		final long sba = (ub >= 0 && va > 0)  ? S1[ub][va - 1] : 0;
		final long sab = (ua > 0  && vb >= 0) ? S1[ua - 1][vb] : 0;
		final long sbb = (ub >= 0 && vb >= 0) ? S1[ub][vb] : 0;
		return sbb + saa - sba - sab;
	}
	
	/**
	 * Calculates the sum of the squared pixel values in the rectangle
	 * R, specified by the corner points a = (ua, va) and b = (b1, vb).
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R
	 * @param vb bottom position in R
	 * @return the second-order block sum (S2(R)) inside the specified rectangle
	 * or zero if the rectangle is empty.
	 */
	public long getBlockSum2(int ua, int va, int ub, int vb) {
		if (ub < ua || vb < va) 
			return 0;
		final long saa = (ua > 0  && va > 0)  ? S2[ua - 1][va - 1] : 0;
		final long sba = (ub >= 0 && va > 0)  ? S2[ub][va - 1] : 0;
		final long sab = (ua > 0  && vb >= 0) ? S2[ua - 1][vb] : 0;
		final long sbb = (ub >= 0 && vb >= 0) ? S2[ub][vb] : 0;
		return sbb + saa - sba - sab;
	}
	
	public int getSize(int u0, int v0, int u1, int v1) {
		return (1 + u1 - u0) * (1 + v1 - v0);
	}
	
	/**
	 * Calculates the mean of the image values in the specified rectangle.
	 * 
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R {@literal (u1 >= u0)}
	 * @param vb bottom position in R {@literal (v1 >= v0)}
	 * @return the mean value for the specified rectangle
	 */
	public double getMean(int ua, int va, int ub, int vb) {
		int N = getSize(ua, va, ub, vb);
		if (N <= 0)
			throw new IllegalArgumentException("region size must be positive");
		double S1 = getBlockSum1(ua, va, ub, vb);
//		IJ.log("u0 = " + ua); IJ.log("v0 = " + va);
//		IJ.log("u1 = " + ub); IJ.log("v1 = " + vb);
//		IJ.log("S1 = " + S1);
		return S1 / N;
	}
	
	/**
	 * Calculates the variance of the image values in the specified rectangle.
	 * parameters.
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R {@literal (u1 >= u0)}
	 * @param vb bottom position in R {@literal (v1 >= v0)}
	 * @return the variance for the specified rectangle
	 */
	public double getVariance(int ua, int va, int ub, int vb) {
		int N = getSize(ua, va, ub, vb);
		if (N <= 0)
			throw new IllegalArgumentException("region size must be positive");
		double S1 = getBlockSum1(ua, va, ub, vb);
		double S2 = getBlockSum2(ua, va, ub, vb);
		return (S2 - (S1 * S1) / N) / N;
	}

}

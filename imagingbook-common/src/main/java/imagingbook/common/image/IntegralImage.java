/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image;

import ij.process.ByteProcessor;


/**
 * <p>
 * This class represents an 'integral image' or 'summed area table' as proposed in [1], 
 * See Sec. 2.8 of [2] for a detailed description.
 * Currently only implemented for images of type {@link ByteProcessor}.
 * </p>
 * <p>
 * [1] F. C. Crow. Summed-area tables for texture mapping. SIGGRAPH, Computer Graphics 18(3), 207–212 (1984).<br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/07/11
 *
 */
public class IntegralImage {
	
	private final int M, N;	
	private final long[][] S1, S2;
	
	/**
	 * Constructor. Creates a new {@link IntegralImage} instance from pixel values 
	 * in a 2D int-array I[x][y].
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
	 * Constructor. Creates a new {@link IntegralImage} instance from pixel values in the 
	 * specified {@link ByteProcessor}.
	 * @param I input image
	 */
	public IntegralImage(ByteProcessor I) {
		this(I.getIntArray());
	}
	
	// -------------------------------------------------------
	
	/**
	 * Returns the summed area table of pixel values (Sigma_1).
	 * @return array of Sigma_1 values
	 */
	public long[][] getS1() {
		return S1;
	}
	
	/**
	 * Returns the summed area table of squared pixel values (Sigma_2).
	 * @return array of Sigma_2 values
	 */
	public long[][] getS2() {
		return S2;
	}
	
	// -------------------------------------------------------
	
	/**
	 * Calculates the sum of the pixel values in the rectangle
	 * R, specified by the corner points a = (ua, va) and b = (ub, vb).
	 * TODO: allow coordinates outside image boundaries
	 * 
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R
	 * @param vb bottom position in R
	 * @return the first-order block sum (S1(R)) inside the specified rectangle 
	 * or zero if the rectangle is empty.
	 * 
	 */
	public long getBlockSum1(int ua, int va, int ub, int vb) {
		if (ub < ua || vb < va) {
			return 0;
		}
		final long saa = (ua > 0  && va > 0)  ? S1[ua - 1][va - 1] : 0;
		final long sba = (ub >= 0 && va > 0)  ? S1[ub][va - 1] : 0;
		final long sab = (ua > 0  && vb >= 0) ? S1[ua - 1][vb] : 0;
		final long sbb = (ub >= 0 && vb >= 0) ? S1[ub][vb] : 0;
		return sbb + saa - sba - sab;
	}
	
	/**
	 * Calculates the sum of the squared pixel values in the rectangle
	 * R, specified by the corner points a = (ua, va) and b = (ub, vb).
	 * TODO: allow coordinates outside image boundaries
	 * 
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R
	 * @param vb bottom position in R
	 * @return the second-order block sum (S2(R)) inside the specified rectangle
	 * or zero if the rectangle is empty.
	 */
	public long getBlockSum2(int ua, int va, int ub, int vb) {
		if (ub < ua || vb < va) {
			return 0;
		}
		final long saa = (ua > 0  && va > 0)  ? S2[ua - 1][va - 1] : 0;
		final long sba = (ub >= 0 && va > 0)  ? S2[ub][va - 1] : 0;
		final long sab = (ua > 0  && vb >= 0) ? S2[ua - 1][vb] : 0;
		final long sbb = (ub >= 0 && vb >= 0) ? S2[ub][vb] : 0;
		return sbb + saa - sba - sab;
	}
	
	/**
	 * Returns the size of (number of pixels in) the specified rectangle.
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R {@literal (ub >= ua)}
	 * @param vb bottom position in R {@literal (vb >= va)}
	 * @return the size of the specified rectangle
	 */
	public int getSize(int ua, int va, int ub, int vb) {
		return (ub - ua + 1) * (vb - va + 1);
	}
	
	/**
	 * Calculates the mean of the image values in the specified rectangle.
	 * 
	 * @param ua leftmost position in R
	 * @param va top position in R
	 * @param ub rightmost position in R {@literal (ub >= ua)}
	 * @param vb bottom position in R {@literal (vb >= va)}
	 * @return the mean value for the specified rectangle
	 */
	public double getMean(int ua, int va, int ub, int vb) {
		int size = getSize(ua, va, ub, vb);
		if (size <= 0) {
			throw new IllegalArgumentException("region size must be positive");
		}
		return getBlockSum1(ua, va, ub, vb) / size;
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
		int size = getSize(ua, va, ub, vb);
		if (size <= 0) {
			throw new IllegalArgumentException("region size must be positive");
		}
		double s1 = getBlockSum1(ua, va, ub, vb);
		double s2 = getBlockSum2(ua, va, ub, vb);
		return (s2 - (s1 * s1) / size) / size;
	}

}

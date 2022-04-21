/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.morphology;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.Arrays;

/**
 * This class implements binary morphological filters.
 * 
 * @author W. Burger
 * @version 2022/01/24 
 */
public abstract class BinaryMorphologyFilter implements BinaryMorphologyOperator {
	
	protected final byte[][] H; // structuring element (used by implementing classes)
	
	// ---- constructors --------------------------------------------------------

	public BinaryMorphologyFilter() {
		H = makeBoxKernel3x3();
	}
	
	public BinaryMorphologyFilter(byte[][] H) {
		this.H = H;
	}

	public BinaryMorphologyFilter(int[][] H) {
		this(toByteArray(H));
	}
	
	// --------------------------------------------------------------------------
	
	public byte[][] getStructuringElement() {
		return this.H;
	}
	
	// ------------------------------------------------------------------

	public static byte[][] makeBoxKernel3x3() {
		return makeBoxKernel(1);
	}

	public static byte[][] makeBoxKernel(int radius) {
		int r = (int) Math.rint(radius);
		if (r <= 1)
			r = 1;
		int size = r + r + 1;
		byte[][] H = new byte[size][size];
		for (int v = 0; v < H.length; v++) {
			for (int u = 0; u < H[v].length; u++) {
				H[v][u] = 1;
			}
		}
		return H;
	}
	
	public static byte[][] makeDiskKernel(double radius){
		int r = (int) Math.rint(radius);	// size of kernel (use ceil?)
		if (r < 1) r = 1;
		int N = r + r + 1;
		byte[][] kernel = new byte[N][N];
		double r2 = sqr(radius);
		for (int v = -r; v <= r; v++) {
			for (int u = -r; u <= r; u++) {
				if (sqr(u) + sqr(v) <= r2)
					kernel[v + r][u + r] = 1;
			}
		}
		return kernel;
	}
	
	// ----------------------------------------------------------------
	
	public static byte[][] toByteArray(int[][] iA) {
		byte[][] bA = new byte[iA.length][];
		for (int i = 0; i < iA.length; i++) {
			bA[i] = new byte[iA[i].length];
			for (int j = 0; j < iA[i].length; j++) {
				bA[i][j] = (byte) iA[i][j];
			}
		}		
		return bA;
	}
	
	public static byte[][] reflect(byte[][] H) {
		// mirrors (transposes) the structuring element around the center (hot spot)
		// used to implement erosion by a dilation
		final int N = H.length; 		// number of rows
		final int M = H[0].length; 	// number of columns
		byte[][] rH = new byte[N][M];
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < M; i++) {
				rH[j][i] = H[N - j - 1][M - i - 1];
			}
		}
		return rH;
	}
	
//	@Deprecated
//	public void showStructuringElement() {
//		show(H, "Structuring Element");
//	}

//	public static void show(byte[][] matrix, String title) {
//		int w = matrix[0].length;
//		int h = matrix.length;
//		ImageProcessor ip = new ByteProcessor(w, h);
//		for (int v = 0; v < h; v++) {
//			for (int u = 0; u < w; u++) {
//				if (matrix[v][u] == 1)
//					ip.putPixel(u, v, 255);
//				else
//					ip.putPixel(u, v, 0);
//			}
//		}
//		ip.invertLut();
//		ImagePlus im = new ImagePlus(title, ip);
//		
//		im.show();
//	}
	
	// -------------------------------------------------------------
	
	private static int[][] iA = {
			{0, 0, 0, 3, 0, 0, 3, 3, 0, 0, 0, 0, 3, 0, 3, 3}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 3, 1}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 0}, 
			{3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 3, 1}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
			{}, 
			{3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
			{3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 1, 0}, 
		};
	
	public static void main(String[] args) {
		byte[][] bA = toByteArray(iA);
		for (int i = 0; i < bA.length; i++) {
			System.out.println(Arrays.toString(bA[i]));
		}
	}
}


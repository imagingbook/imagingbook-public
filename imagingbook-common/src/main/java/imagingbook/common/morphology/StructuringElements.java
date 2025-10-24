/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.morphology;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * This class defines static methods related to binary structuring elements (kernels).
 *
 * @author WB
 */
public abstract class StructuringElements {
	
	private StructuringElements() {}

	/**
	 * Creates and returns a square binary structuring element (kernel) of size 3x3 (radius 1).
	 *
	 * @return a 3x3 binary box kernel
	 */
	public static byte[][] makeBoxKernel3x3() {
		return makeBoxKernel(1);
	}

	/**
	 * Creates and returns a square binary "box" kernel with the specified radius. The kernel size is (2 * radius + 1) x
	 * (2 * radius + 1). It is always odd.
	 *
	 * @param radius the kernel radius
	 * @return a square binary box kernel
	 */
	public static byte[][] makeBoxKernel(int radius) {
		if (radius < 0) {
			throw new IllegalArgumentException("radius must be >= 0");
		}
		int n = radius + radius + 1;
		byte[][] H = new byte[n][n];
		for (int v = 0; v < H.length; v++) {
			for (int u = 0; u < H[v].length; u++) {
				H[v][u] = 1;
			}
		}
		return H;
	}

	// TODO: compare to CircularMask (filters), define BinaryKernel class?

	/**
	 * Creates and returns a square binary "disk" kernel with the specified radius. The kernel size is (2 * radius + 1)
	 * x (2 * radius + 1). It is always odd.
	 *
	 * @param radius the kernel radius
	 * @return a square binary disk kernel
	 */
	public static byte[][] makeDiskKernel(double radius) {
		if (radius < 0) {
			throw new IllegalArgumentException("radius must be >= 0");
		}
		int r = (int) Math.floor(radius);
		int n = r + r + 1;
		byte[][] kernel = new byte[n][n];
		double r2 = sqr(radius) + 0.5;
		for (int v = -r; v <= r; v++) {
			for (int u = -r; u <= r; u++) {
				if (sqr(u) + sqr(v) <= r2)
					kernel[v + r][u + r] = 1;
			}
		}
		return kernel;
	}

	/**
	 * Converts the specified {@code int[][]} to a {@code byte[][]}, which is returned.
	 *
	 * @param iA the {@code int[][]} array to be converted
	 * @return the resulting {@code byte[][]}
	 */
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

	/**
	 * Returns a copy of the specified structuring element that is mirrored along both axes. For example
	 * <pre>
	 * | A B |
	 * | C D |</pre>
	 * converts to
	 * <pre>
	 * | D C |
	 * | B A |</pre>
	 *
	 * @param H the original structuring element
	 * @return the reflected structuring element
	 */
	public static byte[][] reflect(byte[][] H) {
		// mirrors (transposes) the structuring element around the center (hot spot)
		// used to implement erosion by a dilation
		final int n = H.length; 		// number of rows
		final int m = H[0].length; 		// number of columns
		byte[][] rH = new byte[n][m];
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < m; i++) {
				rH[j][i] = H[n - j - 1][m - i - 1];
			}
		}
		return rH;
	}

}

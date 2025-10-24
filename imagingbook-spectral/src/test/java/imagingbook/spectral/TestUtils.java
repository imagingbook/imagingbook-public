/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral;

import java.util.Random;

public abstract class TestUtils {

	public static float[] makeRandomVectorFloat(int n, Random rg) {
		float[] a = new float[n];
		for (int i = 0; i < n; i++) {
			a[i] = 2 * rg.nextFloat() - 1;
		}
		return a;
	}

	public static double[] makeRandomVectorDouble(int n, Random rg) {
		double[] a = new double[n];
		for (int i = 0; i < n; i++) {
			a[i] = 2 * rg.nextDouble() - 1;
		}
		return a;
	}
	
	// --------------------------------------------------------------------

	public static float[][] makeRandomArrayFloat(int n0, int n1, Random rg) {
		float[][] a = new float[n0][n1];
		for (int i = 0; i < n0; i++) {
			for (int j = 0; j < n1; j++) {
				a[i][j] = 2 * rg.nextFloat() - 1;
			}
		}
		return a;
	}

	public static double[][] makeRandomArrayDouble(int n0, int n1, Random rg) {
		double[][] a = new double[n0][n1];
		for (int i = 0; i < n0; i++) {
			for (int j = 0; j < n1; j++) {
				a[i][j] = 2 * rg.nextDouble() - 1;
			}
		}
		return a;
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.testutils;

import static org.junit.Assert.assertEquals;


import org.junit.Assert;

public abstract class NumericTestUtils {
	
	private NumericTestUtils() {}
	
	public static float TOLERANCE = 1E-6f;
	
	// utility methods for comparing 2D arrays ---------------------------------------------------------------
	
	public static void assertArrayEquals(double[][] expecteds, double[][] actuals) {
		assertArrayEquals(expecteds, actuals, TOLERANCE);
	}

	public static void assertArrayEquals(double[][] expecteds, double[][] actuals, double delta) {
		Assert.assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			Assert.assertArrayEquals(expecteds[i], actuals[i], delta);
		}
	}
	
	public static void assertArrayEquals(float[][] expecteds, float[][] actuals) {
		assertArrayEquals(expecteds, actuals, TOLERANCE);
	}

	public static void assertArrayEquals(float[][] expecteds, float[][] actuals, double delta) {
		Assert.assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			Assert.assertArrayEquals(expecteds[i], actuals[i], (float) delta);
		}
	}
	
	// comparison by relative magnitude -------------------------------------------------------
	
	public static void assertEqualsRelative(double expected, double actual, double relDelta) {
		double maxAbs = Math.max(Math.abs(expected), Math.abs(actual));
		// if magnitude of both values is less than relDelta we don't check any more:
		if (maxAbs < relDelta)
			return;
		double delta = maxAbs * relDelta;
//		System.out.format(Locale.US, "x=%f y=%f delta=%f\n", expected, actual, delta);
		assertEquals(null, expected, actual, delta);
	}
        
	
	public static void assertArrayEqualsRelative(double[] expecteds, double[] actuals, double relDelta) {
		Assert.assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			assertEqualsRelative(expecteds[i], actuals[i], relDelta);
		}
	}

	
//	public static void main(String[] args) {
//		assertEqualsRelative(1.0000000000, 1.0000000001, 1e-6);
//		assertEqualsRelative(100000000, 100000001, 1e-6);
//		
//		double[] a = {1.0000000000, 2, 3, 1000000};
//		double[] b = {1.0000000001, 2, 3, 1000001};
//		assertArrayEqualsRelative(a, b, 1e-6);
//	}
}

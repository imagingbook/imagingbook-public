/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.testutils;

import org.junit.Assert;

public abstract class ArrayTests {
	
	public static float TOLERANCE = 1E-6f;
	
	// utility methods ---------------------------------------------------------------
	
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

	

}

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

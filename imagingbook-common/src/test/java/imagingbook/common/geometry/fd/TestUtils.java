package imagingbook.common.geometry.fd;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

import imagingbook.common.math.Complex;

public abstract class TestUtils {

	
	public static void assertArrayEquals(Complex[] expecteds, Complex[] actuals, double tol) {
		Assert.assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			Complex c1 = expecteds[i];
			Complex c2 = actuals[i];
			assertEquals(c1.re, c2.re, tol);
			assertEquals(c1.im, c2.im, tol);
		}
	}

}

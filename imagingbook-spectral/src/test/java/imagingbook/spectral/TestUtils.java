package imagingbook.spectral;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Assert;

import imagingbook.common.math.Complex;

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
	
	// ---------------------------------------------------------------------
	
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

package imagingbook.spectral.dft;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

public class Dft1dFastTest {

	@Test
	public void testFloat1() {
		float TOL = 1E-6f;
		float[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
		float[] im = new float[re.length];
		runTestFloat(re, im, TOL);
	}
	
	@Test
	public void testDouble1() {
		double TOL = 1E-12;
		double[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
		double[] im = new double[re.length];
		runTestDouble(re, im, TOL);
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testFloat2() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 1;
		for (int i = 0; i < 100; i++) {
			float[] re = makeRandomvectorFloat(n, rg);
			float[] im = makeRandomvectorFloat(n, rg);
			runTestFloat(re, im, TOL);
			n++;
		}
	}

	@Test
	public void testDouble2() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 1;
		for (int i = 0; i < 100; i++) {
			double[] re = makeRandomvectorDouble(n, rg);
			double[] im = makeRandomvectorDouble(n, rg);
			runTestDouble(re, im, TOL);
			n++;
		}
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testFloat3() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 10177;
		float[] re = makeRandomvectorFloat(n, rg);
		float[] im = makeRandomvectorFloat(n, rg);
		runTestFloat(re, im, TOL);
	}
	
	@Test
	public void testDouble3() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 10177;
		double[] re = makeRandomvectorDouble(n, rg);
		double[] im = makeRandomvectorDouble(n, rg);
		runTestDouble(re, im, TOL);
	}
	
	// ---------------------------------------------------------
	
	private void runTestFloat(float[] reOrig, float[] imOrig, float TOL) {
		float[] re = reOrig.clone();
		float[] im = imOrig.clone();
		Dft1d.Float dft = new Dft1dFast.Float(re.length);
		dft.forward(re, im);
		dft.inverse(re, im);		
		assertArrayEquals(reOrig, re, TOL);
		assertArrayEquals(imOrig, im, TOL);
	}
	
	private void runTestDouble(double[] reOrig, double[] imOrig, double TOL) {
		double[] re = reOrig.clone();
		double[] im = imOrig.clone();
		Dft1d.Double dft = new Dft1dFast.Double(re.length);		
		dft.forward(re, im);
		dft.inverse(re, im);
		assertArrayEquals(reOrig, re, TOL);
		assertArrayEquals(imOrig, im, TOL);
	}
	
	// ---------------------------------------------------------
	
	private float[] makeRandomvectorFloat(int n, Random rg) {
		float[] a = new float[n];
		for (int i = 0; i < n; i++) {
			a[i] = 2 * rg.nextFloat() - 1;
		}
		return a;
	}
	
	private double[] makeRandomvectorDouble(int n, Random rg) {
		double[] a = new double[n];
		for (int i = 0; i < n; i++) {
			a[i] = 2 * rg.nextDouble() - 1;
		}
		return a;
	}
	
	
}

package imagingbook.spectral.dct;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import imagingbook.spectral.TestUtils;

public class Dct1dSlowTest {

	@Test
	public void test1() {
		double TOL = 1e-6;
		double[] dataOrig = {1,2,3,4,5,3,0};
		double[] data = dataOrig.clone();
		double[] Dct = {6.803361, -0.360627, -3.727944, 1.692069, -0.888121, -0.082772, 0.167211};

		Dct1d.Double dct = new Dct1dSlow(data.length);
		dct.forward(data);
		assertArrayEquals(Dct, data, TOL);

		dct.inverse(data);
		assertArrayEquals(dataOrig, data, TOL);
	}
	
	@Test
	public void test2() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 1;
		for (int i = 0; i < 100; i++) {
			double[] data = TestUtils.makeRandomVectorDouble(n, rg);
			runTestDouble(data, TOL);
			n++;
		}
	}
	
	@Test
	public void test3() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 1177;
		double[] data = TestUtils.makeRandomVectorDouble(n, rg);
		runTestDouble(data, TOL);
	}
	
	// -----------------------------------------------------------------
	
	private void runTestDouble(double[] dataOrig, double TOL) {
		double[] data = dataOrig.clone();
		Dct1d.Double dct = new Dct1dSlow(data.length);
		dct.forward(data);
		dct.inverse(data);
		assertArrayEquals(dataOrig, data, TOL);
	}

}

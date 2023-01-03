/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dct;

import imagingbook.spectral.TestUtils;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class Dct1dFastTest {

	@Test
	public void test1Double() {
		double TOL = 1e-6;
		double[] dataOrig = {1,2,3,4,5,3,0};
		double[] data = dataOrig.clone();
		double[] Dct = {6.803361, -0.360627, -3.727944, 1.692069, -0.888121, -0.082772, 0.167211};

		Dct1d.Double dct = new Dct1dFast.Double(data.length);
		dct.forward(data);
		assertArrayEquals(Dct, data, TOL);

		dct.inverse(data);
		assertArrayEquals(dataOrig, data, TOL);
	}
	
	@Test
	public void test1Float() {
		float TOL = 1e-6f;
		float[] dataOrig = {1,2,3,4,5,3,0};
		float[] data = dataOrig.clone();
		float[] Dct = {6.803361f, -0.360627f, -3.727944f, 1.692069f, -0.888121f, -0.082772f, 0.167211f};

		Dct1d.Float dct = new Dct1dFast.Float(data.length);
		dct.forward(data);
		assertArrayEquals(Dct, data, TOL);

		dct.inverse(data);
		assertArrayEquals(dataOrig, data, TOL);
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testFloat2() {
		Random rg = new Random(17);
		float TOL = 1E-4f;		// reduced accuracy!
		int n = 1;
		for (int i = 0; i < 100; i++) {
			float[] re = TestUtils.makeRandomVectorFloat(n, rg);
			runTestFloat(re, TOL);
			n++;
		}
	}

	@Test
	public void testDouble2() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 1;
		for (int i = 0; i < 100; i++) {
			double[] re = TestUtils.makeRandomVectorDouble(n, rg);
			runTestDouble(re, TOL);
			n++;
		}
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testFloat3() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 10177;
		float[] re = TestUtils.makeRandomVectorFloat(n, rg);
		runTestFloat(re, TOL);
	}
	
	@Test
	public void testDouble3() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 10177;
		double[] re = TestUtils.makeRandomVectorDouble(n, rg);
		runTestDouble(re, TOL);
	}
	
	// -----------------------------------------------------------------
	
	private void runTestFloat(float[] dataOrig, float TOL) {
		float[] data = dataOrig.clone();
		Dct1d.Float dct = new Dct1dFast.Float(data.length);
		dct.forward(data);
		dct.inverse(data);
		assertArrayEquals(dataOrig, data, TOL);
	}

	private void runTestDouble(double[] dataOrig, double TOL) {
		double[] data = dataOrig.clone();
		Dct1d.Double dct = new Dct1dFast.Double(data.length);
		dct.forward(data);
		dct.inverse(data);
		assertArrayEquals(dataOrig, data, TOL);
	}

}

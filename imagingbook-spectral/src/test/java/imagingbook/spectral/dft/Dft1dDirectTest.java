/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;

import imagingbook.spectral.TestUtils;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class Dft1dDirectTest {

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
			float[] re = TestUtils.makeRandomVectorFloat(n, rg);
			float[] im = TestUtils.makeRandomVectorFloat(n, rg);
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
			double[] re = TestUtils.makeRandomVectorDouble(n, rg);
			double[] im = TestUtils.makeRandomVectorDouble(n, rg);
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
		float[] re = TestUtils.makeRandomVectorFloat(n, rg);
		float[] im = TestUtils.makeRandomVectorFloat(n, rg);
		runTestFloat(re, im, TOL);
	}
	
	@Test
	public void testDouble3() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 10177;
		double[] re = TestUtils.makeRandomVectorDouble(n, rg);
		double[] im = TestUtils.makeRandomVectorDouble(n, rg);
		runTestDouble(re, im, TOL);
	}
	
	// ---------------------------------------------------------
	
	private void runTestFloat(float[] reOrig, float[] imOrig, float TOL) {
		float[] re = reOrig.clone();
		float[] im = imOrig.clone();
		Dft1d.Float dft = new Dft1dDirect.Float(re.length);
		dft.forward(re, im);
		dft.inverse(re, im);		
		assertArrayEquals(reOrig, re, TOL);
		assertArrayEquals(imOrig, im, TOL);
	}
	
	private void runTestDouble(double[] reOrig, double[] imOrig, double TOL) {
		double[] re = reOrig.clone();
		double[] im = imOrig.clone();
		Dft1d.Double dft = new Dft1dDirect.Double(re.length);		
		dft.forward(re, im);
		dft.inverse(re, im);
		assertArrayEquals(reOrig, re, TOL);
		assertArrayEquals(imOrig, im, TOL);
	}
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;


import imagingbook.common.math.Matrix;
import imagingbook.spectral.TestUtils;
import imagingbook.testutils.NumericTestUtils;
import org.junit.Test;

import java.util.Random;

public class Dft2dDirectTest {
	
	@Test
	public void testFloat1() {
		float TOL = 1E-6f;
		float[][] re =  { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { -2, -1, 0 } };
		float[][] im = new float[re.length][re[0].length];
		runTestFloat(re, im, TOL);
	}
	
	@Test
	public void testDouble1() {
		double TOL = 1E-12;
		double[][] re =  { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { -2, -1, 0 } };
		double[][] im = new double[re.length][re[0].length];
		runTestDouble(re, im, TOL);
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testFloat2() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
			float[][] re = TestUtils.makeRandomArrayFloat(width, height, rg);
			float[][] im = TestUtils.makeRandomArrayFloat(width, height, rg);
			runTestFloat(re, im, TOL);
			width++;
			height--;
		}
	}
	
	@Test
	public void testDouble2() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
			double[][] re = TestUtils.makeRandomArrayDouble(width, height, rg);
			double[][] im = TestUtils.makeRandomArrayDouble(width, height, rg);
			runTestDouble(re, im, TOL);
			width++;
			height--;
		}
	}
	
	// --------------------------------------------------------------
	
	private void runTestFloat(float[][] reOrig, float[][] imOrig, float TOL) {
		int w = reOrig.length;
		int h = reOrig[0].length;
		float[][] re = Matrix.duplicate(reOrig);
		float[][] im = Matrix.duplicate(imOrig);
		Dft2d.Float dft = new Dft2dDirect.Float(w, h);
		dft.forward(re, im);
		dft.inverse(re, im);		
		NumericTestUtils.assert2dArrayEquals(reOrig, re, TOL);
		NumericTestUtils.assert2dArrayEquals(imOrig, im, TOL);
	}
	
	private void runTestDouble(double[][] reOrig, double[][] imOrig, double TOL) {
		int w = reOrig.length;
		int h = reOrig[0].length;
		double[][] re = Matrix.duplicate(reOrig);
		double[][] im = Matrix.duplicate(imOrig);
		Dft2d.Double dft = new Dft2dDirect.Double(w, h);
		dft.forward(re, im);
		dft.inverse(re, im);		
		NumericTestUtils.assert2dArrayEquals(reOrig, re, TOL);
		NumericTestUtils.assert2dArrayEquals(imOrig, im, TOL);
	}

}

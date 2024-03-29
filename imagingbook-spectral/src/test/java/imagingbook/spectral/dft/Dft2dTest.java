/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;

import imagingbook.common.math.Matrix;
import imagingbook.spectral.TestUtils;
import imagingbook.testutils.NumericTestUtils;
import org.junit.Test;

import java.util.Random;

public class Dft2dTest {
	
	@Test	// compares 2D DFT obtained width direct and fast methods (float)
	public void testDirectFastFloat() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width + " h = " + height);
			float[][] reOrig = TestUtils.makeRandomArrayFloat(width, height, rg);
			float[][] imOrig = TestUtils.makeRandomArrayFloat(width, height, rg);
			
			// direct DFT:
			float[][] reD = Matrix.duplicate(reOrig);
			float[][] imD = Matrix.duplicate(imOrig);
			Dft2d.Float dftD = new Dft2dDirect.Float(width, height);
			dftD.forward(reD, imD);
			
			// fast DFT (FFT):
			float[][] reF = Matrix.duplicate(reOrig);
			float[][] imF = Matrix.duplicate(imOrig);
			Dft2d.Float dftF = new Dft2dFast.Float(width, height);
			dftF.forward(reF, imF);

			NumericTestUtils.assert2dArrayEquals(reD, reF, TOL);
			NumericTestUtils.assert2dArrayEquals(imD, imF, TOL);
			
			width++;
			height--;
		}
	}
	
	@Test	// compares 2D DFT obtained width direct and fast methods (double)
	public void testDirectFastDouble() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width + " h = " + height);
			double[][] reOrig = TestUtils.makeRandomArrayDouble(width, height, rg);
			double[][] imOrig = TestUtils.makeRandomArrayDouble(width, height, rg);
			
			// direct DFT:
			double[][] reD = Matrix.duplicate(reOrig);
			double[][] imD = Matrix.duplicate(imOrig);
			Dft2d.Double dftD = new Dft2dDirect.Double(width, height);
			dftD.forward(reD, imD);
			
			// fast DFT (FFT):
			double[][] reF = Matrix.duplicate(reOrig);
			double[][] imF = Matrix.duplicate(imOrig);
			Dft2d.Double dftF = new Dft2dFast.Double(width, height);
			dftF.forward(reF, imF);

			NumericTestUtils.assert2dArrayEquals(reD, reF, TOL);
			NumericTestUtils.assert2dArrayEquals(imD, imF, TOL);
			
			width++;
			height--;
		}
	}
	
}

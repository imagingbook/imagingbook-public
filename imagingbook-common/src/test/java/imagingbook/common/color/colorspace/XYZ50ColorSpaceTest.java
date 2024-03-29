/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.color.cie.StandardIlluminant;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class XYZ50ColorSpaceTest {

	static ColorSpace sRGB_CS = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	static XYZ50ColorSpace CS = XYZ50ColorSpace.getInstance();
	static float TOL = 1e-5f;

	@Test
	public void test1() {
		doCheck(CS, new int[] {0, 0, 0});
		doCheck(CS, new int[] {255, 255, 255});
		doCheck(CS, new int[] {177, 0, 0});
		doCheck(CS, new int[] {0, 177, 0});
		doCheck(CS, new int[] {0, 0, 177});
		doCheck(CS, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(CS, new int[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations (takes LONG!)
//	public void test3() {
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(CS, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	@Test
	public void testBlack() { // check black point
		float[] srgbPCS = {0, 0, 0};
		float[] xyzTHIS = CS.fromRGB(srgbPCS);
		assertArrayEquals(new float[] {0, 0, 0}, xyzTHIS, 1e-6f);
	}
	
	@Test
	public void testWhite() {	// RGB white should map to D50 white point (Y = 1)
		float[] rgb = {1, 1, 1};
		float[] xyzTHIS = CS.fromRGB(rgb);

		// PrintPrecision.set(16);
		//System.out.println("xyzTHIS = " + Matrix.toString(xyzTHIS));
		// {0.9641662836074829, 1.0000028610229492, 0.8247155547142029}

		float[] xyzIll = Matrix.toFloat(StandardIlluminant.D50.getXYZ());
		//System.out.println("xyzIll = " + Matrix.toString(xyzIll));
		// {0.9642000198364258, 1.0000000000000000, 0.8248999714851379}

		assertArrayEquals(xyzIll, xyzTHIS, 1e-3f);
	}
	
	@Test
	public void testGray() {  // RGB gray must map do D50 xy-chromaticity
		final double[] xy50 = StandardIlluminant.D50.getXy(); // {0.3457, 0.3585};
		for (int c = 1; c < 256; c++) {
			float[] rgb = {c, c, c};
			float[] xyzTHIS = CS.fromRGB(rgb);
			double[] xy = CieUtils.XYZToxy(Matrix.toDouble(xyzTHIS));
			assertArrayEquals(xy50, xy, 1e-4f);
		}
	}
	
	// ---------------------

	private static void doCheck(ColorSpace cs, int[] sRGB) {
		float[] srgbIN = RgbUtils.normalize(sRGB);

		{	// check fromCIEXYZ / toCIEXYZ
			float[] xyzIN = sRGB_CS.toCIEXYZ(srgbIN);
			float[] rgbCS = cs.fromCIEXYZ(xyzIN);
			float[] xyzOUT = cs.toCIEXYZ(rgbCS);
			assertArrayEquals(xyzIN, xyzOUT, TOL);
		}

		{	// check fromRGB / toRGB
			float[] rgbCS = cs.fromRGB(srgbIN);
			float[] srgbOUT = cs.toRGB(rgbCS);
			assertArrayEquals(srgbIN, srgbOUT, TOL);
		}
	}

}

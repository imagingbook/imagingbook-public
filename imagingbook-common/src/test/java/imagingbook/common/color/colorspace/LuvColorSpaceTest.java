/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import static imagingbook.common.color.cie.StandardIlluminant.D65;
import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import org.junit.Test;

import imagingbook.common.color.RgbUtils;

public class LuvColorSpaceTest {	//TODO: adapt to Luv test!

	@Test
	public void test1a() {
		LuvColorSpace cs = LuvColorSpace.getInstance();
		checkSrgbToLuv(cs, new int[] {0, 0, 0});
		checkSrgbToLuv(cs, new int[] {255, 255, 255});
		checkSrgbToLuv(cs, new int[] {177, 0, 0});
		checkSrgbToLuv(cs, new int[] {0, 177, 0});
		checkSrgbToLuv(cs, new int[] {0, 0, 177});
		checkSrgbToLuv(cs, new int[] {19, 3, 174});
	}

	@Test
	public void test1b() {
		LuvColorSpace cs = LuvColorSpace.getInstance();
		checkXyzToLuv(cs, new int[] {0, 0, 0});
		checkXyzToLuv(cs, new int[] {255, 255, 255});
		checkXyzToLuv(cs, new int[] {177, 0, 0});
		checkXyzToLuv(cs, new int[] {0, 177, 0});
		checkXyzToLuv(cs, new int[] {0, 0, 177});
		checkXyzToLuv(cs, new int[] {19, 3, 174});
	}

	@Test
	public void test2() {
		LuvColorSpace cs = LuvColorSpace.getInstance();
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			checkSrgbToLuv(cs, new int[] {r, g, b});
			checkXyzToLuv(cs, new int[] {r, g, b});
		}
	}



//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		LuvColorSpace cs = LuvColorSpace.getInstance();
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck65(cs, new int[] {r, g, b});
//					doCheck50(cs, new int[] {r, g, b});
//				}
//			}
//		}
//	}

	@Test
	public void testBlack() { // check black point
		LuvColorSpace cs = LuvColorSpace.getInstance();
		float[] rgb = {0, 0, 0};
		float[] xyz = cs.fromRGB(rgb);
		assertArrayEquals(new float[] {0, 0, 0}, xyz, 1e-6f);
	}

	@Test
	public void testWhiteXYZ() { //sRGB white in this color space must map to (100, 0, 0) in Luv
		PrintPrecision.set(6);
		LuvColorSpace cs = LuvColorSpace.getInstance();
		//float[] rgb = {1, 1, 1};
		float[] W65 =  Matrix.toFloat(D65.getXYZ());
		float[] wLuv = cs.fromCIEXYZ65(W65);
//		System.out.println("wD65 = " + Matrix.toString(W65));
//		System.out.println("wLuv = " + Matrix.toString(wLuv));
		assertArrayEquals(new float[] {100, 0, 0}, wLuv, 1e-4f);
	}

	@Test
	public void testWhiteRGB() { //sRGB white in this color space must map to D50-XYZ in PCS
		PrintPrecision.set(6);
		LuvColorSpace cs = LuvColorSpace.getInstance();
		float[] rgb = {1, 1, 1};
//		double[] W65 = StandardIlluminant.D65.getXYZ();
		float[] wLuv = cs.fromRGB(rgb);
//		System.out.println("wD65 = " + Matrix.toString(W65));
//		System.out.println("wLuv = " + Matrix.toString(wLuv));
		assertArrayEquals(new float[] {100, 0, 0}, wLuv, 0.1f); // inaccuracies due to float conversions? NO!
	}

	@Test
	public void testBookTableValues() {	// check colors in book Table 14.4
		PrintPrecision.set(4);
		LuvColorSpace cs = LuvColorSpace.getInstance();
		// original (book) values
		// checkLuvValues(cs, 0.00, 0.00, 0.00,   0.00,    0.00,    0.00);
		// checkLuvValues(cs, 1.00, 0.00, 0.00,  53.24,  175.01,   37.75);
		// checkLuvValues(cs, 1.00, 1.00, 0.00,  97.14,  7.70,  106.80);		// was 106.78
		// checkLuvValues(cs, 0.00, 1.00, 0.00,  87.74,  -83.08,  107.41);		// was 107.39
		// checkLuvValues(cs, 0.00, 1.00, 1.00,  91.11,   -70.48,  -15.20);
		// checkLuvValues(cs, 0.00, 0.00, 1.00,  32.30,   -9.40, -130.34);
		// checkLuvValues(cs, 1.00, 0.00, 1.00,  60.32,   84.07, -108.68);
		checkLuvValues(cs, 1.00, 1.00, 1.00,  100.00,   0.00,   0.02);		// was 0.00
		// checkLuvValues(cs, 0.50, 0.50, 0.50,  53.39,   0.00,    0.00);
		// checkLuvValues(cs, 0.75, 0.00, 0.00,  39.77,   130.73,   28.20);
		// checkLuvValues(cs, 0.50, 0.00, 0.00,  25.42,   83.56,   18.02);
		// checkLuvValues(cs, 0.25, 0.00, 0.00,   9.66,   31.74,    6.85);
		// checkLuvValues(cs, 1.00, 0.50, 0.50,  68.11,   92.15,   19.88);
	}

	// --------------------------------------------------------

	// check sRGB to Luv conversion and back
	private static void checkSrgbToLuv(LuvColorSpace lcs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] luv = lcs.fromRGB(srgb1);
		float[] srgb2 = lcs.toRGB(luv);
		assertArrayEquals(srgb1, srgb2, 1e-5f);
	}

	// check XYZ to Luv conversion and back (using standard D50-based conversion space)
	private static void checkXyzToLuv(LuvColorSpace lcs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		{	// D50
			float[] xyz50a = XYZ50ColorSpace.getInstance().fromRGB(srgb1);	// D50-based XYZ space
			float[] luv = lcs.fromCIEXYZ(xyz50a);
			float[] xyz50b = lcs.toCIEXYZ(luv);
			assertArrayEquals(xyz50a, xyz50b, 1e-5f);
		}
		{	// D65
			float[] xyz65a = XYZ65ColorSpace.getInstance().fromRGB(srgb1);	// D50-based XYZ space
			float[] luv = lcs.fromCIEXYZ65(xyz65a);
			float[] xyz65b = lcs.toCIEXYZ65(luv);
			assertArrayEquals(xyz65a, xyz65b, 1e-5f);
		}
	}

	// RGB are sRGB values
	private static void checkLuvValues(LuvColorSpace lcs, double R, double G, double B, double L, double a, double b) {
		float[] srgb1 = new float[] {(float)R, (float)G, (float)B};
		System.out.println("sRGB=" + Matrix.toString(srgb1));
		float[] luv = lcs.fromRGB(srgb1);
		System.out.println("luv=" + Matrix.toString(luv));
		assertArrayEquals(new float[] {(float)L, (float)a, (float)b}, luv, 0.02f);	// not very accurate!!
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.PrintPrecision;

public class LabColorSpaceTest {

	@Test
	public void test1a() {
		LabColorSpace cs = LabColorSpace.getInstance();
		checkSrgbToLab(cs, new int[] {0, 0, 0});
		checkSrgbToLab(cs, new int[] {255, 255, 255});
		checkSrgbToLab(cs, new int[] {177, 0, 0});
		checkSrgbToLab(cs, new int[] {0, 177, 0});
		checkSrgbToLab(cs, new int[] {0, 0, 177});
		checkSrgbToLab(cs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test1b() {
		LabColorSpace cs = LabColorSpace.getInstance();
		checkXyzToLab(cs, new int[] {0, 0, 0});
		checkXyzToLab(cs, new int[] {255, 255, 255});
		checkXyzToLab(cs, new int[] {177, 0, 0});
		checkXyzToLab(cs, new int[] {0, 177, 0});
		checkXyzToLab(cs, new int[] {0, 0, 177});
		checkXyzToLab(cs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		LabColorSpace cs = LabColorSpace.getInstance();
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			checkSrgbToLab(cs, new int[] {r, g, b});
			checkXyzToLab(cs, new int[] {r, g, b});
		}
	}
	
	
	
//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		LabColorSpace cs = LabColorSpace.getInstance();
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
		LabColorSpace cs = LabColorSpace.getInstance();
		double[] rgb = {0, 0, 0};
		double[] xyz = cs.fromRGB(rgb);
		assertArrayEquals(new double[] {0, 0, 0}, xyz, 1e-6f);
	}
	
	@Test
	public void testWhiteXYZ() { //sRGB white in this color space must map to (100, 0, 0) in Lab
		PrintPrecision.set(6);
		LabColorSpace cs = LabColorSpace.getInstance();
		//float[] rgb = {1, 1, 1};
		double[] W65 = StandardIlluminant.D65.getXYZ();
		double[] wLab = cs.fromCIEXYZ65(W65);
//		System.out.println("wD65 = " + Matrix.toString(W65));
//		System.out.println("wLab = " + Matrix.toString(wLab));
		assertArrayEquals(new double[] {100, 0, 0}, wLab, 1e-6f);
	}
	
	@Test
	public void testWhiteRGB() { //sRGB white in this color space must map to D50-XYZ in PCS
		PrintPrecision.set(6);
		LabColorSpace cs = LabColorSpace.getInstance();
		double[] rgb = {1, 1, 1};
		double[] W65 = StandardIlluminant.D65.getXYZ();
		double[] wLab = cs.fromRGB(rgb);
//		System.out.println("wD65 = " + Matrix.toString(W65));
//		System.out.println("wLab = " + Matrix.toString(wLab));
		assertArrayEquals(new double[] {100, 0, 0}, wLab, 1e-1f); // inaccuracies due to float conversions? NO!
	}
	
	@Test
	public void testBookTableValues() {	// check colors in book Table 14.3
		PrintPrecision.set(4);
		LabColorSpace cs = LabColorSpace.getInstance();
		// original (book) values
		checkLabValues(cs, 0.00, 0.00, 0.00,   0.00,    0.00,    0.00);
		checkLabValues(cs, 1.00, 0.00, 0.00,  53.24,   80.09,   67.20);
		checkLabValues(cs, 1.00, 1.00, 0.00,  97.14,   -21.55 ,  94.48);
		checkLabValues(cs, 0.00, 1.00, 0.00,  87.74,   -86.18 ,  83.18);
		checkLabValues(cs, 0.00, 1.00, 1.00,  91.11,   -48.09 , -14.12);	// was -14.13
		checkLabValues(cs, 0.00, 0.00, 1.00,  32.30,   79.19,  -107.85);	// was -107.86
		checkLabValues(cs, 1.00, 0.00, 1.00,  60.32,   98.23,  -60.81);		// was 98.24, -60.83
		checkLabValues(cs, 1.00, 1.00, 1.00,  100.00,   0.00,   0.00);
		checkLabValues(cs, 0.50, 0.50, 0.50,  53.39,   0.00,    0.00);
		checkLabValues(cs, 0.75, 0.00, 0.00,  39.77,   64.51,   54.13);
		checkLabValues(cs, 0.50, 0.00, 0.00,  25.42,   47.91,   37.91);
		checkLabValues(cs, 0.25, 0.00, 0.00,   9.66,   29.68,   15.24);
		checkLabValues(cs, 1.00, 0.50, 0.50,  68.11,   48.39,   22.84);		//  was 22.83
	}
	
	// --------------------------------------------------------

	// check sRGB to Lab conversion and back
	private static void checkSrgbToLab(LabColorSpace lcs, int[] srgb) {
		double[] srgb1 = RgbUtils.normalizeD(srgb);
		double[] lab = lcs.fromRGB(srgb1);
		double[] srgb2 = lcs.toRGB(lab);
		assertArrayEquals(srgb1, srgb2, 1e-5f);
	}
	
	// check XYZ to Lab conversion and back (using standard D50-based conversion space)
	private static void checkXyzToLab(LabColorSpace lcs, int[] srgb) {
		double[] srgb1 = RgbUtils.normalizeD(srgb);
		double[] xyz50a = XYZ50ColorSpace.getInstance().fromRGB(srgb1);	// D50-based XYZ space
		double[] lab = lcs.fromCIEXYZ(xyz50a);
		double[] xyz50b = lcs.toCIEXYZ(lab);
		assertArrayEquals(xyz50a, xyz50b, 1e-5f);
	}
	
	// RGB are sRGB values
	private static void checkLabValues(LabColorSpace lcs, double R, double G, double B, double L, double a, double b) {
		double[] srgb1 = new double[] {R, G, B};
		double[] lab = lcs.fromRGB(srgb1);
//		System.out.println(Matrix.toString(lab));
		assertArrayEquals(new double[] {L, a, b}, lab, 0.02);	// quite inaccurate, check book values!!
	}
}

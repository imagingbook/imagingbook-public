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

import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;

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
	public void test4() {	// check colors in book Table 14.3
		LabColorSpace cs = LabColorSpace.getInstance();
		checkLabValues(cs, 0.00, 0.00, 0.00,   0.0000,  0.0000,  0.0000);
		checkLabValues(cs, 1.00, 0.00, 0.00,  53.2406, 80.0942, 67.2017);
		checkLabValues(cs, 1.00, 1.00, 0.00,  97.1395, -21.5523, 94.4756);
		checkLabValues(cs, 0.00, 1.00, 0.00,  87.7351, -86.1812, 83.1773);
		checkLabValues(cs, 0.00, 1.00, 1.00,  91.1133, -48.0886, -14.1311);
		checkLabValues(cs, 0.00, 0.00, 1.00,  32.2956, 79.1870, -107.8618);
		checkLabValues(cs, 1.00, 0.00, 1.00,  60.3235, 98.2352, -60.8255);
		checkLabValues(cs, 1.00, 1.00, 1.00,  100.0000,  0.0000, -0.0001);
		checkLabValues(cs, 0.50, 0.50, 0.50,  53.3889,  0.0000, -0.0000);
		checkLabValues(cs, 0.75, 0.00, 0.00,  39.7693, 64.5113, 54.1272);
		checkLabValues(cs, 0.50, 0.00, 0.00,  25.4184, 47.9108, 37.9053);
		checkLabValues(cs, 0.25, 0.00, 0.00,   9.6566, 29.6783, 15.2422);
		checkLabValues(cs, 1.00, 0.50, 0.50,  68.1084, 48.3895, 22.8325);
	}
	
	// --------------------------------------------------------

	// check sRGB to Lab conversion and back
	private static void checkSrgbToLab(LabColorSpace lcs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] lab = lcs.fromRGB(srgb1);
		float[] srgb2 = lcs.toRGB(lab);
		assertArrayEquals("lab65 conversion problem for srgb=" + Arrays.toString(srgb), srgb1, srgb2, 1e-5f);
	}
	
	// check XYZ to Lab conversion and back (using standard D50-based conversion space)
	private static void checkXyzToLab(LabColorSpace lcs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] xyz50a = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ).fromRGB(srgb1);	// standard D50-based XYZ space
		float[] lab = lcs.fromCIEXYZ(xyz50a);
		float[] xyz50b = lcs.toCIEXYZ(lab);
		assertArrayEquals("lab50 conversion problem for srgb=" + Arrays.toString(srgb), xyz50a, xyz50b, 1e-5f);
	}
	
	// RGB are sRGB values
	private static void checkLabValues(LabColorSpace lcs, double R, double G, double B, double L, double a, double b) {
		float[] srgb1 = new float[] {(float)R, (float)G, (float)B};
		float[] lab = lcs.fromRGB(srgb1);
		assertArrayEquals(lab, new float[] {(float)L, (float)a, (float)b}, 1e-4f);
	}
}

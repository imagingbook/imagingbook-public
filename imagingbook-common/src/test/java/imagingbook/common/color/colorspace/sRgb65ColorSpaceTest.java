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

public class sRgb65ColorSpaceTest {
	
	static float TOL = 1e-3f;

	@Test
	public void test1() {
		sRgb65ColorSpace cs = sRgb65ColorSpace.getInstance();
		doCheck(cs, new int[] {0, 0, 0});
		doCheck(cs, new int[] {255, 255, 255});
		doCheck(cs, new int[] {177, 0, 0});
		doCheck(cs, new int[] {0, 177, 0});
		doCheck(cs, new int[] {0, 0, 177});
		doCheck(cs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		sRgb65ColorSpace cs = sRgb65ColorSpace.getInstance();
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(cs, new int[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		sRgb65ColorSpace cs = new sRgb65ColorSpace();
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(cs, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	// ---------------------------------------------------
	
	private static void doCheck(ColorSpace cs, int[] srgb65) {
		{
			float[] srgbA = RgbUtils.normalize(srgb65);
			float[] xyz50 = cs.toCIEXYZ(srgbA);
			float[] srgbB = cs.fromCIEXYZ(xyz50);
			assertArrayEquals(Arrays.toString(srgb65), srgbA, srgbB, TOL);
		}
//		{
//			float[] srgbA = RgbUtils.normalize(srgb65);
//			float[] rgb50 = cs.toRGB(srgbA);
//			float[] srgbB = cs.fromRGB(rgb50);
//			assertArrayEquals(srgbA, srgbB, TOL);
//		}
	}
	

}

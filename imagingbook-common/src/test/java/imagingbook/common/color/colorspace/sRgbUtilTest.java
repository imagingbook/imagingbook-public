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
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class sRgbUtilTest {
	
	@Test
	public void test0() {
		for (int i = 0; i < 1000; i++) {
			double lc1 = (double) i / 1000;
			double nlc = sRgbUtil.gammaFwd(lc1);
			double lc2 = sRgbUtil.gammaInv(nlc);
			assertEquals("wrong gamma for lc = " + lc1, lc1, lc2, 1e-15);
		}		
	}

	@Test
	public void test1() {
		doCheck(new float[] {0, 0, 0});
		doCheck(new float[] {255, 255, 255});
		doCheck(new float[] {177, 0, 0});
		doCheck(new float[] {0, 177, 0});
		doCheck(new float[] {0, 0, 177});
		doCheck(new float[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(new float[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(new float[] {r, g, b});
//				}
//			}
//		}
//	}
	
	
	private static void doCheck(float[] srgb1) {
		{	// non-destructive
			float[] rgb   = sRgbUtil.sRgbToRgb(srgb1);
			float[] srgb2 = sRgbUtil.rgbToSrgb(rgb);
			assertArrayEquals(srgb1, srgb2, 1e-9f);
		}
		{	// destructive
			float[] rgb   = srgb1.clone();
			sRgbUtil.sRgbToRgbD(rgb);
			sRgbUtil.rgbToSrgbD(rgb);
			assertArrayEquals(srgb1, rgb, 1e-9f);
		}
	}

}

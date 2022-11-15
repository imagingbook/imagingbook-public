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

public class HlsColorSpaceTest {

	@Test
	public void test1() {
		HlsColorSpace hlsC = HlsColorSpace.getInstance();
		doCheck(hlsC, new int[] {0, 0, 0});
		doCheck(hlsC, new int[] {255, 255, 255});
		doCheck(hlsC, new int[] {177, 0, 0});
		doCheck(hlsC, new int[] {0, 177, 0});
		doCheck(hlsC, new int[] {0, 0, 177});
		doCheck(hlsC, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		HlsColorSpace hlsC = HlsColorSpace.getInstance();
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(hlsC, new int[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		HlsColorSpace hlsC = HlsColorSpace.getInstance();
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(hlsC, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	private static void doCheck(HlsColorSpace lcs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] lab = lcs.fromRGB(srgb1);
		float[] srgb2 = lcs.toRGB(lab);
		assertArrayEquals(srgb1, srgb2, 1e-5f);
	}


}

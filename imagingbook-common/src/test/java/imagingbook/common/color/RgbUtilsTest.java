/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class RgbUtilsTest {

	@Test
	public void testDecodeEncode() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			int[] RGB = new int[] {r, g, b};
			checkEncodeDecode(RGB);
		}
	}

	@Test
	public void testNormalize1() {
		checkNormalization(new int[] {0, 0, 0});
		checkNormalization(new int[] {255, 255, 255});
		checkNormalization(new int[] {177, 0, 0});
		checkNormalization(new int[] {0, 177, 0});
		checkNormalization(new int[] {0, 0, 177});
		checkNormalization(new int[] {19, 3, 174});
	}
	
	@Test
	public void testNormalize2() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			checkNormalization(new int[] {r, g, b});
		}
	}


//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(null, new int[] {r, g, b});
//				}
//			}
//		}
//	}

	private static void checkEncodeDecode(int[] RGB1) {
		int c = RgbUtils.rgbToInt(RGB1);
		int[] RGB2 = RgbUtils.intToRgb(c);
		assertArrayEquals("RgbUtils conversion problem for RGB=" + Arrays.toString(RGB1), RGB1, RGB2);
	}

	private static void checkNormalization(int[] RGB1) {
		float[] srgb = RgbUtils.normalize(RGB1);
		int[] RGB2 = RgbUtils.denormalize(srgb);
		assertArrayEquals("RgbUtils conversion problem for RGB=" + Arrays.toString(RGB1), RGB1, RGB2);
	}


}

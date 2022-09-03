/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.colorspace;

import static org.junit.Assert.*;

import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class LuvColorSpaceTest {

	@Test
	public void test1a() {
		LuvColorSpace lcs = LuvColorSpace.getInstance();
		doCheck65(lcs, new int[] {0, 0, 0});
		doCheck65(lcs, new int[] {255, 255, 255});
		doCheck65(lcs, new int[] {177, 0, 0});
		doCheck65(lcs, new int[] {0, 177, 0});
		doCheck65(lcs, new int[] {0, 0, 177});
		doCheck65(lcs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test1b() {
		LuvColorSpace lcs = LuvColorSpace.getInstance();
		doCheck50(lcs, new int[] {0, 0, 0});
		doCheck50(lcs, new int[] {255, 255, 255});
		doCheck50(lcs, new int[] {177, 0, 0});
		doCheck50(lcs, new int[] {0, 177, 0});
		doCheck50(lcs, new int[] {0, 0, 177});
		doCheck50(lcs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		LuvColorSpace lcs = LuvColorSpace.getInstance();
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck65(lcs, new int[] {r, g, b});
			doCheck50(lcs, new int[] {r, g, b});
		}
	}
	
	private static void doCheck65(LuvColorSpace lcs, int[] srgb) {
		float[] srgb1 = {srgb[0]/255f, srgb[1]/255f, srgb[2]/255f};
		float[] lab = lcs.fromRGB(srgb1);
		float[] srgb2 = lcs.toRGB(lab);
		assertArrayEquals("lab65 conversion problem for srgb=" + Arrays.toString(srgb), srgb1, srgb2, 1e-5f);
	}
	
	private static void doCheck50(LuvColorSpace lcs, int[] srgb) {
		float[] srgb1 = {srgb[0]/255f, srgb[1]/255f, srgb[2]/255f};
		float[] xyz50a = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ).fromRGB(srgb1);	// standard D50-based XYZ space
		float[] lab = lcs.fromCIEXYZ(xyz50a);
		float[] xyz50b = lcs.toCIEXYZ(lab);
		assertArrayEquals("lab50 conversion problem for srgb=" + Arrays.toString(srgb), xyz50a, xyz50b, 1e-5f);
	}

}

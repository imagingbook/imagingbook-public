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

public class LuvColorSpaceTest {	//TODO: adapt to Lab test!
	
	private static final LuvColorSpace CS = LuvColorSpace.getInstance();

	@Test
	public void testRGB() {
		doCheckRGB(CS, new int[] {0, 0, 0});
		doCheckRGB(CS, new int[] {255, 255, 255});
		doCheckRGB(CS, new int[] {177, 0, 0});
		doCheckRGB(CS, new int[] {0, 177, 0});
		doCheckRGB(CS, new int[] {0, 0, 177});
		doCheckRGB(CS, new int[] {19, 3, 174});
	}
	
	@Test
	public void testXYZ() {
		doCheckXYZ(CS, new int[] {0, 0, 0});
		doCheckXYZ(CS, new int[] {255, 255, 255});
		doCheckXYZ(CS, new int[] {177, 0, 0});
		doCheckXYZ(CS, new int[] {0, 177, 0});
		doCheckXYZ(CS, new int[] {0, 0, 177});
		doCheckXYZ(CS, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheckRGB(CS, new int[] {r, g, b});
			doCheckXYZ(CS, new int[] {r, g, b});
		}
	}
	
	// -------------------------------------------------
	
	private static void doCheckRGB(LuvColorSpace lcs, int[] srgb) {
		double[] srgb1 = RgbUtils.normalizeD(srgb);
		double[] lab = lcs.fromRGB(srgb1);
		double[] srgb2 = lcs.toRGB(lab);
		assertArrayEquals(srgb1, srgb2, 1e-5);
	}
	
	private static void doCheckXYZ(LuvColorSpace lcs, int[] srgb) {
		double[] srgb1 = RgbUtils.normalizeD(srgb);
		double[] lab = lcs.fromRGB(srgb1);
		{	// D50
			double[] xyz50a = lcs.toCIEXYZ(lab);	// standard D50-based XYZ space
			double[] luv = lcs.fromCIEXYZ(xyz50a);
			double[] xyz50b = lcs.toCIEXYZ(luv);
			assertArrayEquals(xyz50a, xyz50b, 1e-5);
		}
		{	// D65
			double[] xyz65a = lcs.toCIEXYZ65(lab);	// standard D50-based XYZ space
			double[] luv = lcs.fromCIEXYZ65(xyz65a);
			double[] xyz65b = lcs.toCIEXYZ65(luv);
			assertArrayEquals(xyz65a, xyz65b, 1e-5);
		}
	}

}

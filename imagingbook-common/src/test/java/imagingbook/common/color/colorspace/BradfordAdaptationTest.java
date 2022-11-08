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

public class BradfordAdaptationTest {
	
	static float TOL = 1e-6f;

	@Test
	public void test1a() {
		ColorSpace cs = sRgb65ColorSpace.getInstance();
		doCheck(cs, new int[] {0, 0, 0});
		doCheck(cs, new int[] {255, 255, 255});
		doCheck(cs, new int[] {177, 0, 0});
		doCheck(cs, new int[] {0, 177, 0});
		doCheck(cs, new int[] {0, 0, 177});
		doCheck(cs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		ColorSpace cs = sRgb65ColorSpace.getInstance();
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
//		ColorSpace cs = new sRgb65ColorSpace();
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(cs, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	private static void doCheck(ColorSpace cs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);		
		ChromaticAdaptation adapt65to50 = new BradfordAdaptation(StandardIlluminant.D65, StandardIlluminant.D50);	// adapts from D65 -> D50
		ChromaticAdaptation adapt50to65 = new BradfordAdaptation(StandardIlluminant.D50, StandardIlluminant.D65);	// adapts from D50 -> D65
		
		float[] XYZ65a = cs.toCIEXYZ(srgb1);
		float[] XYZ50 = adapt65to50.applyTo(XYZ65a);
		float[] XYZ65b = adapt50to65.applyTo(XYZ50);
		
		assertArrayEquals("Bradford adapt problem for srgb=" + Arrays.toString(srgb), XYZ65a, XYZ65b, TOL);
	}

}

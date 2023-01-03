/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.adapt;

import static imagingbook.common.color.cie.StandardIlluminant.D50;
import static imagingbook.common.color.cie.StandardIlluminant.D65;
import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;

public class XYZscalingAdaptationTest {
	
	static ColorSpace CS = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);	// any colorspace should work
	static ChromaticAdaptation adapt65to50 = XYZscalingAdaptation.getInstance(D65, D50);	// adapts from D65 to D50
	static ChromaticAdaptation adapt50to65 = XYZscalingAdaptation.getInstance(D50, D65);	// adapts from D50 to D65

	@Test
	public void test1() {
//		ColorSpace cs = sRgb65ColorSpace.getInstance();
		doCheck(CS, new int[] {0, 0, 0});
		doCheck(CS, new int[] {255, 255, 255});
		doCheck(CS, new int[] {177, 0, 0});
		doCheck(CS, new int[] {0, 177, 0});
		doCheck(CS, new int[] {0, 0, 177});
		doCheck(CS, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
//		ColorSpace cs = sRgb65ColorSpace.getInstance();
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(CS, new int[] {r, g, b});
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
	
	@Test
	public void testWhites() {
		float[] W50 = Matrix.toFloat(D50.getXYZ());
		float[] W65 = Matrix.toFloat(D65.getXYZ());
		
		float[] convertedTo65 = adapt50to65.applyTo(Matrix.toFloat(D50.getXYZ()));
		assertArrayEquals(W65, convertedTo65, 1e-6f);
		
		float[] convertedTo50 = adapt65to50.applyTo(Matrix.toFloat(D65.getXYZ()));
		assertArrayEquals(W50, convertedTo50, 1e-6f);
	}
	
	
	private static void doCheck(ColorSpace cs, int[] srgb) {
		float[] srgb50a = RgbUtils.normalize(srgb);
		float[] XYZ50a = cs.toCIEXYZ(srgb50a);
		
		float[] XYZ65a = adapt50to65.applyTo(XYZ50a);
		float[] XYZ50b = adapt65to50.applyTo(XYZ65a);
			
		assertArrayEquals(XYZ50a, XYZ50b, 1e-6f);
	}
}

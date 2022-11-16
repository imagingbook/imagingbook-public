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
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.color.cie.StandardIlluminant;
import imagingbook.common.math.Matrix;


public class LinearRgb65ColorSpaceTest {
	
	private static LinearRgb65ColorSpace CS = LinearRgb65ColorSpace.getInstance();
//	private static sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();

	@Test
	public void test1() {
		doCheck(CS, new int[] {0, 0, 0});
		doCheck(CS, new int[] {255, 255, 255});
		doCheck(CS, new int[] {177, 0, 0});
		doCheck(CS, new int[] {0, 177, 0});
		doCheck(CS, new int[] {0, 0, 177});
		doCheck(CS, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
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
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(cs, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	@Test
	public void testPrimaries() { // check primaries in D65
		for (int i = 0; i < 3; i++) {
			float[] rgb = new float[3];
			rgb[i] = 1;
			float[] xyz = CS.toCIEXYZ65(rgb);
			float[] primary = CS.getPrimary(i);
//			System.out.println("primary = " + Matrix.toString(primary));
//			System.out.println("xyz     = " + Matrix.toString(xyz));
			assertArrayEquals(primary, xyz, 1e-6f);
		}
	}
	
	@Test
	public void testWhite50() { //sRGB white in this color space must map to D50-XYZ in PCS
		float[] rgbTHIS = {1, 1, 1};
		float[] wXYZ50 = CS.toCIEXYZ(rgbTHIS);	// in PCS#
//		System.out.println("wD50 = " + Matrix.toString(StandardIlluminant.D50.getXYZ()));
//		System.out.println("wXYZ = " + Matrix.toString(wXYZ50));
		float[] wIll50 = Matrix.toFloat(StandardIlluminant.D50.getXYZ()); 
		//System.out.println("wIll = " + Matrix.toString(wIll));
		assertArrayEquals(wIll50, wXYZ50, 1e-3f);
	}
	
	@Test
	public void testWhite65() { //sRGB white in this color space must map to D65 XYZ with toCIEXYZ65()
		float[] srgbTHIS = {1, 1, 1};
		float[] wXYZ65 = CS.toCIEXYZ65(srgbTHIS);	// in PCS#
//		System.out.println("wD65 = " + Matrix.toString(StandardIlluminant.D65.getXYZ()));
//		System.out.println("wXYZ = " + Matrix.toString(wXYZ65));
		float[] wIll65 = Matrix.toFloat(StandardIlluminant.D65.getXYZ()); 
		//System.out.println("wIll = " + Matrix.toString(wIll));
		assertArrayEquals(wIll65, wXYZ65, 1e-3f);
	}
	
	@Test
	public void testBlack() { // check black point
		float[] rgb = {0, 0, 0};
		float[] xyz = CS.toCIEXYZ(rgb);
		assertArrayEquals(new float[] {0, 0, 0}, xyz, 1e-6f);
	}
	
	@Test
	public void testGray() {	// any RGB gray in this color space must map do D50-xy in PCS
		final float[] xy50 = Matrix.toFloat(StandardIlluminant.D50.getXy()); //{0.3457, 0.3585};
		for (int c = 1; c < 256; c++) {
			float[] rgbTHIS = {c, c, c};
			float[] xyzPCS = CS.toCIEXYZ(rgbTHIS);
			float[] xy = CieUtils.XYZToxy(xyzPCS);
			assertArrayEquals(xy50, xy, 1e-4f);
		}
	}
	
	// ------------------------------------------
	
	private static void doCheck(DirectD65Conversion cs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] lab = cs.fromRGB(srgb1);
		float[] srgb2 = cs.toRGB(lab);
		assertArrayEquals(srgb1, srgb2, 1e-6f);
	}

}

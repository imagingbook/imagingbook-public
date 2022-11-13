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

public class LinearRgb65ColorSpaceTest {
	
	private static LinearRgb65ColorSpace CS = LinearRgb65ColorSpace.getInstance();
	private static sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();

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
			double[] rgb = new double[3];
			rgb[i] = 1;
			double[] xyz = CS.toCIEXYZ65(rgb);
			double[] primary = srgbCS.getPrimary(i);
//			System.out.println("primary = " + Matrix.toString(primary));
//			System.out.println("xyz     = " + Matrix.toString(xyz));
			assertArrayEquals(primary, xyz, 1e-6);
		}
	}
	
	@Test
	public void testWhite50() { //sRGB white in this color space must map to D50-XYZ in PCS
		double[] rgbTHIS = {1, 1, 1};
		double[] wXYZ50 = CS.toCIEXYZ(rgbTHIS);	// in PCS#
//		System.out.println("wD50 = " + Matrix.toString(StandardIlluminant.D50.getXYZ()));
//		System.out.println("wXYZ = " + Matrix.toString(wXYZ50));
		double[] wIll50 = StandardIlluminant.D50.getXYZ(); 
		//System.out.println("wIll = " + Matrix.toString(wIll));
		assertArrayEquals(wIll50, wXYZ50, 1e-3);
	}
	
	@Test
	public void testWhite65() { //sRGB white in this color space must map to D65 XYZ with toCIEXYZ65()
		double[] srgbTHIS = {1, 1, 1};
		double[] wXYZ65 = CS.toCIEXYZ65(srgbTHIS);	// in PCS#
//		System.out.println("wD65 = " + Matrix.toString(StandardIlluminant.D65.getXYZ()));
//		System.out.println("wXYZ = " + Matrix.toString(wXYZ65));
		double[] wIll65 = StandardIlluminant.D65.getXYZ(); 
		//System.out.println("wIll = " + Matrix.toString(wIll));
		assertArrayEquals(wIll65, wXYZ65, 1e-3);
	}
	
	@Test
	public void testBlack() { // check black point
		double[] rgb = {0, 0, 0};
		double[] xyz = CS.toCIEXYZ(rgb);
		assertArrayEquals(new double[] {0, 0, 0}, xyz, 1e-6);
	}
	
	@Test
	public void testGray() {	// any RGB gray in this color space must map do D50-xy in PCS
		final double[] xy50 = StandardIlluminant.D50.getXy(); //{0.3457, 0.3585};
		for (int c = 1; c < 256; c++) {
			double[] rgbTHIS = {c, c, c};
			double[] xyzPCS = CS.toCIEXYZ(rgbTHIS);
			double[] xy = CieUtil.XYZToXy(xyzPCS);
			assertArrayEquals(xy50, xy, 1e-4);
		}
	}
	
	// ------------------------------------------
	
	private static void doCheck(CustomColorSpace cs, int[] srgb) {
		double[] srgb1 = RgbUtils.normalizeD(srgb);
		double[] lab = cs.fromRGB(srgb1);
		double[] srgb2 = cs.toRGB(lab);
		assertArrayEquals(srgb1, srgb2, 1e-6);
	}

}

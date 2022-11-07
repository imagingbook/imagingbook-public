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
import static org.junit.Assert.assertTrue;

import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class CieUtilTest {

	@Test
	public void test1() {
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
	
	@Test
	public void test4() {
		ColorSpace cs = sRgb65ColorSpace.getInstance();
//		int[] srgb = new int[] {255, 255, 255};
		int[] srgb = new int[] {100, 100, 100};
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] XYZ50 = cs.toCIEXYZ(srgb1);
		double[] xy = CieUtil.XYZToXy(Matrix.toDouble(XYZ50));
		PrintPrecision.set(6);
//		System.out.println("xy = " + Matrix.toString(xy));
//		System.out.println("xy = " + Matrix.toString(CieUtil.XYZToXy(StandardIlluminant.D50.getXYZ())));
	}
	
//	@Test
//	public void test5() {	// sRGB grays map to D65 whitepoint in sRgb65ColorSpace
////		PrintPrecision.set(6);
////		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
//		ColorSpace cs = sRgb65ColorSpace.getInstance();
//		
//		for (int c = 1; c < 256; c++) {
//			float[] srgb1 = RgbUtils.normalize(new int[] { c, c, c });
//
//			float[] XYZ = cs.toCIEXYZ(srgb1);
//			// System.out.println("XYZ = " + Matrix.toString(XYZ));
//			double[] xy = CieUtil.XYZToXy(Matrix.toDouble(XYZ));
//
//			// System.out.println("xy = " + Matrix.toString(xy));
//			// System.out.println("D65 = " +
//			// Matrix.toString(StandardIlluminant.D65.getXy()));
//
//			assertArrayEquals(StandardIlluminant.D65.getXy(), xy, 1e-4);
//		}
//		
////		System.out.println("D50 = " + Matrix.toString(StandardIlluminant.D50.getXy()));
//	}
	
//	@Test
//	public void test6() {	// sRGB grays map to N whitepoint in Java's CIEXYZ color space
//		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
//		for (int c = 1; c < 256; c++) {
//			float[] srgb1 = RgbUtils.normalize(new int[] { c, c, c });
//
//			float[] XYZ = cs.toCIEXYZ(srgb1);
//			// System.out.println("XYZ = " + Matrix.toString(XYZ));
//			double[] xy = CieUtil.XYZToXy(Matrix.toDouble(XYZ));
//
//			// System.out.println("xy = " + Matrix.toString(xy));
//			// System.out.println("N = " +
//			// Matrix.toString(StandardIlluminant.N.getXy()));
//
//			assertArrayEquals(StandardIlluminant.N.getXy(), xy, 1e-4);
//		}
//		
////		System.out.println("D50 = " + Matrix.toString(StandardIlluminant.D50.getXy()));
//	}
	
	
	// ---------------------------------------------------
	
	// map XYZ to xy and back
	private static void doCheck(ColorSpace cs, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] XYZa = cs.toCIEXYZ(srgb1);
		
		double[] xy = CieUtil.XYZToXy(Matrix.toDouble(XYZa));
		assertTrue(Double.isFinite(xy[0]));
		assertTrue(Double.isFinite(xy[1]));
		
		float Y = XYZa[1];
		float[] XYZb = Matrix.toFloat(CieUtil.xyToXYZ(xy[0], xy[1], Y));	
		assertArrayEquals("CieUtil problem for srgb=" + Arrays.toString(srgb), XYZa, XYZb, 1e-6f);
	}

}

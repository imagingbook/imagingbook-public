/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import static imagingbook.common.color.cie.StandardIlluminant.D50;
import static imagingbook.common.color.cie.StandardIlluminant.D65;
import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Random;

import imagingbook.common.color.adapt.BradfordAdaptation;
import imagingbook.common.color.adapt.ChromaticAdaptation;
import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.color.cie.StandardIlluminant;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class AdobeRgbColorSpaceTest {

	private static ColorSpace sRGB_CS = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);

	private static AdobeRgbColorSpaceIcc AdobeCS = AdobeRgbColorSpaceIcc.getInstance();
	private static float TOL = 1e-3f;	// a bit more accurate than standard AWT color spaces!

	@Test	// convvert
	public void test1A() {
		doCheck(AdobeCS, new int[] {0, 0, 0});
		doCheck(AdobeCS, new int[] {255, 255, 255});
		doCheck(AdobeCS, new int[] {177, 0, 0});
		doCheck(AdobeCS, new int[] {0, 177, 0});
		doCheck(AdobeCS, new int[] {0, 0, 177});
		doCheck(AdobeCS, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(AdobeCS, new int[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations (slow!)
//	public void test3() {
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(CS, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	// @Test
	// public void testPrimaries() { // check primaries in D65
	//
	// 	for (int i = 0; i < 3; i++) {
	// 		float[] rgb = new float[3];
	// 		rgb[i] = 1;
	// 		float[] xyz65 = catD50toD65.applyTo(CS.toCIEXYZ(rgb));
	// 		float[] primary = CS.getPrimary(i);
	//
	// 		// PrintPrecision.set(6);
	// 		// System.out.println("xyz     = " + Matrix.toString(xyz));
	// 		// System.out.println("primary = " + Matrix.toString(primary));
	// 		assertArrayEquals(primary, xyz65, 1e-4f);	// inaccuracy due to Bradford adaptation?
	// 	}
	// }
	
	@Test
	public void testBlack() { // check black point
		float[] rgbTHIS = {0, 0, 0};
		float[] xyzPCS = AdobeCS.toCIEXYZ(rgbTHIS);
		assertArrayEquals(new float[] {0, 0, 0}, xyzPCS, 1e-6f);
	}
	
	@Test
	public void testWhite() { //sRGB white in this color space must map to D50-XYZ in PCS
		float[] rgbWhite = {1, 1, 1};
		float[] xyz65 = catD50toD65.applyTo(AdobeCS.toCIEXYZ(rgbWhite));
		float[] w65 = Matrix.toFloat(D65.getXYZ());
		PrintPrecision.set(6);
		System.out.println("xyz65 = " + Matrix.toString(xyz65));
		System.out.println("w65   = " + Matrix.toString(w65));
		assertArrayEquals(w65, xyz65, 1e-3f);	// not very accurate!
		assertArrayEquals(CieUtils.XYZToxy(w65), CieUtils.XYZToxy(xyz65), 1e-4f);
	}
	
	@Test
	public void testGray() {	// any sRGB gray in this color space must map do white-xy
		float[] w65 = Matrix.toFloat(StandardIlluminant.D65.getXy()); //{0.3457, 0.3585};
		for (int c = 1; c < 256; c++) {
			float[] rgbTHIS = {c, c, c};
			float[] xyz65 = catD50toD65.applyTo(AdobeCS.toCIEXYZ(rgbTHIS));
			float[] xy = CieUtils.XYZToxy(xyz65);
			assertArrayEquals(w65, xy, 1e-4f);
		}
	}
	
	// ---------------------------------------------------
	
	private static void doCheck(ColorSpace cs, int[] sRGB) {
		float[] srgbIN = RgbUtils.normalize(sRGB);
		
		{	// check fromCIEXYZ / toCIEXYZ
			float[] xyzIN = sRGB_CS.toCIEXYZ(srgbIN);
			float[] rgbCS = cs.fromCIEXYZ(xyzIN);
			float[] xyzOUT = cs.toCIEXYZ(rgbCS);
			assertArrayEquals(xyzIN, xyzOUT, TOL);
		}

		{	// check fromRGB / toRGB 
			float[] rgbCS = cs.fromRGB(srgbIN);
			float[] srgbOUT = cs.toRGB(rgbCS);
			assertArrayEquals(srgbIN, srgbOUT, TOL);
		}
	}

}

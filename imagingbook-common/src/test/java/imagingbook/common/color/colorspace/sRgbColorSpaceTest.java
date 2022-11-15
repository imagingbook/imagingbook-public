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
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class sRgbColorSpaceTest {
	
	static sRgbColorSpace CS = sRgbColorSpace.getInstance();
	static float TOL = 1e-6f;			

	static {
		PrintPrecision.set(15);
	}
	
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
	public void test2() {					// TODO: too inaccurate, FIX!!
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(CS, new int[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations (takes long!)
//	public void test3() {
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(CS, new int[] {r, g, b});
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
	public void testBlack() { // check black point
		float[] rgb = {0, 0, 0};
		float[] xyz = CS.toCIEXYZ(rgb);
		assertArrayEquals(new float[] {0, 0, 0}, xyz, 1e-6f);
	}
	
	@Test
	public void testWhite50() { //sRGB white in this color space must map to D50-XYZ in PCS
		float[] srgbTHIS = {1, 1, 1};
		float[] wXYZ50 = CS.toCIEXYZ(srgbTHIS);	// in PCS#
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
		float[] whitePt = Matrix.toFloat(StandardIlluminant.D65.getXYZ()); 
		//System.out.println("wIll = " + Matrix.toString(wIll));
		assertArrayEquals(whitePt, wXYZ65, 1e-3f);
	}
	
	@Test
	public void testGray() {	// any sRGB gray in this color space must map do D50-xy in PCS
		final float[] xy50 = Matrix.toFloat(StandardIlluminant.D50.getXy()); //{0.3457, 0.3585};
		for (int c = 1; c < 256; c++) {
			float[] rgbTHIS = {c, c, c};
			float[] xyzPCS = CS.toCIEXYZ(rgbTHIS);
			float[] xy = CieUtil.XYZToxy(xyzPCS);
			assertArrayEquals(xy50, xy, 1e-4f);
		}
	}
	
	@Test
	public void testBookXYZValues() {	// check colors in book Table 14.3
		PrintPrecision.set(4);
		sRgbColorSpace cs = CS;
		// original (book) values
		checkXYZValues(cs, 0.00, 0.00, 0.00,  0.0000, 0.0000, 0.0000);
		checkXYZValues(cs, 1.00, 0.00, 0.00,  0.4125, 0.2127, 0.0193);
		checkXYZValues(cs, 1.00, 1.00, 0.00,  0.7700, 0.9278, 0.1385);
		checkXYZValues(cs, 0.00, 1.00, 0.00,  0.3576, 0.7152, 0.1192);
		checkXYZValues(cs, 0.00, 1.00, 1.00,  0.5380, 0.7873, 1.0694);
		checkXYZValues(cs, 0.00, 0.00, 1.00,  0.1804, 0.0722, 0.9502);
		checkXYZValues(cs, 1.00, 0.00, 1.00,  0.5929, 0.2848, 0.9696);
		checkXYZValues(cs, 1.00, 1.00, 1.00,  0.9505, 1.0000, 1.0888);		// = D65 white point
		checkXYZValues(cs, 0.50, 0.50, 0.50,  0.2034, 0.2140, 0.2330);
		checkXYZValues(cs, 0.75, 0.00, 0.00,  0.2155, 0.1111, 0.0101);
		checkXYZValues(cs, 0.50, 0.00, 0.00,  0.0883, 0.0455, 0.0041);
		checkXYZValues(cs, 0.25, 0.00, 0.00,  0.0210, 0.0108, 0.0010);
		checkXYZValues(cs, 1.00, 0.50, 0.50,  0.5276, 0.3812, 0.2482);
	}
	
	// ---------------------------------------------------
	
	private static void doCheck(DirectD65Conversion cs, int[] srgb) {
		float[] srgbIN = RgbUtils.normalize(srgb);
		float[] xyzPCS = ColorSpace.getInstance(ColorSpace.CS_sRGB).toCIEXYZ(srgbIN); // get some valid XYZ
		
		{	// check fromCIEXYZ / toCIEXYZ 				
			float[] srgbTHIS = cs.fromCIEXYZ(xyzPCS);
			float[] xyzOUT = cs.toCIEXYZ(srgbTHIS);
			assertArrayEquals(xyzPCS, xyzOUT, TOL);	// works fine
		}

		{	// check fromRGB / toRGB 					
			float[] srgbTHIS = cs.fromRGB(srgbIN);
			float[] srgbOUT = cs.toRGB(srgbTHIS);
//			System.out.println("srgbIN  = " + Matrix.toString(srgbIN));
//			System.out.println("srgbOUT = " + Matrix.toString(srgbOUT));
			assertArrayEquals(srgbIN, srgbOUT, TOL);
		}
	}
	
	// RGB are sRGB values
	private static void checkXYZValues(sRgbColorSpace cs, double R, double G, double B, double X, double Y, double Z) {
		float[] srgb1 = new float[] {(float)R, (float)G, (float)B};
		
		// convert to XYZ (D65)
		float[] xyz = cs.toCIEXYZ65(srgb1);
//		System.out.println(Matrix.toString(xyz));
		assertArrayEquals(new float[] {(float)X, (float)Y, (float)Z}, xyz, 1e-4f);
		
		// back to sRGB
		float[] srgb2 = cs.fromCIEXYZ65(xyz);
		assertArrayEquals(srgb1, srgb2, 1e-4f);
	}

}

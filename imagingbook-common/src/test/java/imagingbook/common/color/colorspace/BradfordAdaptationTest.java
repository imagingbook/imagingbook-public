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

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.testutils.NumericTestUtils;

public class BradfordAdaptationTest {
	
	private static double TOL = 1e-6;

	@Test
	public void test0A() {	// adaptation to the same white point gives identity matrix always
		double[][] I = Matrix.idMatrix(3);
		for (Illuminant illum : Arrays.asList(StandardIlluminant.D65, StandardIlluminant.D50, StandardIlluminant.N)) {
			BradfordAdaptation adapt = BradfordAdaptation.getInstance(illum, illum);
			NumericTestUtils.assert2dArrayEquals(adapt.getAdaptationMatrix(), I, 1e-12);
		}
	}
	
	@Test
	public void test0B() {	// reverse adaptation must give inverse matrix
		double[][] I = Matrix.idMatrix(3);
		BradfordAdaptation adapt1 = BradfordAdaptation.getInstance(StandardIlluminant.D65, StandardIlluminant.D50);
		BradfordAdaptation adapt2 = BradfordAdaptation.getInstance(StandardIlluminant.D50, StandardIlluminant.D65);
		double[][] Madapt1 = adapt1.getAdaptationMatrix();
		double[][] Madapt2 = adapt2.getAdaptationMatrix();
		double[][] Madapt12 = Matrix.multiply(Madapt1, Madapt2);
		NumericTestUtils.assert2dArrayEquals(Madapt12, I, 1e-12);
	}
	
	@Test
	public void test0C() {	// reverse adaptation must give inverse matrix
		double[][] I = Matrix.idMatrix(3);
		BradfordAdaptation adapt1 = BradfordAdaptation.getInstance(StandardIlluminant.N, StandardIlluminant.D50);
		BradfordAdaptation adapt2 = BradfordAdaptation.getInstance(StandardIlluminant.D50, StandardIlluminant.N);
		double[][] Madapt1 = adapt1.getAdaptationMatrix();
		double[][] Madapt2 = adapt2.getAdaptationMatrix();
		double[][] Madapt12 = Matrix.multiply(Madapt1, Madapt2);
		NumericTestUtils.assert2dArrayEquals(Madapt12, I, 1e-12);
	}
	
	@Test
	public void test1() {
		sRgbColorSpace cs = sRgbColorSpace.getInstance();
		doCheck(cs, new int[] {0, 0, 0});
		doCheck(cs, new int[] {255, 255, 255});
		doCheck(cs, new int[] {177, 0, 0});
		doCheck(cs, new int[] {0, 177, 0});
		doCheck(cs, new int[] {0, 0, 177});
		doCheck(cs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		sRgbColorSpace cs = sRgbColorSpace.getInstance();
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
	public void testPrimariesD65ToD50() { // check if RGB primaries are mapped (approximately)
		ChromaticAdaptation adapt = BradfordAdaptation.getInstance(StandardIlluminant.D65, StandardIlluminant.D50);
		PrintPrecision.set(6);
		for (int i = 0; i < 3; i++) {
			double[] primary65 =  Matrix.getColumn(CieUtil.Mrgb65i, i);
			double[] primary50 =  Matrix.getColumn(CieUtil.Mrgb50i, i);
			
//			System.out.println("primary65 = " + Matrix.toString(primary65));
//			System.out.println("primary50 = " + Matrix.toString(primary50));
			
			double[] primary50m = adapt.applyTo(primary65);
//			System.out.println("primary50m = " + Matrix.toString(primary50m));
			assertArrayEquals(primary50, primary50m, 1e-3);
		}
	}
	
	@Test
	public void testPrimariesD50ToD65() { // check if RGB primaries are mapped (approximately)
		ChromaticAdaptation adapt = BradfordAdaptation.getInstance(StandardIlluminant.D50, StandardIlluminant.D65);
		PrintPrecision.set(6);
		for (int i = 0; i < 3; i++) {
			double[] primary50 =  Matrix.getColumn(CieUtil.Mrgb50i, i);
			double[] primary65 =  Matrix.getColumn(CieUtil.Mrgb65i, i);
			
//			System.out.println("primary50 = " + Matrix.toString(primary50));
//			System.out.println("primary65 = " + Matrix.toString(primary65));
			
			double[] primary65m = adapt.applyTo(primary50);
//			System.out.println("primary65m = " + Matrix.toString(primary65m));
			assertArrayEquals(primary65, primary65m, 1e-3);
		}
	}
	
	@Test
	public void testWhiteD65ToD50() { // check if white point is mapped (precisely)
		ChromaticAdaptation adapt = BradfordAdaptation.getInstance(StandardIlluminant.D65, StandardIlluminant.D50);
		PrintPrecision.set(6);
		
		double[] W65 = StandardIlluminant.D65.getXYZ();
		double[] W50 = StandardIlluminant.D50.getXYZ();
		
		double[] W50m = adapt.applyTo(W65);
//		System.out.println("W50  = " + Matrix.toString(W50));
//		System.out.println("W50m = " + Matrix.toString(W50m));
			
		assertArrayEquals(W50, W50m, 1e-12);
	}
	
	@Test
	public void testWhiteD50ToD65() { // check if white point is mapped (precisely)
		ChromaticAdaptation adapt = BradfordAdaptation.getInstance(StandardIlluminant.D50, StandardIlluminant.D65);
		PrintPrecision.set(6);
		
		double[] W65 = StandardIlluminant.D65.getXYZ();
		double[] W50 = StandardIlluminant.D50.getXYZ();
		
		double[] W65m = adapt.applyTo(W50);
//		System.out.println("W65  = " + Matrix.toString(W65));
//		System.out.println("W65m = " + Matrix.toString(W65m));
			
		assertArrayEquals(W65, W65m, 1e-12);
	}
	
	// ------------------------------------------------
	
	private static void doCheck(CustomColorSpace cs, int[] srgb) {
		double[] srgb1 = RgbUtils.normalizeD(srgb);		
		ChromaticAdaptation adapt65to50 = BradfordAdaptation.getInstance(StandardIlluminant.D65, StandardIlluminant.D50);	// adapts from D65 -> D50
		ChromaticAdaptation adapt50to65 = BradfordAdaptation.getInstance(StandardIlluminant.D50, StandardIlluminant.D65);	// adapts from D50 -> D65
		
		double[] XYZ65a = cs.toCIEXYZ(srgb1);
		double[] XYZ50 = adapt65to50.applyTo(XYZ65a);
		double[] XYZ65b = adapt50to65.applyTo(XYZ50);
		
		assertArrayEquals("Bradford adapt problem for srgb=" + Arrays.toString(srgb), XYZ65a, XYZ65b, TOL);
	}

}

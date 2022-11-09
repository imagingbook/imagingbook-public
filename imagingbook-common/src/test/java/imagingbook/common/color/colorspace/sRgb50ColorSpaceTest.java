package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;

public class sRgb50ColorSpaceTest {
	
	static float TOL = 1e-5f;
	
	static sRgb50ColorSpace CS = sRgb50ColorSpace.getInstance();

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
	public void testPrimaries() { // check primaries
		for (int i = 0; i < 3; i++) {
			float[] rgb = new float[3];
			rgb[i] = 1;
			float[] xyz = CS.toCIEXYZ(rgb);
			assertArrayEquals(Matrix.toFloat(CS.getPrimary(i)), xyz, 1e-6f);
		}
	}
	
	@Test
	public void testBlack() { // check black point
		float[] rgb = {0, 0, 0};
		float[] xyz = CS.toCIEXYZ(rgb);
		assertArrayEquals(new float[] {0, 0, 0}, xyz, 1e-6f);
	}
	
	@Test
	public void testWhite() { // check tristimulus/white point
		float[] srgbTHIS = {1, 1, 1};
		
		float[] wXYZ = CS.toCIEXYZ(srgbTHIS);
		System.out.println("wXYZ = " + Matrix.toString(wXYZ));
		float[] wIll = Matrix.toFloat(StandardIlluminant.D50.getXYZ()); 
		System.out.println("wIll = " + Matrix.toString(wIll));
		// {0.9642f, 1f, 0.8249f};
		
		assertArrayEquals(wIll, wXYZ, 1e-6f);
		assertArrayEquals(wIll, Matrix.toFloat(CS.getWhiteXYZ()), 1e-6f);
	}
	
	// ---------------------------------------------------
	
	
	private static void doCheck(ColorSpace cs, int[] srgb) {
		float[] srgbIN = RgbUtils.normalize(srgb);
		float[] xyzPCS = ColorSpace.getInstance(ColorSpace.CS_sRGB).toCIEXYZ(srgbIN); // get some valid XYZ
		
		{	// check fromCIEXYZ / toCIEXYZ 
			float[] srgbTHIS = cs.fromCIEXYZ(xyzPCS);
			float[] xyzOUT = cs.toCIEXYZ(srgbTHIS);
			assertArrayEquals(xyzPCS, xyzOUT, TOL);
		}

		{	// check fromRGB / toRGB 
			float[] srgbTHIS = cs.fromRGB(srgbIN);
			float[] srgbOUT = cs.toRGB(srgbTHIS);
			assertArrayEquals(srgbIN, srgbOUT, TOL);
		}
	}
	
	
//	private static void doCheck(ColorSpace cs, int[] srgb) {
//		{
//			float[] srgbA = RgbUtils.normalize(srgb);
//			float[] xyz50 = cs.toCIEXYZ(srgbA);
//			float[] srgbB = cs.fromCIEXYZ(xyz50);
//			assertArrayEquals(Arrays.toString(srgb), srgbA, srgbB, TOL);
//		}
//		{
//			float[] srgbA = RgbUtils.normalize(srgb);
//			float[] rgb50 = cs.toRGB(srgbA);
//			float[] srgbB = cs.fromRGB(rgb50);
//			assertArrayEquals(srgbA, srgbB, TOL);
//		}
//	}
		

}

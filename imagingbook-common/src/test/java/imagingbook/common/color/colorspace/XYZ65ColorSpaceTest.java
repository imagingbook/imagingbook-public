package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class XYZ65ColorSpaceTest {

	static XYZ65ColorSpace CS = XYZ65ColorSpace.getInstance();
	static float TOL = 1e-5f;

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
	
//	@Test	// tests all possible rgb combinations (takes LONG!)
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
	public void testBlack() { // check black point
		float[] srgbPCS = {0, 0, 0};
		float[] xyzTHIS = CS.fromRGB(srgbPCS);
		assertArrayEquals(new float[] {0, 0, 0}, xyzTHIS, 1e-6f);
	}
	
	@Test
	public void testWhite() {	// external white must map to D65 in this color space
		float[] rgb = {1, 1, 1};
		float[] xyzTHIS = CS.fromRGB(rgb);

		PrintPrecision.set(16);
//		System.out.println("xyzTHIS = " + Matrix.toString(xyzTHIS));
		// {0.9642028808593750, 1.0000000000000000, 0.8248901367187500}

		float[] xyzIll = Matrix.toFloat(StandardIlluminant.D65.getXYZ());
//		System.out.println("xyzIll = " + Matrix.toString(xyzIll));
		// {0.9642000198364258, 1.0000000000000000, 0.8249000906944275}

		assertArrayEquals(xyzIll, xyzTHIS, 1e-6f);
	}
	
	@Test
	public void testGray() {	// any external sRGB gray must map do D65-xy in this color space
		final double[] xy65 = StandardIlluminant.D65.getXy(); // {0.3127, 0.3290};
		for (int c = 1; c < 256; c++) {
			float[] rgb = {c, c, c};
			float[] xyzTHIS = CS.fromRGB(rgb);
			double[] xy = CieUtil.XYZToXy(Matrix.toDouble(xyzTHIS));
			assertArrayEquals(xy65, xy, 1e-6f);
		}
	}
	
	// ---------------------
	
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
	
}

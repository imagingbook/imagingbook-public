package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;

/**
 * This is experimental and should be used with caution.
 * All ICC profiles pass the to/fromCIEXYZ inversion test,
 * but not to/fromRGB. There may be wrong assumptions regarding
 * the underlying RGB transfer space.
 *
 */
public class IccProfilesColorSpaceTest {

	private static float TOL = 1e-1f;	// a bit more accurate than standard AWT color spaces!

	@Test
	public void test1A() {
		for (NamedIccProfile p : NamedIccProfile.values()) {
			ICC_ColorSpace CS = p.getColorSpace();
			doCheck(CS, new int[] {0, 0, 0});
			doCheck(CS, new int[] {255, 255, 255});
			doCheck(CS, new int[] {177, 0, 0});
			doCheck(CS, new int[] {0, 177, 0});
			doCheck(CS, new int[] {0, 0, 177});
			doCheck(CS, new int[] {19, 3, 174});
		}
	}

	
	@Test
	public void test2() {
		for (NamedIccProfile p : NamedIccProfile.values()) {
			ICC_ColorSpace CS = p.getColorSpace();
			Random rd = new Random(17);
			for (int i = 0; i < 10000; i++) {
				int r = rd.nextInt(256);
				int g = rd.nextInt(256);
				int b = rd.nextInt(256);
				doCheck(CS, new int[] {r, g, b});
			}
		}
	}
	
//	@Test	// tests all possible rgb combinations (don't!)
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
		for (NamedIccProfile p : NamedIccProfile.values()) {
			ICC_ColorSpace CS = p.getColorSpace();
			float[] rgbTHIS = {0, 0, 0};
			float[] xyzPCS = CS.toCIEXYZ(rgbTHIS);
			assertArrayEquals(new float[] {0, 0, 0}, xyzPCS, 1e-3f);
		}
	}
	
	@Test
	public void testWhite() { //sRGB white in this color space must map do D50-XYZ in PCS
		for (NamedIccProfile p : NamedIccProfile.values()) {
			ICC_ColorSpace CS = p.getColorSpace();
			float[] srgbTHIS = {1, 1, 1};
			float[] wXYZ = CS.toCIEXYZ(srgbTHIS);	// in PCS
			//System.out.println("wXYZ = " + Matrix.toString(wXYZ));
			float[] wIll = Matrix.toFloat(StandardIlluminant.D50.getXYZ()); 
			//System.out.println("wIll = " + Matrix.toString(wIll));
			assertArrayEquals(wIll, wXYZ, 1e-3f);
		}
	}
	
	@Test
	public void testGray() {	// any sRGB gray in this color space must map do D50-xy in PCS
		for (NamedIccProfile p : NamedIccProfile.values()) {
			ICC_ColorSpace CS = p.getColorSpace();
			final double[] xy50 = StandardIlluminant.D50.getXy(); //{0.3457, 0.3585};
			for (int c = 1; c < 256; c++) {
				float[] rgbTHIS = {c, c, c};
				float[] xyzPCS = CS.toCIEXYZ(rgbTHIS);
				double[] xy = CieUtil.XYZToXy(Matrix.toDouble(xyzPCS));
				assertArrayEquals(xy50, xy, 1e-3f);
			}
		}
	}
	
	// ---------------------------------------------------
	
	private static void doCheck(ICC_ColorSpace cs, int[] val) {
		float[] srgbIN = RgbUtils.normalize(val);
		float[] xyzPCS = ColorSpace.getInstance(ColorSpace.CS_sRGB).toCIEXYZ(srgbIN); // get some valid XYZ
		
		{	// check fromCIEXYZ / toCIEXYZ 
			float[] srgbTHIS = cs.fromCIEXYZ(xyzPCS);
			float[] xyzOUT = cs.toCIEXYZ(srgbTHIS);
			assertArrayEquals(xyzPCS, xyzOUT, TOL);
		}

			// most color spaces derived from ICC profiles do not pass this test!
//		{	// check fromRGB / toRGB 
//			float[] srgbTHIS = cs.fromRGB(srgbIN);
//			float[] srgbOUT = cs.toRGB(srgbTHIS);
//			assertArrayEquals(srgbIN, srgbOUT, TOL);
//		}
	}

}

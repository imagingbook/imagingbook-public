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
		doCheck(new int[] {0, 0, 0});
		doCheck(new int[] {255, 255, 255});
		doCheck(new int[] {177, 0, 0});
		doCheck(new int[] {0, 177, 0});
		doCheck(new int[] {0, 0, 177});
		doCheck(new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(new int[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations (takes LONG!)
//	public void test3() {
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	@Test
	public void test4() {	
		{	// check black point
			float[] rgb = {0, 0, 0};
			float[] xyz = CS.toCIEXYZ(rgb);
			assertArrayEquals(new float[] {0, 0, 0}, xyz, 1e-6f);
		}
		{	// check tristimulus and white point
			float[] rgb = {1, 1, 1};
			float[] xyz = CS.fromRGB(rgb);
			PrintPrecision.set(16);
//			System.out.println(Matrix.toString(xyz));
//			System.out.println(Matrix.toString(sRgb65ColorSpace.getInstance().getWhiteXYZ()));
			float[] wXYZ = Matrix.toFloat(StandardIlluminant.D65.getXYZ());
				//{0.9505f, 1f, 1.0888f}
//				{0.9505f, 1f, 1.0890f};
//				{0.9505000141764465f, 0.9999999905008820f, 1.0890001060762940f};
			assertArrayEquals(wXYZ, xyz, 1e-6f);
			
//			double[] wxy = {0.3457029085924369, 0.3585385827835399};	// D65 white point
//			assertArrayEquals(wxy, CieUtil.XYZToXy(Matrix.toDouble(xyz)), 1e-6f);
		}
	}
	
	// ---------------------
	
	private static void doCheck(int[] sRGB) {
		float[] srgb = RgbUtils.normalize(sRGB);
		// check fromXYZ() and toXYZ():
		{
			ColorSpace srgbCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);	// create some valid XYZ
			float[] xyzStd = srgbCS.toCIEXYZ(srgb);
			float[] XYZ = CS.fromCIEXYZ(xyzStd);	// doesn't do anything in this case
			assertArrayEquals(xyzStd, CS.toCIEXYZ(XYZ), TOL);
		}		
		// check fromRGB() and toRGB():
		{
			float[] XYZ = CS.fromRGB(srgb);
			assertArrayEquals(srgb, CS.toRGB(XYZ), TOL);
		}
	}
}

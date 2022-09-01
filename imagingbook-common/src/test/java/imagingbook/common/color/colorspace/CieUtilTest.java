package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.math.Matrix;

public class CieUtilTest {

	@Test
	public void test1() {
		ColorSpace cs = new sRgb65ColorSpace();
		doCheck(cs, new int[] {0, 0, 0});
		doCheck(cs, new int[] {255, 255, 255});
		doCheck(cs, new int[] {177, 0, 0});
		doCheck(cs, new int[] {0, 177, 0});
		doCheck(cs, new int[] {0, 0, 177});
		doCheck(cs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		ColorSpace cs = new sRgb65ColorSpace();
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
	
	private static void doCheck(ColorSpace cs, int[] srgb) {
		float[] srgb1 = {srgb[0]/255f, srgb[1]/255f, srgb[2]/255f};		
		
		float[] XYZa = cs.toCIEXYZ(srgb1);
		double[] xy = CieUtil.XYZToXy(Matrix.toDouble(XYZa));
		assertTrue(Double.isFinite(xy[0]));
		assertTrue(Double.isFinite(xy[1]));
		
		float[] XYZb = Matrix.toFloat(CieUtil.xyToXYZ(xy[0], xy[1], XYZa[1]));
		
		assertArrayEquals("CieUtil problem for srgb=" + Arrays.toString(srgb), XYZa, XYZb, 1e-6f);
	}

}

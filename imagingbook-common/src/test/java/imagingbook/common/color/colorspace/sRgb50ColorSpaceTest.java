package imagingbook.common.color.colorspace;

import static org.junit.Assert.*;

import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class sRgb50ColorSpaceTest {
	
	static float TOL = 1e-5f;
	
	static ColorSpace CS = sRgb50ColorSpace.getInstance();

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
	
	@Test
	public void test3() {
		PrintPrecision.set(9);
		// check primaries
		for (int i = 0; i < 3; i++) {
			float[] rgb = new float[3];
			rgb[i] = 1;
			float[] xyz = CS.toCIEXYZ(rgb);
			assertArrayEquals(Matrix.toFloat(sRgb50ColorSpace.getPrimary(i)), xyz, 1e-6f);
		}
		{	// check black point
			float[] rgb = {0, 0, 0};
			float[] xyz = CS.toCIEXYZ(rgb);
			assertArrayEquals(new float[] {0, 0, 0}, xyz, 1e-6f);
		}
		{	// check white point
			float[] rgb = {1, 1, 1};
			float[] xyz = CS.toCIEXYZ(rgb);
//			System.out.println(Matrix.toString(xyz));
//			System.out.println(Matrix.toString(sRgb50ColorSpace.getWhiteXYZ()));
			float[] tristimulusD50 = {0.9642f, 1f, 0.8249f};
			assertArrayEquals(tristimulusD50, xyz, 1e-6f);
		}
	}
	
	
	// ---------------------------------------------------
	
	private static void doCheck(ColorSpace cs, int[] srgb) {
		{
			float[] srgbA = RgbUtils.normalize(srgb);
			float[] xyz50 = cs.toCIEXYZ(srgbA);
			float[] srgbB = cs.fromCIEXYZ(xyz50);
			assertArrayEquals(Arrays.toString(srgb), srgbA, srgbB, TOL);
		}
		{
			float[] srgbA = RgbUtils.normalize(srgb);
			float[] rgb50 = cs.toRGB(srgbA);
			float[] srgbB = cs.fromRGB(rgb50);
			assertArrayEquals(srgbA, srgbB, TOL);
		}
	}
		

}

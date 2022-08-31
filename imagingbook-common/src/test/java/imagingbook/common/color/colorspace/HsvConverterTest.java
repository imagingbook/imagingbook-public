package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

public class HsvConverterTest {

	@Test
	public void test1() {
		HsvConverter hsvC = new HsvConverter();
		doCheck(hsvC, new int[] {0, 0, 0});
		doCheck(hsvC, new int[] {255, 255, 255});
		doCheck(hsvC, new int[] {177, 0, 0});
		doCheck(hsvC, new int[] {0, 177, 0});
		doCheck(hsvC, new int[] {0, 0, 177});
		doCheck(hsvC, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		HsvConverter hsvC = new HsvConverter();
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(hsvC, new int[] {r, g, b});
		}
	}
	
//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		HsvConverter hsvC = new HsvConverter();
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(hsvC, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	private static void doCheck(HsvConverter hsvC, int[] rgb1) {
		float[] hsv = hsvC.fromRGB(rgb1);
		int[] rgb2 = hsvC.toRGB(hsv);
		assertArrayEquals(rgb1, rgb2);
	}

}

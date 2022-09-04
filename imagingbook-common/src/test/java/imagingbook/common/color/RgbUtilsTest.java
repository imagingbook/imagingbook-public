package imagingbook.common.color;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class RgbUtilsTest {

	@Test
	public void test1() {
		Object hlsC = null;
		doCheck(hlsC, new int[] {0, 0, 0});
		doCheck(hlsC, new int[] {255, 255, 255});
		doCheck(hlsC, new int[] {177, 0, 0});
		doCheck(hlsC, new int[] {0, 177, 0});
		doCheck(hlsC, new int[] {0, 0, 177});
		doCheck(hlsC, new int[] {19, 3, 174});
	}
	
	@Test
	public void test2() {
		Random rd = new Random(17);
		for (int i = 0; i < 10000; i++) {
			int r = rd.nextInt(256);
			int g = rd.nextInt(256);
			int b = rd.nextInt(256);
			doCheck(null, new int[] {r, g, b});
		}
	}

//	@Test	// tests all possible rgb combinations
//	public void test3() {
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					doCheck(null, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	private static void doCheck(Object lcs, int[] RGB1) {
		float[] srgb = RgbUtils.normalize(RGB1);
		int[] RGB2 = RgbUtils.unnormalize(srgb);
		assertArrayEquals("RgbUtils conversion problem for RGB=" + Arrays.toString(RGB1), RGB1, RGB2);
	}
}

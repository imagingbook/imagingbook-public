package imagingbook.testutils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Locale;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.ij.IjUtils;


public class ImageTests {
	
	public static float TOLERANCE = 1E-6f;
	
	public static boolean match(ImageProcessor ip1, ImageProcessor ip2) {
		return match(ip1, ip2, TOLERANCE);
	}
	
	public static boolean match(ImageProcessor ip1, ImageProcessor ip2, double tolerance) {
		assertTrue("images are of different type", IjUtils.sameType(ip1, ip2));
		assertTrue("images are of different size", IjUtils.sameSize(ip1, ip2));
		int width = ip1.getWidth();
		int height = ip1.getHeight();
		
		if (ip1 instanceof ColorProcessor) {
			ColorProcessor cp1 = (ColorProcessor) ip1;
			ColorProcessor cp2 = (ColorProcessor) ip2;
			int[] rgb1 = new int[3];
			int[] rgb2 = new int[3];
			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					cp1.getPixel(u, v, rgb1);
					cp2.getPixel(u, v, rgb2);
					for (int k = 0; k < 3; k++) {
						int c1 = rgb1[k];
						int c2 = rgb2[k];
						if (Math.abs(c1 - c2) > tolerance) {
							fail(msgRgb(u, v, rgb1, rgb2));
						}
					}
				}
			}
		}
		
//		else 
//			if (ip1 instanceof ByteProcessor || ip1 instanceof ShortProcessor) {
//			for (int v = 0; v < height; v++) {
//				for (int u = 0; u < width; u++) {
//					int v1 = ip1.get(u, v);
//					int v2 = ip2.get(u, v);
//					if (v1 != v2) {
//						fail(msgInt(u, v, v1, v2));
//					}
//				}
//			}
//		}
		
		else if (ip1 instanceof ByteProcessor || ip1 instanceof ShortProcessor || ip1 instanceof FloatProcessor) {
			final float toleranceF = (float) tolerance;
			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					float v1 = ip1.getf(u, v);
					float v2 = ip2.getf(u, v);
					if (Math.abs(v1 - v2) > toleranceF) {
						fail(msgFloat(u, v, v1, v2));
					}
				}
			}
		}
		else {
			throw new IllegalArgumentException("unknown processor type " + ip1.getClass().getSimpleName());
		}
		
		return true;
	}
		
	private static String msgRgb(int u, int v, int[] rgb1, int[] rgb2) {
		return String.format(Locale.US, "different pixel values at pos. (%d, %d) : %s vs. %s", u, v,
				Arrays.toString(rgb1), Arrays.toString(rgb2));
	}
	
	@SuppressWarnings("unused")
	private static String msgInt(int u, int v, int v1, int v2) {
		return String.format(Locale.US, "different pixel values at pos. (%d, %d) : %d vs. %d", u, v, v1, v2);
	}
	
	private static String msgFloat(int u, int v, float v1, float v2) {
		return String.format(Locale.US, "different pixel values at pos. (%d, %d) : %.3f vs. %.3f", u, v, v1, v2);
	}
	
}

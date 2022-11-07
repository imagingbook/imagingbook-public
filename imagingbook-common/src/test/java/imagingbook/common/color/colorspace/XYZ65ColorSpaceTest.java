package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Arrays;

import org.junit.Test;

import imagingbook.common.color.RgbUtils;

public class XYZ65ColorSpaceTest {

	@Test
	public void test1a() {
		XYZ65ColorSpace cs = XYZ65ColorSpace.getInstance();
		checkRgb65(cs, new int[] {0, 0, 0});
		checkRgb65(cs, new int[] {255, 255, 255});
		checkRgb65(cs, new int[] {177, 0, 0});
		checkRgb65(cs, new int[] {0, 177, 0});
		checkRgb65(cs, new int[] {0, 0, 177});
		checkRgb65(cs, new int[] {19, 3, 174});
	}
	
	@Test
	public void test1b() {
		XYZ65ColorSpace cs = XYZ65ColorSpace.getInstance();
		checkXyz50(cs, new int[] {0, 0, 0});
		checkXyz50(cs, new int[] {255, 255, 255});
		checkXyz50(cs, new int[] {177, 0, 0});
		checkXyz50(cs, new int[] {0, 177, 0});
		checkXyz50(cs, new int[] {0, 0, 177});
		checkXyz50(cs, new int[] {19, 3, 174});
	}
	
//	@Test	// tests all possible rgb combinations (takes LONG!)
//	public void test3() {
//		XYZ65ColorSpace csp = XYZ65ColorSpace.getInstance();
//		for (int r = 0; r < 256; r++) {
//			for (int g = 0; g < 256; g++) {
//				for (int b = 0; b < 256; b++) {
//					checkRgb65(csp, new int[] {r, g, b});
//					checkXyz50(csp, new int[] {r, g, b});
//				}
//			}
//		}
//	}
	
	@Test
	public void test4() {	// check colors in book Table 14.3
		checkXyz(0.00, 0.00, 0.00,   0.0000,  0.0000,  0.0000);
		checkXyz(1.00, 0.00, 0.00,   0.4125,  0.2127,  0.0193);
		checkXyz(1.00, 1.00, 0.00,   0.7700,  0.9278,  0.1385);
		checkXyz(0.00, 1.00, 0.00,   0.3576,  0.7152,  0.1192);
		checkXyz(0.00, 1.00, 1.00,   0.5380,  0.7873,  1.0694);
		checkXyz(0.00, 0.00, 1.00,   0.1804,  0.0722,  0.9502);
		checkXyz(1.00, 0.00, 1.00,   0.5929,  0.2848,  0.9696);
		checkXyz(1.00, 1.00, 1.00,   0.9505,  1.0000,  1.0888);
		checkXyz(0.50, 0.50, 0.50,   0.2034,  0.2140,  0.2330);
		checkXyz(0.75, 0.00, 0.00,   0.2155,  0.1111,  0.0101);
		checkXyz(0.50, 0.00, 0.00,   0.0883,  0.0455,  0.0041);
		checkXyz(0.25, 0.00, 0.00,   0.0210,  0.0108,  0.0010);
		checkXyz(1.00, 0.50, 0.50,   0.5276,  0.3812,  0.2482);
	}
	
	// ---------------------
	
	private static void checkRgb65(XYZ65ColorSpace csp, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] xyz65 = csp.fromRGB(srgb1);
		float[] srgb2 = csp.toRGB(xyz65);
		assertArrayEquals("XYZ65 conversion problem for srgb=" + Arrays.toString(srgb), srgb1, srgb2, 1e-5f);
	}
	
	private static void checkXyz50(XYZ65ColorSpace csp, int[] srgb) {
		float[] srgb1 = RgbUtils.normalize(srgb);
		float[] xyz50a = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ).fromRGB(srgb1);	// standard D50-based XYZ space
		float[] xyz65 = csp.fromCIEXYZ(xyz50a);
		float[] xyz50b = csp.toCIEXYZ(xyz65);
		assertArrayEquals("XYZ50 conversion problem for srgb=" + Arrays.toString(srgb), xyz50a, xyz50b, 1e-5f);
	}
	
	// RGB are sRGB values
	private static void checkXyz(double R, double G, double B, double L, double a, double b) {
		XYZ65ColorSpace cs = XYZ65ColorSpace.getInstance();
		float[] srgb1 = new float[] {(float)R, (float)G, (float)B};
		float[] lab = cs.fromRGB(srgb1);
		assertArrayEquals(lab, new float[] {(float)L, (float)a, (float)b}, 1e-4f);
	}

}

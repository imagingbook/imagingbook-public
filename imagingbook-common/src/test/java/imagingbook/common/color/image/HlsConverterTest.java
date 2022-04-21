package imagingbook.common.color.image;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.colorspace.HlsConverter;

public class HlsConverterTest {

	@Test
	public void testFromRGBtoRGB() {  // tests all 16 mio RGB colors
		Random rg = new Random(17);
		HlsConverter hlsC = new HlsConverter();
		int[] rgb = new int[3];
		for (int i = 0; i < 1000; i++) {
			rgb[0] = rg.nextInt(256);
			rgb[1] = rg.nextInt(256);
			rgb[2] = rg.nextInt(256);
			float[] hls = hlsC.fromRGB(rgb);
			assertArrayEquals(rgb, hlsC.toRGB(hls));
		}
	}
}

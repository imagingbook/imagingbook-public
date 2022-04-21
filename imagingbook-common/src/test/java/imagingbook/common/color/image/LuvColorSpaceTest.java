package imagingbook.common.color.image;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.colorspace.LuvColorSpace;

public class LuvColorSpaceTest {

	@Test
	public void testRgbLuv() {
		Random rg = new Random(17);
		LuvColorSpace cs = new LuvColorSpace();
		for (int i = 0; i < 1000; i++) {
		   	int sr = rg.nextInt(256);
	    	int sg = rg.nextInt(256);
	    	int sb = rg.nextInt(256);   	
	    	float[] RGB1 = {sr/255f, sg/255f, sb/255f};
	    	float[] LUV = cs.fromRGB(new float[] {sr/255f, sg/255f, sb/255f});
	    	float[] RGB2 = cs.toRGB(LUV);
	    	assertArrayEquals(RGB1, RGB2, 0.0001f);
		}
	}

}

package imagingbook.common.image.access;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

public class RgbAccessorRandomValuesTest {
	
	int width = 300;
	int height = 200;
	
//	Path path = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
//	ImageProcessor ip = IjUtils.openImage(path).getProcessor();
	
	@Test
	public void testRgbRandomWriteAndRead() {
		ImageProcessor ip = new ColorProcessor(width, height);
		ImageAccessor ia = ImageAccessor.create(ip, null, null);
		
		assertEquals(width, ia.getWidth());
		assertEquals(height, ia.getHeight());
		assertEquals(3, ia.getDepth());
		
		Random rd = new Random(17);
		
		
		
		// write and read back random values on all pixels 
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				int r = (int)(rd.nextFloat() * 256);
				int g = (int)(rd.nextFloat() * 256);
				int b = (int)(rd.nextFloat() * 256);
				int[] rgbI = {r, g, b};
				float[] rgbF = {r, g, b};
				// set the pixel value
				ia.setPix(u, v, rgbF);
				// read back with ImageAccessor and check
				assertArrayEquals(rgbF, ia.getPix(u, v), 1E-6F);
				// read back directly from ImageProcessor and check
				assertArrayEquals(rgbI, ((ColorProcessor)ip).getPixel(u, v, null));
				// get interpolated value at (u,v) and check
				assertArrayEquals(rgbF, ia.getPix((double)u, (double)v), 1E-6F);
			}
		}
	}

}

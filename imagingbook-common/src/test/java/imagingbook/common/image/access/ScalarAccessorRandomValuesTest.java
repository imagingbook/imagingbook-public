package imagingbook.common.image.access;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;


/**
 * Write/read random values
 */
public class ScalarAccessorRandomValuesTest {
	
//	Path path = new imagingbook.DATA.images.Resources().getResourcePath("boats.png");
//	ImageProcessor ip = IjUtils.openImage(path).getProcessor();
	
	int width = 300;
	int height = 200;

	@Test
	public void testRandomScalarValuesByte() {
		run(new ByteProcessor(width, height));
	}
	
	@Test
	public void testRandomScalarValuesShort() {
		run(new ShortProcessor(width, height));
	}
	
	@Test
	public void testRandomScalarValuesFloat() {
		run(new FloatProcessor(width, height));
	}
	
	private void run(ImageProcessor ip) {
		ScalarAccessor ia = ScalarAccessor.create(ip, null, null);
		assertEquals(width, ia.getWidth());
		assertEquals(height, ia.getHeight());
		assertEquals(1, ia.getDepth());
		
		Random rd = new Random(17);
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				int v1 = (int)(rd.nextFloat() * 255);
				ia.setVal(u, v, v1);
				assertEquals(v1, ia.getVal(u, v), 1E-6F);
				assertEquals(v1, ia.getVal((double)u, (double)v), 1E-6F);	// interpolated
				assertEquals(v1, ip.getPixelValue(u, v), 1E-6F);
				if (!(ip instanceof FloatProcessor))
					assertEquals(v1, ip.getPixel(u, v));
			}
		}
	}

}

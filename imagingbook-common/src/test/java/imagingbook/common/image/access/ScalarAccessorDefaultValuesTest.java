package imagingbook.common.image.access;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 * Test default values (reading from coordinates outside the image boundaries)
 */
public class ScalarAccessorDefaultValuesTest {
	
	static float TOL = 1E-6F;
	
//	Path path = new imagingbook.DATA.images.Resources().getResourcePath("boats.png");
//	ImageProcessor ip = IjUtils.openImage(path).getProcessor();
	
	int width = 300;
	int height = 200;
	int defaultVal = 0;

	@Test
	public void testDefaultValueByte() {
		run(new ByteProcessor(width, height));
	}
	
	@Test
	public void testDefaultValueShort() {
		run(new ShortProcessor(width, height));
	}
	
	@Test
	public void testDefaultValueFloat() {
		run(new FloatProcessor(width, height));
	}
	
	private void run(ImageProcessor ip) {
		ScalarAccessor ia = ScalarAccessor.create(ip, OutOfBoundsStrategy.ZeroValues, null);
//		ia.setDefaultValue(defaultVal);
		assertEquals(defaultVal, ia.getVal(-1, 10), TOL);
		assertEquals(defaultVal, ia.getVal(width, 10), TOL);
		assertEquals(defaultVal, ia.getVal(3, -1), TOL);
		assertEquals(defaultVal, ia.getVal(0, height), TOL);
	}

}

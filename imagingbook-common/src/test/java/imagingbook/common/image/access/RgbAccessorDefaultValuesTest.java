package imagingbook.common.image.access;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * Test default values (reading from coordinates outside the image boundaries)
 */
public class RgbAccessorDefaultValuesTest {
	
	static float TOL = 1E-6F;
	
	int width = 300;
	int height = 200;
	ImageProcessor ip = new ColorProcessor(width, height);
	ImageAccessor ia = ImageAccessor.create(ip, OutOfBoundsStrategy.ZeroValues, null);
	
	int defVal = 0;
	int defR = 0;
	int defG = 0;
	int defB = 0;
	
//	Path path = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
//	ImageProcessor ip = IjUtils.openImage(path).getProcessor();
	
	@Test
	public void testRgbDefaultValueSingle() {
//		ia.setDefaultValue(defVal);
		assertEquals(defVal, ia.getPix(-1, 10)[0], TOL);
		assertEquals(defVal, ia.getPix(-1, 10)[1], TOL);
		assertEquals(defVal, ia.getPix(-1, 10)[2], TOL);
		
		assertEquals(defVal, ia.getVal(-1, 10, 0), TOL);
		assertEquals(defVal, ia.getVal(-1, 10, 1), TOL);
		assertEquals(defVal, ia.getVal(-1, 10, 2), TOL);
	}
	
	@Test
	public void testRgbDefaultValueAll() {	
//		ia.setDefaultValue(new float[] {defR, defG, defB});
		assertEquals(defR, ia.getPix(-1, 10)[0], TOL);
		assertEquals(defG, ia.getPix(-1, 10)[1], TOL);
		assertEquals(defB, ia.getPix(-1, 10)[2], TOL);
		
		assertEquals(defR, ia.getVal(-1, 10, 0), TOL);
		assertEquals(defG, ia.getVal(-1, 10, 1), TOL);
		assertEquals(defB, ia.getVal(-1, 10, 2), TOL);
	}

}

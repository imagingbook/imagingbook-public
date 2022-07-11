package imagingbook.common.image;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;
import imagingbook.testutils.ImageTestUtils;

// TODO: these tests are incomplete
public class ImageMathTest {
	
	static ImageResource ir1 = GeneralTestImage.Clown;
	static ImageResource ir2 = GeneralTestImage.Boats;
	
	static ImageProcessor[] images = {
			ir1.getImage().getProcessor(),							// ColorProcessor
			ir2.getImage().getProcessor(),							// ByteProcessor
			ir2.getImage().getProcessor().convertToFloatProcessor()	// FloatProcessor
			};

	@Test
	public void testAbs() {
		for (ImageProcessor ip : images) {
			ImageProcessor ip2 = ip.duplicate(); ip2.abs();		
			assertTrue(ImageTestUtils.match(ip2, ImageMath.abs(ip)));
		}
	}
	
	@Test
	public void testSqr() {
		for (ImageProcessor ip : images) {
			ImageProcessor ip2 = ip.duplicate(); ip2.sqr();		
			assertTrue(ImageTestUtils.match(ip2, ImageMath.sqr(ip)));
		}
	}
	
	@Test
	public void testSqrt() {
		for (ImageProcessor ip : images) {
			ImageProcessor ip2 = ip.duplicate(); ip2.sqrt();		
			assertTrue(ImageTestUtils.match(ip2, ImageMath.sqrt(ip)));
		}
	}
	

}

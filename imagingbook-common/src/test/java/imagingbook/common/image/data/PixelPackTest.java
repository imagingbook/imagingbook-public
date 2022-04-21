package imagingbook.common.image.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.DATA.GeneralTestImage;
import imagingbook.core.resource.ImageResource;
import imagingbook.testutils.ImageTests;

public class PixelPackTest {
	
	ImageResource res1 = GeneralTestImage.MonasterySmall;
	ImageResource res2 = GeneralTestImage.Clown;

	@Test
	public void testByteImage() {
		ImageProcessor ip1 = res1.getImage().getProcessor();
    	PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testShortImage() {
		ImageProcessor ip1 = res1.getImage().getProcessor().convertToShortProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testFloatImage() {
		ImageProcessor ip1 = res1.getImage().getProcessor().convertToFloatProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testColorImage() {
		ImageProcessor ip1 = res2.getImage().getProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}

}

package imagingbook.common.image;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;
import imagingbook.testutils.ImageTestUtils;

public class ColorStackTest {
	
	static double TOL = 1e-4;
	
	ImageResource res1 = GeneralTestImage.MonasterySmall;
	ImageResource res2 = GeneralTestImage.Clown;

	@Test
	public void test1() {
		ImageProcessor ip1 = res2.getImage().getProcessor();
		assertTrue(ip1 instanceof ColorProcessor);
		ColorStack cstack = new ColorStack((ColorProcessor)ip1);
		ImageProcessor ip2 = cstack.toColorProcessor();
		assertTrue(ImageTestUtils.match(ip1, ip2, TOL));
	}

}

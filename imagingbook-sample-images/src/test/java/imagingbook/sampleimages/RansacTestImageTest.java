package imagingbook.sampleimages;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.RansacTestImage;
import imagingbook.sampleimages.lib.TestUtils;

public class RansacTestImageTest {

	@Test
	public void test1() {
		for (ImageResource ir : RansacTestImage.values()) {
			assertNotNull("could not get URL for resource " + ir.toString(), ir.getURL());
			assertNotNull("could not open image for resource " + ir,  ir.getImage());
		}
	}
	
//	@Test
//	public void test2() {
//		TestUtils.testNamedResource(RansacTestImage.class);
//	}
	
	@Test
	public void test3() {
		TestUtils.testImageResource(RansacTestImage.class);
	}

}

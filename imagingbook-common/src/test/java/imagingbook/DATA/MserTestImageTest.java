package imagingbook.DATA;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;


/**
 * Checks if images for all named resources exist.
 * @author WB
 *
 */
public class MserTestImageTest {

	@Test
	public void test1() {
	for (ImageResource ir : MserTestImage.values()) {
		assertNotNull("could not find URL for resource " + ir.toString(), ir.getURL());
		assertNotNull("could not open image for resource " + ir,  ir.getImage());
	}
}

}

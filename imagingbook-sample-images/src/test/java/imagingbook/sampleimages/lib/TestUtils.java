package imagingbook.sampleimages.lib;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import imagingbook.core.resource.ImageResource;
import imagingbook.core.resource.NamedResource;

public abstract class TestUtils {
	
	/**
	 * Scans all resources defined by enumClass, validates that they exist.
	 */
	public static <E extends Enum<E>> void testNamedResource(Class<E> enumClass) {
		assertTrue("enum is not a NamedResource", NamedResource.class.isAssignableFrom(enumClass));
		if (NamedResource.class.isAssignableFrom(enumClass)) {
			for (E c : enumClass.getEnumConstants()) {
//				System.out.println(c.toString());
				NamedResource nr = (NamedResource) c;
				assertNotNull("could not find URL for resource " + nr.toString(), nr.getURL());
			}
				
		}
	}
	
	/**
	 * Scans all resources defined by enumClass, validates that they can be opened
	 * as images.
	 */
	public static <E extends Enum<E>> void testImageResource(Class<E> enumClass) {
		assertTrue("enum is not a ImageResource", ImageResource.class.isAssignableFrom(enumClass));
		if (ImageResource.class.isAssignableFrom(enumClass)) {
			for (E c : enumClass.getEnumConstants()) {
//				System.out.println(c.toString());
				ImageResource ir = (ImageResource) c;
				assertNotNull("could not open image for resource " + ir,  ir.getImage());
			}
		}
	}

}

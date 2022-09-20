package imagingbook.testutils;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;

/**
 * This class should be extended by some static class nested
 * inside a {@link ImageResource} enum type.
 * If so defined, the {@link ImageResource} will be tested automatically.
 * See {@link BinaryTestImage}, for example.
 * 
 * @author WB
 */
public abstract class ImageResourceSelfTest {
	
	/**
	 * This method must be called from on an instance of a public class nested inside 
	 * an ImageResource enum type.
	 * JUnit will instantiate this class and execute this test method.
	 */
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMe() {
		Class clazz = this.getClass().getEnclosingClass();
		if (clazz == null) {
			throw new RuntimeException(clazz + " has no enclosing class");
		}
		ResourceTestUtils.testImageResource(clazz);
//		ResourceTestUtils.testImageResource((Class)nestedItem.getClass().getEnclosingClass());
	}
	
//	/**
//	 * Scans all resources defined by enumClass, validates that they can be opened
//	 * as images.
//	 */
//	private static <E extends Enum<E>> void testImageResource(Class<E> enumClass) {
//		assertTrue("enum is not a ImageResource", ImageResource.class.isAssignableFrom(enumClass));
//		if (ImageResource.class.isAssignableFrom(enumClass)) {
//			for (E c : enumClass.getEnumConstants()) {
//				ImageResource ir = (ImageResource) c;
//				File file = new File(ir.getRelativePath());
//				assertNotNull("could not find file " + file.getAbsolutePath(), ir.getURL());
//				assertNotNull("could not open image for resource " + ir,  ir.getImage());
//			}
//		}
//	}

}

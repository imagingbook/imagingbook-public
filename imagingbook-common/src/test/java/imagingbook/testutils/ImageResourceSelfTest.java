package imagingbook.testutils;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.BinaryTestImage;

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

}

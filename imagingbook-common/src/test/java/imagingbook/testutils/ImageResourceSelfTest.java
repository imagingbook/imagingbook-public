package imagingbook.testutils;

import org.junit.Test;

/**
 * This class should be extended by some static class nested
 * inside a {@link ImageResource} enum type.
 * If so defined, the {@link ImageResource} will be tested automatically.
 * 
 * @author WB
 */
public abstract class ImageResourceSelfTest {
	
	/**
	 * This method must be called from on an instance of a class nested inside 
	 * an ImageResource enum type.
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

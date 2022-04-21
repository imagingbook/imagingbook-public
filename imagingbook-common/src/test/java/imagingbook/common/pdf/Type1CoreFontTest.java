package imagingbook.common.pdf;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class Type1CoreFontTest {

	@Test
	public void test1() {
		for (Type1CoreFont fnt : Type1CoreFont.values()) {
			assertNotNull("could not find URL for resource " + fnt, fnt.getURL());
			assertNotNull("could not create font for resource " + fnt, fnt.getBaseFont());
		}
	}

}

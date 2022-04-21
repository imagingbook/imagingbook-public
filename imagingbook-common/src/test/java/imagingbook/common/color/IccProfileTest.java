package imagingbook.common.color;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import imagingbook.core.resource.NamedResource;

public class IccProfileTest {

	@Test
	public void test1() {
		for (NamedResource ir : IccProfile.values()) {
			assertNotNull("could not find URL for resource " + ir.toString(), ir.getURL());
		}
	}

}

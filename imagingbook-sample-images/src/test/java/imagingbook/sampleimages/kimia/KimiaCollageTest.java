/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.sampleimages.kimia;

import imagingbook.testutils.ResourceTestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KimiaCollageTest {
	
	@Test
	public void testKimiaCollage() {
		int n = ResourceTestUtils.testImageResource(KimiaCollage.class);
        assertEquals(KimiaCollage.values().length, n);
	}
	
}

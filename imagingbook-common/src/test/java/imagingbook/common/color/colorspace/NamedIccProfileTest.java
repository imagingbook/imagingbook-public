/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.colorspace;

import static org.junit.Assert.assertNotNull;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

import org.junit.Test;

import imagingbook.testutils.ResourceTestUtils;

public class NamedIccProfileTest {
	
	@Test
	public void test1() {
		ResourceTestUtils.testNamedResource(NamedIccProfile.class);
	}

	@Test
	public void testProfileInstantiation() {
		for (NamedIccProfile p : NamedIccProfile.values()) {
			ICC_Profile profile = p.getProfile();
			assertNotNull(profile);
		}
	}
	
	@Test
	public void testColorspaceInstantiation() {
		for (NamedIccProfile p : NamedIccProfile.values()) {
			ICC_ColorSpace colorspace = p.getColorSpace();
			assertNotNull(colorspace);
		}
	}
}

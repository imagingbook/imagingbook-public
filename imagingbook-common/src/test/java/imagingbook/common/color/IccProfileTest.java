/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
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

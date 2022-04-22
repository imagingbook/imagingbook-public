/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.sampleimages;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.RansacTestImage;
import imagingbook.sampleimages.lib.TestUtils;

public class RansacTestImageTest {

	@Test
	public void test1() {
		for (ImageResource ir : RansacTestImage.values()) {
			assertNotNull("could not get URL for resource " + ir.toString(), ir.getURL());
			assertNotNull("could not open image for resource " + ir,  ir.getImage());
		}
	}
	
//	@Test
//	public void test2() {
//		TestUtils.testNamedResource(RansacTestImage.class);
//	}
	
	@Test
	public void test3() {
		TestUtils.testImageResource(RansacTestImage.class);
	}

}

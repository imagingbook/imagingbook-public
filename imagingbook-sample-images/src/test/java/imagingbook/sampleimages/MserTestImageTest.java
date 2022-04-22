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


/**
 * Checks if images for all named resources exist.
 * @author WB
 *
 */
public class MserTestImageTest {

	@Test
	public void test1() {
	for (ImageResource ir : MserTestImage.values()) {
		assertNotNull("could not find URL for resource " + ir.toString(), ir.getURL());
		assertNotNull("could not open image for resource " + ir,  ir.getImage());
	}
}

}

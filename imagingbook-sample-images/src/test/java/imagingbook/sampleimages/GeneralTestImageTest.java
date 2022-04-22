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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;

public class GeneralTestImageTest {

	@Test
	public void test1() {
		for (ImageResource res : GeneralTestImage.values()) {
			assertNotNull("could not find URL for resource " + res.toString(), res.getURL());
			assertNotNull("could not open image for resource " + res,  res.getImage());
		}
	}
	
	@Test
	public void test2() throws IOException {
		for (ImageResource res : GeneralTestImage.values()) {
			try (InputStream strm = res.getStream()) {
				assertNotNull("could not obtain InputStream for resource " + res.toString(), strm);
				byte[] bytes = strm.readAllBytes();
				assertNotNull("could read all bytes from InputStream for resource " + res.toString(), bytes);
//				System.out.println("read bytes " + bytes.length);
				assertTrue("empty input from stream for resource " + res.toString(), bytes.length > 0);
			}
		}
	}


}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.sampleimages;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;

import ij.IJ;
import ij.ImagePlus;
import imagingbook.core.resource.ImageResource;
//import imagingbook.sampleimages.testutils.ResourceTestUtils;
import imagingbook.testutils.ResourceTestUtils;

@Deprecated
public class GeneralSampleImageTest {
	
	@Test
	public void test1() {
		ResourceTestUtils.testImageResource(GeneralSampleImage.class);
	}
	
	@Test
	public void test2() {
		for (ImageResource ir : GeneralSampleImage.values()) {
			URL url = ir.getURL();
			ImagePlus im = IJ.openImage(url.toString());
			assertNotNull(im);
		}
	}
	
//	@Test
//	public void test2() throws IOException {
//		for (ImageResource res : GeneralSampleImage.values()) {
//			try (InputStream strm = res.getStream()) {
//				assertNotNull("could not obtain InputStream for resource " + res.toString(), strm);
//				byte[] bytes = strm.readAllBytes();
//				assertNotNull("could read all bytes from InputStream for resource " + res.toString(), bytes);
////				System.out.println("read bytes " + bytes.length);
//				assertTrue("empty input from stream for resource " + res.toString(), bytes.length > 0);
//			}
//		}
//	}


}

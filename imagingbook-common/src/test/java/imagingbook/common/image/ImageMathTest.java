/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageTestUtils;

// TODO: these tests are incomplete
public class ImageMathTest {
	
	static ImageResource ir1 = GeneralSampleImage.Clown;
	static ImageResource ir2 = GeneralSampleImage.Boats;
	
	static ImageProcessor[] images = {
			ir1.getImagePlus().getProcessor(),							// ColorProcessor
			ir2.getImagePlus().getProcessor(),							// ByteProcessor
			ir2.getImagePlus().getProcessor().convertToFloatProcessor()	// FloatProcessor
			};

	@Test
	public void testAbs() {
		for (ImageProcessor ip : images) {
			ImageProcessor ip2 = ip.duplicate(); ip2.abs();		
			assertTrue(ImageTestUtils.match(ip2, ImageMath.abs(ip)));
		}
	}
	
	@Test
	public void testSqr() {
		for (ImageProcessor ip : images) {
			ImageProcessor ip2 = ip.duplicate(); ip2.sqr();		
			assertTrue(ImageTestUtils.match(ip2, ImageMath.sqr(ip)));
		}
	}
	
	@Test
	public void testSqrt() {
		for (ImageProcessor ip : images) {
			ImageProcessor ip2 = ip.duplicate(); ip2.sqrt();		
			assertTrue(ImageTestUtils.match(ip2, ImageMath.sqrt(ip)));
		}
	}
	
	@Test
	public void testAdd1() {
		// TODO: test missing!
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFail1() {
		ImageMath.add(images[0], images[1]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFail2() {
		ImageMath.add(images[0], images[2]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFail3() {
		ImageMath.add(images[1], images[2]);
	}
}

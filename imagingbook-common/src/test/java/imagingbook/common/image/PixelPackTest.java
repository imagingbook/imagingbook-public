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

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;
import imagingbook.testutils.ImageTestUtils;

public class PixelPackTest {
	
	ImageResource res1 = GeneralTestImage.MonasterySmall;
	ImageResource res2 = GeneralTestImage.Clown;

	@Test
	public void testByteImage() {
		ImageProcessor ip1 = res1.getImage().getProcessor();
		assertTrue(ip1 instanceof ByteProcessor);
    	PixelPack pack = new PixelPack(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	pack.copyToImageProcessor(ip2);
    	ImageProcessor ip2 = pack.toByteProcessor();
    	assertTrue(ImageTestUtils.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testShortImage() {
		ImageProcessor ip1 = res1.getImage().getProcessor().convertToShortProcessor();
		assertTrue(ip1 instanceof ShortProcessor);
		PixelPack pack = new PixelPack(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	pack.copyToImageProcessor(ip2);
    	ImageProcessor ip2 = pack.toShortProcessor();
    	assertTrue(ImageTestUtils.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testFloatImage() {
		ImageProcessor ip1 = res1.getImage().getProcessor().convertToFloatProcessor();
		assertTrue(ip1 instanceof FloatProcessor);
		PixelPack pack = new PixelPack(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	pack.copyToImageProcessor(ip2);
    	ImageProcessor ip2 = pack.toFloatProcessor();
    	assertTrue(ImageTestUtils.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testColorImage() {
		ImageProcessor ip1 = res2.getImage().getProcessor();
		assertTrue(ip1 instanceof ColorProcessor);
		PixelPack pack = new PixelPack(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	pack.copyToImageProcessor(ip2);
    	ImageProcessor ip2 = pack.toColorProcessor();
    	assertTrue(ImageTestUtils.match(ip1, ip2, 1E-6));
	}

}

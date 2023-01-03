/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.testimages;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ResourceTestUtils;

/**
 * Enumeration defining a set of {@link ImageResource} objects associated
 * with SIFT test images.
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum SiftTestImage implements ImageResource {
	
	Box00,
	Box15,
	Box30,
	Box45,
	Box60,
	Box75,
	Box90,
	HalfDiskH,
	HalfDiskV,
	Ireland02tiny,
	RectangleH,
	RectangleV,
	Stars,
	StarsH,
	StarsV,
	;

	// ---------------------------------------------------
	
	public static class SelfTest {
		@Test
		public void test() {
			ResourceTestUtils.testImageResource(SiftTestImage.class);
		}
	}
	
}
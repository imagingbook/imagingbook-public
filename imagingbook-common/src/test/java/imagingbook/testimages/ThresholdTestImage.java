/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.testimages;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ResourceTestUtils;

/**
 * Enumeration defining a set of {@link ImageResource} objects associated
 * with test images for automatic thresholding.
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum ThresholdTestImage implements ImageResource {
	/** Original gray scan from Kepler manuscript. */
	kepler,
	/** Binary Kepler image with values 0 and 1 only. */
	keplerBin_000_001,
	/** Binary Kepler image with values 254 and 255 only. */
	keplerBin_254_255,
	/** Binary Kepler image with values 17 and 18 only. */
	keplerBin_017_018,
	/** Binary Kepler image darkened by 100 units. */
	keplerBinMinus100,
	/** Binary Kepler image brightened by 100 units. */
	keplerBinPlus100,
	/** Binary Kepler image with values 0 and 255. */
	keplerBin,
	/** Flat image with 0 values only. */
	flat000,
	/** Flat image with 31 values only. */
	flat031,
	/** Flat image with 255 values only. */
	flat255,	
	;
		
	// ---------------------------------------------------
	
	public static class SelfTest {
		@Test
		public void test() {
			ResourceTestUtils.testImageResource(ThresholdTestImage.class);
		}
	}
}

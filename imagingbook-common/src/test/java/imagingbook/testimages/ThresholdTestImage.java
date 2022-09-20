/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.testimages;

import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageResourceSelfTest;

/**
 * Enumeration defining a set of {@link ImageResource} objects associated
 * with test images for automatic thresholding.
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum ThresholdTestImage implements ImageResource {
	/** Original gray scan from Kepler manuscript. */
	kepler,
	/** Binary Kepler image with values 17 and 18 only. */
	keplerBin1718,
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
		
	// -----------------------------------------------------------------
	
	/**
	 * This definition causes this ImageResource to be tested automatically.
	 * The class must be public and static, the name is arbitrary.
	 */
	public static class SelfTest extends ImageResourceSelfTest {
	}
}

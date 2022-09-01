/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.sampleimages;

import imagingbook.core.resource.ImageResource;

/**
 * Enumeration defining a set of {@link ImageResource} objects associated
 * with test images for automatic thresholding.
 * @see ImageResource
 * @see GeneralTestImage
 */
public enum ThresholdTestImage implements ImageResource {
	/** Original gray scan from Kepler manuscript. */
	kepler("kepler.png"),
	/** Binary Kepler image with values 17 and 18 only. */
	keplerBin_17_18("keplerBin-17-18.png"),
	/** Binary Kepler image darkened by 100 units. */
	keplerBinMinus100("keplerBinMinus100.png"),
	/** Binary Kepler image brightened by 100 units. */
	keplerBinPlus100("keplerBinPlus100.png"),
	/** Binary Kepler image with values 0 and 255. */
	keplerBin("keplerBin.png"),
	/** Flat image with 0 values only. */
	flat000("flat000.png"),
	/** Flat image with 31 values only. */
	flat031("flat031.png"),
	/** Flat image with 255 values only. */
	flat255("flat255.png"),
	;
	
private final String filename;
	
	ThresholdTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
}

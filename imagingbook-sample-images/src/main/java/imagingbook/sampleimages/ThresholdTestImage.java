/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.sampleimages;

import imagingbook.core.resource.ImageResource;

/**
 * <p>
 * Enumeration defining a set of {@link ImageResource} objects for SIFT test images.
 * Usage example:
 * </p>
 * <pre>
 * import ij.process.ImageProcessor;
 * import imagingbook.core.resource.ImageResource;
 * import imagingbook.sampleimages.GeneralTestImage;
 * 
 * ImageResource ir = SiftTestImage.Stars;
 * ImageProcessor ip = ir.getImage().getProcessor();
 * // process ip ...
 * </pre>
 * @see ImageResource
 *
 */
public enum ThresholdTestImage implements ImageResource {
	kepler("kepler.png"),
	keplerBin_17_18("keplerBin-17-18.png"),
	keplerBinMinus100("keplerBinMinus100.png"),
	keplerBinPlus100("keplerBinPlus100.png"),
	keplerBin("keplerBin.png"),
	flat000("flat000.png"),
	flat031("flat031.png"),
	flat255("flat255.png"),
	;
	
	private static final String BASEDIR = "threshold/";
	
	private final String relPath;
	
	@Override
	public String getRelativePath() {
		return relPath;
	}
	
	ThresholdTestImage(String filename) {
		this.relPath = BASEDIR + filename;
	}
}

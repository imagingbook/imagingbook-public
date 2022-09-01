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
 * with RANSAC test images.
 * @see ImageResource
 * @see GeneralTestImage
 */
public enum RansacTestImage implements ImageResource {
	
	NoisyLines("noisy-lines.png"), 
	NoisyCircles("noisy-circles.png"), 
	NoisyEllipses("noisy-ellipses.png");
	
	private final String filename;
	
	RansacTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
}

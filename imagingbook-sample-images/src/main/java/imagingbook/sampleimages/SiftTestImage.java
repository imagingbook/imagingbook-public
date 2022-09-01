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
 * with SIFT test images.
 * @see ImageResource
 * @see GeneralTestImage
 */
public enum SiftTestImage implements ImageResource {
	Box00("box00.png"),
	Box15("box15.png"),
	Box30("box30.png"),
	Box45("box45.png"),
	Box60("box60.png"),
	Box75("box75.png"),
	Box90("box90.png"),
	HalfDiskH("halfdiskH.png"),
	HalfDiskV("halfdiskV.png"),
	Ireland02tiny("ireland02tiny.png"),
	RectangleH("rectangleH.png"),
	RectangleV("rectangleV.png"),
	Stars("stars.png"),
	StarsH("starsH.png"),
	StarsV("starsV.png"),
	;
	
private final String filename;
	
	SiftTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
}

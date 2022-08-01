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


public enum SiftTestImage implements ImageResource {
	box00("box00.png"),
	box15("box15.png"),
	box30("box30.png"),
	box45("box45.png"),
	box60("box60.png"),
	box75("box75.png"),
	box90("box90.png"),
	halfdiskH("halfdiskH.png"),
	halfdiskV("halfdiskV.png"),
	ireland02tiny("ireland02tiny.png"),
	rectangleH("rectangleH.png"),
	rectangleV("rectangleV.png"),
	stars("stars.png"),
	starsH("starsH.png"),
	starsV("starsV.png"),
	;
	
	private static final String BASEDIR = "sift/";
	
	private final String relPath;
	
	@Override
	public String getRelativePath() {
		return relPath;
	}
	
	SiftTestImage(String filename) {
		this.relPath = BASEDIR + filename;
	}
}

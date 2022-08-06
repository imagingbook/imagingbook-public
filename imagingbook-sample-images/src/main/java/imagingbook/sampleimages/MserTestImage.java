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
 * Enumeration defining a set of {@link ImageResource} objects for MSER test images.
 * Usage example:
 * </p>
 * <pre>
 * import ij.process.ImageProcessor;
 * import imagingbook.core.resource.ImageResource;
 * import imagingbook.sampleimages.GeneralTestImage;
 * 
 * ImageResource ir = MserTestImage.BoatsTiny;
 * ImageProcessor ip = ir.getImage().getProcessor();
 * // process ip ...
 * </pre>
 * @see ImageResource
 *
 */
public enum MserTestImage implements ImageResource {
	AllBlack("all-black.png"),
	Blob1("blob1.png"),  
	Blob3("blob3.png"),            
	BlobLevelTestNoise("blob-level-test-noise.png"),
	BlobsInWhite("blobs-in-white.png"),  
	BoatsTinyB("boats-tiny-b.png"),   
	BoatsTinyW("boats-tiny-w.png"),
	AllWhite("all-white.png"),  
	Blob2("blob2.png"),  
	BlobLevelTest("blob-level-test.png"),  
	BlobOriented("blob-oriented.png"),          
	BoatsTiny("boats-tiny.png"),      
	BoatsTinyBW("boats-tiny-bw.png"),  
	BoatsTinyW2("boats-tiny-w2.png");
	
	private static final String BASEDIR = "mser/";
	
	private final String relPath;
	
	@Override
	public String getRelativePath() {
		return relPath;
	}
	
	MserTestImage(String filename) {
		this.relPath = BASEDIR + filename;
	}
}

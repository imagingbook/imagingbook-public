/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.testimages;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ResourceTestUtils;

/**
 * Enumeration defining a set of {@link ImageResource} objects associated
 * with MSER test images.
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum MserTestImage implements ImageResource {
	AllBlack("all-black.png"),
	AllWhite("all-white.png"),  
	Blob1("blob1.png"),
	Blob2("blob2.png"),
	Blob3("blob3.png"),          
	BlobLevelTestNoise("blob-level-test-noise.png"),
	BlobsInWhite("blobs-in-white.png"),  
	BoatsTinyB("boats-tiny-b.png"),   
	BoatsTinyW("boats-tiny-w.png"),
	BlobLevelTest("blob-level-test.png"),  
	BlobOriented("blob-oriented.png"),          
	BoatsTiny("boats-tiny.png"),      
	BoatsTinyBW("boats-tiny-bw.png"),  
	BoatsTinyW2("boats-tiny-w2.png");

	// ---------------------------------------------------
	
	private final String filename;
	
	MserTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
	
	// ---------------------------------------------------
	
	public static class SelfTest {		
		@Test
		public void testMe() {
			ResourceTestUtils.checkImageResource(this);
		}
	}
	
}

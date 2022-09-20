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
 * with MSER test images.
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum MserTestImage implements ImageResource {
	
	AllBlack,
	AllWhite,  
	Blob1,
	Blob2,
	Blob3,         
	BlobLevelTestNoise,
	BlobsInWhite,  
	BoatsTinyB,  
	BoatsTinyW,
	BlobLevelTest,  
	BlobOriented,       
	BoatsTiny,    
	BoatsTinyBW, 
	BoatsTinyW2,
	;
	
	// ---------------------------------------------------
	
	/**
	 * This definition causes this ImageResource to be tested automatically.
	 * The class must be public and static, the name is arbitrary.
	 */
	public static class SelfTest extends ImageResourceSelfTest {
	}
	
}

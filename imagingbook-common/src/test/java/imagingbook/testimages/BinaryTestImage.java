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
 * Enumeration defining a set of {@link ImageResource} objects for testing 
 * binary segmentation and morphology detection methods.
 * 
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum BinaryTestImage implements ImageResource {
	
	BinaryTest,
	
	BinaryTestDilation1,
	BinaryTestDilation2,
	
	BinaryTestErosion1,
	BinaryTestErosion2,
	
	BinaryTestClosing1,
	BinaryTestClosing2,
	
	BinaryTestOpening1,
	BinaryTestOpening2,
	
	BinaryTestOutlineNH4,
	BinaryTestOutlineNH8,
	
	BinaryTestThinning,
	
	Cat,
	CatThinning,
	CatDilation3,
	CatErosion3,
	CatClosing3,
	CatOpening3,
	CatOutlineNH4,
	CatOutlineNH8,
	
	SegmentationMed,
	SegmentationSmall,
	
	SimpleTestGridImg,
	;
	
	// ---------------------------------------------------
	
	private final String filename;
	
	BinaryTestImage(String filename) {
		this.filename = filename;
	}
	
	BinaryTestImage() {
		this.filename = autoName();
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
	
	// ---------------------------------------------------
		
	/**
	 * This definition causes this ImageResource to be tested automatically.
	 * The class must be public and static, the name is arbitrary.
	 */
	public static class SelfTest extends ImageResourceSelfTest {
	}
	
}

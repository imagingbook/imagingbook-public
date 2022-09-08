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
 * Enumeration defining a set of {@link ImageResource} objects for testing 
 * binary segmentation and morphology detection methods.
 * 
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum BinaryTestImage implements ImageResource {
	Cat("cat.png"),
	CatSkeleton("cat-skeleton.png"),
	
	SegmentationMed("segmentation-med.png"),
	SegmentationSmall("segmentation-small.png"),
	
	SimpleTestGridImg("simple-test-grid-img.png"),
	;
	
	// ---------------------------------------------------
	
	private final String filename;
	
	BinaryTestImage(String filename) {
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

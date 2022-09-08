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
 * edge detection methods.
 * 
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum EdgeDetectionTestImage implements ImageResource {
	Balloons600color("balloons-600.png"),
	Balloons600gray("balloons-600-gray.png"),
	
	Balloons600colorCannyEdgeMagnitude("balloons-600-CannyEdgeMagnitude.tif"),
	Balloons600colorCannyEdgeOrientation("balloons-600-CannyEdgeOrientation.tif"),
	Balloons600colorCannyBinaryEdges("balloons-600-CannyBinaryEdges.png"),
	
	Balloons600grayCannyEdgeMagnitude("balloons-600-gray-CannyEdgeMagnitude.tif"),
	Balloons600grayCannyEdgeOrientation("balloons-600-gray-CannyEdgeOrientation.tif"),
	Balloons600grayCannyBinaryEdges("balloons-600-gray-CannyBinaryEdges.png"),
	
	Balloons600colorMultigradientEdgeMagnitude("balloons-600-MultigradientEdgeMagnitude.tif"),
	Balloons600colorMultigradientEdgeOrientation("balloons-600-MultigradientEdgeOrientation.tif"),
	
	Balloons600colorMonochromaticEdgeMagnitude("balloons-600-MonochromaticEdgeMagnitude.tif"),
	Balloons600colorMonochromaticEdgeOrientation("balloons-600-MonochromaticEdgeOrientation.tif"),
	
	Balloons600colorGrayscaleEdgeMagnitude("balloons-600-GrayscaleEdgeMagnitude.tif"),
	Balloons600colorGrayscaleEdgeOrientation("balloons-600-GrayscaleEdgeOrientation.tif"),
	;

	// ---------------------------------------------------
	
	private final String filename;
	
	EdgeDetectionTestImage(String filename) {
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
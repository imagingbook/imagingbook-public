/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
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
	
	// test images
	Balloons600color,
	Balloons600gray,
	
	// CannyEdgeDetector
	Balloons600colorCannyEdgeMagnitude_tif,
	Balloons600colorCannyEdgeOrientation_tif,
	Balloons600colorCannyBinaryEdges,
	
	Balloons600grayCannyEdgeMagnitude_tif,
	Balloons600grayCannyEdgeOrientation_tif,
	Balloons600grayCannyBinaryEdges,
	
	// MultiGradientEdgeDetector
	Balloons600colorMultigradientEdgeMagnitude_tif,
	Balloons600colorMultigradientEdgeOrientation_tif,
	
	// MonochromaticEdgeDetector (color only)
	Balloons600colorMonochromaticEdgeMagnitudeL2_tif,
	Balloons600colorMonochromaticEdgeOrientationL2_tif,
	
	// GrayscaleEdgeDetector
	Balloons600colorGrayscaleEdgeMagnitude_tif,
	Balloons600colorGrayscaleEdgeOrientation_tif,
	
	Balloons600grayGrayscaleEdgeMagnitude_tif,
	Balloons600grayGrayscaleEdgeOrientation_tif,

	;
	
	// ---------------------------------------------------
	
	public static class SelfTest {
		@Test
		public void test() {
			ResourceTestUtils.testImageResource(EdgeDetectionTestImage.class);
		}
	}
	
}
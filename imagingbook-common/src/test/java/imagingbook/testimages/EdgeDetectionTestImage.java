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
 * edge detection methods.
 * 
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum EdgeDetectionTestImage implements ImageResource {
	Balloons600color,
	Balloons600gray,
	
	// CannyEdgeDetector
	Balloons600colorCannyEdgeMagnitude_tif, //("Balloons600colorCannyEdgeMagnitude.tif"),
	Balloons600colorCannyEdgeOrientation_tif, //("Balloons600colorCannyEdgeOrientation.tif"),
	Balloons600colorCannyBinaryEdges,
	
	Balloons600grayCannyEdgeMagnitude_tif, //("Balloons600grayCannyEdgeMagnitude.tif"),
	Balloons600grayCannyEdgeOrientation_tif, //("Balloons600grayCannyEdgeOrientation.tif"),
	Balloons600grayCannyBinaryEdges,
	
	// MultiGradientEdgeDetector
	Balloons600colorMultigradientEdgeMagnitude_tif,	//("Balloons600colorMultigradientEdgeMagnitude.tif"),
	Balloons600colorMultigradientEdgeOrientation_tif,	//("Balloons600colorMultigradientEdgeOrientation.tif"),
	
	// MonochromaticEdgeDetector (color only)
	Balloons600colorMonochromaticEdgeMagnitudeL2_tif,	//("Balloons600colorMonochromaticEdgeMagnitudeL2.tif"),
	Balloons600colorMonochromaticEdgeOrientationL2_tif,	//("Balloons600colorMonochromaticEdgeOrientationL2.tif"),
	
	// GrayscaleEdgeDetector
	Balloons600colorGrayscaleEdgeMagnitude_tif,	//("Balloons600colorGrayscaleEdgeMagnitude.tif"),
	Balloons600colorGrayscaleEdgeOrientation_tif,	//("Balloons600colorGrayscaleEdgeOrientation.tif"),
	
	Balloons600grayGrayscaleEdgeMagnitude_tif,	//("Balloons600grayGrayscaleEdgeMagnitude.tif"),
	Balloons600grayGrayscaleEdgeOrientation_tif,	//("Balloons600grayGrayscaleEdgeOrientation.tif"),

	;

	//java.lang.AssertionError: could not find file C:\_GITHUB\imagingbook-super\imagingbook-public\imagingbook-common\EdgeDetectionTestImage\Balloons600grayCannyEdgeMagnitude.tif
	

	// ---------------------------------------------------
	
	private final String filename;
	
	EdgeDetectionTestImage(String filename) {
		this.filename = filename;
	}
	
	EdgeDetectionTestImage() {
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
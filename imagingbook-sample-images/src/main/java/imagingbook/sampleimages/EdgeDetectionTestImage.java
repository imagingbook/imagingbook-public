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
 * Enumeration defining a set of {@link ImageResource} objects for testing 
 * edge detection methods.
 * 
 * @see ImageResource
 * @see GeneralTestImage
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
	
	Balloons600colorDiZenzoEdgeMagnitude("balloons-600-DiZenzoEdgeMagnitude.tif"),
	Balloons600colorDiZenzoEdgeOrientation("balloons-600-DiZenzoEdgeOrientation.tif"),
//	Balloons600colorDiZenzoBinaryEdges("balloons-600-DiZenzoBinaryEdges.png"),
	;
	
	private final String filename;
	
	EdgeDetectionTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
}

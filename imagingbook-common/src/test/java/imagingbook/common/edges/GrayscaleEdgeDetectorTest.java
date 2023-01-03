/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.edges;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.testimages.EdgeDetectionTestImage;
import imagingbook.testutils.ImageTestUtils;


public class GrayscaleEdgeDetectorTest {
	
	static double TOL = 0.001;

	static {	// default parameters used to produce test images
		// no parameters to set
	}
	
	@Test		// run test on color image
	public void testColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImagePlus().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		
		GrayscaleEdgeDetector detector = new GrayscaleEdgeDetector(ip);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorGrayscaleEdgeMagnitude_tif.getImagePlus().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorGrayscaleEdgeOrientation_tif.getImagePlus().getProcessor(), eOrt, TOL));
	}
	
	@Test		// run test on gray image
	public void testGray() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600gray.getImagePlus().getProcessor();
		assertTrue(ip instanceof ByteProcessor);
		
		GrayscaleEdgeDetector detector = new GrayscaleEdgeDetector(ip);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600grayGrayscaleEdgeMagnitude_tif.getImagePlus().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600grayGrayscaleEdgeOrientation_tif.getImagePlus().getProcessor(), eOrt, TOL));
	}

}

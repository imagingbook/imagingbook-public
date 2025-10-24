/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.edges;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.testimages.EdgeDetectionTestImage;
import imagingbook.testutils.ImageTestUtils;


public class MultiGradientEdgeDetectorTest {
	
	static double TOL = 1e-3;
	
	static MultiGradientEdgeDetector.Parameters params = new MultiGradientEdgeDetector.Parameters();
	static {	// default parameters used to produce DiZenzo test images
		// no parameters to set
	}
	
	@Test
	public void testMultigradientColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImagePlus().getProcessor();
		assertTrue("must be a ColorProcessor", ip instanceof ColorProcessor);
		
		MultiGradientEdgeDetector detector = new MultiGradientEdgeDetector((ColorProcessor) ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMultigradientEdgeMagnitude_tif.getImagePlus().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMultigradientEdgeOrientation_tif.getImagePlus().getProcessor(), eOrt, TOL));
	}

}

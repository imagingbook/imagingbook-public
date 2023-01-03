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

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.testimages.EdgeDetectionTestImage;
import imagingbook.testutils.ImageTestUtils;


public class MonochromaticEdgeDetectorTest {
	
	private static final double TOL = 1e-3;

	@Test
	public void testColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImagePlus().getProcessor();
		assertTrue("must be a ColorProcessor", ip instanceof ColorProcessor);
		
		MonochromaticEdgeDetector detector = new MonochromaticEdgeDetector((ColorProcessor) ip);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMonochromaticEdgeMagnitudeL2_tif.getImagePlus().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMonochromaticEdgeOrientationL2_tif.getImagePlus().getProcessor(), eOrt, TOL));
	}

}

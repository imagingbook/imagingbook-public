/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.corners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.corners.SubpixelMaxInterpolator.Method;
import imagingbook.sampleimages.GeneralSampleImage;

public class HarrisCornerDetectorTest {
	
	static GradientCornerDetector.Parameters params = new GradientCornerDetector.Parameters();
	static {
		params.doPreFilter = true;
		params.sigma = 1.275;
		params.border = 20;
		params.doCleanUp = true;
		params.dmin = 10;
		params.maxLocatorMethod = Method.None;
		params.scoreThreshold = 100;
	}
	
	ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();

	@Test
	public void testNoCornerRefinement() {
		params.maxLocatorMethod = Method.None;
		assertTrue(ip instanceof ByteProcessor);
		HarrisCornerDetector detector = new HarrisCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(164, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement1() {
		params.maxLocatorMethod = Method.QuadraticLeastSquares;
		assertTrue(ip instanceof ByteProcessor);
		HarrisCornerDetector detector = new HarrisCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(161, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement2() {
		params.maxLocatorMethod = Method.QuadraticTaylor;
		assertTrue(ip instanceof ByteProcessor);
		HarrisCornerDetector detector = new HarrisCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(160, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement3() {
		params.maxLocatorMethod = Method.Quartic;
		assertTrue(ip instanceof ByteProcessor);
		HarrisCornerDetector detector = new HarrisCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(157, corners.size());
	}

}

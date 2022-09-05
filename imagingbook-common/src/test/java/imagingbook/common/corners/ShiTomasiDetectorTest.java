package imagingbook.common.corners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.corners.subpixel.SubpixelMaxInterpolator.Method;
import imagingbook.sampleimages.GeneralTestImage;

public class ShiTomasiDetectorTest {
	
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
	
	ImageProcessor ip = GeneralTestImage.MonasterySmall.getImage().getProcessor();

	@Test
	public void testNoCornerRefinement() {
		params.maxLocatorMethod = Method.None;
		assertTrue(ip instanceof ByteProcessor);
		ShiTomasiCornerDetector detector = new ShiTomasiCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(148, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement1() {
		params.maxLocatorMethod = Method.QuadraticLeastSquares;
		assertTrue(ip instanceof ByteProcessor);
		ShiTomasiCornerDetector detector = new ShiTomasiCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(146, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement2() {
		params.maxLocatorMethod = Method.QuadraticTaylor;
		assertTrue(ip instanceof ByteProcessor);
		ShiTomasiCornerDetector detector = new ShiTomasiCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(147, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement3() {
		params.maxLocatorMethod = Method.Quartic;
		assertTrue(ip instanceof ByteProcessor);
		ShiTomasiCornerDetector detector = new ShiTomasiCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(145, corners.size());
	}

}

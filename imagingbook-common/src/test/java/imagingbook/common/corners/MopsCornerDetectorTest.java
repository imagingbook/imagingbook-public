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

public class MopsCornerDetectorTest {
	
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
		MopsCornerDetector detector = new MopsCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(125, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement1() {
		params.maxLocatorMethod = Method.QuadraticLeastSquares;
		assertTrue(ip instanceof ByteProcessor);
		MopsCornerDetector detector = new MopsCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(126, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement2() {
		params.maxLocatorMethod = Method.QuadraticTaylor;
		assertTrue(ip instanceof ByteProcessor);
		MopsCornerDetector detector = new MopsCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(127, corners.size());
	}
	
	@Test
	public void testWithCornerRefinement3() {
		params.maxLocatorMethod = Method.Quartic;
		assertTrue(ip instanceof ByteProcessor);
		MopsCornerDetector detector = new MopsCornerDetector(ip, params);
		List<Corner> corners = detector.getCorners();
		assertNotNull(corners);
		assertEquals(127, corners.size());
	}

}
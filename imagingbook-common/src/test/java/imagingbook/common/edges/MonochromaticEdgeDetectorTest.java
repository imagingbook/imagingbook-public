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
	
	// default parameters used to produce test images
	private static final MonochromaticEdgeDetector.Parameters params = new MonochromaticEdgeDetector.Parameters();

	@Test
	public void testColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImagePlus().getProcessor();
		assertTrue("must be a ColorProcessor", ip instanceof ColorProcessor);
		
		MonochromaticEdgeDetector detector = new MonochromaticEdgeDetector((ColorProcessor) ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMonochromaticEdgeMagnitudeL2_tif.getImagePlus().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMonochromaticEdgeOrientationL2_tif.getImagePlus().getProcessor(), eOrt, TOL));
	}

}

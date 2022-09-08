package imagingbook.common.color.edge;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.testimages.EdgeDetectionTestImage;
import imagingbook.testutils.ImageTestUtils;


public class GrayscaleEdgeDetectorTest {
	
	static double TOL = 1e-3;
	
	static GrayscaleEdgeDetector.Parameters params = new GrayscaleEdgeDetector.Parameters();
	static {	// default parameters used to produce test images
		// no parameters to set
	}
	
	@Test
	public void testMultigradientColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImage().getProcessor();
		assertTrue("must be a ColorProcessor", ip instanceof ColorProcessor);
		
		GrayscaleEdgeDetector detector = new GrayscaleEdgeDetector((ColorProcessor) ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorGrayscaleEdgeMagnitude.getImage().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorGrayscaleEdgeOrientation.getImage().getProcessor(), eOrt, TOL));
	}

}

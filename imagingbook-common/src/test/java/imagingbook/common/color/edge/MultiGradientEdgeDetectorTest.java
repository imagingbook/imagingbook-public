package imagingbook.common.color.edge;

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
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImage().getProcessor();
		assertTrue("must be a ColorProcessor", ip instanceof ColorProcessor);
		
		MultiGradientEdgeDetector detector = new MultiGradientEdgeDetector((ColorProcessor) ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMultigradientEdgeMagnitude.getImage().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorMultigradientEdgeOrientation.getImage().getProcessor(), eOrt, TOL));
	}

}

package imagingbook.common.edges;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.testutils.ImageTestUtils;
import imagingbook.testimages.EdgeDetectionTestImage;


public class GrayscaleEdgeDetectorTest {
	
	static double TOL = 0.001;
	
	static GrayscaleEdgeDetector.Parameters params = new GrayscaleEdgeDetector.Parameters();
	static {	// default parameters used to produce test images
		// no parameters to set
	}
	
	@Test		// run test on color image
	public void testColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImage().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		
		GrayscaleEdgeDetector detector = new GrayscaleEdgeDetector(ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorGrayscaleEdgeMagnitude_tif.getImage().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorGrayscaleEdgeOrientation_tif.getImage().getProcessor(), eOrt, TOL));
	}
	
	@Test		// run test on gray image
	public void testGray() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600gray.getImage().getProcessor();
		assertTrue(ip instanceof ByteProcessor);
		
		GrayscaleEdgeDetector detector = new GrayscaleEdgeDetector(ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600grayGrayscaleEdgeMagnitude_tif.getImage().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600grayGrayscaleEdgeOrientation_tif.getImage().getProcessor(), eOrt, TOL));
	}

}

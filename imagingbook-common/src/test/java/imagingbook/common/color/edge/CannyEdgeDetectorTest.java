package imagingbook.common.color.edge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.sampleimages.EdgeDetectionTestImage;
import imagingbook.testutils.ImageTestUtils;


public class CannyEdgeDetectorTest {
	
	static double TOL = 1e-3;
	
//	static ImageResource ballonsColor = EdgeDetectionTestImage.Balloons600color;
//	static ImageResource ballonsGray = EdgeDetectionTestImage.Balloons600gray;
	
	static CannyEdgeDetector.Parameters params = new CannyEdgeDetector.Parameters();
	static {	// default parameters used to produce Canny test images
		params.gSigma = 2.0f;
		params.hiThr  = 20.0f;
		params.loThr  = 5.0f;
		params.normGradMag = true;
	}
	
	@Test
	public void testCannyColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImage().getProcessor();
		assertTrue("must be a ColorProcessor", ip instanceof ColorProcessor);
		
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorCannyEdgeMagnitude.getImage().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorCannyEdgeOrientation.getImage().getProcessor(), eOrt, TOL));

		ByteProcessor eBin = detector.getEdgeBinary();
		assertNotNull(eBin);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorCannyBinaryEdges.getImage().getProcessor(), eBin, TOL));
		
		List<EdgeTrace> traces = detector.getTraces();
		assertNotNull(traces);
		assertEquals(138, traces.size());
	}

	@Test
	public void testCannyGray() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600gray.getImage().getProcessor();
		assertTrue("must be a ByteProcessor", ip instanceof ByteProcessor);
		
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600grayCannyEdgeMagnitude.getImage().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600grayCannyEdgeOrientation.getImage().getProcessor(), eOrt, TOL));

		ByteProcessor eBin = detector.getEdgeBinary();
		assertNotNull(eBin);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600grayCannyBinaryEdges.getImage().getProcessor(), eBin, TOL));
		
		List<EdgeTrace> traces = detector.getTraces();
		assertNotNull(traces);
		assertEquals(203, traces.size());
		
	}
}
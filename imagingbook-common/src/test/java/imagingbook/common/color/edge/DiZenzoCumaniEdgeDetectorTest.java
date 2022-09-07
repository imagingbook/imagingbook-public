package imagingbook.common.color.edge;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.testimages.EdgeDetectionTestImage;
import imagingbook.testutils.ImageTestUtils;


public class DiZenzoCumaniEdgeDetectorTest {
	
	static double TOL = 1e-3;
	
	static DiZenzoCumaniEdgeDetector.Parameters params = new DiZenzoCumaniEdgeDetector.Parameters();
	static {	// default parameters used to produce DiZenzo test images
		// no parameters to set
	}
	
	@Test
	public void testDiZenzoColor() {
		ImageProcessor ip = EdgeDetectionTestImage.Balloons600color.getImage().getProcessor();
		assertTrue("must be a ColorProcessor", ip instanceof ColorProcessor);
		
		DiZenzoCumaniEdgeDetector detector = new DiZenzoCumaniEdgeDetector((ColorProcessor) ip, params);
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		assertNotNull(eMag);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorDiZenzoEdgeMagnitude.getImage().getProcessor(), eMag, TOL));

		FloatProcessor eOrt = detector.getEdgeOrientation();
		assertNotNull(eOrt);
		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorDiZenzoEdgeOrientation.getImage().getProcessor(), eOrt, TOL));

//		ByteProcessor eBin = detector.getEdgeBinary();
//		assertNotNull(eBin);
//		assertTrue(ImageTestUtils.match(EdgeDetectionTestImage.Balloons600colorCannyBinaryEdges.getImage().getProcessor(), eBin, TOL));
		
//		List<EdgeTrace> traces = detector.getTraces();
//		assertNotNull(traces);
//		assertEquals(138, traces.size());
	}

}

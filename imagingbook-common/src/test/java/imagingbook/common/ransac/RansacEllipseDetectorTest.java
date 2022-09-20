package imagingbook.common.ransac;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.testimages.RansacTestImage;

public class RansacEllipseDetectorTest {
	
	static double TOL = 1e-3;

	@Test
	public void test1() {	
		ByteProcessor bp = (ByteProcessor) RansacTestImage.NoisyEllipses.getImage().getProcessor();
		
		RansacEllipseDetector.Parameters params = new RansacEllipseDetector.Parameters();
		params.randomPointDraws = 1000;
		params.maxInlierDistance = 2.0;
		params.minInlierCount = 100;
		params.removeInliers = true;
		params.randomSeed = 17;
		
		int maxCount = 3;
		
		RansacEllipseDetector detector = new RansacEllipseDetector(params);	
		List<RansacResult<GeometricEllipse>> circles = detector.detectAll(bp, maxCount);
		
		assertEquals(2, circles.size());
		assertEquals(241, circles.get(0).getScore(), TOL);
		assertEquals(186, circles.get(1).getScore(), TOL);
	}

}

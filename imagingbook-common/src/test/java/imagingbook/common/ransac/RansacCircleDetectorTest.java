package imagingbook.common.ransac;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.testimages.RansacTestImage;

public class RansacCircleDetectorTest {
	
	static double TOL = 1e-3;

	@Test
	public void test1() {	
		ByteProcessor bp = (ByteProcessor) RansacTestImage.NoisyCircles.getImage().getProcessor();
		
		RansacCircleDetector.Parameters params = new RansacCircleDetector.Parameters();
		params.randomPointDraws = 1000;
		params.maxInlierDistance = 2.0;
		params.minInlierCount = 70;
		params.removeInliers = true;
		params.randomSeed = 17;
		
		int maxCount = 3;
		
		RansacCircleDetector detector = new RansacCircleDetector(params);	
		List<RansacResult<GeometricCircle>> circles = detector.detectAll(bp, maxCount);
		
		assertEquals(maxCount, circles.size());
		assertEquals(284, circles.get(0).getScore(), TOL);
		assertEquals(181, circles.get(1).getScore(), TOL);
		assertEquals(100, circles.get(2).getScore(), TOL);
	}

}

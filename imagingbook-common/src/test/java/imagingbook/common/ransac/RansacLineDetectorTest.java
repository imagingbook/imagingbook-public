package imagingbook.common.ransac;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.testimages.RansacTestImage;

public class RansacLineDetectorTest {

	static double TOL = 1e-3;
	
	@Test
	public void test1() {	
		ByteProcessor bp = (ByteProcessor) RansacTestImage.NoisyLines.getImage().getProcessor();
		
		RansacLineDetector.Parameters params = new RansacLineDetector.Parameters();
		params.randomPointDraws = 1000;
		params.maxInlierDistance = 2.0;
		params.minInlierCount = 100;
		params.minPairDistance = 25;
		params.removeInliers = true;
		params.randomSeed = 17;
		
		int maxCount = 6;
		
		RansacLineDetector detector = new RansacLineDetector(params);	
		List<RansacResult<AlgebraicLine>> lines = detector.detectAll(bp, maxCount);
		
		assertEquals(maxCount, lines.size());
		assertEquals(536, lines.get(0).getScore(), TOL);
		assertEquals(506, lines.get(1).getScore(), TOL);
		assertEquals(384, lines.get(2).getScore(), TOL);
	}

}

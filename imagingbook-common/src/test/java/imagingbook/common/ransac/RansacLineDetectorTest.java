package imagingbook.common.ransac;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.testimages.RansacTestImage;

public class RansacLineDetectorTest {

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
		
		assertEquals(6, lines.size());
	}

}

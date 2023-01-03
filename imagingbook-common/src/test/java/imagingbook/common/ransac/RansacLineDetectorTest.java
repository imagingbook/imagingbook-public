/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
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
		ByteProcessor bp = (ByteProcessor) RansacTestImage.NoisyLines.getImagePlus().getProcessor();
		
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

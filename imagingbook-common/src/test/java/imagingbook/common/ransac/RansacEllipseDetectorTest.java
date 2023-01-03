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
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.testimages.RansacTestImage;

public class RansacEllipseDetectorTest {
	
	static double TOL = 1e-3;

	@Test
	public void test1() {	
		ByteProcessor bp = (ByteProcessor) RansacTestImage.NoisyEllipses.getImagePlus().getProcessor();
		
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

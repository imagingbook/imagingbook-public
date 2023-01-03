/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.hough;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class HoughTransformLinesTest {
	
	private static ImageResource resource = GeneralSampleImage.NoisyLines;
	
	@Test
	public void test1() {
		HoughTransformLines.Parameters params = new HoughTransformLines.Parameters();
		params.nAng = 256;
		params.nRad = 128;
		int MaxLines = 5;			// number of strongest lines to be found
		int MinPointsOnLine = 50;	// min. number of points on each line
		
		ImageProcessor ip = resource.getImagePlus().getProcessor();
		assertTrue(ip instanceof ByteProcessor);
		
		HoughTransformLines ht = new HoughTransformLines((ByteProcessor)ip, params);
		HoughLine[] lines = ht.getLines(MinPointsOnLine, MaxLines);
		assertEquals(5, lines.length);
		
		assertEquals(251, lines[0].getCount());
		assertEquals(243, lines[1].getCount());
		assertEquals(230, lines[2].getCount());
		assertEquals(188, lines[3].getCount());
		assertEquals(128, lines[4].getCount());
	}

}

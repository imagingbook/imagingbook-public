/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image;

import static imagingbook.common.math.Arithmetic.sqr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

// TODO: Complete tests!
public class IntegralImageTest {
	
	private static ImageResource ir = GeneralSampleImage.Boats;

	@Test
	public void test1() {
		ByteProcessor bp = (ByteProcessor) ir.getImagePlus().getProcessor();
		IntegralImage iim = new IntegralImage(bp);
		
		assertNotNull(iim.getS1());
		assertNotNull(iim.getS2());
		
		int width = bp.getWidth();
		int height = bp.getHeight();
		
		// check sums over all single-pixel blocks 
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				
				// single pixel rectangle (size = 1):
				assertEquals(1, iim.getSize(u, v, u, v));
				assertEquals(bp.get(u, v), iim.getBlockSum1(u, v, u, v));
				assertEquals(sqr(bp.get(u, v)), iim.getBlockSum2(u, v, u, v));
				
				// empty rectangle
				assertEquals(0, iim.getBlockSum1(u, v, u - 1, v));
				assertEquals(0, iim.getBlockSum1(u, v, u, v - 1));
				assertEquals(0, iim.getBlockSum2(u, v, u - 1, v));
				assertEquals(0, iim.getBlockSum2(u, v, u, v - 1));
				
				// 0 outside first quadrant
				assertEquals(iim.getBlockSum1(0, 0, u, v), iim.getBlockSum1(-10, -99, u, v));
				assertEquals(iim.getBlockSum2(0, 0, u, v), iim.getBlockSum2(-10, -99, u, v));
				
				// TODO: index problem, check!
//				assertEquals(iim.getBlockSum1(u, v, width + 33, height + 17), iim.getBlockSum1(u, v, width - 1, height - 1));
//				assertEquals(iim.getBlockSum2(u, v, width + 33, height + 17), iim.getBlockSum2(u, v, width - 1, height - 1));

			}
		}
		
//		System.out.println(iim.getBlockSum1(0, 0, width - 1, height - 1));
//		System.out.println(iim.getBlockSum2(0, 0, width - 1, height - 1));
		assertEquals(49777530L, iim.getBlockSum1(0, 0, width - 1, height - 1));
		assertEquals(6978017698L, iim.getBlockSum2(0, 0, width - 1, height - 1));
		
//		System.out.println(iim.getBlockSum1(10, 7, 66, 41));	// 277697
//		System.out.println(iim.getBlockSum2(10, 7, 66, 41));	// 38818717
		assertEquals(277697, iim.getBlockSum1(10, 7, 66, 41));
		assertEquals(38818717, iim.getBlockSum2(10, 7, 66, 41));
		
	}
	

}

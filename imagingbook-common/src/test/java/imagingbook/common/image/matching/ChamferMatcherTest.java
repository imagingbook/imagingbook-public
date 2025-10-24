/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.testimages.BinaryTestImage;

public class ChamferMatcherTest {
	
	static float TOL = 1e-6f;
	static ByteProcessor I = (ByteProcessor) BinaryTestImage.CatThinning.getImagePlus().getProcessor();
	static int MI = I.getWidth();
	static int NI = I.getHeight();

	@Test
	public void testL1() {
		ChamferMatcher matcher = new ChamferMatcher(I, DistanceTransform.DistanceType.L1);

		int x0 = 23, y0 = 20;	// top/left corner of extraction
		int MR = 14, NR = 16;
		
		ByteProcessor R = IjUtils.crop(I, x0, y0, MR, NR);
		assertNotNull(R instanceof ByteProcessor);
		assertEquals(MR, R.getWidth());
		assertEquals(NR, R.getHeight());
		
		float[][] Q = matcher.getMatch(R);
		assertNotNull(Q);
		assertEquals(MI - MR + 1, Q.length);
		assertEquals(NI - NR + 1, Q[0].length);
		
//		System.out.println((MI - MR + 1));
//		System.out.println((NI - NR + 1));
//		IJ.save(new ImagePlus("result", new FloatProcessor(Q)), "D:/tmp/skeletonQ.tif");
		
		// optimal match must be at extraction point:
		assertEquals(0.0f, Q[x0][y0], TOL);
		
		// check score values at corners of Q:
		assertEquals(210f, Q[0][0], TOL);
		assertEquals(437f, Q[MI - MR][0], TOL);
		assertEquals(747f, Q[0][NI - NR], TOL);
		assertEquals(393f, Q[MI - MR][NI - NR], TOL);
	}
	
	@Test
	public void testL2() {
		ChamferMatcher matcher = new ChamferMatcher(I, DistanceTransform.DistanceType.L2);

		int x0 = 23, y0 = 20;	// top/left corner of extraction
		int MR = 14, NR = 16;
		
		ByteProcessor R = IjUtils.crop(I, x0, y0, MR, NR);
		assertNotNull(R instanceof ByteProcessor);
		assertEquals(MR, R.getWidth());
		assertEquals(NR, R.getHeight());
		
		float[][] Q = matcher.getMatch(R);
		assertNotNull(Q);
		assertEquals(MI - MR + 1, Q.length);
		assertEquals(NI - NR + 1, Q[0].length);
		
//		System.out.println((MI - MR + 1));
//		System.out.println((NI - NR + 1));
//		IJ.save(new ImagePlus("result", new FloatProcessor(Q)), "D:/tmp/skeletonQ.tif");
		
		// optimal match must be at extraction point:
		assertEquals(0.0f, Q[x0][y0], TOL);
		
		// check score values at corners of Q:
		assertEquals(175.26706f, Q[0][0], TOL);
		assertEquals(383.32092f, Q[MI - MR][0], TOL);
		assertEquals(664.33527f, Q[0][NI - NR], TOL);
		assertEquals(356.92395f, Q[MI - MR][NI - NR], TOL);
	}

}

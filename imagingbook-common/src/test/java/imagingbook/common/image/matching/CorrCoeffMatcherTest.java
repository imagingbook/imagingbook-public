/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.sampleimages.GeneralSampleImage;

public class CorrCoeffMatcherTest {
	
	static float TOL = 1e-3f;
	static ByteProcessor I = (ByteProcessor) GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
	static int MI = I.getWidth();
	static int NI = I.getHeight();

	@Test
	public void test1() {
		FloatProcessor fp = I.convertToFloatProcessor();
		CorrCoeffMatcher matcher = new CorrCoeffMatcher(fp);
		
		int x0 = 23, y0 = 20;	// top/left corner of extraction
		int MR = 14, NR = 16;
		
		FloatProcessor R = IjUtils.crop(fp, x0, y0, MR, NR);
		assertNotNull(R instanceof FloatProcessor);
		assertEquals(MR, R.getWidth());
		assertEquals(NR, R.getHeight());
		
		float[][] Q = matcher.getMatch(R);
		assertNotNull(Q);
		assertEquals(MI - MR + 1, Q.length);
		assertEquals(NI - NR + 1, Q[0].length);
		
		// optimal match must be at extraction point:
		assertEquals(1.0f, Q[x0][y0], TOL);
		
		// check score values at corners of Q:
		assertEquals(0.2773945f, Q[0][0], TOL);
		assertEquals(0.18839705f, Q[MI - MR][0], TOL);
		assertEquals(-0.34577155f, Q[0][NI - NR], TOL);
		assertEquals(0.22718114, Q[MI - MR][NI - NR], TOL);
	}

}

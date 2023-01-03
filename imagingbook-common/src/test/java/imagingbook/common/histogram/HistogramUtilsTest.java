/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.histogram;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.math.Matrix;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class HistogramUtilsTest {

	@Test
	public void test1() {
		ImageResource ir = GeneralSampleImage.Boats;
		
		ImageProcessor ip = ir.getImagePlus().getProcessor();
		int[] h = ip.getHistogram();
		
		assertEquals("wrong histogram count (all)", ip.getHeight() * ip.getWidth(), HistogramUtils.count(h));
		assertEquals("wrong histogram count (range)", 412708, HistogramUtils.count(h, 10, 200));
		
		assertEquals("wrong histogram max", 7480, HistogramUtils.max(h));
		
		int[] H = HistogramUtils.cumulate(h);
		assertEquals("wrong H[0]", h[0], H[0]);
		assertEquals("wrong H[K-1]", HistogramUtils.count(h), H[H.length - 1]);
		
		double[] pdf = HistogramUtils.pdf(h);
		assertEquals("pdf does not sum to 1", 1.0, Matrix.sum(pdf), 1e-6);
		
		double[] cdf = HistogramUtils.cdf(h);
		assertEquals("cdf[K-1] should be 1", 1.0, cdf[cdf.length - 1], 1e-6);
		
		assertEquals("wrong mean", 120.026837, HistogramUtils.mean(h), 1e-6);
		assertEquals("wrong variance (fast)", 2419.411167, HistogramUtils.variance(h), 1e-6);
		assertEquals("wrong variance (slow)", 2419.411167, HistogramUtils.varianceSlow(h, 0, h.length-1), 1e-6);
		
		assertEquals("wrong median", 138, HistogramUtils.median(h));
	}

	@Test
	public void testMatchHistograms() {
		System.out.println("*** test missing: testMatchHistograms");
	}
	
	@Test
	public void testMatchPiecewiseLinearHistograms() {
		System.out.println("*** test missing: testMatchPiecewiseLinearHistograms");
	}
}



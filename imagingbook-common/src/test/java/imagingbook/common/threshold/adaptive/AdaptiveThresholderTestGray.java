/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.threshold.adaptive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class AdaptiveThresholderTestGray {

	@Test
	public void testBernsenThresholder() {
		AdaptiveThresholder thresholder = new BernsenThresholder();
		runThreshold(thresholder, GeneralSampleImage.Blobs, 43574);
		runThreshold(thresholder, GeneralSampleImage.Boats, 177739);		// 147239
	}
	
	// @Test
	// public void testInterpolatingThresholder() {
	// 	AdaptiveThresholder thresholder = new InterpolatingThresholder();
	// 	runThreshold(thresholder, GeneralSampleImage.Blobs, 42300);			// 42354
	// 	runThreshold(thresholder, GeneralSampleImage.Boats, 157430);		// 164349
	// }
	
	@Test
	public void testNiblackThresholderBox() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Box();
		runThreshold(thresholder, GeneralSampleImage.Blobs, 44063);		// 44121
		runThreshold(thresholder, GeneralSampleImage.Boats, 319677);	// 323029
	}
	
	@Test
	public void testNiblackThresholderDisk() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Disk();
		runThreshold(thresholder, GeneralSampleImage.Blobs, 44492);		// 44571
		runThreshold(thresholder, GeneralSampleImage.Boats, 325444);	// 328857
	}
	
	@Test
	public void testNiblackThresholderGauss() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Gauss();
		runThreshold(thresholder, GeneralSampleImage.Blobs, 44300);		// 44358
		runThreshold(thresholder, GeneralSampleImage.Boats, 325418);	// 328838
	}
	
	@Test
	public void testSauvolaThresholder() {
		AdaptiveThresholder thresholder = new SauvolaThresholder();
		runThreshold(thresholder, GeneralSampleImage.Blobs, 45302);		// 45399
		runThreshold(thresholder, GeneralSampleImage.Boats, 386671);	// 387246
	}
	
	// ------------------------
	
	private void runThreshold(AdaptiveThresholder thresholder, ImageResource res, int expectedZeros) {
		ByteProcessor bp = res.getImagePlus().getProcessor().convertToByteProcessor();
		thresholder.threshold(bp);
		int zeros = countZeros(bp);
//		System.out.println(res + ": " + zeros + " / " + (bp.getWidth() * bp.getHeight()));
		assertEquals("threshold to zero pixels (" + res + ")", expectedZeros, zeros);
	}
	
	private int countZeros(ByteProcessor bp) {
		byte[] pixels = (byte[]) bp.getPixels();
		int cnt = 0;
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == 0) {
				cnt++;
			}
		}
		return cnt;
	}

}

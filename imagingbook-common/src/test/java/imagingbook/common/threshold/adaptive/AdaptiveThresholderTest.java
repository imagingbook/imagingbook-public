/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.threshold.adaptive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class AdaptiveThresholderTest {

	@Test
	public void testBernsenThresholder() {
		AdaptiveThresholder thresholder = new BernsenThresholder();
		runThreshold(thresholder, GeneralSampleImage.blobs, 43574);
		runThreshold(thresholder, GeneralSampleImage.boats, 147239);	
	}
	
	@Test
	public void testInterpolatingThresholder() {
		AdaptiveThresholder thresholder = new InterpolatingThresholder();
		runThreshold(thresholder, GeneralSampleImage.blobs, 42354);
		runThreshold(thresholder, GeneralSampleImage.boats, 164349);	
	}
	
	@Test
	public void testNiblackThresholderBox() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Box();
		runThreshold(thresholder, GeneralSampleImage.blobs, 44121);
		runThreshold(thresholder, GeneralSampleImage.boats, 323029);	
	}
	
	@Test
	public void testNiblackThresholderDisk() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Disk();
		runThreshold(thresholder, GeneralSampleImage.blobs, 44571);
		runThreshold(thresholder, GeneralSampleImage.boats, 328857);	
	}
	
	@Test
	public void testNiblackThresholderGauss() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Gauss();
		runThreshold(thresholder, GeneralSampleImage.blobs, 44358);
		runThreshold(thresholder, GeneralSampleImage.boats, 328838);	
	}
	
	@Test
	public void testSauvolaThresholder() {
		AdaptiveThresholder thresholder = new SauvolaThresholder();
		runThreshold(thresholder, GeneralSampleImage.blobs, 45399);
		runThreshold(thresholder, GeneralSampleImage.boats, 387246);	
	}
	
	// ------------------------
	
	private void runThreshold(AdaptiveThresholder thresholder, ImageResource res, int expectedZeros) {
		ByteProcessor bp = res.getImage().getProcessor().convertToByteProcessor();
		thresholder.threshold(bp);
		int zeros = countZeros(bp);
//		System.out.println(res + ": " + zeros + " / " + (bp.getWidth() * bp.getHeight()));
		assertEquals("threhold to zero pixels (" + res + ")", expectedZeros, zeros);
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

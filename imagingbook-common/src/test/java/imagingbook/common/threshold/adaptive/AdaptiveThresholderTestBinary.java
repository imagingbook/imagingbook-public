/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.threshold.adaptive;

import ij.process.ByteProcessor;
import imagingbook.common.threshold.Thresholder;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testimages.ThresholdTestImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AdaptiveThresholderTestBinary {

	@Test
	public void testBernsenThresholder() {
		BernsenThresholder.Parameters params = new BernsenThresholder.Parameters();
		params.radius = 15;
		params.cmin = 1;
		params.bgMode = Thresholder.BackgroundMode.BRIGHT;
		AdaptiveThresholder thresholder = new BernsenThresholder(params);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 32306);
	}

	@Test
	public void testInterpolatingThresholder() {
		AdaptiveThresholder thresholder = new InterpolatingThresholder();
		runThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 57374);	// PROBLEM!
		runThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 57374);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 40638);
	}

	@Test
	public void testNiblackThresholderBox() {
		NiblackThresholder.Parameters params = new NiblackThresholder.Parameters();
		params.radius = 15;
		params.kappa = 0.3;
		params.dMin = 0.1;
		params.bgMode = Thresholder.BackgroundMode.BRIGHT;
		AdaptiveThresholder thresholder = new NiblackThresholder.Box(params);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 32306);
	}

	@Test
	public void testNiblackThresholderDisk() {
		NiblackThresholder.Parameters params = new NiblackThresholder.Parameters();
		params.radius = 15;
		params.kappa = 0.3;
		params.dMin = 0.1;
		params.bgMode = Thresholder.BackgroundMode.BRIGHT;
		AdaptiveThresholder thresholder = new NiblackThresholder.Disk(params);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 32306);
	}

	@Test
	public void testNiblackThresholderGauss() {
		NiblackThresholder.Parameters params = new NiblackThresholder.Parameters();
		params.radius = 15;
		params.kappa = 0.3;
		params.dMin = 0.1;
		params.bgMode = Thresholder.BackgroundMode.BRIGHT;
		AdaptiveThresholder thresholder = new NiblackThresholder.Gauss(params);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 32306);
	}

	// @Test	// Sauvola makes no sense on binary images, since the local mean determines the rel. threshold
	// public void testSauvolaThresholder() {
	// 	SauvolaThresholder.Parameters params = new SauvolaThresholder.Parameters();
	// 	params.radius = 15;
	// 	params.kappa = 0.1;
	// 	params.sigmaMax = 128;
	// 	params.bgMode = Thresholder.BackgroundMode.BRIGHT;
	// 	AdaptiveThresholder thresholder = new SauvolaThresholder(params);
	// 	runThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 32328);
	// 	runThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 32328);
	// 	runThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 32328);
	// }
	
	// ------------------------
	
	private void runThreshold(AdaptiveThresholder thresholder, ImageResource res, int expectedZeros) {
		ByteProcessor bp = res.getImagePlus().getProcessor().convertToByteProcessor();
		thresholder.threshold(bp);
		int zeros = countZeros(bp);
		System.out.println(res + ": " + zeros + " / " + (bp.getWidth() * bp.getHeight()));
		// assertEquals("threshold to zero pixels (" + res + ")", expectedZeros, zeros);
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

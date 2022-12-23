/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.threshold.global;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.ThresholdTestImage;

public class GlobalThresholderBinaryTest {
	
	@Test
	public void testIsodataThresholder() {
		GlobalThresholder thresholder = new IsodataThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 127, 127);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 77, 177);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 177, 77);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 203, 51);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 123, 131);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 223, 31);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}
	
	@Test
	public void testMaxMedianThresholder() {
		GlobalThresholder thresholder = new MedianThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 154, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 127, 127);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 77, 177);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 177, 77);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder(0.10);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_000_001, 0, 254);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_254_255, 254, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_017_018, 17, 237);
	}

	// ----------------------------------------------------
	
	private void checkThreshold(GlobalThresholder thresholder, ImageResource res, int thresh1, int thresh2) {
		ByteProcessor bp = res.getImagePlus().getProcessor().convertToByteProcessor();
		// check it a valid threshold was found:
		int q1 = thresholder.getThreshold(bp);
		assertEquals("threshold 1 not as expected (" + res + ")", thresh1, q1);
		// System.out.format("threshold 1 not as expected (" + res + "): %d vs %d\n", thresh1, q1);
		//
		bp.invert();
		int q2 = thresholder.getThreshold(bp);
		assertEquals("threshold 2 not as expected (" + res + ")", thresh2, q2);
		// System.out.format("threshold 2 not as expected (" + res + "): %d vs %d\n\n", thresh2, q2);
	}

}

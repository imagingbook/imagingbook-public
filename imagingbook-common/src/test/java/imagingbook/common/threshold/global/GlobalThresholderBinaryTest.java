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
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 127);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 77);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 177);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, 17);
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, 17);
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 203);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 123);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 223);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, 17);
	}
	
	@Test
	public void testMaxMedianThresholder() {
		GlobalThresholder thresholder = new MedianThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, -1);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, -1);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, -1);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, -1);
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, 17);
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 127);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 77);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 177);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, 17);
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, 17);
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder(0.10);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin1718, 17);
	}

	// ----------------------------------------------------
	
	private void checkThreshold(GlobalThresholder thresholder, ImageResource res, int expectedThreshold) {
		ByteProcessor bp = res.getImagePlus().getProcessor().convertToByteProcessor();
		
		// check it a valid threshold was found:
		int q = thresholder.getThreshold(bp);
		assertEquals("threshold not as expected (" + res + ")", expectedThreshold, q);
	}

}

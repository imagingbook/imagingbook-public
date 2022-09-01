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
import imagingbook.sampleimages.GeneralTestImage;
import imagingbook.sampleimages.ThresholdTestImage;

public class GlobalThresholderGrayTest {
	
	@Test
	public void testIsodataThresholder() {
		GlobalThresholder thresholder = new IsodataThresholder();
		checkThreshold(thresholder, GeneralTestImage.Blobs, 125);
		checkThreshold(thresholder, GeneralTestImage.Boats, 93);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 128);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		checkThreshold(thresholder, GeneralTestImage.Blobs, 112);
		checkThreshold(thresholder, GeneralTestImage.Boats, 111);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 133);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		checkThreshold(thresholder, GeneralTestImage.Blobs, 103);
		checkThreshold(thresholder, GeneralTestImage.Boats, 120);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 158);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}
	
	@Test
	public void testMaxMedianThresholder() {
		GlobalThresholder thresholder = new MedianThresholder();
		checkThreshold(thresholder, GeneralTestImage.Blobs, 64);
		checkThreshold(thresholder, GeneralTestImage.Boats, 138);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 179);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		checkThreshold(thresholder, GeneralTestImage.Blobs, 80);
		checkThreshold(thresholder, GeneralTestImage.Boats, 78);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 161);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		checkThreshold(thresholder, GeneralTestImage.Blobs, 128);
		checkThreshold(thresholder, GeneralTestImage.Boats, 111);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 115);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		checkThreshold(thresholder, GeneralTestImage.Blobs, 120);
		checkThreshold(thresholder, GeneralTestImage.Boats, 92);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 128);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder(0.25);
		checkThreshold(thresholder, GeneralTestImage.Blobs, 48);
		checkThreshold(thresholder, GeneralTestImage.Boats, 98);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 151);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1);
	}

	// ----------------------------------------------------
	
	private void checkThreshold(GlobalThresholder thresholder, ImageResource res, int expectedThreshold) {
		ByteProcessor bp = res.getImage().getProcessor().convertToByteProcessor();
		
		// check it a valid threshold was found:
		int q = thresholder.getThreshold(bp);
		assertEquals("threshold not as expected (" + res + ")", expectedThreshold, q);
	}

}

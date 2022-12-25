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
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testimages.ThresholdTestImage;

public class GlobalThresholderGrayTest {
	
	@Test
	public void testIsodataThresholder() {
		GlobalThresholder thresholder = new IsodataThresholder();
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 125, 129);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 93, 161);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 128, 126);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 112, 135);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 111, 143);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 133, 121);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 103, 151);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 120, 134);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 158, 96);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
	}
	
	@Test
	public void testMaxMedianThresholder() {
		GlobalThresholder thresholder = new MedianThresholder();
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 64, 191);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 138, 117);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 179, 76);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 80, 167);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 78, 176);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 161, 93);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 128, 127);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 111, 143);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 115, 140);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 120, 127);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 92, 162);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 128, 126);
		// flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder(0.25);
		checkThreshold(thresholder, GeneralSampleImage.Blobs, 48, 79);
		checkThreshold(thresholder, GeneralSampleImage.Boats, 98, 99);
		checkThreshold(thresholder, ThresholdTestImage.kepler, 151, 71);
		// // flat (single-value) images
		checkThreshold(thresholder, ThresholdTestImage.flat000, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat031, -1, -1);
		checkThreshold(thresholder, ThresholdTestImage.flat255, -1, -1);
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
		// System.out.format("threshold 2 not as expected (" + res + "): %d vs %d\n", thresh2, q2);
	}

}

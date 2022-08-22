package imagingbook.common.threshold.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.ThresholdTestImage;

public class GlobalThresholderBinaryTest {
	
	@Test
	public void testIsodataThresholder() {
		GlobalThresholder thresholder = new IsodataThresholder();
		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}
	
	@Test
	public void testMaxMedianThresholder() {	// TODO: fix median/quantile thresholder to work on any binary image
		GlobalThresholder thresholder = new MedianThresholder();
//		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);
//		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
//		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
//		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
//		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);	
		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder(0.10);
		runThreshold(thresholder, ThresholdTestImage.keplerBin, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 32306);
		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 32306);
	}

	// ----------------------------------------------------
	
	private void runThreshold(GlobalThresholder thresholder, ImageResource res, int expectedZeros) {
		ByteProcessor bp = res.getImage().getProcessor().convertToByteProcessor();
		
		// check it a valid threshold was found:
		int q = thresholder.getThreshold(bp);
		assertTrue("threshold must be positive", q >= 0);
		
		// apply thresholder and count resulting zero pixels:
		thresholder.threshold(bp);
		int zeros1 = countZeros(bp);
//		System.out.println(res + ": " + zeros + " / " + (bp.getWidth() * bp.getHeight()));
		assertEquals("threshold zero pixels 1st (" + res + ")", expectedZeros, zeros1);
		
		// repeat on the thresholded (binary) image: this should give the same number of zero pixels
		thresholder.threshold(bp);
		int zeros2 = countZeros(bp);
		assertEquals("threshold zero pixels 2nd (" + res + ")", expectedZeros, zeros2);
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

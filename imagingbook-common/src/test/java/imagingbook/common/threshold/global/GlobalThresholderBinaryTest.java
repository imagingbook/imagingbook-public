package imagingbook.common.threshold.global;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.ThresholdTestImage;

public class GlobalThresholderBinaryTest {
	
	@Test
	public void testIsodataThresholder() {
		GlobalThresholder thresholder = new IsodataThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 127);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 77);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 177);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 203);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 123);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 223);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);
	}
	
	@Test
	public void testMaxMedianThresholder() {	// TODO: fix median/quantile thresholder to work on any binary image
		GlobalThresholder thresholder = new MedianThresholder();
//		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 203);
//		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 123);
//		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 223);
//		checkThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 128);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 78);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 178);
//		runThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);	// fix!
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder(0.10);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinMinus100, 0);
		checkThreshold(thresholder, ThresholdTestImage.keplerBinPlus100, 100);
		checkThreshold(thresholder, ThresholdTestImage.keplerBin_17_18, 17);
	}

	// ----------------------------------------------------
	
	private void checkThreshold(GlobalThresholder thresholder, ImageResource res, int expectedThreshold) {
		ByteProcessor bp = res.getImage().getProcessor().convertToByteProcessor();
		
		// check it a valid threshold was found:
		int q = thresholder.getThreshold(bp);
		assertEquals("threshold not as expected (" + res + ")", expectedThreshold, q);
	}

}

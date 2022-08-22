package imagingbook.common.threshold.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;
import imagingbook.sampleimages.ThresholdTestImage;

public class GlobalThresholderGrayTest {
	
	@Test
	public void testIsodataThresholder() {
		GlobalThresholder thresholder = new IsodataThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 42781);
		runThreshold(thresholder, GeneralTestImage.Boats, 99145);
		runThreshold(thresholder, ThresholdTestImage.kepler, 32306);	
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 41941);
		runThreshold(thresholder, GeneralTestImage.Boats, 122011);
		runThreshold(thresholder, ThresholdTestImage.kepler, 33979);
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 40055);
		runThreshold(thresholder, GeneralTestImage.Boats, 142257);
		runThreshold(thresholder, ThresholdTestImage.kepler, 43184);
	}
	
	@Test
	public void testMaxMedianThresholder() {
		GlobalThresholder thresholder = new MedianThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 34489);
		runThreshold(thresholder, GeneralTestImage.Boats, 211025);
		runThreshold(thresholder, ThresholdTestImage.kepler, 85123);
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 37875);
		runThreshold(thresholder, GeneralTestImage.Boats, 86613);
		runThreshold(thresholder, ThresholdTestImage.kepler, 44522);
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 43611);
		runThreshold(thresholder, GeneralTestImage.Boats, 123750);
		runThreshold(thresholder, ThresholdTestImage.kepler, 28096);
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 42781);
		runThreshold(thresholder, GeneralTestImage.Boats, 98204);
		runThreshold(thresholder, ThresholdTestImage.kepler, 32306);
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder(0.25);
		runThreshold(thresholder, GeneralTestImage.Blobs, 23204);
		runThreshold(thresholder, GeneralTestImage.Boats, 104230);
		runThreshold(thresholder, ThresholdTestImage.kepler, 40302);
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

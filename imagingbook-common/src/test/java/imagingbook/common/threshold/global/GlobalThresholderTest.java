package imagingbook.common.threshold.global;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;

public class GlobalThresholderTest {
	
	@Test
	public void testIsodataThresholder() {
		GlobalThresholder thresholder = new IsodataThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 42781);
		runThreshold(thresholder, GeneralTestImage.Boats, 99145);	
	}

	@Test
	public void testMaxEntropyThresholder() {
		GlobalThresholder thresholder = new MaxEntropyThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 41941);
		runThreshold(thresholder, GeneralTestImage.Boats, 122011);	
	}
	
	@Test
	public void testMaxMeanThresholder() {
		GlobalThresholder thresholder = new MeanThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 40055);
		runThreshold(thresholder, GeneralTestImage.Boats, 142257);	
	}
	
	@Test
	public void testMaxMedianThresholder() {
		GlobalThresholder thresholder = new MedianThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 34489);
		runThreshold(thresholder, GeneralTestImage.Boats, 211025);	
	}
	
	@Test
	public void testMinErrorThresholder() {
		GlobalThresholder thresholder = new MinErrorThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 37875);
		runThreshold(thresholder, GeneralTestImage.Boats, 86613);	
	}
	
	@Test
	public void testMinMaxThresholder() {
		GlobalThresholder thresholder = new MinMaxThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 43611);
		runThreshold(thresholder, GeneralTestImage.Boats, 123750);	
	}
	
	@Test
	public void testOtsuThresholder() {
		GlobalThresholder thresholder = new OtsuThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 42781);
		runThreshold(thresholder, GeneralTestImage.Boats, 98204);	
	}

	@Test
	public void testQuantileThresholder() {
		GlobalThresholder thresholder = new QuantileThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 34489);
		runThreshold(thresholder, GeneralTestImage.Boats, 211025);	
	}

	// ----------------------------------------------------
	
	private void runThreshold(GlobalThresholder thresholder, ImageResource res, int expectedZeros) {
		ByteProcessor bp = res.getImage().getProcessor().convertToByteProcessor();
		// apply thresholder and count resulting zero pixels
		thresholder.threshold(bp);
		int zeros1 = countZeros(bp);
//		System.out.println(res + ": " + zeros + " / " + (bp.getWidth() * bp.getHeight()));
		assertEquals("threshold zero pixels 1st (" + res + ")", expectedZeros, zeros1);
		
		// repeat on binary image (should give the same number of zero pixels)
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

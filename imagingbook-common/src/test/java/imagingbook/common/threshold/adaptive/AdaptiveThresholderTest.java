package imagingbook.common.threshold.adaptive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;

public class AdaptiveThresholderTest {

	@Test
	public void testBernsenThresholder() {
		AdaptiveThresholder thresholder = new BernsenThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 21896);
		runThreshold(thresholder, GeneralTestImage.Boats, 147239);	
	}
	
	@Test
	public void testInterpolatingThresholder() {
		AdaptiveThresholder thresholder = new InterpolatingThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 21908);
		runThreshold(thresholder, GeneralTestImage.Boats, 164349);	
	}
	
	@Test
	public void testNiblackThresholderBox() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Box();
		runThreshold(thresholder, GeneralTestImage.Blobs, 31876);
		runThreshold(thresholder, GeneralTestImage.Boats, 323029);	
	}
	
	@Test
	public void testNiblackThresholderDisk() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Disk();
		runThreshold(thresholder, GeneralTestImage.Blobs, 34699);
		runThreshold(thresholder, GeneralTestImage.Boats, 328857);	
	}
	
	@Test
	public void testNiblackThresholderGauss() {
		AdaptiveThresholder thresholder = new NiblackThresholder.Gauss();
		runThreshold(thresholder, GeneralTestImage.Blobs, 33200);
		runThreshold(thresholder, GeneralTestImage.Boats, 328838);	
	}
	
	@Test
	public void testSauvolaThresholder() {
		AdaptiveThresholder thresholder = new SauvolaThresholder();
		runThreshold(thresholder, GeneralTestImage.Blobs, 48597);
		runThreshold(thresholder, GeneralTestImage.Boats, 387246);	
	}
	
	// ------------------------
	
	private void runThreshold(AdaptiveThresholder thresholder, ImageResource res, int expectedZeros) {
		ByteProcessor bp = res.getImage().getProcessor().convertToByteProcessor();
		thresholder.threshold(bp);
		int zeros = countZeros(bp);
//		System.out.println(res + ": " + zeros + " / " + (bp.getWidth() * bp.getHeight()));
		assertEquals("detected SIFT features (" + res + ")", expectedZeros, zeros);
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

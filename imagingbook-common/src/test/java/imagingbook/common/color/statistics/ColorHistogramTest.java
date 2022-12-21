package imagingbook.common.color.statistics;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import imagingbook.common.ij.IjUtils;
import org.junit.Test;

import ij.process.ColorProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.ColorTestImage;

public class ColorHistogramTest {

	@Test
	public void test1() {
		runTestA(ColorTestImage.Clown);
	}
	
	@Test
	public void test2() {
		runTestA(ColorTestImage.SingleColor);
	}
	
	@Test
	public void test3() {
		runTestA(ColorTestImage.TwoColors);
	}
	
	@Test
	public void test4() {
		runTestA(ColorTestImage.Balls_jpg);
	}
	
	private void runTestA(ImageResource ir) {
		ColorProcessor cp = (ColorProcessor) ir.getImagePlus().getProcessor();
		int[] pixels = (int[]) cp.getPixels();
		ColorHistogram ch = new ColorHistogram(pixels);
		
		// check if the total number of colors is correct
		int n = ch.getNumberOfColors();
		assertEquals(IjUtils.countColors(cp), n);
		
		// validate the size of some color bins by counting pixels with the bin's value
		Random rg = new Random(17);
		for (int i = 0; i < 10; i++) {
			int k = rg.nextInt(n);
			int col = ch.getColor(k);
			assertEquals("color " + col, countPixels(pixels, col), ch.getFrequency(k));
		}
		
		// check if the size of all color bins adds up to number of pixels
		int totalCnt = 0;
		for (int k = 0; k < n; k++) {
			totalCnt = totalCnt + ch.getFrequency(k);
		}
		assertEquals(pixels.length, totalCnt);
	}
	
	// -------------------------
	
	private int countPixels(int[] pixels, int rgb) {
		int cnt = 0;
		for (int i = 0; i < pixels.length; i++) {
			if ((0xFFFFFF & pixels[i]) == rgb) {
				cnt++;
			}
		}
		return cnt;
	}

}

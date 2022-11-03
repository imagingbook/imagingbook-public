package imagingbook.common.color.statistics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ColorProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.ColorTestImage;

public class ColorStatisticsTest {

	@Test
	public void test1() {
		runTest(ColorTestImage.Alps01s, 90705);
	}
	
	@Test
	public void test2() {
		runTest(ColorTestImage.Balls_jpg, 266919);
	}
	
	@Test
	public void test3() {
		runTest(ColorTestImage.RampFire, 98);
	}
	
	@Test
	public void test4() {
		runTest(ColorTestImage.RandomColors, 9888);
	}
	
	@Test
	public void test5() {
		runTest(ColorTestImage.SingleColor, 1);
	}
	
	@Test
	public void test6() {
		runTest(ColorTestImage.TwoColors, 2);
	}
	
	// --------------------
	
	private void runTest(ImageResource ir, int expectedCols) {
		ColorProcessor cp = (ColorProcessor) ir.getImage().getProcessor();
		assertEquals(expectedCols, ColorStatistics.countColors(cp));
		cp.invert();
		assertEquals(expectedCols, ColorStatistics.countColors(cp));
	}
}

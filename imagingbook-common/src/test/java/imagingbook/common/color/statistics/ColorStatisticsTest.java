package imagingbook.common.color.statistics;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import ij.process.ColorProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class ColorStatisticsTest {

	@Test
	public void test1() {
		ImageResource ir = GeneralSampleImage.Clown;
		ColorProcessor cp = (ColorProcessor) ir.getImage().getProcessor();
		assertEquals(32959, ColorStatistics.countColors(cp));
		cp.invert();
		assertEquals(32959, ColorStatistics.countColors(cp));
	}
	
	@Test
	public void test2() {
		ImageResource ir = GeneralSampleImage.MonasterySmall;
		ColorProcessor cp = ir.getImage().getProcessor().convertToColorProcessor();
		assertEquals(256, ColorStatistics.countColors(cp));
		cp.invert();
		assertEquals(256, ColorStatistics.countColors(cp));
	}
	
	@Test
	public void test3() {
		ColorProcessor cp = new ColorProcessor(100, 70);
		assertEquals(1, ColorStatistics.countColors(cp));
		
		cp.setColor(Color.red);
		cp.fill();
		assertEquals(1, ColorStatistics.countColors(cp));
		
		cp.setColor(Color.blue);
		cp.drawPixel(0, 0);
		assertEquals(2, ColorStatistics.countColors(cp));
		
		cp.invert();
		assertEquals(2, ColorStatistics.countColors(cp));
	}


}

package imagingbook.common.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.color.ColorSpace;

import org.junit.Test;

import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.HlsColorSpace;
import imagingbook.common.color.colorspace.HsvColorSpace;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LinearRgb65ColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageTestUtils;

public class ColorPackTest {
	
	static double TOL = 1e-4;
	
	ImageResource clown = GeneralSampleImage.Clown;

	@Test
	public void testBasicConversion() {	// conversion ColorProcessor/ColorStack
		ImageProcessor ip1 = clown.getImage().getProcessor();
		assertTrue(ip1 instanceof ColorProcessor);
		ColorPack cstack = new ColorPack((ColorProcessor)ip1);
		ImageProcessor ip2 = cstack.toColorProcessor();
		assertTrue(ImageTestUtils.match(ip1, ip2, TOL));
	}

	@Test
	public void testLab() {	// conversion from/to Lab
		testColorStackConversion(LabColorSpace.getInstance());
	}
	
	@Test
	public void testLuv() {	// conversion from/to Lab
		testColorStackConversion(LuvColorSpace.getInstance());
	}
	
	@Test
	public void testLinearRgb() {	// conversion from/to Lab
		testColorStackConversion(LinearRgb65ColorSpace.getInstance());
	}
	
	@Test
	public void testHls() {	// conversion from/to Lab
		testColorStackConversion(HlsColorSpace.getInstance());
	}
	
	@Test
	public void testHsv() {	// conversion from/to Lab
		testColorStackConversion(HsvColorSpace.getInstance());
	}
	
	// ------------------------------------------------------------
	
	private void testColorStackConversion(ColorSpace cs) {
		ImageProcessor ip1 = clown.getImage().getProcessor();
		assertTrue(ip1 instanceof ColorProcessor);
		
		ColorPack cstack = new ColorPack((ColorProcessor)ip1);
		cstack.convertFromSrgbTo(cs);
		cstack.convertToSrgb();
		
		ImageProcessor ip2 = cstack.toColorProcessor();
		assertTrue(ImageTestUtils.match(ip1, ip2, TOL));
	}
	
	// ------------------------------------------------------------
	
	@Test
	public void testImageStack() {
		ImageProcessor ip1 = clown.getImage().getProcessor();
		assertTrue(ip1 instanceof ColorProcessor);
		
		ColorPack cstack = new ColorPack((ColorProcessor)ip1);
		
		ImageStack istack = cstack.toImageStack();
		assertEquals(3, istack.getSize());
		
		for (int k = 0; k < cstack.getDepth(); k++) {
			ImageProcessor fp1 = cstack.getFloatProcessor(k);
			assertNotNull(fp1);
			ImageProcessor fp2 = istack.getProcessor(k + 1);
			assertNotNull(fp2);
			assertTrue(ImageTestUtils.match(fp1, fp2, TOL));
		}
		
		ImageProcessor ip2 = cstack.toColorProcessor();
		assertTrue(ImageTestUtils.match(ip1, ip2, TOL));
	}

}

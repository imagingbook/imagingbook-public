package imagingbook.common.image;

import static org.junit.Assert.*;

import java.awt.color.ColorSpace;

import org.junit.Test;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.HlsColorSpace;
import imagingbook.common.color.colorspace.HsvColorSpace;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LinearRgbColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.common.testutils.ImageTestUtils;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class ColorStackTest {
	
	static double TOL = 1e-4;
	
	ImageResource clown = GeneralSampleImage.clown;

	@Test
	public void testBasicConversion() {	// conversion ColorProcessor/ColorStack
		ImageProcessor ip1 = clown.getImage().getProcessor();
		assertTrue(ip1 instanceof ColorProcessor);
		ColorStack cstack = new ColorStack((ColorProcessor)ip1);
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
		testColorStackConversion(LinearRgbColorSpace.getInstance());
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

		ColorStack cstack = new ColorStack((ColorProcessor)ip1);
		cstack.convertFromSrgbTo(cs);
		cstack.convertToSrgb();
		
		ImageProcessor ip2 = cstack.toColorProcessor();
		assertTrue(ImageTestUtils.match(ip1, ip2, TOL));
	}

}

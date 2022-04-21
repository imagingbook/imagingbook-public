package imagingbook.common.filter.linear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.DATA.GeneralTestImage;
import imagingbook.core.resource.ImageResource;
import imagingbook.testutils.ImageTests;

public class GaussianFilterTest {
	
	static double SIGMA = 3.0;
	static float TOL = 1f;	// deviations +/-1 are possible due to rounding to integer images

	ImageResource res1A = GeneralTestImage.MonasterySmall;
	ImageResource res1B = GeneralTestImage.MonasterySmallGauss3;
	
	ImageResource res2A = GeneralTestImage.Clown;
	ImageResource res2B = GeneralTestImage.ClownGauss3;
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testGaussianGray() {
		ImageProcessor ipA = res1A.getImage().getProcessor();
		ImageProcessor ipB = res1B.getImage().getProcessor();
		
		//new GaussianFilter(ipA, SIGMA).apply();
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGaussianRgb() {
		ImageProcessor ipA = res2A.getImage().getProcessor();
		ImageProcessor ipB = res2B.getImage().getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testGaussianSeparableGray() {
		ImageProcessor ipA = res1A.getImage().getProcessor();
		ImageProcessor ipB = res1B.getImage().getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}

	@Test
	public void testGaussianSeparableRgb() {
		ImageProcessor ipA = res2A.getImage().getProcessor();
		ImageProcessor ipB = res2B.getImage().getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
}

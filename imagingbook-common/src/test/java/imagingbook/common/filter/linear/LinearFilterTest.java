package imagingbook.common.filter.linear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.DATA.GeneralTestImage;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.core.resource.ImageResource;
import imagingbook.testutils.ImageTests;

public class LinearFilterTest {
	
	static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	static float[][] H1 = {
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}};
	
	static float[][] H2 = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	
	ImageResource res1A = GeneralTestImage.MonasterySmall;
	ImageResource res1B = GeneralTestImage.MonasterySmallFilter3x3;
	
	ImageResource res2A = GeneralTestImage.Clown;
	ImageResource res2B = GeneralTestImage.ClownFilter3x3;
	
	@Test
	public void testLinearFilterUnitKernel() {
		ImageProcessor ipA = res1A.getImage().getProcessor();
		float[][] H = H1;
		ImageProcessor ipAf = ipA.duplicate();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTests.match(ipAf, ipA, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3gray() {
		ImageProcessor ipA = res1A.getImage().getProcessor();
		ImageProcessor ipB = res1B.getImage().getProcessor();
		float[][] H = H2;
		new LinearFilter(new Kernel2D(H)).applyTo(ipA, OBS);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3float() {
		ImageProcessor ipA = res1A.getImage().getProcessor();
		ImageProcessor ipB = res1B.getImage().getProcessor();
		float[][] H = H2;
		ImageProcessor ipAf = ipA.convertToFloatProcessor();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTests.match(ipAf, ipB.convertToFloatProcessor(), 0.5f));
	}
	
	@Test
	public void testLinearFilter3x3rgb() {
		ImageProcessor ipA = res2A.getImage().getProcessor();
		ImageProcessor ipB = res2B.getImage().getProcessor();
		float[][] H = H2;
		
		ImageProcessor ipAf = ipA.duplicate();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
	}

}

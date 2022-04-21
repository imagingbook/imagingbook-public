package imagingbook.common.color.filters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.DATA.GeneralTestImage;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.core.resource.ImageResource;
import imagingbook.testutils.ImageTests;

public class ColorMedianFilterTest {
	
	ImageResource resA = GeneralTestImage.Clown;
	
	@Test
	public void testScalarMedianFilter() {
		
		ImageResource resB = GeneralTestImage.ClownMedianScalar3;
		
		ImageProcessor ipA = resA.getImage().getProcessor();
		ImageProcessor ipB = resB.getImage().getProcessor();
		
		ScalarMedianFilter.Parameters params = new ScalarMedianFilter.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NearestBorder;
		
		ScalarMedianFilter filter = new ScalarMedianFilter(params);
		filter.applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testVectorMedianFilter() {
		ImageResource resB = GeneralTestImage.ClownMedianVector3L1;
		ImageProcessor ipA = resA.getImage().getProcessor();
		ImageProcessor ipB = resB.getImage().getProcessor();
		
		VectorMedianFilter.Parameters params = new VectorMedianFilter.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NearestBorder;
		params.distanceNorm = NormType.L1;
		
		VectorMedianFilter filter = new VectorMedianFilter(params);
		filter.applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testVectorMedianFilterSharpen() {
		ImageResource resB = GeneralTestImage.ClownMedianVectorsharpen3L1;
		ImageProcessor ipA = resA.getImage().getProcessor();
		ImageProcessor ipB = resB.getImage().getProcessor();
		
		VectorMedianFilterSharpen.Parameters params = new VectorMedianFilterSharpen.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NearestBorder;
		params.distanceNorm = NormType.L1;
		params.sharpen = 0.5;
		params.threshold = 0.0;	
		
		VectorMedianFilterSharpen filter = new VectorMedianFilterSharpen(params);
		filter.applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}

}

package imagingbook.common.filter.nonlinear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.common.testutils.ImageTestUtils;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.FilterTestImage;

public class VectorMedianFilterTest {
	
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	ImageResource resA = FilterTestImage.Clown;

	@Test
	public void testVectorMedianFilter() {
		ImageResource resB = FilterTestImage.ClownMedianVector3L1;
		ImageProcessor ipA = resA.getImage().getProcessor();
		ImageProcessor ipB = resB.getImage().getProcessor();
		
		VectorMedianFilter.Parameters params = new VectorMedianFilter.Parameters();
		params.radius = 3.0;
		params.distanceNorm = NormType.L1;
		
		VectorMedianFilter filter = new VectorMedianFilter(params);
		filter.applyTo(ipA, OBS);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}

}

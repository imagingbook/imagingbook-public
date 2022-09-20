package imagingbook.common.filter.nonlinear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class ScalarMedianFilterTest {

	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	ImageResource resA = FilterTestImage.Clown;
	
	@Test
	public void testScalarMedianFilter() {
		ImageResource resB = FilterTestImage.ClownMedianScalar3;
		
		ImageProcessor ipA = resA.getImage().getProcessor();
		ImageProcessor ipB = resB.getImage().getProcessor();
		
		ScalarMedianFilter.Parameters params = new ScalarMedianFilter.Parameters();
		params.radius = 3.0;
		
		ScalarMedianFilter filter = new ScalarMedianFilter(params);
		filter.applyTo(ipA, OBS);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}

}

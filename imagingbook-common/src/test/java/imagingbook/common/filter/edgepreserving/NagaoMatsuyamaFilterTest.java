package imagingbook.common.filter.edgepreserving;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.NagaoMatsuyamaF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class NagaoMatsuyamaFilterTest {
	
	private static final double TOL = 1E-6;
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	private final ImageProcessor monastery, clown;
	private final Parameters params;
	
	public NagaoMatsuyamaFilterTest() {
		monastery = FilterTestImage.MonasterySmall.getImagePlus().getProcessor();
		clown = FilterTestImage.Clown.getImagePlus().getProcessor();
		
		assertTrue(monastery instanceof ByteProcessor);
		assertTrue(clown instanceof ColorProcessor);
		
		// use default parameters:
		params = new Parameters();
		params.varThreshold = 0.0;
	}
	
	@Test
	public void testGrayScalar() {
		ImageProcessor ipA = monastery.duplicate();
		GenericFilter filter = new NagaoMatsuyamaFilterScalar(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallNagaoMatsuyama.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testColorScalar() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new NagaoMatsuyamaFilterScalar(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownNagaoMatsuyamaScalar.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorVector() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new NagaoMatsuyamaFilterVector(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownNagaoMatsuyamaVector.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}

}

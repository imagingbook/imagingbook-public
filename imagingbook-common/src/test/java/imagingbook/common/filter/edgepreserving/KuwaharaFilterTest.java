package imagingbook.common.filter.edgepreserving;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.KuwaharaF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class KuwaharaFilterTest {
	
	private static final double TOL = 1E-6;
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	private final ImageProcessor monastery, clown;
	private final Parameters params;
	
	public KuwaharaFilterTest() {
		monastery = FilterTestImage.MonasterySmall.getImagePlus().getProcessor();
		clown = FilterTestImage.Clown.getImagePlus().getProcessor();
		
		assertTrue(monastery instanceof ByteProcessor);
		assertTrue(clown instanceof ColorProcessor);
		
		// use default parameters:
		params = new Parameters();
		params.radius = 2;
		params.tsigma = 5.0;
	}
	
	@Test
	public void testGrayScalar() {
		ImageProcessor ipA = monastery.duplicate();
		GenericFilter filter = new KuwaharaFilterScalar(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallKuwahara.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testColorScalar() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new KuwaharaFilterScalar(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownKuwaharaScalar.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorVector() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new KuwaharaFilterVector(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownKuwaharaVector.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}

}

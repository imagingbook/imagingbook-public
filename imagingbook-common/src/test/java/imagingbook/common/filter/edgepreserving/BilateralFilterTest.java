package imagingbook.common.filter.edgepreserving;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.BilateralF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BilateralFilterTest {
	
	private static final double TOL = 1E-6;
	private final ImageProcessor monastery, clown;
	private final BilateralF.Parameters params;
	
	public BilateralFilterTest() {
		monastery = FilterTestImage.MonasterySmall.getImage().getProcessor();
		clown = FilterTestImage.Clown.getImage().getProcessor();
		
		assertTrue(monastery instanceof ByteProcessor);
		assertTrue(clown instanceof ColorProcessor);
		
		// use default parameters:
		params = new Parameters();
		params.sigmaD = 2; 		
		params.sigmaR = 50;
		params.colorNormType = NormType.L2;
		params.obs = OutOfBoundsStrategy.NearestBorder;
	}
	
	@Test
	public void testGrayScalarNonsep() {
		ImageProcessor ipA = monastery.duplicate();
		GenericFilter filter = new BilateralFilterScalar(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.MonasterySmallBilateralNonsep.getImage().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGrayScalarSep() {
		ImageProcessor ipA = monastery.duplicate();
		GenericFilter filter = new BilateralFilterScalarSeparable(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.MonasterySmallBilateralSep.getImage().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testColorScalarNonsep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterScalar(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.ClownBilateralNonsepScalar.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorScalarSep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterScalarSeparable(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.ClownBilateralSepScalar.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorVectorNonsep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterVector(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.ClownBilateralNonsepVector.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}

	@Test
	public void testColorVectorSep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterVectorSeparable(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.ClownBilateralSepVector.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
}

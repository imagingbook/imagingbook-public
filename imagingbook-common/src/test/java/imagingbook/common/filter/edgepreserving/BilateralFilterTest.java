/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.edgepreserving;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.BilateralF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BilateralFilterTest {
	
	private static final double TOL = 1E-6;
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	private final ImageProcessor monastery, clown;
	private final BilateralF.Parameters params;
	
	public BilateralFilterTest() {
		monastery = FilterTestImage.MonasterySmall.getImagePlus().getProcessor();
		clown = FilterTestImage.Clown.getImagePlus().getProcessor();
		
		assertTrue(monastery instanceof ByteProcessor);
		assertTrue(clown instanceof ColorProcessor);
		
		// use default parameters:
		params = new Parameters();
		params.sigmaD = 2; 		
		params.sigmaR = 50;
		params.colorNormType = NormType.L2;
	}
	
	@Test
	public void testGrayScalarNonsep() {
		ImageProcessor ipA = monastery.duplicate();
		GenericFilter filter = new BilateralFilterScalar(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallBilateralNonsep.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGrayScalarSep() {
		ImageProcessor ipA = monastery.duplicate();
		GenericFilter filter = new BilateralFilterScalarSeparable(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallBilateralSep.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testColorScalarNonsep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterScalar(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownBilateralNonsepScalar.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorScalarSep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterScalarSeparable(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownBilateralSepScalar.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorVectorNonsep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterVector(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownBilateralNonsepVector.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}

	@Test
	public void testColorVectorSep() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new BilateralFilterVectorSeparable(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownBilateralSepVector.getImagePlus().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
}

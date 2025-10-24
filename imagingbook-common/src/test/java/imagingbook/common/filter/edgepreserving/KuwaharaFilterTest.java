/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
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

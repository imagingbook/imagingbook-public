/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.edgepreserving;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.TschumperleDericheF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class TschumperleDericheFilterTest {
	
	private static final double TOL = 1E-6;
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	private final ImageProcessor monastery, clown;
	private final Parameters params;
	
	public TschumperleDericheFilterTest() {
		monastery = FilterTestImage.MonasterySmall.getImage().getProcessor();
		clown = FilterTestImage.Clown.getImage().getProcessor();
		
		assertTrue(monastery instanceof ByteProcessor);
		assertTrue(clown instanceof ColorProcessor);
		
		// use default parameters:
		params = new Parameters();
		params.iterations = 5;		// for speed, default is 20 iterations
		params.dt = 20.0;  		
		params.sigmaD  = 0.5;
		params.sigmaM  = 0.5;
		params.a0 = 0.25f;  	
		params.a1 = 0.90f;
		params.alpha0 = 0.5f;
	}
	
	// --------------------------------------------------------------------------
	
	@Test
	public void testGray() {
		ImageProcessor ipA = monastery.duplicate();
		GenericFilter filter = new TschumperleDericheFilter(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.MonasterySmallTschumperleIter5.getImage().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	// --------------------------------------------------------------------------
	
	@Test
	public void testColor() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new TschumperleDericheFilter(params);
		filter.applyTo(ipA);
		ImageProcessor ipB = FilterTestImage.ClownSmallTschumperleIter5.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
}

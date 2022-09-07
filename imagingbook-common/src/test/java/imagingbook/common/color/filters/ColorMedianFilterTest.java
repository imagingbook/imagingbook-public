/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.filters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.filter.nonlinear.ScalarMedianFilter;
import imagingbook.common.filter.nonlinear.VectorMedianFilter;
import imagingbook.common.filter.nonlinear.VectorMedianFilterSharpen;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageTestUtils;

public class ColorMedianFilterTest {
	
	ImageResource resA = GeneralSampleImage.Clown;
	
	@Test
	public void testScalarMedianFilter() {
		
		ImageResource resB = GeneralSampleImage.ClownMedianScalar3;
		
		ImageProcessor ipA = resA.getImage().getProcessor();
		ImageProcessor ipB = resB.getImage().getProcessor();
		
		ScalarMedianFilter.Parameters params = new ScalarMedianFilter.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NearestBorder;
		
		ScalarMedianFilter filter = new ScalarMedianFilter(params);
		filter.applyTo(ipA);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testVectorMedianFilter() {
		ImageResource resB = GeneralSampleImage.ClownMedianVector3L1;
		ImageProcessor ipA = resA.getImage().getProcessor();
		ImageProcessor ipB = resB.getImage().getProcessor();
		
		VectorMedianFilter.Parameters params = new VectorMedianFilter.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NearestBorder;
		params.distanceNorm = NormType.L1;
		
		VectorMedianFilter filter = new VectorMedianFilter(params);
		filter.applyTo(ipA);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testVectorMedianFilterSharpen() {
		ImageResource resB = GeneralSampleImage.ClownMedianVectorsharpen3L1;
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
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}

}

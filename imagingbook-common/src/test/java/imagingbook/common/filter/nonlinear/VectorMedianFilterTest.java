/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.nonlinear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class VectorMedianFilterTest {
	
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	ImageResource resA = FilterTestImage.Clown;

	@Test
	public void testVectorMedianFilter() {
		ImageResource resB = FilterTestImage.ClownMedianVector3L1;
		ImageProcessor ipA = resA.getImagePlus().getProcessor();
		ImageProcessor ipB = resB.getImagePlus().getProcessor();
		
		VectorMedianFilter.Parameters params = new VectorMedianFilter.Parameters();
		params.radius = 3.0;
		params.distanceNorm = NormType.L1;
		
		VectorMedianFilter filter = new VectorMedianFilter(params);
		filter.applyTo(ipA, OBS);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
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
		
		ImageProcessor ipA = resA.getImagePlus().getProcessor();
		ImageProcessor ipB = resB.getImagePlus().getProcessor();
		
		ScalarMedianFilter.Parameters params = new ScalarMedianFilter.Parameters();
		params.radius = 3.0;
		
		ScalarMedianFilter filter = new ScalarMedianFilter(params);
		filter.applyTo(ipA, OBS);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}

}

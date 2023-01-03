/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.examples;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class ExampleFilter3x3ScalarTest {
	
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	ImageResource resA = FilterTestImage.Clown;

	@Test
	public void test() {
		ImageProcessor ipA = resA.getImagePlus().getProcessor();
		ImageProcessor ipB = FilterTestImage.ClownExampleFilter3x3Scalar.getImagePlus().getProcessor();
		GenericFilter filter = new ExampleFilter3x3Scalar();
		filter.applyTo(ipA, OBS);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
		
//		IJ.save(new ImagePlus("result", ipA), "D:/tmp/result.png");
		
	}

}

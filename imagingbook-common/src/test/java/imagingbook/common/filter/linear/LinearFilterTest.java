/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.linear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class LinearFilterTest {
	
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	private static final float[][] H1 = {
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}};
	
	private static final float[][] H2 = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	
	ImageResource res1A = FilterTestImage.MonasterySmall;
	ImageResource res1B = FilterTestImage.MonasterySmallFilter3x3;
	
	ImageResource res2A = FilterTestImage.Clown;
	ImageResource res2B = FilterTestImage.ClownFilter3x3;
	
	@Test
	public void testLinearFilterUnitKernel() {
		ImageProcessor ipA = res1A.getImagePlus().getProcessor();
		float[][] H = H1;
		ImageProcessor ipAf = ipA.duplicate();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTestUtils.match(ipAf, ipA, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3gray() {
		ImageProcessor ipA = res1A.getImagePlus().getProcessor();
		ImageProcessor ipB = res1B.getImagePlus().getProcessor();
		float[][] H = H2;
		new LinearFilter(new Kernel2D(H)).applyTo(ipA, OBS);
		assertTrue(ImageTestUtils.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3float() {
		ImageProcessor ipA = res1A.getImagePlus().getProcessor();
		ImageProcessor ipB = res1B.getImagePlus().getProcessor();
		float[][] H = H2;
		ImageProcessor ipAf = ipA.convertToFloatProcessor();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTestUtils.match(ipAf, ipB.convertToFloatProcessor(), 0.5f));
	}
	
	@Test
	public void testLinearFilter3x3rgb() {
		ImageProcessor ipA = res2A.getImagePlus().getProcessor();
		ImageProcessor ipB = res2B.getImagePlus().getProcessor();
		float[][] H = H2;
		
		ImageProcessor ipAf = ipA.duplicate();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTestUtils.match(ipAf, ipB, 1E-6));
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.access;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;

/**
 * Test default values (reading from coordinates outside the image boundaries)
 */
public class ScalarAccessorDefaultValuesTest {
	
	static float TOL = 1E-6F;
	
//	Path path = new imagingbook.DATA.images.Resources().getResourcePath("boats.png");
//	ImageProcessor ip = IjUtils.openImage(path).getProcessor();
	
	int width = 300;
	int height = 200;
	int defaultVal = 0;

	@Test
	public void testDefaultValueByte() {
		run(new ByteProcessor(width, height));
	}
	
	@Test
	public void testDefaultValueShort() {
		run(new ShortProcessor(width, height));
	}
	
	@Test
	public void testDefaultValueFloat() {
		run(new FloatProcessor(width, height));
	}
	
	private void run(ImageProcessor ip) {
		ScalarAccessor ia = ScalarAccessor.create(ip, OutOfBoundsStrategy.DefaultValue, null);
//		ia.setDefaultValue(defaultVal);
		assertEquals(defaultVal, ia.getVal(-1, 10), TOL);
		assertEquals(defaultVal, ia.getVal(width, 10), TOL);
		assertEquals(defaultVal, ia.getVal(3, -1), TOL);
		assertEquals(defaultVal, ia.getVal(0, height), TOL);
	}

}

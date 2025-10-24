/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.image.OutOfBoundsStrategy;

public class CubicBSplineInterpolatorTest {
	
	static double TOL = 1e-6;
	static OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;

	@Test
	public void test1() {
		CubicBSplineInterpolator interp = new CubicBSplineInterpolator();
		assertEquals(2.0/3, interp.getWeight(0.0), TOL);
		assertEquals(0.4791666666666667, interp.getWeight(0.5), TOL);
		assertEquals(0.1666666666666667, interp.getWeight(1.0), TOL);
		assertEquals(0.020833333333333332, interp.getWeight(1.5), TOL);
		assertEquals(0.0, interp.getWeight(2.0), TOL);
		assertEquals(0.0, interp.getWeight(2.5), TOL);
	}
	
//	@Test
//	public void test2() {
//		float[][] data = {
//				{0, 0, 0},
//				{0, 1, 0},
//				{0, 0, 0}
//		};
//		FloatProcessor fp = new FloatProcessor(data);
//		ScalarAccessor ia = ScalarAccessor.create(fp, obs, InterpolationMethod.Bicubic);
//		
//		float[][] data2 = Matrix.duplicate(data);
//		int w = ia.getWidth();
//		int h = ia.getHeight();
//		for (int u = 0; u < w; u++) {
//			for (int v = 0; v < h; v++) {
//				
//				data2[u][v] = ia.getVal((double)u, v);
//				//assertEquals(ip.get(u, v), ia.getVal(u, v), TOL);
//			}
//		}
//		System.out.println("data2 = \n" + Matrix.toString(data2));
//		
//	}

}

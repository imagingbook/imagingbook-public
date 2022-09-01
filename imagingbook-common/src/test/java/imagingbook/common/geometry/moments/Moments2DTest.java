/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.moments;

import static imagingbook.common.geometry.moments.Moments2D.centralMoment;
import static imagingbook.common.geometry.moments.Moments2D.normalizedCentralMoment;
import static imagingbook.common.geometry.moments.Moments2D.ordinaryMoment;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class Moments2DTest {
	
	private static double TOL = 1e-6;
	
	private static List<Pnt2d> points = Arrays.asList(
			Pnt2d.from(10, 15), 
			Pnt2d.from(3, 7), 
			Pnt2d.from(-1, 5), 
			Pnt2d.from(-1, 5));
		

	@Test
	public void testOrdinaryMoment() {
		assertEquals(4.0, ordinaryMoment(points, 0, 0), TOL);
		assertEquals(11.0, ordinaryMoment(points, 1, 0), TOL);
		assertEquals(32.0, ordinaryMoment(points, 0, 1), TOL);
		assertEquals(161.0, ordinaryMoment(points, 1, 1), TOL);
		assertEquals(111.0, ordinaryMoment(points, 2, 0), TOL);
		assertEquals(324.0, ordinaryMoment(points, 0, 2), TOL);
		
		double a = ordinaryMoment(points, 0, 0);
		double xc = ordinaryMoment(points, 1, 0) / a;
		double yc = ordinaryMoment(points, 0, 1) / a;	
		assertEquals(2.75, xc, TOL);
		assertEquals(8.0, yc, TOL);
	}
	
	@Test
	public void testCentralMoment() {
		assertEquals(0.0, centralMoment(points, 1, 0), TOL);
		assertEquals(0.0, centralMoment(points, 0, 1), TOL);
		assertEquals(73.0, centralMoment(points, 1, 1), TOL);
		assertEquals(80.75, centralMoment(points, 2, 0), TOL);
		assertEquals(68.0, centralMoment(points, 0, 2), TOL);
	}
	
	@Test
	public void testNormalizedCentralMoment() {
		assertEquals(0.0, normalizedCentralMoment(points, 1, 0), TOL);
		assertEquals(0.0, normalizedCentralMoment(points, 0, 1), TOL);
		assertEquals(4.5625, normalizedCentralMoment(points, 1, 1), TOL);
		assertEquals(5.046875, normalizedCentralMoment(points, 2, 0), TOL);
		assertEquals(4.25, normalizedCentralMoment(points, 0, 2), TOL);
	}

}

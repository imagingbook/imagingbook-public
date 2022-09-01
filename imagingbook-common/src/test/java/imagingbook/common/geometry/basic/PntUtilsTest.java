/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.basic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class PntUtilsTest {

	@Test
	public void testCentroid1() {	// single point ctr
		Pnt2d p = Pnt2d.from( 4, -5);
		Pnt2d[] points = {p};
		Pnt2d ctr = PntUtils.centroid(points);
		assertEquals(p, ctr);
		
	}
	
	@Test
	public void testCentroid2() {	// multiple same points ctr
		Pnt2d p = Pnt2d.from( Math.PI, Math.E);
		Pnt2d[] points = {p, p, p};
		Pnt2d ctr = PntUtils.centroid(points);
		assertEquals(p, ctr);
		
	}
	
	@Test
	public void testCentroid3() {
		Pnt2d[] points = {
				Pnt2d.from( 4, -5),
				Pnt2d.from(-4,  5),
				Pnt2d.from( 2,  1),
				Pnt2d.from(-2, -1)
		};
		
		Pnt2d ctr = PntUtils.centroid(points);
		assertEquals(Pnt2d.from(0, 0), ctr);
	}
	
	@Test(expected=IllegalArgumentException.class)
	// test empty set
	public void testCentroid4() {
		List<Pnt2d> points = new ArrayList<>();
		@SuppressWarnings("unused")
		Pnt2d ctr = PntUtils.centroid(points);
	}
	
	// ----------------------------------------------------
	
	@Test
	public void testToDoubleArray() {
		int N = 100; 	// number of random points
		Random rg = new Random(17);
		Pnt2d[] points1 = new Pnt2d[N];
		for (int i = 0; i < N; i++) {
			points1[i] = Pnt2d.from(100 * rg.nextDouble(), 100 * rg.nextDouble());
		}
		// convert points to double[][]
		double[][] pa = PntUtils.toDoubleArray(points1);
		assertEquals(points1.length, pa.length);
		
		// convert back to point array
		Pnt2d[] points2 = PntUtils.fromDoubleArray(pa);
		assertEquals(points1.length, points2.length);
		
		// check all points
		for (int i = 0; i < N; i++) {
			assertEquals(points1[i], points2[i]);
		}
	}

}

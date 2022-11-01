/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.hulls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class AxisAlignedBoundingBoxTest {
	
	private static final Pnt2d[] pointArray = {
			Pnt2d.from(2, 5),
			Pnt2d.from(5, 4),
			Pnt2d.from(5, 7),
			Pnt2d.from(7, 6),
			Pnt2d.from(9, 9),
			Pnt2d.from(6, 2)
	};

	@Test
	public void test1A() {	
		runPointTest(pointArray, new AxisAlignedBoundingBox(pointArray));
	}
	
	@Test
	public void test1B() {
		runPointTest(pointArray, new AxisAlignedBoundingBox(Arrays.asList(pointArray)));
	}
	
	@Test
	public void test2() {
		int N = 100; 	// number of random points
		int K = 100;	// number of tries
		Random rg = new Random(17);
		Pnt2d[] pointArray = new Pnt2d[N];
		
		for (int k = 0; k < K; k++) {
			for (int i = 0; i < N; i++) {
				pointArray[i] = Pnt2d.from(100 * rg.nextDouble(), 100 * rg.nextDouble());
			}
			runPointTest(pointArray, new AxisAlignedBoundingBox(pointArray));
		}
	}
	
	@Test
	public void testSinglePoint() {
		Pnt2d[] pnts = { Pnt2d.from(2, 5),  Pnt2d.from(2, 5)};
		runPointTest(pnts, new AxisAlignedBoundingBox(Arrays.asList(pnts)));
	}
	
	@Test
	public void testTwoPoints() {
		Pnt2d[] pnts = { Pnt2d.from(2, 5), Pnt2d.from(5, 4)};
		runPointTest(pnts, new AxisAlignedBoundingBox(Arrays.asList(pnts)));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testEmptyPoints() {
		new AxisAlignedBoundingBox(new Pnt2d[0]);
	}
	
	// -------------------------------------------------------------------
	
	private static void runPointTest(Pnt2d[] points, AxisAlignedBoundingBox box) {
		Pnt2d[] corners = box.getCornerPoints();
		assertNotNull(corners);
		assertEquals(4, corners.length);
		
		// check if all sample points are inside the bounding box
		for (Pnt2d p : points) {
//			System.out.println("Point " + p);
			assertTrue("point not contained in bounding box: " + p, box.contains(p));
		}
	}

}

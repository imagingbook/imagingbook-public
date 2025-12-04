/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// Testing distance calculations
public class Pnt2dDistanceTest {

	static double DELTA = 1E-6;

	@Test
	public void testDistanceIntInt() {
		// int + int points
		Pnt2d p1 = PntInt.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		double dist = 5.0990195;
		assertEquals(dist, p1.distance(p2), DELTA);
		assertEquals(dist, p2.distance(p1), DELTA);
	}

	@Test
	public void testDistanceDoubleDouble() {
		// double + double points 
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		double dist = 5.0990195;
		assertEquals(dist, p1.distance(p2), DELTA);
		assertEquals(dist, p2.distance(p1), DELTA);
	}
	
	@Test
	public void testDistanceDoubleInt() {
		// adding double + int point a double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		double dist = 5.0990195;
		assertEquals(dist, p1.distance(p2), DELTA);
		assertEquals(dist, p2.distance(p1), DELTA);
	}
	
	
	@Test
	public void testL1DistanceDouble() {
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		double dist = 6;
		assertEquals(dist, p1.distL1(p2), DELTA);
		assertEquals(dist, p2.distL1(p1), DELTA);
	}
	
	
	@Test
	public void testL1DistanceInt() {
		PntInt p1 = PntInt.from( 3, 8);
		PntInt p2 = PntInt.from(-2, 7);
		int dist = 6;
		assertEquals(dist, p1.distL1(p2));
		assertEquals(dist, p2.distL1(p1));
	}

    // ------------------------------------------------------

    @Test
    public void testIsCloseTo1() {
        PntInt p1i = PntInt.from( 3, 8);
        PntInt p2i = PntInt.from( 2, 8);
        PntInt p3i = PntInt.from( 3, 9);

        assertTrue(p1i.isCloseTo(p1i));
        assertTrue(p1i.isCloseTo(p1i.duplicate()));
        assertTrue(p1i.isCloseTo(Pnt2d.from(p1i)));
        assertTrue(p1i.isCloseTo(PntInt.from(p1i)));
        assertTrue(p1i.isCloseTo(PntDouble.from(p1i)));

        assertFalse(p1i.isCloseTo(p2i));
        assertFalse(p2i.isCloseTo(p1i));
        assertFalse(p1i.isCloseTo(p3i));
        assertFalse(p3i.isCloseTo(p1i));
    }

    @Test
    public void testIsCloseTo2() {
        Pnt2d p1i = PntInt.from(999, -1);
        Pnt2d p2i = p1i.plus(-Pnt2d.TOLERANCE/2, 0);
        Pnt2d p3i = p1i.plus(0, Pnt2d.TOLERANCE/2);
        assertTrue(p1i.isCloseTo(p2i));
        assertTrue(p1i.isCloseTo(p3i));
        assertTrue(p1i.isCloseTo(p2i, Pnt2d.TOLERANCE));
        assertTrue(p1i.isCloseTo(p3i, Pnt2d.TOLERANCE));

        PntDouble p1d = PntDouble.from(999, -1);
        PntDouble p2d = p1d.plus(Pnt2d.TOLERANCE/2, -Pnt2d.TOLERANCE/2);
        assertFalse(p1d.isCloseTo(p2d));
        assertFalse(p1d.isCloseTo(p2d, Pnt2d.TOLERANCE));
    }

}

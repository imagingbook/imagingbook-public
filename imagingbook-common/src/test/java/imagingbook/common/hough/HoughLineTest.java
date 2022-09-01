/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.hough;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.line.AlgebraicLine;


public class HoughLineTest {
	
	static double TOL = 1e-6;
	
	static Pnt2d p1 = PntInt.from(30, 10);
	static Pnt2d p2 = PntInt.from(200, 100);
	static Pnt2d p3 = PntInt.from(90, 40);
	
	static Pnt2d pRef = PntInt.from(70, 50);
	
	@Test
	public void test0() {
		// example from CV lecture notes
		Pnt2d xRef = PntInt.from(90, 60);
		AlgebraicLine AL = AlgebraicLine.from(p1, p2);
	
		HoughLine HL = new HoughLine(AL, xRef.getX(), xRef.getY(), 0);
		assertEquals(0.0, HL.getDistance(p1), TOL);
		assertEquals(0.0, HL.getDistance(p2), TOL);
		
		assertTrue(HL.equals(AL, TOL));
		assertTrue(AL.equals(HL, TOL));
	}

	@Test
	public void test1() {
		HoughLine HL1 = HoughLine.from(p1, p2, pRef, 2);
		HoughLine HL2 = HoughLine.from(p2, p1, pRef, 2);
		
		assertEquals(0.0, HL1.getDistance(p1), TOL);
		assertEquals(0.0, HL1.getDistance(p2), TOL);
		
		assertEquals(0.0, HL2.getDistance(p1), TOL);
		assertEquals(0.0, HL2.getDistance(p2), TOL);
		
		assertTrue(HL1.equals(HL2, TOL));
		assertTrue(HL2.equals(HL1, TOL));
	}
	
	@Test
	public void test2() {
		HoughLine l12 = HoughLine.from(p1, p2, pRef, 2);
		Pnt2d x0 = l12.getClosestLinePoint(p3);
		assertEquals(0.0, l12.getDistance(x0), TOL);						// x0 is actually ON the line
		assertEquals(p3.distance(x0), Math.abs(l12.getDistance(p3)), TOL);	// distance (p3,x0) is shortest 
	}
	
	@Test
	public void test3() {
		// AL, HL1 HL2 must be the same lines:
		AlgebraicLine AL = AlgebraicLine.from(p1, p2);
		HoughLine HL1 = HoughLine.from(p1, p2, pRef, 2);
		HoughLine HL2 = new HoughLine(AL, pRef.getX(), pRef.getY(), 2);
		
		assertEquals(0.0, AL.getDistance(p1), TOL);
		assertEquals(0.0, AL.getDistance(p2), TOL);
		
		assertEquals(0.0, HL1.getDistance(p1), TOL);
		assertEquals(0.0, HL1.getDistance(p2), TOL);
		
		assertEquals(0.0, HL2.getDistance(p1), TOL);
		assertEquals(0.0, HL2.getDistance(p2), TOL);
		
		assertTrue(AL.equals(HL1, TOL));
		assertTrue(AL.equals(HL2, TOL));
		
		assertTrue(HL1.equals(AL, TOL));
		assertTrue(HL2.equals(AL, TOL));
	}
	
	@Test
	public void test4() {
		// create a HoughLine from two points
		HoughLine HL1 = HoughLine.from(p1, p2, pRef, 2);
		// check if both points are on the line
		assertEquals(0.0, HL1.getDistance(p1), TOL);
		assertEquals(0.0, HL1.getDistance(p2), TOL);
		
		// create a duplicate HoughLine from L1's angle and radius
		HoughLine HL2 = new HoughLine(HL1.getAngle(), HL1.getRadius(), pRef.getX(), pRef.getY(), 2);
		// check if the two points are on this line too
		assertEquals(0.0, HL2.getDistance(p1), TOL);
		assertEquals(0.0, HL2.getDistance(p2), TOL);
		assertTrue(HL1.equals(HL2, TOL));
		
		// create a duplicate HoughLine directly from HL1 (using the same reference point)
		HoughLine HL3 = new HoughLine(HL1, pRef.getX(), pRef.getY(), 2);
		// check if the two points are on this line too
		assertEquals(0.0, HL3.getDistance(p1), TOL);
		assertEquals(0.0, HL3.getDistance(p2), TOL);
		assertTrue(HL1.equals(HL3, TOL));
		
		// create a duplicate HoughLine directly from HL1 (using a different reference point)
		HoughLine HL4 = new HoughLine(HL1, pRef.getX() - 10, pRef.getY() + 15, 2);
		// check if the two points are on this line too
		assertEquals(0.0, HL4.getDistance(p1), TOL);
		assertEquals(0.0, HL4.getDistance(p2), TOL);
		assertTrue(HL1.equals(HL4, TOL));
	}
	
	@Test
	public void test5() { // Example is used in CV lecture notes
		// create AL (zero-referenced):
		double a1 = -90, b1 = 170, c1 = 1000;
		AlgebraicLine AL = new AlgebraicLine(a1, b1, c1);
		
		// create HL (with specific reference point):
		double xR = 10, yR = 50;
		
		HoughLine HL = new HoughLine(AL, xR, yR, 0);
		
		assertTrue(AL.equals(HL, TOL));
		assertTrue(HL.equals(AL, TOL));
	}
	
	@Test	// copy the reference point of a HoughLine
	public void test6() {
		HoughLine HL1 = HoughLine.from(p1, p2, pRef, 2);
//		System.out.println("HL1 = " + HL1);
		
		HoughLine HL2 = new HoughLine(HL1);
//		System.out.println("HL2 = " + HL2);
		
		// the 2 lines must be equal (describe the same points)
		assertTrue(HL1.equals(HL2, TOL));
		assertTrue(HL2.equals(HL1, TOL));
		
		// the 2 lines must have the same reference point
		assertEquals(HL1.getXref(), HL2.getXref(), TOL);
		assertEquals(HL1.getYref(), HL2.getYref(), TOL);
	}

	@Test	// change the reference point of a HoughLine
	public void test7() {
		Pnt2d pRefA = PntInt.from(70, 50);
		Pnt2d pRefB = PntInt.from(-5, 19);

		HoughLine HL1 = HoughLine.from(p1, p2, pRefA, 2);
		HoughLine HL2 = HoughLine.from(p1, p2, pRefB, 2);
			
//		System.out.println("HL1 = " + HL1);
//		System.out.println("HL2 = " + HL2);
		
		// the 2 lines must be equal (describe the same points)
		assertTrue(HL1.equals(HL2, TOL));
		assertTrue(HL2.equals(HL1, TOL));

		HoughLine HL3 = new HoughLine(HL1, pRefB.getX(), pRefB.getY(), 2);
//		System.out.println("HL3 = " + HL3);
		assertTrue(HL1.equals(HL3, TOL));
		assertTrue(HL3.equals(HL1, TOL));
		
		// the 2 lines must have the same reference point
		assertEquals(HL2.getXref(), HL3.getXref(), TOL);
		assertEquals(HL2.getYref(), HL3.getYref(), TOL);
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.shape.ShapeChecker;

public class HessianLineTest {
	
	static double TOL = 1E-6;
	
	static Pnt2d p1 = PntInt.from(30, 10);
	static Pnt2d p2 = PntInt.from(200, 100);
	static Pnt2d p3 = PntInt.from(90, 40);

	@Test
	public void test1() {
		// example from CV lecture notes
		HessianLine H1 = HessianLine.from(p1, p2);
		
		assertEquals(0.0, H1.getDistance(p1), TOL);						// x1 is actually ON the line
		assertEquals(0.0, H1.getDistance(p2), TOL);						// x1 is actually ON the line
		
		assertEquals(-0.4678877204190327, H1.A, TOL);
		assertEquals( 0.8837879163470618, H1.B, TOL);
		assertEquals( 5.198752449100363, H1.C, TOL);
		
		assertEquals( 2.0576955586061656, H1.getAngle(), TOL); 	
		assertEquals(-5.198752449100363, H1.getRadius(), TOL); 
	}
	
	@Test
	public void test2() {
		double angle = 0.2;
		double radius = 80;
		HessianLine H1 = new HessianLine(angle, radius);
		HessianLine H2 = new HessianLine(H1);
		assertEquals(angle, H2.getAngle(), TOL);
		assertEquals(radius, H2.getRadius(), TOL);
		
		assertTrue(H1.equals(H2, TOL));
		assertTrue(H2.equals(H1, TOL));
	}
	
	@Test
	public void test3() {
		HessianLine H1 = HessianLine.from(p1, p2);
		Pnt2d x0 = H1.getClosestLinePoint(p3);
		assertEquals(0.0, H1.getDistance(x0), TOL);							// x0 is actually ON the line
		assertEquals(p3.distance(x0), Math.abs(H1.getDistance(p3)), TOL);	// distance (p3,x0) is shortest 
	}
	
	@Test
	public void test4() {
		HessianLine L12 = HessianLine.from(p1, p2);
		Assert.assertTrue("produced Shape does not match line", new ShapeChecker().check(L12, L12.getShape()));
	}

}

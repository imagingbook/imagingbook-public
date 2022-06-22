/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;

import java.awt.Shape;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.shape.ShapeChecker;


public class AlgebraicLineTest {
	
	static double TOL = 1E-6;
	
	static Pnt2d p1 = PntInt.from(30, 10);
	static Pnt2d p2 = PntInt.from(200, 100);
	static Pnt2d p3 = PntInt.from(90, 40);

	@Test
	public void test1() {
		AlgebraicLine L12 = AlgebraicLine.from(p1, p2);		
		AlgebraicLine L21 = AlgebraicLine.from(p2, p1);
		
		Assert.assertTrue(L12.equals(L21, TOL));
		Assert.assertTrue(L21.equals(L12, TOL));
		
		Assert.assertEquals(0.0, L12.getDistance(p1), TOL);
		Assert.assertEquals(0.0, L12.getDistance(p2), TOL);
		
		Assert.assertEquals(0.0, L21.getDistance(p1), TOL);
		Assert.assertEquals(0.0, L21.getDistance(p2), TOL);
	}
	
	@Test
	public void test2() {
		AlgebraicLine L12 = AlgebraicLine.from(p1, p2);
		Pnt2d x0 = L12.getClosestLinePoint(p3);
		Assert.assertEquals(0.0, L12.getDistance(x0), TOL);						// x0 is actually ON the line
		Assert.assertEquals(p3.distance(x0), Math.abs(L12.getDistance(p3)), TOL);	// distance (p3,x0) is shortest 
	}

	@Test
	public void test3() {
		AlgebraicLine L1 = new AlgebraicLine(10, 15, -2);
		AlgebraicLine L2 = new AlgebraicLine(L1.getParameters());
		Assert.assertTrue(L1.equals(L2));
		Assert.assertTrue(L2.equals(L1));
	}
	
	@Test
	public void test4() {
		AlgebraicLine L1 = new AlgebraicLine(10, 3, -2);
		AlgebraicLine L2 = new AlgebraicLine(-10, -3, 2);
		Assert.assertTrue(L1.equals(L2));
		Assert.assertTrue(L2.equals(L1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test5() {
		new AlgebraicLine(0, 0, -2);
	}
	
	
	@Test	// check AWT Shape generation
	public void test6() {
		AlgebraicLine L12 = AlgebraicLine.from(p1, p2);
		Assert.assertTrue("produced Shape does not match line", new ShapeChecker().check(L12, L12.getShape()));
	}
}

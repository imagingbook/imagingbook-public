/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.basic;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

// Adding points to points.
public class Pnt2dAdditionTest {

	static double DELTA = 1E-6;
	
	static int x1 = 3,  y1 = 8;
	static int x2 = -2, y2 = 7;
	static int x3 = 1, y3 = 15;


	@Test
	public void testAdditionIntInt() {
		// int + int points must create another int point
		Pnt2d p1 = PntInt.from( x1, y1);
		Pnt2d p2 = PntInt.from(x2, y2);
		Pnt2d p3 = p1.plus(p2);
		Assert.assertTrue(p3 instanceof PntInt);
		Assert.assertEquals(x3, ((PntInt)p3).x);
		Assert.assertEquals(y3, ((PntInt)p3).y);
	}

	@Test
	public void testAdditionDoubleDouble() {
		// double + double points must create another double point
		Pnt2d p1 = PntDouble.from( x1, y1);
		Pnt2d p2 = PntDouble.from(x2, y2);
		Pnt2d p3 = p1.plus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(x3, p3.getX(), DELTA);
		Assert.assertEquals(y3, p3.getY(), DELTA);
	}
	
	@Test
	public void testAdditionDoubleInt() {
		// adding double + int point a double point
		Pnt2d p1 = PntDouble.from( x1, y1);
		Pnt2d p2 = PntInt.from(x2, y2);
		Pnt2d p3 = p1.plus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(x3, p3.getX(), DELTA);
		Assert.assertEquals(y3, p3.getY(), DELTA);
	}

	@Test
	public void testAdditionIntDouble() {
		// adding int + double
		Pnt2d p1 = PntInt.from( x1, y1);
		Pnt2d p2 = PntDouble.from(x2, y2);
		Pnt2d p3 = p1.plus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(x3, p3.getX(), DELTA);
		Assert.assertEquals(y3, p3.getY(), DELTA);
	}
}

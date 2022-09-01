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

// Subtract points from points.
public class Pnt2dSubtractionTest {

	static double DELTA = 1E-6;

	@Test
	public void testSubtractIntInt() {
		// subtracting int - int points must create another int point
		Pnt2d p1 = PntInt.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		Pnt2d p3 = p1.minus(p2);
		Assert.assertTrue(p3 instanceof PntInt);
		Assert.assertEquals(5, ((PntInt)p3).x);
		Assert.assertEquals(1, ((PntInt)p3).y);
	}

	@Test
	public void testSubtractDoubleDouble() {
		// subtracting double - double points must create another double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		Pnt2d p3 = p1.minus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(5, p3.getX(), DELTA);
		Assert.assertEquals(1, p3.getY(), DELTA);
	}
	
	@Test
	public void testSubtractDoubleInt() {
		// subtracting double - int point a double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		Pnt2d p3 = p1.minus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(5, p3.getX(), DELTA);
		Assert.assertEquals(1, p3.getY(), DELTA);
	}

	@Test
	public void testSubtractIntDouble() {
		// subtracting int - double
		Pnt2d p1 = PntInt.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		Pnt2d p3 = p1.minus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(5, p3.getX(), DELTA);
		Assert.assertEquals(1, p3.getY(), DELTA);
	}
}

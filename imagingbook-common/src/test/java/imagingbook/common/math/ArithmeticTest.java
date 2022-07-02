/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class ArithmeticTest {
	
	static double TOL = 1E-9;

	@Test
	public void testModDoubleDouble() {
		
		assertEquals( 1.4, Arithmetic.mod( 3.5,  2.1), TOL);
		assertEquals( 0.7, Arithmetic.mod(-3.5,  2.1), TOL);
		assertEquals(-0.7, Arithmetic.mod( 3.5, -2.1), TOL);
		assertEquals(-1.4, Arithmetic.mod(-3.5, -2.1), TOL);
	}

	@Test
	public void testEqualsDoubleDouble() {
		assertFalse(Arithmetic.equals(-0.7, 0.8));
		assertTrue(Arithmetic.equals(-0.7, -0.7));
		assertTrue(Arithmetic.equals(1.0, 1.0 + Arithmetic.EPSILON_DOUBLE / 2));
		assertTrue(Arithmetic.equals(1.0, 1.0 - Arithmetic.EPSILON_DOUBLE / 2));
	}

	@Test
	public void testEqualsFloatFloat() {
		assertFalse(Arithmetic.equals(-0.7f, 0.8f));
		assertTrue(Arithmetic.equals(-0.7f, -0.7f));
		assertTrue(Arithmetic.equals(1.0f, 1.0f + Arithmetic.EPSILON_FLOAT / 2));
		assertTrue(Arithmetic.equals(1.0f, 1.0f - Arithmetic.EPSILON_FLOAT / 2));
	}
	
	@Test
	public void testToPolar0() {
		double x = 0;
		double y = 0;
		double[] polar = Arithmetic.toPolar(x, y);
		assertEquals(0, polar[0], TOL);
		double[] cart = Arithmetic.toCartesian(polar);
		assertEquals(x, cart[0], TOL);
		assertEquals(y, cart[1], TOL);
	}
	
	@Test
	public void testToPolar1() {
		Random rg = new Random(17);
		for (int i = 0; i < 1000; i++) {
			double x = 100 * rg.nextDouble() - 50;
			double y = 100 * rg.nextDouble() - 50;
			double[] polar = Arithmetic.toPolar(x, y);
			double[] cart = Arithmetic.toCartesian(polar);
			assertEquals(x, cart[0], TOL);
			assertEquals(y, cart[1], TOL);
		}
	}
	
//	@Test
//	public void modVsRemainder() {
//		double a = 3.5, b = 2.1;
//		doIt(a,b);
//		doIt(a,-b);
//		doIt(-a,b);
//		doIt(-a,-b);
//	}
//	
//	private void doIt(double a, double b) {
//		double r1 = Arithmetic.mod( a,  b);
//		double r2 = a %  b;
//		System.out.format("(%.1f, %.1f): %.1f  vs %.1f\n", a, b, r1, r2);
//		//System.out.format("(%.1f mod %.1f) -> %.3f\n", a, b, r1);
//	}

}

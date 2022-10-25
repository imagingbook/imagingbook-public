/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.mod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class ComplexTest {
	
	Complex z1 = new Complex(0.3, 0.6);
	Complex z2 = new Complex(-1, 0.2);
	
	@Test
	public void testComplexEquals() {
		assertTrue(z1.equals(z1));
		assertTrue(z2.equals(z2));
		
		assertFalse(z1.equals(z2));
		assertFalse(z2.equals(z1));
		assertFalse(z1.equals(Double.valueOf(0.3)));
		
		assertTrue(z1.equals(new Complex(z1)));
		assertTrue(z2.equals(new Complex(z2)));
		
		assertTrue(z1.equals(new Complex(z1.re, z1.im)));
		assertTrue(z2.equals(new Complex(z2.re, z2.im)));
		
		assertTrue(z1.equals(new Complex(z1.toArray())));
		assertTrue(z2.equals(new Complex(z2.toArray())));
	}
	
	@Test
	public void testComplexArg() {
		Random rg = new Random(17);
		for (int i = 0; i < 1000; i++) {
			double phi = rg.nextDouble() * 20 - 10;	// phi in [-10, 10)
			Complex z = new Complex(phi).multiply(3);
			assertEquals(mod(phi, Math.PI), mod(z.arg(), Math.PI), 1e-6);
		}
	}
	
	@Test
	public void testComplexNaN() { 
		assertFalse(z1.isNaN());
		assertFalse(z2.isNaN());	
		assertTrue(new Complex(Double.NaN, 3).isNaN());
		assertTrue(new Complex(1, Double.NaN).isNaN());
		assertTrue(z1.multiply(0.0/0).isNaN());
	}
	
	@Test	// z1 + z2 = (-0.700000000, 0.800000000)
	public void testComplexAdd() {
		Complex z12 = z1.add(z2);
//		assertEquals(-0.7, z12.re, 1E-6);
//		assertEquals( 0.8, z12.im, 1E-6);
		assertTrue(z12.equals(new Complex(-0.7, 0.8)));
		assertTrue(z12.equals(-0.7, 0.8));
		
		Complex z21 = z2.add(z1);
//		assertEquals(-0.7, z21.re, 1E-6);
//		assertEquals( 0.8, z21.im, 1E-6);
		assertTrue(z21.equals(new Complex(-0.7, 0.8)));
		assertTrue(z21.equals(-0.7, 0.8));
	}

	@Test // z1 * z2 = (-0.420000000, -0.540000000)
	public void testComplexMultiply() {
		// test multiplication (commutative)
		Complex z12 = z1.multiply(z2);
//		assertEquals(-0.42, z12.re, 1E-6);
//		assertEquals(-0.54, z12.im, 1E-6);
		assertTrue(z12.equals(new Complex(-0.42, -0.54)));
		assertTrue(z12.equals(-0.42, -0.54));
		
		Complex z21 = z2.multiply(z1);
//		assertEquals(-0.42, z21.re, 1E-6);
//		assertEquals(-0.54, z21.im, 1E-6);
		assertTrue(z21.equals(new Complex(-0.42, -0.54)));
		assertTrue(z21.equals(-0.42, -0.54));
	}
	
	@Test // z1.pow(5) = (0.099630000, -0.092340000)
	public void testComplexPow() {
		Complex z12 = z1.pow(5);
//		assertEquals( 0.09963, z12.re, 1E-6);
//		assertEquals(-0.09234, z12.im, 1E-6);
		assertTrue(z12.equals(0.09963, -0.09234, 1E-6));
	}

	@Test // z1.rotate(0.1) = (0.238601200, 0.626952524)
	public void testComplexRotate1() {
		Complex z12 = z1.rotate(0.1);
//		assertEquals(0.238601200, z12.re, 1E-6);
//		assertEquals(0.626952524, z12.im, 1E-6);
		assertTrue(z12.equals(0.238601200, 0.626952524, 1E-6));
	}
	
	@Test 
	public void testComplexRotate2() {
		Random rg = new Random(17);
		for (int i = 0; i < 1000; i++) {
			double phi = rg.nextDouble() * 20 - 10;	// phi in [-10, 10)
			Complex z12 = z1.rotate(phi).rotate(-phi);
			assertTrue(z1.equals(z12, 1E-6));
		}
	}
	
}

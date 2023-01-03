/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class ArithmeticTest {
	
	static double TOLD = 1E-9;
	static float TOLF = 1E-6f;

	@Test
	public void testModDoubleDouble() {
		
		assertEquals( 1.4, Arithmetic.mod( 3.5,  2.1), TOLD);
		assertEquals( 0.7, Arithmetic.mod(-3.5,  2.1), TOLD);
		assertEquals(-0.7, Arithmetic.mod( 3.5, -2.1), TOLD);
		assertEquals(-1.4, Arithmetic.mod(-3.5, -2.1), TOLD);
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
		assertEquals(0, polar[0], TOLD);
		double[] cart = Arithmetic.toCartesian(polar);
		assertEquals(x, cart[0], TOLD);
		assertEquals(y, cart[1], TOLD);
	}
	
	@Test
	public void testToPolar1() {
		Random rg = new Random(17);
		for (int i = 0; i < 1000; i++) {
			double x = 100 * rg.nextDouble() - 50;
			double y = 100 * rg.nextDouble() - 50;
			double[] polar = Arithmetic.toPolar(x, y);
			double[] cart = Arithmetic.toCartesian(polar);
			assertEquals(x, cart[0], TOLD);
			assertEquals(y, cart[1], TOLD);
		}
	}
	
	@Test
	public void testMinMax() {
		assertEquals(13, Arithmetic.min(13));
		assertEquals(-3.5, Arithmetic.min(-3.5), TOLD);	
		assertEquals(7, Arithmetic.min(13, 7, 22));
		assertEquals(7.0, Arithmetic.min(13.0, 7.0, 22.0), TOLD);
		
		assertEquals(13, Arithmetic.max(13));
		assertEquals(-3.5, Arithmetic.max(-3.5), TOLD);
		assertEquals(22, Arithmetic.max(13, 7, 22));
		assertEquals(22.0, Arithmetic.max(13.0, 7.0, 22.0), TOLD);	
	}
	
	@Test
	public void testModDouble() {
		assertEquals(1.4, Arithmetic.mod( 3.5, 2.1), TOLD);
		assertEquals(0.7, Arithmetic.mod(-3.5, 2.1), TOLD); 
		assertEquals(-0.7, Arithmetic.mod( 3.5,-2.1), TOLD);
		assertEquals(-1.4, Arithmetic.mod(-3.5,-2.1), TOLD);
	}
	
	@Test
	public void testRoots() {
		{
			double[] x12 = Arithmetic.getRealRoots(1, -7, 10);	// -> {2, 5}
			Arrays.sort(x12);
			assertEquals(2.0, x12[0], TOLD);
			assertEquals(5.0, x12[1], TOLD);
		}		
		{
			double[] x12 = Arithmetic.getRealRoots(-2, 2, 1);	// -> {1.3660254037844386, -0.3660254037844386}
			Arrays.sort(x12);
			assertEquals(-0.3660254037844386, x12[0], TOLD);
			assertEquals( 1.3660254037844386, x12[1], TOLD);
			
		}		
		{
			double[] x12 = Arithmetic.getRealRoots(5, 2, 3);	// -> {-0.2 - 0.748331 I, -0.2 + 0.748331 I}
			assertNull(x12);
		}
	}

	@Test
	public void testClipTo() {
		assertEquals(0.3, Arithmetic.clipTo(0.3, 0.0, 1.0), TOLD);
		assertEquals(0.0, Arithmetic.clipTo(-0.3, 0.0, 1.0), TOLD);
		assertEquals(1.0, Arithmetic.clipTo(1.3, 0.0, 1.0), TOLD);
		
		assertEquals(0.3f, Arithmetic.clipTo(0.3f, 0.0f, 1.0f), TOLF);
		assertEquals(0.0f, Arithmetic.clipTo(-0.3f, 0.0f, 1.0f), TOLF);
		assertEquals(1.0f, Arithmetic.clipTo(1.3f, 0.0f, 1.0f), TOLF);
		
		assertEquals(23, Arithmetic.clipTo(23, 0, 255));
		assertEquals(0, Arithmetic.clipTo(-5, 0, 255));
		assertEquals(255, Arithmetic.clipTo(256, 0, 255));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testClipToFailD() {
		Arithmetic.clipTo(0.3, 1.0, 0.0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testClipToFailF() {
		Arithmetic.clipTo(0.3f, 1.0f, 0.0f);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testClipToFailI() {
		Arithmetic.clipTo(3, 255, 0);
	}
}

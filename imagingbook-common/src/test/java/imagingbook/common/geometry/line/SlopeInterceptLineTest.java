/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.shape.ShapeChecker;

public class SlopeInterceptLineTest {
	
	static double TOL = 1e-6;

	@Test
	public void test1() { 	// check y-values obtained from getY()
		for (int k = -10; k <= 10; k++) {
			for (int d = -10; d <= 10; d++) {
				SlopeInterceptLine line = new SlopeInterceptLine(k, d);
				assertEquals(0 + d, line.getY(0), TOL);
				assertEquals(k + d, line.getY(1), TOL);
				assertEquals(-k + d, line.getY(-1), TOL);
			}
		}
	}
	
	@Test	// check conversion to AlgebraicLine and equality
	public void test2() {
		for (int k = -1000; k <= 1000; k+=33) {
			for (int d = -1000; d <= 1000; d+=77) {
				SlopeInterceptLine SL1 = new SlopeInterceptLine(k, d);
				AlgebraicLine AL1 = AlgebraicLine.from(SL1);
				SlopeInterceptLine SL2 = new SlopeInterceptLine(AL1);
				assertTrue(SL1.equals(SL2, TOL));
				assertTrue(SL2.equals(SL1, TOL));
			}
		}
	}
	
	@Test	// check points on line
	public void test3() {
		SlopeInterceptLine sil = new SlopeInterceptLine(-2, 3);
		assertEquals(0, sil.getDistance(0, 3), TOL);
		assertEquals(0, sil.getDistance(1, 1), TOL);
	}
	
	@Test	// check AWT Shape generation
	public void test4() {
		SlopeInterceptLine sil = new SlopeInterceptLine(-2, 3);
		Assert.assertTrue("produced Shape does not match line", new ShapeChecker().check(sil, sil.getShape()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test5() {
		AlgebraicLine al = AlgebraicLine.from(Pnt2d.from(2, -1), Pnt2d.from(2, 17)); 	// vertical line
		@SuppressWarnings("unused")
		SlopeInterceptLine sl = new SlopeInterceptLine(al);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test6() {
		AlgebraicLine al = new AlgebraicLine(0.3, 0, -2); 	// vertical line
		@SuppressWarnings("unused")
		SlopeInterceptLine sl = new SlopeInterceptLine(al);
	}

}


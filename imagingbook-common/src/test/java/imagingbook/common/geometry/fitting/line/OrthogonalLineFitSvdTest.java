/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;

public class OrthogonalLineFitSvdTest {

	static double TOL = 1e-6;

	@Test
	public void test1() {
		double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
		AlgebraicLine refLine = new AlgebraicLine(-0.4968900437902618, -0.8678135078357052, 7.244827854073206);
		
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		LineFit fit = new OrthogonalLineFitSvd(pts);
		AlgebraicLine line = fit.getLine();
		assertNotNull(line);
		assertTrue(refLine.equals(line, TOL));	
		assertEquals(2.6645834350486606, fit.getSquaredOrthogonalError(pts), TOL);
	}
	
	@Test
	public void test2() {
		double[][] X = {{1, 8}, {4, 5}}; // 2 points only
		AlgebraicLine refLine = new AlgebraicLine(-0.7071067811865476, -0.7071067811865476, 6.3639610306789285);
		
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		LineFit fit = new OrthogonalLineFitSvd(pts);
		AlgebraicLine line = fit.getLine();
		assertNotNull(line);
		assertTrue(refLine.equals(line, TOL));	
		assertEquals(0.0, fit.getSquaredOrthogonalError(pts), TOL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test3() {
		double[][] X = {{1, 8}};
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		@SuppressWarnings("unused")
		LineFit fit = new OrthogonalLineFitSvd(pts);
	}


}

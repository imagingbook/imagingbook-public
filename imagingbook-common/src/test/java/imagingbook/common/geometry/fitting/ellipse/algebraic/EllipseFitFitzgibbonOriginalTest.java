/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;


import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.AlgebraicEllipse;
import imagingbook.common.geometry.fitting.ellipse.algebraic.data.TestPointSet5;
import imagingbook.common.geometry.fitting.ellipse.algebraic.data.TestPointSet6;

public class EllipseFitFitzgibbonOriginalTest {

	@Test(expected = IllegalArgumentException.class)
	public void test1() {
		Pnt2d[] points = TestPointSet5.points;	// 5 points are not enough!
		new EllipseFitFitzgibbonOriginal(points);
	}
	
	@Test
	public void test2() {
		Pnt2d[] points = TestPointSet6.points;
		double[] q_exp = TestPointSet6.qExpected;
		
		EllipseFitAlgebraic fit = new EllipseFitFitzgibbonOriginal(points);
		double[] q = fit.getParameters();
		assertNotNull("ellipse parameters are null", q);
		
		AlgebraicEllipse ell = fit.getEllipse();
		assertNotNull("ellipse is null", ell);
		
		double[] qn = ell.getParameters(); 	// normalized ellipse parameters
		assertArrayEquals("ellipse parameters do not match", q_exp, qn, 1e-6);
	}

}

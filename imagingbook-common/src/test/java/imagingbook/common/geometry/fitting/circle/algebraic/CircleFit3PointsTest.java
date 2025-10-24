/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.AlgebraicCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic.FitType;

public class CircleFit3PointsTest {
	
	// 3 points for the circle to pass through
	static Pnt2d p0 = Pnt2d.from(6, 7);
	static Pnt2d p1 = Pnt2d.from(1, 9);
	static Pnt2d p2 = Pnt2d.from(-3, 5);
	static Pnt2d[] pnts = {p0, p1, p2};
	
	static AlgebraicCircle acExp = new AlgebraicCircle(28.0, -108.0, -228.0, -136.0);	// expected circle (normalized)

	/**
	 * Fits a circle to 3 given points and checks the outcome.
	 */
	@Test	// check dedicated 3-point fit
	public void test1() {
		doCheck12(new CircleFit3Points(p0, p1, p2), acExp);
		doCheck12(new CircleFit3Points(p2, p0, p1), acExp);
		doCheck12(new CircleFit3Points(p1, p2, p0), acExp);
	}
	
	@Test	// test all algebraic circle fit types on 3 points
	public void test2() {
		for (FitType type : FitType.values()) {		
//			System.out.println("fit type = " + type);
			doCheck12(CircleFitAlgebraic.getFit(type, pnts), acExp);
		}
	}
	
	private void doCheck12(CircleFitAlgebraic fit, AlgebraicCircle acExp) {
		double[] p = fit.getParameters();
		String msg = "fit type = " + fit.getClass().getSimpleName();
		assertNotNull(msg, p);
		AlgebraicCircle ac = fit.getAlgebraicCircle();		// fitted circle
		assertNotNull(msg, ac);	
//		System.out.println(msg + " ac=" + ac);
		assertTrue(msg, acExp.equals(ac, 1e-6));					// compare ac to expected circle
	}
	
	
	@Test
	public void test3() {
		// fit to 3 collinear points  (getParameters() must return null)
		Pnt2d x0 = Pnt2d.from(-1, -1);
		Pnt2d x1 = Pnt2d.from(4, 4);
		Pnt2d x2 = Pnt2d.from(99, 99);
		
		CircleFitAlgebraic fit = new CircleFit3Points(x0, x1, x2);
		assertNull(fit.getParameters());

	}
	


}

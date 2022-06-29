/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.ellipse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.shape.ShapeChecker;

public class GeometricEllipseTest {
	
	private static final double TOL = 1e-6;

	@Test		// GeometricEllipse to/from AlgebraicEllipse conversion
	public void test1() {
		{
			GeometricEllipse ge1 = new GeometricEllipse(120, 50, 200, -70, Math.PI/3);
			AlgebraicEllipse ae1 = new AlgebraicEllipse(ge1);	
			GeometricEllipse ge2 = new GeometricEllipse(ae1);
	
			assertTrue(ge1.equals(ge2, TOL));
			assertTrue(ge2.equals(ge1, TOL));
		}
		{	// axis lengths reversed, big angle
			GeometricEllipse ge1 = new GeometricEllipse(50, 120, 200, -70, 1000);
			AlgebraicEllipse ae1 = new AlgebraicEllipse(ge1);	
			GeometricEllipse ge2 = new GeometricEllipse(ae1);
	
			assertTrue(ge1.equals(ge2, TOL));
			assertTrue(ge2.equals(ge1, TOL));
		}
	}
	
	@Test		// GeometricEllipse to/from AlgebraicEllipse conversion (random)
	public void test2() {
		Random rg = new Random(17);
		for (int i = 0; i < 1000; i++) {
			double ra = 1 + 100 * rg.nextDouble();
			double rb = 1 + 100 * rg.nextDouble();
			double xc = 500 * (rg.nextDouble() - 0.5);
			double yc = 500 * (rg.nextDouble() - 0.5);
			double theta = 20 * (rg.nextDouble() - 0.5);
			
			GeometricEllipse ge1 = new GeometricEllipse(ra, rb, xc, yc, theta);
			AlgebraicEllipse ae1 = new AlgebraicEllipse(ge1);	
			GeometricEllipse ge2 = new GeometricEllipse(ae1);
	
			assertTrue(ge1.equals(ge2, TOL));
		}
	}
	
	
	@Test		// check duplicate and equals
	public void test3() {
		GeometricEllipse ge1 = new GeometricEllipse(120, 50, 200, -70, Math.PI/3);
		GeometricEllipse ge2 = ge1.duplicate();
		assertTrue(ge1.equals(ge2, TOL));
		assertTrue(ge2.equals(ge1, TOL));
	}
	
	@Test	// GeometricEllipse closest point
	public void test4() {	
		GeometricEllipse ell = new GeometricEllipse(6, 5, 0, 0, 0);
		{
			Pnt2d x = Pnt2d.from(0, -0.000000001);
			Pnt2d xp = Pnt2d.from(0, -5);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(10, 0);
			Pnt2d xp = Pnt2d.from(6, 0);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(0, 10000);
			Pnt2d xp = Pnt2d.from(0, 5);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(0, -10000);
			Pnt2d xp = Pnt2d.from(0, -5);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(-1, 0);
			Pnt2d xp = Pnt2d.from(-3.2727272727272725, 4.190702026042222);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(1, 0.1);
			Pnt2d xp = Pnt2d.from(3.107598626723163, 4.277104138229151);
			assertEquals(xp, ell.getClosestPoint(x));
		}
	}
	
	@Test		// check AWT Shape generation
	public void test5() {
		GeometricEllipse ge = new GeometricEllipse(120, 50, 200, -70, Math.PI/3);
		Assert.assertTrue("produced Shape does not match line", new ShapeChecker().check(ge, ge.getShape()));
	}

}

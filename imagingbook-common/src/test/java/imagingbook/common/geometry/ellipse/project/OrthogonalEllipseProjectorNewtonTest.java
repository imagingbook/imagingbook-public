/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.ellipse.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;

public class OrthogonalEllipseProjectorNewtonTest {

	private static double TOL = 1e-4;

	@Test
	public void test1() {
		GeometricEllipse ell = new GeometricEllipse(6, 5, 0, 0, 0);
		EllipseProjector projector = new OrthogonalEllipseProjectorNewton(ell);
		
		Pnt2d p = Pnt2d.from(0.1 , 0.1);
		Pnt2d p0exp = Pnt2d.from(0.31302492, 4.99319088);
		double distExp = 4.897825699181927;
		
		doCheck(projector, p, p0exp, distExp);
		// TODO: add more points
	}
	
	private void doCheck(EllipseProjector projector, Pnt2d p, Pnt2d p0Exp, double distExp) {
		Pnt2d p0 = projector.project(p);
		assertEquals(p0Exp.getX(), p0.getX(), TOL);
		assertEquals(p0Exp.getY(), p0.getY(), TOL);
		double dist = projector.getDistance(p.toDoubleArray());
		assertEquals(distExp, dist, TOL);
	}
}

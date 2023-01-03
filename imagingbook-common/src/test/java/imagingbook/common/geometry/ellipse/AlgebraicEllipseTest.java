/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.ellipse;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import imagingbook.common.math.Matrix;

public class AlgebraicEllipseTest {

	@Test		// test parameter normalization and equals
	public void test1() {
		AlgebraicEllipse ae1 = new AlgebraicEllipse(0.5, -0.3, 1, 4, -5, 7);
		double[] p = ae1.getParameters();
		assertNotNull(p);
		
		AlgebraicEllipse ae2 = ae1.duplicate();
		assertTrue(ae1.equals(ae2, 1e-6));
		assertTrue(ae2.equals(ae1, 1e-6));
		
		AlgebraicEllipse ae3 = new AlgebraicEllipse(Matrix.multiply(-1, p));
		assertTrue(ae1.equals(ae3, 1e-6));
		
		AlgebraicEllipse ae4 = new AlgebraicEllipse(Matrix.multiply(1e7, p));
		assertTrue(ae1.equals(ae4, 1e-6));
	}

	
	@Test		// test AlgebraicEllipse/GeometricEllipse conversion
	public void test2() {
//		GeometricEllipse ge1  = new GeometricEllipse(150, 50, 150, 150, 0.5);
		
		AlgebraicEllipse ae1 = new AlgebraicEllipse(0.5, -0.3, 1, 4, -5, 7);
		GeometricEllipse ge1 = new GeometricEllipse(ae1);
		
		AlgebraicEllipse ae2  = new AlgebraicEllipse(ge1);
//		System.out.println("ae2 = " + ae2);
		
		assertTrue(ae1.equals(ae2, 1e-6));
	}

}

// 0.000055610, -0.000131871, 0.000140283, 0.003097622, -0.022304377, 0.999746408
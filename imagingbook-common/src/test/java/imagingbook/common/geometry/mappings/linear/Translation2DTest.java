/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.mappings.linear;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class Translation2DTest {

	@Test
	public void testInvert() {
		Translation2D m = new Translation2D(-1.5, 19.0);
		Pnt2d p = Pnt2d.from(3.5, -17);
		Pnt2d q = m.applyTo(p);
		
		Pnt2d pp = m.getInverse().applyTo(q);
		
		Assert.assertArrayEquals(p.toDoubleArray(), pp.toDoubleArray(), 1E-6);
	}
	
	@Test
	public void testFromPoints() {
		Pnt2d p = Pnt2d.from(3.5, -17);
		Pnt2d q = Pnt2d.from(-5, 7.1);
		
		Translation2D m = Translation2D.fromPoints(p, q);
		Pnt2d pp = m.applyTo(p);

		Assert.assertArrayEquals(q.toDoubleArray(), pp.toDoubleArray(), 1E-6);
	}

}

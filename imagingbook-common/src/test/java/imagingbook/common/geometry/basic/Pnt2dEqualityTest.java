/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

public class Pnt2dEqualityTest {

	@Test
	public void testEquals() {
		Pnt2d pi1 = PntInt.from(3, 8);
		Pnt2d pi2 = PntInt.from(3, 8);
		
		Pnt2d pd1 = PntDouble.from(3, 8);
		Pnt2d pd2 = PntDouble.from(3, 8);
		
		assertEquals(pi1, pi2);
		assertEquals(pi2, pi1);
		
		assertEquals(pd1, pd2);
		assertEquals(pd2, pd1);
		
		assertEquals(pi1, pd1);
		assertEquals(pd1, pd2);
		
		Pnt2d pd3 = PntDouble.from(3, 8.1);
		assertNotEquals(pd1, pd3);
		assertNotEquals(pi1, pd3);
	}

}

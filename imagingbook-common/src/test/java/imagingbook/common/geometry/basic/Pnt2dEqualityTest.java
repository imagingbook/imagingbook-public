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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

public class Pnt2dEqualityTest {

    static Pnt2d pi1 = PntInt.from(3, 8);
    static Pnt2d pi2 = PntInt.from(3, 8);

    static Pnt2d pd1 = PntDouble.from(3, 8);
    static Pnt2d pd2 = PntDouble.from(3, 8);

	@Test
	public void testEqualsInt1() {
		assertEquals(pi1, pi2);
		assertEquals(pi2, pi1);
        assertTrue(pi1.equals(pi2));
        assertTrue(pi2.equals(pi1));
	}

    @Test
    public void testEqualsDouble1() {
        assertEquals(pd1, pd2);
        assertEquals(pd2, pd1);
        assertTrue(pd1.equals(pd2));
        assertTrue(pd2.equals(pd1));
    }

    @Test
    public void testEqualsMixed1() {
        assertEquals(pi1, pd1);
        assertEquals(pd1, pi1);
        assertTrue(pi1.equals(pd1));
        assertTrue(pd1.equals(pi1));
    }

    @Test
    public void testEqualsDouble2() {
        Pnt2d pd3 = PntDouble.from(3, 8.1);
        assertNotEquals(pd1, pd3);
        assertNotEquals(pi1, pd3);
    }

    @Test
    public void testEqualsDouble3() {
        Pnt2d pA = PntDouble.from(Double.NaN, Double.POSITIVE_INFINITY);
        Pnt2d pB = PntDouble.from(0.0/0.0, 1.0/0.0);
        assertEquals(pA, pB);
        assertEquals(pB, pA);
    }

}

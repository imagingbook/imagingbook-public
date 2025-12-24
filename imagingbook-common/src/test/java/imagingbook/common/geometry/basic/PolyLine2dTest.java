/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.basic;

import org.junit.Test;

import java.util.List;

import static imagingbook.common.util.ListUtils.reversedCopy;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PolyLine2dTest {

    // static List<Pnt2d> UNIT_SQUARE_CCW = Arrays.asList(Pnt2d.from(0, 0), Pnt2d.from(1, 0), Pnt2d.from(1, 1), Pnt2d.from(0, 1));
    // static List<Pnt2d> UNIT_SQUARE_CW = reversedCopy(UNIT_SQUARE_CCW);

    static List<Pnt2d> unitSquareCW = Polygon2d.makePntList(0, 0, 0, 1, 1, 1, 1, 0);
    static List<Pnt2d> unitSquareCCW = reversedCopy(unitSquareCW);
    static List<Pnt2d> triangleCCW  = Polygon2d.makePntList(-2, -1, 4, 3, -1, 5);
    static List<Pnt2d> triangleCW  = reversedCopy(triangleCCW);

    @Test
    public void getLengthTest() {
        assertEquals(3, new PolyLine2d(unitSquareCW).getLength(), 1e-6);
        assertEquals(3, new PolyLine2d(unitSquareCCW).getLength(), 1e-6);

        assertEquals(12.596267358062482, new PolyLine2d(triangleCW).getLength(), 1e-6);
        assertEquals(12.596267358062482, new PolyLine2d(triangleCCW).getLength(), 1e-6);
    }

    @Test
    public void getCentroidTest() {
        double[] expected = {1.0/3, 7.0/3};
        double[] ctr1 = new PolyLine2d(triangleCCW).getCentroid().toDoubleArray();
        double[] ctr2 = new PolyLine2d(triangleCW).getCentroid().toDoubleArray();
        assertArrayEquals(expected, ctr1, 1e-6);
        assertArrayEquals(expected, ctr2, 1e-6);
    }

}
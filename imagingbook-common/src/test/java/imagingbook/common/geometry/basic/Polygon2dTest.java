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

import static imagingbook.common.geometry.basic.AbstractPoly2d.makePntList;
import static imagingbook.common.util.ListUtils.reversedCopy;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Polygon2dTest {

    // static List<Pnt2d> UNIT_SQUARE_CCW = Arrays.asList(Pnt2d.from(0, 0), Pnt2d.from(1, 0), Pnt2d.from(1, 1), Pnt2d.from(0, 1));
    // static List<Pnt2d> UNIT_SQUARE_CW = reversedCopy(UNIT_SQUARE_CCW);

    static List<Pnt2d> unitSquareCW = makePntList(0, 0, 0, 1, 1, 1, 1, 0);
    static List<Pnt2d> unitSquareCCW = reversedCopy(unitSquareCW);
    static List<Pnt2d> triangleCCW  = makePntList(-2, -1, 4, 3, -1, 5);
    static List<Pnt2d> triangleCW  = reversedCopy(triangleCCW);


    @Test
    public void getLengthTest() {
        assertEquals(4, new Polygon2d(unitSquareCW).getLength(), 1e-6);
        assertEquals(4, new Polygon2d(unitSquareCCW).getLength(), 1e-6);

        assertEquals(18.679029888360702, new Polygon2d(triangleCW).getLength(), 1e-6);
        assertEquals(18.679029888360702, new Polygon2d(triangleCCW).getLength(), 1e-6);
    }

    @Test
    public void getSignedAreaTest() {
        assertEquals(-1.0, new Polygon2d(unitSquareCW).getSignedArea(), 1e-6);
        assertEquals( 1.0, new Polygon2d(unitSquareCCW).getSignedArea(), 1e-6);

        assertEquals(-16, new Polygon2d(triangleCW).getSignedArea(), 1e-6);
        assertEquals( 16, new Polygon2d(triangleCCW).getSignedArea(), 1e-6);
    }

    @Test
    public void getAreaTest() {
        assertEquals(1.0, new Polygon2d(unitSquareCW).getArea(), 1e-6);
        assertEquals(1.0, new Polygon2d(unitSquareCCW).getArea(), 1e-6);

        assertEquals(16, new Polygon2d(triangleCW).getArea(), 1e-6);
        assertEquals(16, new Polygon2d(triangleCCW).getArea(), 1e-6);
    }

    @Test
    public void isClockwiseTest() {
        assertTrue(new Polygon2d(unitSquareCW).isClockwise());
        assertFalse(new Polygon2d(unitSquareCCW).isClockwise());

        assertTrue(new Polygon2d(triangleCW).isClockwise());
        assertFalse(new Polygon2d(triangleCCW).isClockwise());
    }

    @Test
    public void getConvexityTest() {
        assertEquals(-1, new Polygon2d(unitSquareCW).getConvexity());
        assertEquals( 1, new Polygon2d(unitSquareCCW).getConvexity());

        assertEquals(1, new Polygon2d(triangleCCW).getConvexity());
        assertEquals(-1, new Polygon2d(triangleCW).getConvexity());
    }

    @Test
    public void getCircularityTest() {
        assertEquals(0.78539816, new Polygon2d(unitSquareCW).getCircularity(), 1e-6);
        assertEquals(0.78539816, new Polygon2d(unitSquareCCW).getCircularity(), 1e-6);

        assertEquals(0.57626363, new Polygon2d(triangleCW).getCircularity(), 1e-6);
        assertEquals(0.57626363, new Polygon2d(triangleCCW).getCircularity(), 1e-6);
    }

    @Test
    public void getCentroidTest() {
        double[] expected = {1.0/3, 7.0/3};
        double[] ctr1 = new Polygon2d(triangleCCW).getCentroid().toDoubleArray();
        double[] ctr2 = new Polygon2d(triangleCW).getCentroid().toDoubleArray();
        assertArrayEquals(expected, ctr1, 1e-6);
        assertArrayEquals(expected, ctr2, 1e-6);
    }

    @Test
    public void simplifyTest() {    // TODO
        List<Pnt2d> contour0 = makePntList(Polygon2dTestData.contour0);
        List<Pnt2d> corners0 = makePntList(Polygon2dTestData.corners0);
        assertEquals(contour0.size(), new Polygon2d(contour0).length());
        assertEquals(corners0.size(), new Polygon2d(corners0).length());
    }
}
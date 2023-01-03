/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image;

import ij.process.ByteProcessor;
import imagingbook.common.image.LocalMinMaxFinder.ExtremalPoint;
import imagingbook.common.util.ClassUtils;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MinMaxFinderTest {

    @Test
    public void test1() {
        ByteProcessor ip = new ByteProcessor(30, 20);
        ip.setValue(128);
        ip.fill();

        ip.putPixel(14, 12, 160);
        ip.putPixel(5, 13, 166);
        ip.putPixel(9, 12, 155);
        ip.putPixel(22, 17, 133);

        ip.putPixel(5, 7, 22);
        ip.putPixel(3, 9, 33);
        ip.putPixel(17, 4, 11);
        ip.putPixel(19, 3, 44);

        LocalMinMaxFinder mf = new LocalMinMaxFinder(ip, 1, 1);

        ExtremalPoint[] maxPts = mf.getMaxima(3);
        assertEquals(166, maxPts[0].q, 0.0f);
        assertEquals(160, maxPts[1].q, 0.0f);
        assertEquals(155, maxPts[2].q, 0.0f);

        ExtremalPoint[] minPts = mf.getMinima(3);
        assertEquals(11, minPts[0].q, 0.0f);
        assertEquals(22, minPts[1].q, 0.0f);
        assertEquals(33, minPts[2].q, 0.0f);
    }

    @Test
    public void testComparator1() {
        ExtremalPoint p1 = new ExtremalPoint(1, 2, 77);
        ExtremalPoint p2 = new ExtremalPoint(3, 4, 88);
        ExtremalPoint p3 = new ExtremalPoint(5, 6, 88);
        // System.out.println("p1 | p2 -> " + p1.compareTo(p2));
        // System.out.println("p2 | p1 -> " + p2.compareTo(p1));
        // System.out.println("p2 | p3 -> " + p2.compareTo(p3));
        assertEquals(-1, p1.compareTo(p2));
        assertEquals( 1, p2.compareTo(p1));
        assertEquals( 0, p2.compareTo(p3));
    }

    @Test
    public void testComparator2() {
        ExtremalPoint p1 = new ExtremalPoint(1, 2, 77);
        ExtremalPoint p2 = new ExtremalPoint(3, 4, 88);
        ExtremalPoint p3 = new ExtremalPoint(5, 6, 88);

        Comparator<ExtremalPoint> cprF = ClassUtils.getComparator(ExtremalPoint.class);
        assertEquals(-1, cprF.compare(p1, p2));
        assertEquals( 1, cprF.compare(p2, p1));
        assertEquals( 0, cprF.compare(p2, p3));

        Comparator<ExtremalPoint> cprR = cprF.reversed();
        assertEquals( 1, cprR.compare(p1, p2));
        assertEquals(-1, cprR.compare(p2, p1));
        assertEquals( 0, cprR.compare(p2, p3));
    }

    @Test
    public void testComparator3() {
        ExtremalPoint p1 = new ExtremalPoint(1, 2, 77);
        ExtremalPoint p2 = new ExtremalPoint(3, 4, 88);
        ExtremalPoint p3 = new ExtremalPoint(5, 6, 88);

        Comparator<ExtremalPoint> cprF = ClassUtils.getComparator(ExtremalPoint.class);
        assertEquals(Float.compare(p1.q, p2.q), cprF.compare(p1, p2));
        assertEquals(Float.compare(p2.q, p1.q), cprF.compare(p2, p1));
        assertEquals(Float.compare(p2.q, p3.q), cprF.compare(p2, p3));

        Comparator<ExtremalPoint> cprR = cprF.reversed();
        assertEquals(Float.compare(p2.q, p1.q), cprR.compare(p1, p2));
        assertEquals(Float.compare(p1.q, p2.q), cprR.compare(p2, p1));
        assertEquals(Float.compare(p3.q, p2.q), cprR.compare(p2, p3));
    }


}
/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import org.junit.Test;

import java.util.Comparator;
import java.util.Random;

import static imagingbook.common.util.ClassUtils.getComparator;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SortedVectorTest {

    @Test
    public void test1() {
        SortedVector<Integer> sv = new SortedVector<>(new Integer[3]);
        doInserts(sv);
        assertEquals(3, sv.size());
        // System.out.println(sv.toString());
        assertArrayEquals(new Integer[] {22, 11, 9}, sv.getArray());
    }

    @Test
    public void test2() {
        SortedVector<Integer> sv = new SortedVector<>(new Integer[3], getComparator(Integer.class));
        doInserts(sv);
        assertEquals(3, sv.size());
        // System.out.println(sv.toString());
        assertArrayEquals(new Integer[] {22, 11, 9}, sv.getArray());
    }

    @Test
    public void test3() {
        SortedVector<Integer> sv =
                new SortedVector<>(new Integer[3], getComparator(Integer.class).reversed());
        // new SortedVector<>(new Integer[3], (Comparator<Integer>) Comparator.naturalOrder().reversed());
        doInserts(sv);
        assertEquals(3, sv.size());
        // System.out.println(sv.toString());
        assertArrayEquals(new Integer[] {-1, 1, 2}, sv.getArray());
    }

    @Test
    public void test4() {
        Comparator<Integer> cpr = new Comparator<> () {
            @Override
            public int compare(Integer x, Integer y) {
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        };

        {   // collect max values
            SortedVector<Integer> sv = new SortedVector<>(new Integer[3], cpr);
            doInserts(sv);
            assertEquals(3, sv.size());
            // System.out.println(sv.toString());
            assertArrayEquals(new Integer[]{22, 11, 9}, sv.getArray());
        }
        {   // collect min values
            SortedVector<Integer> sv = new SortedVector<>(new Integer[3], cpr.reversed());
            doInserts(sv);
            assertEquals(3, sv.size());
            // System.out.println(sv.toString());
            assertArrayEquals(new Integer[] {-1, 1, 2}, sv.getArray());
        }
    }

    @Test
    public void testManyMaximum() {
        Double[] a1 = new Double[3];
        SortedVector<Double> sv = new SortedVector<>(a1);
        Random rg = new Random(17);
        double maxVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 100000; i++) {
            double x = rg.nextDouble();
            maxVal = Math.max(maxVal, x);
            sv.add(x);
        }
        assertEquals(3, sv.size());
        Double[] a2 = sv.getArray();
        assertEquals(3, a2.length);
        assertEquals(maxVal, a2[0], 0);
        for (int i = 1; i < a2.length; i++) {
            assertTrue(a2[i-1] >= a2[i]);
        }
    }

    @Test
    public void testManyMinimum() {
        Double[] a1 = new Double[3];
        SortedVector<Double> sv = new SortedVector<>(a1, getComparator(Double.class).reversed());
        Random rg = new Random(17);
        double minVal = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 100000; i++) {
            double x = rg.nextDouble();
            minVal = Math.min(minVal, x);
            sv.add(x);
        }
        assertEquals(3, sv.size());
        Double[] a2 = sv.getArray();
        assertEquals(3, a2.length);
        assertEquals(minVal, a2[0], 0);
        for (int i = 1; i < a2.length; i++) {
            assertTrue(a2[i-1] <= a2[i]);
        }
    }

    private void doInserts(SortedVector<Integer> sv) {
        sv.add(5);
        sv.add(2);
        sv.add(9);
        sv.add(7);
        sv.add(-1);
        sv.add(11);
        sv.add(22);
        sv.add(1);
    }
}
/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.util;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class SortedVectorTest {

    @Test
    public void test1() {
        SortedVector<Integer> sv = new SortedVector<>(new Integer[3]);
        doInserts(sv);
        assertEquals(3, sv.size());
        // System.out.println(sv.toString());
        assertArrayEquals(new Integer[] {9, 11, 22}, sv.getArray());
    }

    @Test
    public void test2() {
        SortedVector<Integer> sv = new SortedVector<>(new Integer[3],  (Comparator<Integer>) Comparator.naturalOrder());
        doInserts(sv);
        assertEquals(3, sv.size());
        // System.out.println(sv.toString());
        assertArrayEquals(new Integer[] {9, 11, 22}, sv.getArray());
    }

    @Test
    public void test3() {
        SortedVector<Integer> sv =
                new SortedVector<>(new Integer[3], (Comparator<Integer>) Comparator.naturalOrder().reversed());
        doInserts(sv);
        assertEquals(3, sv.size());
        // System.out.println(sv.toString());
        assertArrayEquals(new Integer[] {2, 1, -1}, sv.getArray());
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
            assertArrayEquals(new Integer[]{9, 11, 22}, sv.getArray());
        }
        {   // collect min values
            SortedVector<Integer> sv = new SortedVector<>(new Integer[3], cpr.reversed());
            doInserts(sv);
            assertEquals(3, sv.size());
            // System.out.println(sv.toString());
            assertArrayEquals(new Integer[] {2, 1, -1}, sv.getArray());
        }

    }

    private void doInserts(SortedVector<Integer> sv) {
        sv.insert(5);
        sv.insert(2);
        sv.insert(9);
        sv.insert(7);
        sv.insert(-1);
        sv.insert(11);
        sv.insert(22);
        sv.insert(1);
    }

}
/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.util;

import imagingbook.sampleimages.GeneralSampleImage;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

import static imagingbook.common.util.ClassUtils.getComparator;
import static imagingbook.common.util.ClassUtils.getEnumConstantsSorted;
import static org.junit.Assert.*;

public class ClassUtilsTest {

    private enum Foo {
        zappa, abra, ka, Abra, zaPPa, dabra;
    }

    @Test
    public void testGetEnumConstantsSorted() {
        Foo[] x = ClassUtils.getEnumConstantsSorted(Foo.class);
        // System.out.println(Arrays.toString(x));
        assertEquals(Foo.Abra, x[0]);
        assertEquals(Foo.abra, x[1]);
        assertEquals(Foo.dabra, x[2]);
        assertEquals(Foo.ka, x[3]);
        assertEquals(Foo.zaPPa, x[4]);
        assertEquals(Foo.zappa, x[5]);
    }

    @Test
    public void testEnumStrings() {
        Foo[] sortedItems = ClassUtils.getEnumConstantsSorted(Foo.class);
        String[] x = Arrays.stream(sortedItems).map(Enum::toString).toArray(String[]::new);
        // System.out.println(Arrays.toString(x));
        assertEquals("Abra", x[0]);
        assertEquals("abra", x[1]);
        assertEquals("dabra", x[2]);
        assertEquals("ka", x[3]);
        assertEquals("zaPPa", x[4]);
        assertEquals("zappa", x[5]);
    }

    @Test
    public void testGetComparator1() {
        Comparator<Integer> cpr = getComparator(Integer.class);
        assertEquals(-1, cpr.compare(5, 7));
        assertEquals( 0, cpr.compare(5, 5));
        assertEquals( 1, cpr.compare(7, 5));
    }

    @Test
    public void testGetComparator2() {
        Comparator<Integer> cpr = getComparator(Integer.class).reversed();
        assertEquals( 1, cpr.compare(5, 7));
        assertEquals( 0, cpr.compare(5, 5));
        assertEquals(-1, cpr.compare(7, 5));
    }
}
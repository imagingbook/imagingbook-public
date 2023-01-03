/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.regions;

import imagingbook.common.util.DynamicProperties;
import imagingbook.common.util.DynamicProperties.PropertyKey;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BinaryRegionTest {

    @Test
    public void testProperties1() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        double x1 = 1.234;
        double x2 = 5.678;
        PropertyKey<Double> key = new PropertyKey<>("SomeDouble");
        r.setProperty(key, x1);
        r.setProperty(key, x2);
        r.removeProperty(new PropertyKey<Double>("OtherDouble"));        // should do nothing
        double y = r.getProperty(key);
        assertEquals(x2, y , 0);
    }

    @Test
    public void testProperties2() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        PropertyKey<Object> key = new PropertyKey<>("foo");
        assertNull(r.getProperty(key));
    }

    @Test
    public void testProperties3() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        PropertyKey<Double> key = new PropertyKey<>("SomeDouble");
        r.setProperty(key, 1.234);
        assertEquals(1.234, r.getProperty(key) , 0);
        assertNull(r.getProperty(new PropertyKey<Double>("OTHER-KEY")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProperties4() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        PropertyKey<Double> key = new PropertyKey<>("SomeDouble");
        r.setProperty(key, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProperties5() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        r.setProperty(null, 123);
    }

    @Test
    public void testProperties6() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        PropertyKey<Double> key = new PropertyKey<>("SomeDouble");
        r.setProperty(key, 1.234);
        r.removeProperty(key);
        assertNull(r.getProperty(key));
    }

    @Test
    public void testProperties7() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        double[] x = {1, 2, 3};
        PropertyKey<double[]> key = new PropertyKey<>("SomeDoubleArray");
        r.setProperty(key, x);
        double[] y = r.getProperty(key);
        assertNotNull(y);
        assertArrayEquals(x, y, 0);
    }

    @Test
    public void testProperties8() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        PropertyKey<Double> key1 = new PropertyKey<>("SomeDouble");     // two keys with same type and name
        PropertyKey<Double> key2 = new PropertyKey<>("SomeDouble");
        double x = Math.PI;
        r.setProperty(key1, x);             // use key1 to indert
        double y = r.getProperty(key2);     // use key2 to retrieve
        assertEquals(x, y, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProperties9() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        PropertyKey<Double> key1 = new PropertyKey<>("SomeDouble");     // two keys with different types but same name
        PropertyKey<Integer> key2 = new PropertyKey<>("SomeDouble");
        r.setProperty(key1, Math.PI);
        r.setProperty(key2, 99);            // this should cause an error!!
    }

}
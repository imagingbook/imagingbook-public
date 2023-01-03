package imagingbook.common.regions;

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
        String key = "SomeDouble";
        r.setProperty(key, x1);
        r.setProperty(key, x2);
        r.removeProperty("foo");        // should do nothing
        double y = (double) r.getProperty(key);
        assertEquals(x2, y , 0);
    }

    @Test
    public void testProperties2() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        String key = "SomeDouble";
        assertNull(r.getProperty(key));
    }

    @Test
    public void testProperties3() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        String key = "SomeDouble";
        r.setProperty(key, 1.234);
        assertNull(r.getProperty("OTHER-KEY"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProperties4() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        String key = "SomeDouble";
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
        String key = "SomeDouble";
        r.setProperty(key, 1.234);
        r.removeProperty(key);
        assertNull(r.getProperty(key));
    }

    @Test
    public void testProperties7() {
        BinaryRegion r = new SegmentationBackedRegion(99, null);    // dummy region
        double[] x = {1, 2, 3};
        String key = "SomeDoubleArray";
        r.setProperty(key, x);
        double[] y = (double[]) r.getProperty(key);
        assertNotNull(y);
        assertArrayEquals(x, y, 0);
    }

}
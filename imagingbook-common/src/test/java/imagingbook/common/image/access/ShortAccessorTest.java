/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.access;

import ij.process.ShortProcessor;
import imagingbook.testutils.DeterministicRandom;
import org.junit.Test;


import java.util.random.RandomGenerator;

import static imagingbook.testutils.ImageTestUtils.makeRandomShortProcessor;
import static org.junit.Assert.assertEquals;

public class ShortAccessorTest {

    static int W = 50, H = 30;
    static ShortProcessor SP = makeRandomShortProcessor(W, H, 529);

    @Test
    public void defaultsTest1() {
        ScalarAccessor SA = (ScalarAccessor) ImageAccessor.create(SP);
        assertEquals(ImageAccessor.DefaultInterpolationMethod, SA.getInterpolationMethod());
        assertEquals(ImageAccessor.DefaultOutOfBoundsStrategy, SA.getOutOfBoundsStrategy());
        assertEquals(0, SA.getXOrigin());
        assertEquals(0, SA.getYOrigin());
    }

    @Test
    public void defaultsTest2() {
        int x0 = 23, y0 = 7;
        ScalarAccessor SA = (ScalarAccessor) ImageAccessor.create(SP, null, null, x0, y0);
        assertEquals(1, SA.getDepth());
        assertEquals(ImageAccessor.DefaultInterpolationMethod, SA.getInterpolationMethod());
        assertEquals(ImageAccessor.DefaultOutOfBoundsStrategy, SA.getOutOfBoundsStrategy());
        assertEquals(x0, SA.getXOrigin());
        assertEquals(y0, SA.getYOrigin());
    }

    @Test
    public void getValTest() {
        ScalarAccessor SA = (ScalarAccessor) ImageAccessor.create(SP);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                assertEquals(SP.get(u, v), (int)SA.getVal(u, v));
            }
        }
    }

    @Test
    public void getValOffsetTest() {
        int x0 = 23, y0 = 7;
        ScalarAccessor SA1 = (ScalarAccessor) ImageAccessor.create(SP, null, null, 0, 0);
        ScalarAccessor SA2 = (ScalarAccessor) ImageAccessor.create(SP, null, null, x0, y0);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                var val1 = SA1.getVal(u, v);
                var val2 = SA2.getVal(u - x0, v - y0);
                assertEquals(val1, val2, 1e-6f);
            }
        }
    }

    @Test
    public void setValTest() {
        RandomGenerator rg = new DeterministicRandom(31);
        ScalarAccessor AC = (ScalarAccessor) ImageAccessor.create(SP);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                int tmp = (int)AC.getVal(u, v);
                int rv = rg.nextInt(0x10000);
                AC.setVal(u, v, rv);
                assertEquals(rv, (int)AC.getVal(u, v));
                AC.setVal(u, v, tmp);
                assertEquals(tmp, (int)AC.getVal(u, v));
            }
        }
    }

    @Test
    public void setValOffsetTest() {
        int x0 = 23, y0 = 7;
        RandomGenerator rg = new DeterministicRandom(77);
        ScalarAccessor AC1 = (ScalarAccessor) ImageAccessor.create(SP, null, null, 0, 0);
        ScalarAccessor AC2 = (ScalarAccessor) ImageAccessor.create(SP, null, null, x0, y0);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                int rv = rg.nextInt(0x10000);
                AC1.setVal(u, v, rv);
                assertEquals(rv, (int)AC1.getVal(u, v));
                assertEquals(rv, (int)AC2.getVal(u - x0, v - y0));
            }
        }
    }
}

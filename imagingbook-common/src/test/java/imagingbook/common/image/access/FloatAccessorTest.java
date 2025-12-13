/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.access;

import ij.process.FloatProcessor;
import imagingbook.testutils.DeterministicRandom;
import org.junit.Test;

import java.util.random.RandomGenerator;

import static imagingbook.testutils.ImageTestUtils.makeRandomFloatProcessor;
import static org.junit.Assert.assertEquals;

public class FloatAccessorTest {

    static int W = 50, H = 30;
    static FloatProcessor FP = makeRandomFloatProcessor(W, H, 317);

    @Test
    public void defaultsTest1() {
        ScalarAccessor FA = (ScalarAccessor) ImageAccessor.create(FP);
        assertEquals(1, FA.getDepth());
        assertEquals(ImageAccessor.DefaultInterpolationMethod, FA.getInterpolationMethod());
        assertEquals(ImageAccessor.DefaultOutOfBoundsStrategy, FA.getOutOfBoundsStrategy());
        assertEquals(0, FA.getXOrigin());
        assertEquals(0, FA.getYOrigin());
    }

    @Test
    public void defaultsTest2() {
        int x0 = 23, y0 = 7;
        ScalarAccessor FA = (ScalarAccessor) ImageAccessor.create(FP, null, null, x0, y0);
        assertEquals(ImageAccessor.DefaultInterpolationMethod, FA.getInterpolationMethod());
        assertEquals(ImageAccessor.DefaultOutOfBoundsStrategy, FA.getOutOfBoundsStrategy());
        assertEquals(x0, FA.getXOrigin());
        assertEquals(y0, FA.getYOrigin());
    }

    @Test
    public void getValTest() {
        ScalarAccessor FA = (ScalarAccessor) ImageAccessor.create(FP);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                assertEquals(FP.getf(u, v), FA.getVal(u, v), 1e-6f);
            }
        }
    }

    @Test
    public void getValOffsetTest() {
        int x0 = 23, y0 = 7;
        ScalarAccessor BA1 = (ScalarAccessor) ImageAccessor.create(FP, null, null, 0, 0);
        ScalarAccessor BA2 = (ScalarAccessor) ImageAccessor.create(FP, null, null, x0, y0);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                var val1 = BA1.getVal(u, v);
                var val2 = BA2.getVal(u - x0, v - y0);
                assertEquals(val1, val2, 1e-6f);
            }
        }
    }

    @Test
    public void setValTest() {
        RandomGenerator rg = new DeterministicRandom(31);
        ScalarAccessor AC = (ScalarAccessor) ImageAccessor.create(FP);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                float tmp = AC.getVal(u, v);
                float rv = rg.nextFloat(2f) - 1f;
                AC.setVal(u, v, rv);
                assertEquals(rv, AC.getVal(u, v), 1e-6f);
                AC.setVal(u, v, tmp);
                assertEquals(tmp, AC.getVal(u, v), 1e-6f);
            }
        }
    }

    @Test
    public void setValOffsetTest() {
        int x0 = 23, y0 = 7;
        RandomGenerator rg = new DeterministicRandom(77);
        ScalarAccessor AC1 = (ScalarAccessor) ImageAccessor.create(FP, null, null, 0, 0);
        ScalarAccessor AC2 = (ScalarAccessor) ImageAccessor.create(FP, null, null, x0, y0);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                float rv = rg.nextFloat(2f) - 1f;
                AC1.setVal(u, v, rv);
                assertEquals(rv, AC1.getVal(u, v), 1e-6f);
                assertEquals(rv, AC2.getVal(u - x0, v - y0), 1e-6f);
            }
        }
    }
}

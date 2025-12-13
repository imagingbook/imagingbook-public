/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.access;

import ij.process.ColorProcessor;
import imagingbook.testutils.DeterministicRandom;
import org.junit.Test;

import java.util.random.RandomGenerator;

import static imagingbook.common.util.ArrayUtils.newFloatArray;
import static imagingbook.testutils.ImageTestUtils.makeRandomColorProcessor;
import static org.junit.Assert.assertEquals;

public class RgbAccessorTest {

    static int W = 50, H = 30;
    static ColorProcessor CP = makeRandomColorProcessor(W, H, 529);

    @Test
    public void defaultsTest1() {
        ImageAccessor CA = ImageAccessor.create(CP);
        assertEquals(3, CA.getDepth());
        assertEquals(ImageAccessor.DefaultInterpolationMethod, CA.getInterpolationMethod());
        assertEquals(ImageAccessor.DefaultOutOfBoundsStrategy, CA.getOutOfBoundsStrategy());
        assertEquals(0, CA.getXOrigin());
        assertEquals(0, CA.getYOrigin());
    }

    @Test
    public void defaultsTest2() {
        int x0 = 23, y0 = 7;
        ImageAccessor CA = ImageAccessor.create(CP, null, null, x0, y0);
        assertEquals(ImageAccessor.DefaultInterpolationMethod, CA.getInterpolationMethod());
        assertEquals(ImageAccessor.DefaultOutOfBoundsStrategy, CA.getOutOfBoundsStrategy());
        assertEquals(x0, CA.getXOrigin());
        assertEquals(y0, CA.getYOrigin());
    }

    @Test
    public void getValTest() {
        ImageAccessor CA = ImageAccessor.create(CP);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                int[] rgb = CP.getPixel(u, v, null);
                float[] RGB = CA.getPix(u, v);
                for (int k = 0; k < 3; k++) {
                    assertEquals(rgb[k], (int)RGB[k]);
                }
            }
        }
    }

    @Test
    public void getValOffsetTest() {
        int x0 = 23, y0 = 7;
        // int x0 = 0, y0 = 0;
        ImageAccessor CA1 = ImageAccessor.create(CP, null, null, 0, 0);
        ImageAccessor CA2 = ImageAccessor.create(CP, null, null, x0, y0);

        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                float[] RGB1 = CA1.getPix(u, v);
                float[] RGB2 = CA2.getPix(u - x0, v - y0);
                for (int k = 0; k < 3; k++) {
                    assertEquals((int)RGB1[k], (int)RGB2[k]);
                }
            }
        }
    }

    @Test
    public void setValTest() {
        DeterministicRandom rg = new DeterministicRandom(31);
        ImageAccessor AC = ImageAccessor.create(CP);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                float[] tmp = AC.getPix(u, v);
                float[] RGB1 = newFloatArray(5, () -> rg.nextInt(255)); // set to random RGB
                AC.setPix(u, v, RGB1);
                float[] RGB2 = AC.getPix(u, v);
                for (int k = 0; k < 3; k++) {
                    // assertEquals((int)RGB1[k], (int)RGB2[k]);
                }
                // set back to original RGB
                AC.setPix(u, v, tmp);
                float[] RGB3 = AC.getPix(u, v);
                for (int k = 0; k < 3; k++) {
                    assertEquals((int)tmp[k], (int)RGB3[k]);
                }
            }
        }
    }

    @Test
    public void setValOffsetTest() {
        int x0 = 23, y0 = 7;
        RandomGenerator rg = new DeterministicRandom(77);
        ImageAccessor AC1 = ImageAccessor.create(CP, null, null, 0, 0);
        ImageAccessor AC2 = ImageAccessor.create(CP, null, null, x0, y0);
        for (int u = 0; u < W; u++) {
            for (int v = 0; v < H; v++) {
                float[] RGB = newFloatArray(5, () -> rg.nextInt(255)); // set to random RGB
                AC1.setPix(u, v, RGB);

                float[] RGB1 = AC1.getPix(u, v);
                for (int k = 0; k < 3; k++) {
                    assertEquals((int)RGB[k], (int)RGB1[k]);
                }

                float[] RGB2 = AC2.getPix(u - x0, v - y0);
                for (int k = 0; k < 3; k++) {
                    assertEquals((int)RGB[k], (int)RGB2[k]);
                }
            }
        }
    }
}

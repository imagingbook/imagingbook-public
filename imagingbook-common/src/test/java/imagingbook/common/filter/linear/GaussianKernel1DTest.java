/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.linear;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class GaussianKernel1DTest {

    @Test
    public void makeGaussKernel1DTest1() {
        double sigma = 0.8;
        float[] h = GaussianKernel1D.makeGaussKernel1D(sigma);
        // PrintPrecision.set(8);
        // System.out.println("kernel h =" + Matrix.toString(h));
        float[] hexpected = {0.00044074f, 0.02191032f, 0.22831072f, 0.49867645f, 0.22831072f, 0.02191032f, 0.00044074f};
        assertEquals(hexpected.length, h.length);
        assertArrayEquals(hexpected, h, 1e-6f);

    }

    @Test
    public void makeGaussKernel1DTest2() {
        double sigma = 0.8; boolean normalize = false;
        float[] h = GaussianKernel1D.makeGaussKernel1D(sigma, normalize);
        // PrintPrecision.set(8);
        // System.out.println("kernel h =" + Matrix.toString(h));
        float[] hexpected = {0.00088383f, 0.04393693f, 0.45783335f, 1.00000000f, 0.45783335f, 0.04393693f, 0.00088383f};
        assertEquals(hexpected.length, h.length);
        assertArrayEquals(hexpected, h, 1e-6f);
    }

    @Test
    public void makeGaussKernel1DTest3() {
        double sigma = 0.8;
        boolean normalize = false;
        double sizeFactor = 2.5;
        float[] h = GaussianKernel1D.makeGaussKernel1D(sigma, normalize, sizeFactor);
        // PrintPrecision.set(8);
        // System.out.println("kernel h =" + Matrix.toString(h));
        float[] hexpected = {0.04393693f, 0.45783335f, 1.00000000f, 0.45783335f, 0.04393693f};
        assertEquals(hexpected.length, h.length);
        assertArrayEquals(hexpected, h, 1e-6f);
    }
}
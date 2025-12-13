/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.access;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImageAccessorTest {

    static int W = 50, H = 30;
    static ByteProcessor BP = new ByteProcessor(W, H);
    static ShortProcessor SP = new ShortProcessor(W, H);
    static FloatProcessor FP = new FloatProcessor(W, H);
    static ColorProcessor CP = new ColorProcessor(W, H);

    static ImageAccessor BA = ImageAccessor.create(BP);
    static ImageAccessor SA = ImageAccessor.create(SP);
    static ImageAccessor FA = ImageAccessor.create(FP);
    static ImageAccessor CA = ImageAccessor.create(CP);

    @Test
    public void createTest1() {
        assertTrue(BA instanceof ByteAccessor);
        assertTrue(SA instanceof ShortAccessor);
        assertTrue(FA instanceof FloatAccessor);
        assertTrue(CA instanceof RgbAccessor);
    }

    @Test
    public void createTest2() {
        assertTrue(BA instanceof ScalarAccessor);
        assertTrue(SA instanceof ScalarAccessor);
        assertTrue(FA instanceof ScalarAccessor);
        assertTrue(CA instanceof VectorAccessor);
    }

    @Test
    public void getDepthTest() {
        assertEquals(1, BA.getDepth());
        assertEquals(1, SA.getDepth());
        assertEquals(1, FA.getDepth());
        assertEquals(3, CA.getDepth());
    }

    @Test
    public void setGetPixTest() {
    }



    @Test
    public void getValTest() {
    }

}
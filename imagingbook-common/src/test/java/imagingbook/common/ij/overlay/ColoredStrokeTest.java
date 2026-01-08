/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2026 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ij.overlay;

import org.junit.Test;

import java.awt.BasicStroke;
import java.awt.Color;

import static org.junit.Assert.*;

public class ColoredStrokeTest {

    @Test
    public void duplicateTest() {
        ColoredStroke stroke1 = new ColoredStroke(new BasicStroke(), Color.green, Color.gray);
        assertEquals(Color.green, stroke1.getStrokeColor());
        assertEquals(Color.gray, stroke1.getFillColor());
        assertNull(stroke1.getDashArray());

        ColoredStroke stroke2 = stroke1.duplicate();
        assertNotSame(stroke1, stroke2);
        assertEquals(Color.green, stroke2.getStrokeColor());
        assertEquals(Color.gray, stroke2.getFillColor());
    }

    @Test
    public void builderTest1() {
        ColoredStroke stroke1 = new ColoredStroke.Builder()
                .withLineWidth(0.7)
                .withStrokeColor(Color.green)
                .withFillColor(Color.gray)
                .withDashArray(8, 4)
                .build();

        assertEquals(0.7, stroke1.getLineWidth(), 1e-6);
        assertEquals(Color.green, stroke1.getStrokeColor());
        assertEquals(Color.gray, stroke1.getFillColor());
        assertArrayEquals(new float[]{8, 4}, stroke1.getDashArray(), 1e-6f);
    }

    @Test
    public void builderTest2() {
        ColoredStroke stroke1 = new ColoredStroke.Builder()
                .withLineWidth(0.7)
                .withStrokeColor(Color.green)
                .withFillColor(Color.gray)
                .withDashArray(8, 4.0)
                .build();

        // build from stroke1 but with different color:
        ColoredStroke stroke2 = new ColoredStroke.Builder(stroke1)
                .withStrokeColor(Color.red)
                .build();

        assertNotSame(stroke1, stroke2);
        assertEquals(0.7, stroke2.getLineWidth(), 1e-6);
        assertEquals(Color.red, stroke2.getStrokeColor());
        assertEquals(Color.gray, stroke2.getFillColor());
        assertArrayEquals(new float[]{8, 4}, stroke2.getDashArray(), 1e-6f);
    }
}
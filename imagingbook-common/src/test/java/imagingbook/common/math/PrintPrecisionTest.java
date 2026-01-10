/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2026 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math;

import org.junit.Test;

import static imagingbook.common.math.PrintPrecision.DefaultPrecision;
import static org.junit.Assert.*;

public class PrintPrecisionTest {

    @Test
    public void clearAllTest() {
    }

    @Test
    public void resetTest() {
    }

    @Test
    public void setTest() {
    }

    @Test
    public void currentTest() {
    }

    @Test
    public void currentFormatStringFloatTest() {
    }


    @Test
    public void autoCloseableTest() {
        PrintPrecision.clearAll();
        assertEquals(DefaultPrecision, PrintPrecision.current().getPrecision());
        try (var prec1 = new PrintPrecision(6)) {
            assertEquals(6, PrintPrecision.current().getPrecision());
            try (var prec2 = new PrintPrecision(10)) {
                assertEquals(10, PrintPrecision.current().getPrecision());
            }
            assertEquals(6, PrintPrecision.current().getPrecision());
        }
        assertEquals(DefaultPrecision, PrintPrecision.current().getPrecision());
    }
}
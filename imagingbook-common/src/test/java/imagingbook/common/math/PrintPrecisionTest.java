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

import static imagingbook.common.math.PrintPrecision.DefaultLocale;
import static imagingbook.common.math.PrintPrecision.DefaultPrecision;
import static org.junit.Assert.*;

public class PrintPrecisionTest {

    @Test
    public void clearTest() {
        PrintPrecision.clear();
        assertEquals(1, PrintPrecision.stackSize());
        assertEquals(DefaultPrecision, PrintPrecision.getCurrent().getPrecision());
        assertEquals(DefaultLocale, PrintPrecision.getCurrent().getLocale());
    }

    @Test
    public void setTest() {
    }

    @Test
    public void getCurrentTest() {
    }

    @Test
    public void autoCloseableTest1() {
        PrintPrecision.clear();
        assertEquals(DefaultPrecision, PrintPrecision.getCurrent().getPrecision());

        try (var prec1 = PrintPrecision.set(6)) {
            assertEquals(6, PrintPrecision.getCurrent().getPrecision());

            try (var prec2 = PrintPrecision.set(10)) {
                assertEquals(10, PrintPrecision.getCurrent().getPrecision());
            }

            assertEquals(6, PrintPrecision.getCurrent().getPrecision());
        }
        assertEquals(DefaultPrecision, PrintPrecision.getCurrent().getPrecision());
    }

    @Test
    public void autoCloseableTest2() {
        PrintPrecision.clear();
        assertEquals(DefaultPrecision, PrintPrecision.getCurrent().getPrecision());

        PrintPrecision.setTo(6);
            assertEquals(6, PrintPrecision.getCurrent().getPrecision());

            PrintPrecision.setTo(10);
                assertEquals(10, PrintPrecision.getCurrent().getPrecision());
            PrintPrecision.revert();

            assertEquals(6, PrintPrecision.getCurrent().getPrecision());
        PrintPrecision.revert();
        assertEquals(DefaultPrecision, PrintPrecision.getCurrent().getPrecision());
    }


}
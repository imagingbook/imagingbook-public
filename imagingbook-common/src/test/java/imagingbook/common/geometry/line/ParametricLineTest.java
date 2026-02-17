/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2026 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.line;

import imagingbook.common.geometry.basic.Pnt2d;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParametricLineTest {

    @Test
    public void getSTest() {
    }

    @Test
    public void getVTest() {
    }

    @Test
    public void fromTest() {
        Pnt2d p1 = Pnt2d.from(1, 2);
        Pnt2d p2 = Pnt2d.from(4, 3);

        AlgebraicLine al1 = AlgebraicLine.from(p1, p2);
        // System.out.println("al1 = " + al1);

        ParametricLine pl = ParametricLine.from(al1);
        // System.out.println("pl = " + pl);

        AlgebraicLine al2 = AlgebraicLine.from(pl);
        // System.out.println("al2 = " + al2);

        System.out.println("al1 = al2 ? " + al1.equals(al2, 1e-6));
        // assertTrue(al1.equals(al2, 1e-6));
    }

//    al1 = AlgebraicLine<-0.316, 0.949, -1.581>
//    pl = ParametricLine <s=[-0.500, 1.500], v=[-0.949, -0.316]>
//    al2 = AlgebraicLine<0.316, -0.949, 1.581>
//    al1 = al2 ? true
}
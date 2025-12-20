/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.mappings.linear;

import imagingbook.common.geometry.basic.Pnt2d;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimilarityMapping2DTest {

    static double TOL = 1e-6;

    static Pnt2d p0 = Pnt2d.from(0, 0);
    static Pnt2d p1 = Pnt2d.from(1, 0);
    static Pnt2d p2 = Pnt2d.from(2, 0);

    static Pnt2d q0 = Pnt2d.from(0, 0);
    static Pnt2d q1 = Pnt2d.from(1, 0);


    @Test
    public void constructorTest1() {
        SimilarityMapping2D sm = new SimilarityMapping2D(3, -4, 5, 7);
        // System.out.println(sm);
        // System.out.println("scale = " + sm.getScale());
        // System.out.println("angle = " + sm.getAngle());
        assertEquals(5, sm.getScale(), TOL);
        assertEquals(-0.9272952180016122, sm.getAngle(), TOL);
        assertEquals(5, sm.a02, TOL);
        assertEquals(7, sm.a12, TOL);
    }

    @Test
    public void constructorTest2() {
        double s = 27;
        double theta = -0.23;
        SimilarityMapping2D sm = SimilarityMapping2D.fromScaleAndAngle(s, theta, 5, 7);
        assertEquals(s, sm.getScale(), TOL);
        assertEquals(theta, sm.getAngle(), TOL);
        assertEquals(5, sm.a02, TOL);
        assertEquals(7, sm.a12, TOL);
        // System.out.println(sm);
    }

    @Test
    public void fromPointsTest1() {
        Pnt2d[] P = {p0, p1};
        SimilarityMapping2D sm = SimilarityMapping2D.fromPoints(P, P);
        // System.out.println(sm);
        // System.out.println("scale = " + sm.getScale());
        // System.out.println("angle = " + sm.getAngle());
        assertEquals(1, sm.getScale(), TOL);
        assertEquals(0, sm.getAngle(), TOL);
        assertEquals(0, sm.a02, TOL);
        assertEquals(0, sm.a12, TOL);
    }

    @Test
    public void fromPointsTest2() {
        Pnt2d[] P = {p0, p1, p2};
        SimilarityMapping2D sm = SimilarityMapping2D.fromPoints(P, P);
        // System.out.println(sm);
        // System.out.println("scale = " + sm.getScale());
        // System.out.println("angle = " + sm.getAngle());
        assertEquals(1, sm.getScale(), TOL);
        assertEquals(0, sm.getAngle(), TOL);
        assertEquals(0, sm.a02, TOL);
        assertEquals(0, sm.a12, TOL);
    }

    @Test
    public void fromPointsTest3() {
        Pnt2d[] P = {p0, p1};
        Pnt2d[] Q = {p0, p2};
        SimilarityMapping2D sm = SimilarityMapping2D.fromPoints(P, Q);
        // System.out.println(sm);
        // System.out.println("scale = " + sm.getScale());
        // System.out.println("angle = " + sm.getAngle());
        assertEquals(2, sm.getScale(), TOL);
        assertEquals(0, sm.getAngle(), TOL);
        assertEquals(0, sm.a02, TOL);
        assertEquals(0, sm.a12, TOL);
    }

    @Test
    public void fromPointsTest4() {
        Pnt2d p0 = Pnt2d.from(-5, -5);
        Pnt2d p1 = Pnt2d.from(5, 50);
        Pnt2d q0 = Pnt2d.from(-2, 0);
        Pnt2d q1 = Pnt2d.from(2, 0);
        Pnt2d[] P = {p0, p1};
        Pnt2d[] Q = {q0, q1};

        SimilarityMapping2D sm = SimilarityMapping2D.fromPoints(P, Q);
        // System.out.println(sm);
        // System.out.println("scale = " + sm.getScale());
        // System.out.println("angle = " + sm.getAngle());
        assertEquals(0.07155417527999333, sm.getScale(), TOL);
        assertEquals(-1.3909428270024184, sm.getAngle(), TOL);
        assertEquals(-1.584, sm.a02, TOL);
        assertEquals(-0.288, sm.a12, TOL);

        assertTrue(AffineMapping2D.isAffine(sm));

        // forward mapping:
        assertTrue(q0.isCloseTo(sm.applyTo(p0)));
        assertTrue(q1.isCloseTo(sm.applyTo(p1)));

        // inverse mapping
        LinearMapping2D smi = sm.getInverse();
        assertTrue(p0.isCloseTo(smi.applyTo(q0)));
        assertTrue(p1.isCloseTo(smi.applyTo(q1)));
    }
}
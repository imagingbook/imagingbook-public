/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.hulls;

import imagingbook.common.geometry.basic.Pnt2d;
import org.junit.Test;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConvexHull2dTest {

	@Test
	public void testSixPointSet() {
		List<Pnt2d> points = Arrays.asList(
				Pnt2d.from(2, 5),
				Pnt2d.from(5, 4),
				Pnt2d.from(5, 7),
				Pnt2d.from(7, 6),
				Pnt2d.from(9, 9),
				Pnt2d.from(6, 2)
				);
		ConvexHull2d hull = new ConvexHull2d(points);
		Pnt2d[] vertices = hull.getVertices();
		assertNotNull(vertices);
		assertEquals(4, vertices.length);
		
		for (Pnt2d p : points) {
			assertTrue(hull.contains(p));
		}
	}

    @Test
    public void testSixPointSetDupllicates() {
        List<Pnt2d> points = Arrays.asList(
                Pnt2d.from(2, 5),
                Pnt2d.from(5, 4),
                Pnt2d.from(5, 7),
                Pnt2d.from(7, 6),
                Pnt2d.from(9, 9),
                Pnt2d.from(9, 9),   // duplicate points on hull!
                Pnt2d.from(9, 9),
                Pnt2d.from(6, 2)
        );
        ConvexHull2d hull = new ConvexHull2d(points);
        Pnt2d[] vertices = hull.getVertices();
        assertNotNull(vertices);
        assertEquals(4, vertices.length);

        for (Pnt2d p : points) {
            assertTrue(hull.contains(p));
        }
    }
	
	@Test
	public void testTwoPointSet() {
		List<Pnt2d> points = Arrays.asList(
				Pnt2d.from(2, 5),
				Pnt2d.from(5, 4)
				);
        ConvexHull2d hull = new ConvexHull2d(points);
		Pnt2d[] vertices = hull.getVertices();
		assertNotNull(vertices);
		assertEquals(2, vertices.length);
        for (Pnt2d p : points) {
            assertTrue(hull.contains(p));
        }
	}
	
	@Test
	public void testSinglePointSet() {
		List<Pnt2d> points = Arrays.asList(
                Pnt2d.from(2, 5)
        );
        ConvexHull2d hull = new ConvexHull2d(points);
		Pnt2d[] vertices = hull.getVertices();
		assertNotNull(vertices);
		assertEquals(1, vertices.length);
        for (Pnt2d p : points) {
            assertTrue(hull.contains(p));
        }
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testEmptyPointSetException() {
		new ConvexHull2d(new Pnt2d[0]);
	}
	
	@Test
	public void testAllInputPointsAreInsideHull() {
		int N = 100; 	// number of random points
		int K = 100;	// number of tries
		Random rg = new Random(17);
		Pnt2d[] pointArray = new Pnt2d[N];
		for (int k = 0; k < K; k++) {
			for (int i = 0; i < N; i++) {
				pointArray[i] = Pnt2d.from(100 * rg.nextDouble(), 100 * rg.nextDouble());
			}

            ConvexHull2d hull = new ConvexHull2d(pointArray);
			for (Pnt2d p : pointArray) {
				assertTrue(hull.contains(p));
			}
		}
	}

    @Test
    public void testGetShape() {
        List<Pnt2d> points = Arrays.asList(
                Pnt2d.from(2, 5),
                Pnt2d.from(5, 4),
                Pnt2d.from(5, 7),
                Pnt2d.from(7, 6),
                Pnt2d.from(9, 9),
                Pnt2d.from(6, 2)
        );
        ConvexHull2d hull = new ConvexHull2d(points);
        Shape s = hull.getShape(1.0);
        assertNotNull(s);
        assertTrue(s instanceof Path2D.Double);
        s.contains(8, 8);
        s.contains(3, 5);
    }

}

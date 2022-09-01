/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.delaunay.guibas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.Triangle;

public class TriangulationGuibasTest {

	@Test
	public void test1() {
		Pnt2d p1 = Pnt2d.from(-10, 10);
		Pnt2d p2 = Pnt2d.from(10, 10);
		Pnt2d p3 = Pnt2d.from(0, -10);

		Pnt2d p4 = Pnt2d.from(0, 0);
		Pnt2d p5 = Pnt2d.from(1, 0);
		Pnt2d p6 = Pnt2d.from(0, 1);
		
		List<Pnt2d> points = Arrays.asList(p1, p2, p3, p4, p5, p6);

		TriangulationGuibas triangulation = new TriangulationGuibas(points, true);
		assertEquals("triangulation: wrong number of triangles", 7, triangulation.size());
		assertEquals("triangulation: wrong number of points", 6, triangulation.getPoints().size());
		
		Triangle triangle1 = triangulation.findContainingTriangle(Pnt2d.from(2, 0.5));
		List<Pnt2d> pts1 = Arrays.asList(triangle1.getPoints());
		assertTrue(pts1.contains(p2));
		assertTrue(pts1.contains(p3));
		assertTrue(pts1.contains(p5));
		
		Triangle triangle2 = triangulation.findContainingTriangle(Pnt2d.from(100, 0));
		assertNull(triangle2);
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.delaunay;

import java.util.Collection;
import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.guibas.TriangulationGuibas;

/**
 * Interface specification for various implementations of the
 * Delaunay triangulation.
 */
public interface DelaunayTriangulation {
	
	/**
	 * Returns the number of triangles in this triangulation.
	 * @return the number of triangles
	 */
	public int size();
	
	/**
	 * Returns a list of {@link Triangle} instances
	 * contained in this triangulation. The list does not contain the initial outer 
	 * triangle.
	 * @return a list of triangles 
	 */
	public List<Triangle> getTriangles();
	
	/**
	 * Returns a list of 2D vertices (implementing the {@link Pnt2d} interface)
	 * contained in this triangulation. The list does not contain the
	 * vertices of the initial (outer) triangle.
	 * @return a list of points
	 */
	public List<Pnt2d> getPoints();
	
	// utility methods: ----------------------------------------
	
	/**
	 * Creates a 2D triangle that is sufficiently large to be used as 
	 * an outer triangle for the Delaunay triangulation of the given
	 * point set.
	 * @param points the 2D point set
	 * @return a triangle as an array of 3 points
	 */
	public static Pnt2d[] makeOuterTriangle(Collection<? extends Pnt2d> points) {
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = xmin;
		double ymax = xmax;
		
		for (Pnt2d p : points) {
			double x = p.getX();
			double y = p.getY();
			xmin = Math.min(x, xmin);
			xmax = Math.max(x, xmax);
			ymin = Math.min(y, ymin);
			ymax = Math.max(y, ymax);
		}
		return makeOuterTriangle(xmin, xmax, ymin, ymax);
	}

	/**
	 * Creates a 2D triangle that is sufficiently large to be used as 
	 * an outer triangle for the Delaunay triangulation of points 
	 * inside the given bounding rectangle.
	 * @param xmin minimum x-coordinate of the bounding rectangle
	 * @param xmax maximum x-coordinate of the bounding rectangle
	 * @param ymin minimum y-coordinate of the bounding rectangle
	 * @param ymax maximum y-coordinate of the bounding rectangle
	 * @return a triangle as an array of 3 points
	 */
	public static Pnt2d[] makeOuterTriangle(double xmin, double xmax, double ymin, double ymax) {
		double width = xmax - xmin;
		double height = ymax - ymin;
		double diam = Math.max(width,  height);
		double xc = xmin + width / 2;
		double yc = ymin + height / 2;
		double s = 50;
		return new Pnt2d[] {
				Pnt2d.PntDouble.from(xc, yc + s * diam),
				Pnt2d.PntDouble.from(xc + s * diam, yc),
				Pnt2d.PntDouble.from(xc - s * diam, yc - s * diam)
		};
	}

	/**
	 * Creates a 2D triangle that is sufficiently large to be used as 
	 * an outer triangle for the Delaunay triangulation of points 
	 * inside the given bounding rectangle, anchored at (0,0).
	 * @param width the width of the bounding rectangle
	 * @param height the height of the bounding rectangle
	 * @return a triangle as an array of 3 points
	 */
	public static Pnt2d[] makeOuterTriangle(double width, double height) {
		return makeOuterTriangle(0, width, 0, height);
	}

	/**
	 * Performs Delaunay triangulation on the specified points.
	 * Supplied points are inserted without shuffling, i.e.,
	 * in their original order.
	 * 
	 * @param points the point set to be triangulated
	 * @return a {@link DelaunayTriangulation} instance
	 */
	public static DelaunayTriangulation from(Collection<? extends Pnt2d> points) {
		return DelaunayTriangulation.from(points, false);
	}
	
	// static coonstruction methods: -----------------------------------
	
	/**
	 * Performs Delaunay triangulation on the specified points with
	 * (optional) random insertion order.
	 * 
	 * @param points the point set to be triangulated
	 * @param shuffle set {@code true} to randomly shuffle the input points
	 * @return a {@link DelaunayTriangulation} instance
	 */
	public static DelaunayTriangulation from(Collection<? extends Pnt2d> points, boolean shuffle) {
		return new TriangulationGuibas(points, shuffle);
	}

}

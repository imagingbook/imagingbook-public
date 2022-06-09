/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.delaunay;

import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.guibas.Triangle2D;

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
	 * Returns a list of {@link Triangle2D} instances
	 * contained in this triangulation. The list does not contain the initial outer 
	 * triangle.
	 * @return a list of triangles 
	 */
	public List<Triangle> getTriangles();
	
	/**
	 * Returns a list of 2D vertices (implementing the {@link Pnt2d} interface)
	 * contained in this triangulation. The sequence of points is assumed
	 * to be in the order of their actual insertion. The list does not contain the
	 * vertices of the initial (outer) triangle.
	 * @return a list of points
	 */
	public List<Pnt2d> getPoints();

}

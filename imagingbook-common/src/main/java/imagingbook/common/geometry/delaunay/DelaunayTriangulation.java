package imagingbook.common.geometry.delaunay;

import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * Interface specification for various implementations of the
 * Delaunay triangulation.
 */
public interface DelaunayTriangulation {
	
	/**
	 * Returns a list of 2D triangles (implementing the {@link Triangle} interface) 
	 * contained in this triangulation. The list does not contain the initial outer 
	 * triangle.
	 * @return a list of triangles 
	 */
	List<Triangle> getTriangles();
	
	/**
	 * Returns a list of 2D vertices (implementing the {@link Pnt2d} interface)
	 * contained in this triangulation. The sequence of points is assumed
	 * to be in the order of their actual insertion. The list does not contain the
	 * vertices of the initial (outer) triangle.
	 * @return a list of points
	 */
	List<Pnt2d> getPoints();

}

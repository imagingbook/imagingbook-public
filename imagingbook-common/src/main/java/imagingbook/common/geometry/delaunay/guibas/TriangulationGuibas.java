/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.geometry.delaunay.guibas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.DelaunayTriangulation;
import imagingbook.common.geometry.delaunay.Triangle;

/**
 * This is an implementation of the triangulation algorithm described in
 * <blockquote>
 * L. J. Guibas, D. E. Knuth, and M. Sharir" "Randomized incremental construction of 
 * Delaunay and Voronoi diagrams", Algorithmica, 7, pp. 381--413 (1992).
 * </blockquote>
 * 
 * @author WB
 *
 */
public class TriangulationGuibas implements DelaunayTriangulation {

	private final List<Pnt2d> points;
	private final List<Triangle2D> triangles;
	private final Triangle2D outerTriangle;

	/**
	 * Constructor. 
	 * @param points the point set to be triangulated
	 * @param shuffle set {@code true} to randomly shuffle the input points
	 */
	public TriangulationGuibas(Collection<? extends Pnt2d> points, boolean shuffle) {
		if (points == null || points.size() < 3) {
			throw new IllegalArgumentException("Point set must contain at least 3 points.");
		}
		this.points = new ArrayList<Pnt2d>(points);
		if (shuffle) {
			Collections.shuffle(this.points);
		}
		this.outerTriangle = new Triangle2D(DelaunayTriangulation.makeOuterTriangle(points));
		this.triangles = new ArrayList<>();
		triangulate();
	}
	
	/**
	 * Constructor. 
	 * Supplied points are inserted without shuffling, i.e.,
	 * in their original order.
	 * 
	 * @param points the point set to be triangulated
	 */
	public TriangulationGuibas(Collection<? extends Pnt2d> points) {
		this(points, false);
	}
	
	// -----------------------------------------------------------------------------
	
	@Override
	public int size() {
		return triangles.size();
	}
	
	@Override
	public List<Triangle> getTriangles() {
		return Collections.unmodifiableList(triangles);
	}
	
	@Override
	public List<Pnt2d> getPoints() {
		return points;
	}
	
	// -----------------------------------------------------------------------------

	private void triangulate() {
		triangles.add(outerTriangle);
		
		for (Pnt2d pnt : points) {
			Triangle2D triangle = this.findContainingTriangle(pnt);

			if (triangle == null) {	// pnt is outside of any triangle
				/* If no containing triangle exists, then the vertex is not inside a triangle
				 * (this can also happen due to numerical errors) and lies on an edge. In order
				 * to find this edge we search all edges of the triangle soup and select the one
				 * which is nearest to the point we try to add. This edge is removed and four
				 * new edges are added.
				 */
				Edge2D edge = this.findNearestEdge(pnt);

				Triangle2D tr1 = this.findOneTriangleSharing(edge);
				Triangle2D tr2 = this.findNeighbour(tr1, edge);

				Pnt2d noneEdgeVertex1 = tr1.getOppositeVertex(edge);
				Pnt2d noneEdgeVertex2 = tr2.getOppositeVertex(edge);

				triangles.remove(tr1);
				triangles.remove(tr2);

				Triangle2D triangle1 = new Triangle2D(edge.a, noneEdgeVertex1, pnt);
				Triangle2D triangle2 = new Triangle2D(edge.b, noneEdgeVertex1, pnt);
				Triangle2D triangle3 = new Triangle2D(edge.a, noneEdgeVertex2, pnt);
				Triangle2D triangle4 = new Triangle2D(edge.b, noneEdgeVertex2, pnt);

				triangles.add(triangle1);
				triangles.add(triangle2);
				triangles.add(triangle3);
				triangles.add(triangle4);


				legalizeEdge(triangle1, new Edge2D(edge.a, noneEdgeVertex1), pnt);
				legalizeEdge(triangle2, new Edge2D(edge.b, noneEdgeVertex1), pnt);
				legalizeEdge(triangle3, new Edge2D(edge.a, noneEdgeVertex2), pnt);
				legalizeEdge(triangle4, new Edge2D(edge.b, noneEdgeVertex2), pnt);
			} 
			else { // pnt is inside the triangle <a,b,c>.
				Pnt2d a = triangle.a;
				Pnt2d b = triangle.b;
				Pnt2d c = triangle.c;

				triangles.remove(triangle);

				Triangle2D triangle1 = new Triangle2D(a, b, pnt);
				Triangle2D triangle2 = new Triangle2D(b, c, pnt);
				Triangle2D triangle3 = new Triangle2D(c, a, pnt);

				triangles.add(triangle1);
				triangles.add(triangle2);
				triangles.add(triangle3);

				legalizeEdge(triangle1, new Edge2D(a, b), pnt);
				legalizeEdge(triangle2, new Edge2D(b, c), pnt);
				legalizeEdge(triangle3, new Edge2D(c, a), pnt);
			}
		}

		// Remove all triangles containing any vertex of the super triangle:
		this.removeTrianglesUsing(outerTriangle.a);
		this.removeTrianglesUsing(outerTriangle.b);
		this.removeTrianglesUsing(outerTriangle.c);
	}

	/**
	 * This method legalizes edges by recursively flipping all illegal edges.
	 * 
	 * @param triangle  the triangle
	 * @param edge      the edge to be legalized
	 * @param newVertex the new vertex
	 */
	private void legalizeEdge(Triangle2D triangle, Edge2D edge, Pnt2d newVertex) {
		Triangle2D neighbourTriangle = this.findNeighbour(triangle, edge);
		// If the triangle has a neighbor, then legalize the edge
		if (neighbourTriangle != null) {
			if (neighbourTriangle.isPointInCircumCircle(newVertex)) {
				triangles.remove(triangle);
				triangles.remove(neighbourTriangle);

				Pnt2d noneEdgeVertex = neighbourTriangle.getOppositeVertex(edge);

				Triangle2D triangle1 = new Triangle2D(noneEdgeVertex, edge.a, newVertex);
				Triangle2D triangle2 = new Triangle2D(noneEdgeVertex, edge.b, newVertex);

				triangles.add(triangle1);
				triangles.add(triangle2);

				legalizeEdge(triangle1, new Edge2D(noneEdgeVertex, edge.a), newVertex);
				legalizeEdge(triangle2, new Edge2D(noneEdgeVertex, edge.b), newVertex);
			}
		}
	}
	
	// triangle-related methods ---------------------------

	/**
	 * Returns the triangle that contains the specified point or null if no
	 * such triangle exists.
	 * @param point the query point
	 * @return the containing triangle or {@code null} if none was found
	 */
	public Triangle2D findContainingTriangle(Pnt2d point) {
		for (Triangle2D triangle : triangles) {
			if (triangle.containsPoint(point)) {
				return triangle;
			}
		}
		return null;
	}

	/**
	 * Returns the neighboring triangle of the specified triangle sharing the same edge
	 * as specified. If no neighbor sharing the same edge exists {@code null} is returned.
	 * NOTE: Searching over ALL triangles seems to be unnecessarily expensive!
	 * 
	 * @param tri1 the triangle
	 * @param edge the edge
	 * @return the triangle's neighboring triangle sharing the same edge or {@code null} if no
	 *         such triangle exists
	 */
	public Triangle2D findNeighbour(Triangle2D tri1, Edge2D edge) {
		for (Triangle2D tri2 : triangles) {
			if (tri2.containsEdge(edge) && tri2 != tri1) {
				return tri2;
			}
		}
		return null;
	}

	/**
	 * Returns one of the possible triangles sharing the specified edge. Based on
	 * the ordering of the triangles in this triangle soup the returned triangle may
	 * differ. To find the other triangle that shares this edge use the
	 * {@code findNeighbour(Triangle2D triangle, Edge2D edge)} method.
	 * @param edge the edge
	 * @return the triangle that shares the specified edge or {@code null} if none exists
	 */
	public Triangle2D findOneTriangleSharing(Edge2D edge) {
		for (Triangle2D triangle : triangles) {
			if (triangle.containsEdge(edge)) {
				return triangle;
			}
		}
		return null;
	}
	
	/**
	 * Returns the triangle edge nearest to the specified point.
	 * 
	 * @param point the query point
	 * @return the triangle edge nearest to the specified point
	 */
	public Edge2D findNearestEdge(Pnt2d point) {
		Edge2D minEdge = null;
		double minDist = Double.POSITIVE_INFINITY;
		for (Triangle2D tri : triangles) {
			Edge2D.Distance ed = tri.findMinEdgeDistance(point);
			double dist = ed.getDistance();
			if (dist < minDist) {
				minDist = dist;
				minEdge = ed.getEdge();
			}
		}
		return minEdge;
	}

	/**
	 * Removes all triangles that contain the specified corner point.
	 * @param point the corner point
	 */
	public void removeTrianglesUsing(Pnt2d point) {
		List<Triangle2D> trianglesToBeRemoved = new LinkedList<>();
		for (Triangle2D triangle : triangles) {
			if (triangle.hasVertex(point)) {
				trianglesToBeRemoved.add(triangle);
			}
		}
		triangles.removeAll(trianglesToBeRemoved);
	}
	
	// --------------------------------------------------------------------------------------
	
//	public static void main (String[] args) {
//		List<Pnt2d> points = new ArrayList<>();
//		points.add(Pnt2d.from(-10,10));
//		points.add(Pnt2d.from(10,10));
//		points.add(Pnt2d.from(0,-10));
//		
//		points.add(Pnt2d.from(0,0));
//		points.add(Pnt2d.from(1,0));
//		points.add(Pnt2d.from(0,1));
//		
//		TriangulationGuibas triangulation = new TriangulationGuibas(points, false);
//		
//		List<Triangle> triangles = triangulation.getTriangles();
//		System.out.println("triangles: " + triangles.size());
//		
//		System.out.println("containing triangle: " + triangulation.findContainingTriangle(Pnt2d.from(2, 0.5)));
//		System.out.println("containing triangle: " + triangulation.findContainingTriangle(Pnt2d.from(100, 0)));
//	}


}
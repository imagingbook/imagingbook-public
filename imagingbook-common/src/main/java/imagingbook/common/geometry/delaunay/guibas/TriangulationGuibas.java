package imagingbook.common.geometry.delaunay.guibas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.DelaunayTriangulation;
import imagingbook.common.geometry.delaunay.Triangle;
import imagingbook.common.geometry.delaunay.Utils;


/**
 * <p>
 * Implementation of the randomized incremental algorithm described in
 * Guibas, Knuth, Sharir, "Randomized incremental construction of Delaunay and Voronoi diagrams", 
 * Algorithmica, no. 7, p. 381-413 (1992).
 * See also Berg et al., <em>Computational Geometry - Algorithms and Applications</em>, 
 * 3rd ed., Springer (2008), Sec. 9.3-9.6.
 * The code in this package is partly based on an earlier implementation by Johannes Diemke
 * (https://github.com/jdiemke/delaunay-triangulator) published under the 
 * following license:
 * </p>
 * <pre>
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Johannes Diemke
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * </pre>
 * Adapted by W. Burger
 * @version 2020-01-02
 */
public class TriangulationGuibas implements DelaunayTriangulation {

	private final List<Vector2D> points;
	private final List<Triangle2D> triangles;
	private final Triangle2D outerTriangle;

	/**
	 * Constructor. 
	 * @param pointSet the point set to be triangulated
	 * @param shuffle set {@code true} to randomly shuffle the input points
	 */
	public TriangulationGuibas(List<? extends Pnt2d> pointSet, boolean shuffle) {
		if (pointSet == null || pointSet.size() < 3) {
			throw new IllegalArgumentException("Point set must contain at least 3 points.");
		}
		this.points = makePointset(pointSet, shuffle);
		this.triangles = new ArrayList<>();
		this.outerTriangle = new Triangle2D(Utils.makeOuterTriangle(pointSet));
		triangulate();
	}
	
	/**
	 * The simplest constructor. The supplied points will be inserted
	 * in their original order (without shuffling).
	 * @param pointSet the point set to be triangulated
	 */
	public TriangulationGuibas(List<? extends Pnt2d> pointSet) {
		this(pointSet, false);
	}
	
	// -----------------------------------------------------------------------------
	
	/** 
	 * Converts the incoming points (of unknown type but implementing the {@link Pnt2d} interface)
	 * to instances of the local implementation ({@link Vector2D}).
	 * 
	 * @param inPoints the input points (must implement {@link Pnt2d})
	 * @param shuffle if {@code true}, the input point sequence is randomly permuted
	 * @return the new point sequence
	 */
	private List<Vector2D> makePointset(Collection<? extends Pnt2d> inPoints, boolean shuffle) {
		Vector2D[] outPoints = new Vector2D[inPoints.size()];
		int i = 0;
		for (Pnt2d ip : inPoints) {
			outPoints[i] = new Vector2D(ip);
			i++;
		}
		List<Vector2D> outList = Arrays.asList(outPoints);
		if (shuffle) {
			Collections.shuffle(outList);	// random permutation of pointset (in-place)
		}
		return outList;
	}

	private void triangulate() {
		triangles.add(outerTriangle);
		
		for (Vector2D pnt : points) {

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

				Vector2D noneEdgeVertex1 = tr1.getOppositeVertex(edge);
				Vector2D noneEdgeVertex2 = tr2.getOppositeVertex(edge);

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
				Vector2D a = triangle.a;
				Vector2D b = triangle.b;
				Vector2D c = triangle.c;

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
	private void legalizeEdge(Triangle2D triangle, Edge2D edge, Vector2D newVertex) {
		Triangle2D neighbourTriangle = this.findNeighbour(triangle, edge);
		// If the triangle has a neighbor, then legalize the edge
		if (neighbourTriangle != null) {
			if (neighbourTriangle.isPointInCircumCircle(newVertex)) {
				triangles.remove(triangle);
				triangles.remove(neighbourTriangle);

				Vector2D noneEdgeVertex = neighbourTriangle.getOppositeVertex(edge);

				Triangle2D triangle1 = new Triangle2D(noneEdgeVertex, edge.a, newVertex);
				Triangle2D triangle2 = new Triangle2D(noneEdgeVertex, edge.b, newVertex);

				triangles.add(triangle1);
				triangles.add(triangle2);

				legalizeEdge(triangle1, new Edge2D(noneEdgeVertex, edge.a), newVertex);
				legalizeEdge(triangle2, new Edge2D(noneEdgeVertex, edge.b), newVertex);
			}
		}
	}

	/**
	 * Returns the point set in form of a vector of 2D vectors.
	 * 
	 * @return Returns the points set.
	 */
	public List<Vector2D> getPointSet() {
		return points;
	}

	/**
	 * Returns the triangles of the triangulation in form of a vector of 2D
	 * triangles. The initial 'superTriangle' is removed.
	 * The resulting triangles should form a convex structure.
	 * 
	 * @return the triangles of this triangulation.
	 */
	@Override
	public List<Triangle> getTriangles() {
		return Collections.unmodifiableList(triangles);
	}

	@Override
	public List<Pnt2d> getPoints() {
		return Collections.unmodifiableList(points);
	}
	
	// triangle-related methods ---------------------------

	/**
	 * Returns the triangle that contains the specified point or null if no
	 * such triangle exists.
	 * @param point the query point
	 * @return the containing triangle or {@code null} if none was found
	 */
	public Triangle2D findContainingTriangle(Vector2D point) {
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
	 * TODO: searching over ALL triangles seems to be unnecessarily expensive
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
	 * @param point the query point
	 * @return the triangle edge nearest to the specified point
	 */
	private Edge2D findNearestEdge(Vector2D point) {
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
	public void removeTrianglesUsing(Vector2D point) {
		List<Triangle2D> trianglesToBeRemoved = new LinkedList<>();
		for (Triangle2D triangle : triangles) {
			if (triangle.hasVertex(point)) {
				trianglesToBeRemoved.add(triangle);
			}
		}
		triangles.removeAll(trianglesToBeRemoved);
	}

}
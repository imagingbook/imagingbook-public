package imagingbook.common.geometry.delaunay.chew;

/*
 * Copyright (c) 2005, 2007 by L. Paul Chew.
 *
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.DelaunayTriangulation;
import imagingbook.common.geometry.delaunay.Triangle;
import imagingbook.common.geometry.delaunay.Utils;


/**
 * <p>
 * Implementation of the incremental triangulation algorithm proposed by Paul Chew.
 * This code is partly based on Paul Chew's original (applet) implementation, downloaded from 
 * http://www.cs.cornell.edu/Info/People/chew/Delaunay.html (DelaunaySourceCodeJava60.zip)
 * under the following license:
 * </p>
 * <pre>
 * Copyright (c) 2005, 2007 by L. Paul Chew.
 * 
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * </pre>
 * <p>
 * <strong>Original description:</strong> 
 * <em>The actual data structure here is a Delaunay Triangulation. The Voronoi Diagram is built on-the-fly
 * from the Delaunay Triangulation. The Delaunay Triangulation is built within a large triangle whose vertices
 * are well off-screen. That's why in the Delaunay Triangulation there are lines heading off-screen. 
 * This technique makes the code simpler since otherwise additional code would be needed to handle new sites
 * that are outside the convex hull of the previous sites.</em>
 * <br>
 * <em>The algorithm: To insert a new site, we walk across the triangulation, starting from the most recently created
 * triangle, until we find the triangle that contains the new site. This triangle and any adjacent triangles that
 * contain this new site in their circumcircle are eliminated and the resulting empty spot is re-triangulated.
 * The site-insertion part of this technique is commonly called the Bowyer-Watson Algorithm. The expected time to
 * insert a new site is roughly O(n1/2) where n is the current number of sites.</em>
 * </p>
 * 
 * Adapted by W. Burger
 * @version 2020-01-02
 */
public class TriangulationChew extends AbstractSet<Triangle2D> implements DelaunayTriangulation {

    private Triangle2D mostRecent = null;      // Most recently "active" triangle
    private final Triangle2D superTriangle;
    private final Graph<Triangle2D> triGraph;        // Holds triangles for navigation
    private final List<Pnt2d> points;
    
    /** 
     * Convenience constructor for insertion into a bounding rectangle
     * of the specified size (with the origin assumed at 0/0).
     * Typical used for images.
     * @param width the width of the insertion rectangle
     * @param height the height of the insertion rectangle
     */
    public TriangulationChew(double width, double height) {
    	this(Utils.makeOuterTriangle(width, height));
    }
   
    /**
     * Constructor that takes an initial outer (super) triangle.
     * @param outerTriangle the outer (super) triangle
     */
    public TriangulationChew(Pnt2d[] outerTriangle) {
    	this(new Triangle2D(outerTriangle));
    }
    
    /**
     * Constructor that accepts a set of 2D points to triangulate.
     * The outer (super) triangle is calculated from the point coordinates.
     * The points are inserted into the resulting triangulation.
     * @param pointSet a sequence of 2D points
     */
    public TriangulationChew(Collection<Pnt2d> pointSet) {
    	this(new Triangle2D(Utils.makeOuterTriangle(pointSet)));
    	insertAll(pointSet);
    }
    
    /**
     * All sites must fall within the initial triangle.
     * @param superTriangle the initial triangle
     */
    private TriangulationChew (Triangle2D superTriangle) {
    	this.superTriangle = superTriangle;
    	this.triGraph = new Graph<Triangle2D>();
    	this.triGraph.add(superTriangle);
        this.mostRecent = superTriangle;
        this.points = new ArrayList<>();
    }
    
    // ----------------------------------------------------------
    
    public Triangle2D getSuperTriangle() {
    	return this.superTriangle;
    }

    /* The following two methods are required by AbstractSet */

    @Override
    public Iterator<Triangle2D> iterator () {
        return triGraph.nodeSet().iterator();
    }

    @Override
    public int size () {
        return triGraph.nodeSet().size();
    }

    @Override
    public String toString () {
        return "Triangulation with " + size() + " triangles";
    }

    /**
     * True iff triangle is a member of this triangulation.
     * This method isn't required by AbstractSet, but it improves efficiency.
     * @param triangle the object to check for membership
     */
    public boolean contains (Object triangle) {
        return triGraph.nodeSet().contains(triangle);
    }

    /**
     * Report neighbor opposite the given vertex of triangle.
     * @param site a vertex of triangle
     * @param triangle we want the neighbor of this triangle
     * @return the neighbor opposite site in triangle; null if none
     * @throws IllegalArgumentException if site is not in this triangle
     */
    public Triangle2D neighborOpposite (Pnt site, Triangle2D triangle) {
        if (!triangle.contains(site))
            throw new IllegalArgumentException("Bad vertex; not in triangle");
        for (Triangle2D neighbor: triGraph.neighbors(triangle)) {
            if (!neighbor.contains(site)) return neighbor;
        }
        return null;
    }

    /**
     * Return the set of triangles adjacent to triangle.
     * @param triangle the triangle to check
     * @return the neighbors of triangle
     */
    public Set<Triangle2D> neighbors(Triangle2D triangle) {
        return triGraph.neighbors(triangle);
    }

    /**
     * Report triangles surrounding site in order (cw or ccw).
     * @param site we want the surrounding triangles for this site
     * @param triangle a "starting" triangle that has site as a vertex
     * @return all triangles surrounding site in order (cw or ccw)
     * @throws IllegalArgumentException if site is not in triangle
     */
    public List<Triangle2D> surroundingTriangles (Pnt site, Triangle2D triangle) {
        if (!triangle.contains(site))
            throw new IllegalArgumentException("Site not in triangle");
        List<Triangle2D> list = new ArrayList<Triangle2D>();
        Triangle2D start = triangle;
        Pnt guide = triangle.getVertexButNot(site);        // Affects cw or ccw
        while (true) {
            list.add(triangle);
            Triangle2D previous = triangle;
            triangle = this.neighborOpposite(guide, triangle); // Next triangle
            guide = previous.getVertexButNot(site, guide);     // Update guide
            if (triangle == start) break;
        }
        return list;
    }

    /**
     * Locate the triangle with point inside it or on its boundary.
     * @param point the point to locate
     * @return the triangle that holds point; null if no such triangle
     */
    public Triangle2D locate (Pnt point) {
        Triangle2D triangle = mostRecent;
        if (!this.contains(triangle)) {
        	triangle = null;
        }
        // Try a directed walk (this works fine in 2D, but can fail in 3D)
        Set<Triangle2D> visited = new HashSet<Triangle2D>();
        while (triangle != null) {
            if (visited.contains(triangle)) { // This should never happen
                System.out.println("Warning: Caught in a locate loop");
                break;
            }
            visited.add(triangle);
            // Corner opposite point
            Pnt corner = point.isOutside(triangle.toArray(new Pnt[0]));
            if (corner == null) return triangle;
            triangle = this.neighborOpposite(corner, triangle);
        }
        // No luck; try brute force
        System.out.println("Warning: Checking all triangles for " + point);
        for (Triangle2D tri: this) {
            if (point.isOutside(tri.toArray(new Pnt[0])) == null) {
            	return tri;
            }
        }
        // No such triangle
        System.out.println("Warning: No triangle holds " + point);
        return null;
    }
    
    public void insertAll(Collection<Pnt2d> pointSet) {
    	for (Pnt2d p : pointSet) {
    		insert(p);
    	}
    }

    /**
     * Place a new site into the DT.
     * Nothing happens if the site matches an existing DT vertex.
     * Uses straightforward scheme rather than best asymptotic time.
     * Throws an {@link IllegalArgumentException} if p does not lie in any triangle.
     * @param p the point to be inserted
     */
    public void insert(Pnt2d p) {
    	Pnt vec = new Pnt(p);
        // Locate containing triangle
        Triangle2D triangle = locate(vec);
        // Give up if no containing triangle or if site is already in DT
        if (triangle == null)
            throw new IllegalArgumentException("No containing triangle");
//        if (triangle.contains(vec)) 
//        	return;
        if (!triangle.contains(vec)) {
	        // Determine the cavity and update the triangulation
	        Set<Triangle2D> cavity = getCavity(vec, triangle);
	        mostRecent = update(vec, cavity);
        }
        points.add(p);
    }

    /**
     * Determine the cavity caused by site.
     * @param site the site causing the cavity
     * @param triangle the triangle containing site
     * @return set of all triangles that have site in their circumcircle
     */
    private Set<Triangle2D> getCavity (Pnt site, Triangle2D triangle) {
        Set<Triangle2D> encroached = new HashSet<Triangle2D>();
        Queue<Triangle2D> toBeChecked = new LinkedList<Triangle2D>();
        Set<Triangle2D> marked = new HashSet<Triangle2D>();
        toBeChecked.add(triangle);
        marked.add(triangle);
        while (!toBeChecked.isEmpty()) {
            triangle = toBeChecked.remove();
            if (site.vsCircumcircle(triangle.toArray(new Pnt[0])) == 1)
                continue; // Site outside triangle => triangle not in cavity
            encroached.add(triangle);
            // Check the neighbors
            for (Triangle2D neighbor: triGraph.neighbors(triangle)){
                if (marked.contains(neighbor)) continue;
                marked.add(neighbor);
                toBeChecked.add(neighbor);
            }
        }
        return encroached;
    }

    /**
     * Update the triangulation by removing the cavity triangles and then
     * filling the cavity with new triangles.
     * @param site the site that created the cavity
     * @param cavity the triangles with site in their circumcircle
     * @return one of the new triangles
     */
    private Triangle2D update (Pnt site, Set<Triangle2D> cavity) {
        Set<Set<Pnt>> boundary = new HashSet<Set<Pnt>>();
        Set<Triangle2D> theTriangles = new HashSet<Triangle2D>();

        // Find boundary facets and adjacent triangles
        for (Triangle2D triangle: cavity) {
            theTriangles.addAll(neighbors(triangle));
            for (Pnt vertex: triangle) {
                Set<Pnt> facet = triangle.facetOpposite(vertex);
                if (boundary.contains(facet)) boundary.remove(facet);
                else boundary.add(facet);
            }
        }
        theTriangles.removeAll(cavity);        // Adj triangles only

        // Remove the cavity triangles from the triangulation
        for (Triangle2D triangle: cavity) triGraph.remove(triangle);

        // Build each new triangle and add it to the triangulation
        Set<Triangle2D> newTriangles = new HashSet<Triangle2D>();
        for (Set<Pnt> vertices: boundary) {
            vertices.add(site);
            Triangle2D tri = new Triangle2D(vertices);
            triGraph.add(tri);
            newTriangles.add(tri);
        }

        // Update the graph links for each new triangle
        theTriangles.addAll(newTriangles);    // Adj triangle + new triangles
        for (Triangle2D triangle: newTriangles)
            for (Triangle2D other: theTriangles)
                if (triangle.isNeighbor(other))
                    triGraph.add(triangle, other);

        // Return one of the new triangles
        return newTriangles.iterator().next();
    }
    
    /**
     * Checks if the given triangle shares a vertex with the triangulation's
     * initial ('super') triangle.
     * @param otherTriangle the other triangle
     * @return {@code true} iff there is a common vertex
     */
    private boolean connectsToSuperTriangle(Triangle2D otherTriangle) {
        boolean connects = false;
        for (Pnt vertex : superTriangle) {
            if (otherTriangle.contains(vertex)) {
            	connects = true;
            	break;
            }
        }
        return connects;
    }
    
	@Override
	public List<Triangle> getTriangles() {
		return this.stream().filter(t -> !connectsToSuperTriangle(t)).collect(Collectors.toList());
	}
	
	@Override
	public List<Pnt2d> getPoints() {
		return Collections.unmodifiableList(points);
	}
	
	// --- this should not be here ----------------------------------------
	
//	private void drawVoronoiDiagram (ImageProcessor ip) {
//		TriangulationChew dt = this;
//    	// Keep track of sites done; no drawing for initial triangles sites
//    	//Pnt[] initPoints = dt.getInitialTriangle().getVertices();
//    	Point[] initPoints = superTriangle.getPoints();
//    	Set<Point> processed = new HashSet<>(Arrays.asList(initPoints));
//    	for (Triangle triangle : dt.getTriangles()) {
//    		for (Point pnt : triangle.getPoints()) {
//    			if (!processed.contains(pnt)) {
//	    			processed.add(pnt);
//	    			List<Triangle> triangles = dt.surroundingTriangles(pnt, triangle);
//	    			List<Pnt> vertices = new ArrayList<>(triangles.size());
//	    			for (Triangle tri : triangles) {
//	    				Pnt center = tri.getCircumcenter();
//	    				vertices.add(center);
//	    			}
//	    			IjUtil.draw(vertices.toArray(new Point[0]), ip);
//    			}
//    		}
//    	}
//    }

//    /**
//     * Main program; used for testing. ------------------------------------
//     */
//    public static void main (String[] args) {
//        Triangle2D tri =
//            new Triangle2D(new Vector2D(-10,10), new Vector2D(10,10), new Vector2D(0,-10));
//        System.out.println("Triangle created: " + tri);
//        TriangulationChew dt = new TriangulationChew(tri);
//        System.out.println("DelaunayTriangulation created: " + dt);
//        dt.insert(new Vector2D(0,0));
//        dt.insert(new Vector2D(1,0));
//        dt.insert(new Vector2D(0,1));
//        System.out.println("After adding 3 points, we have a " + dt);
//        Triangle2D.moreInfo = true;
//        System.out.println("Triangles: " + dt.triGraph.nodeSet());
//    }


}
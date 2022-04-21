package imagingbook.common.geometry.delaunay.guibas;

import java.util.Arrays;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.Triangle;


/**
 * Represents a 2D triangle, specified by three corner points.
 * Instances of this class are immutable.
 */
public class Triangle2D implements Triangle {

	protected final Vector2D a;
	protected final Vector2D b;
	protected final Vector2D c;
	private final boolean isOrientedCCW;

	/**
	 * Constructor of the 2D triangle class used to create a new triangle instance
	 * from three 2D vectors describing the triangle's vertices.
	 * 
	 * @param a The first vertex of the triangle
	 * @param b The second vertex of the triangle
	 * @param c The third vertex of the triangle
	 */
	public Triangle2D(Vector2D a, Vector2D b, Vector2D c) {
		this.a = a;
		this.b = b;
		this.c = c;
		isOrientedCCW = findIfOrientedCCW();
	}
	
	public Triangle2D(Pnt2d[] points) {
		this(new Vector2D(points[0]), new Vector2D(points[1]), new Vector2D(points[2]));
	}

	/**
	 * Tests if a 2D point lies inside this 2D triangle. See See Christer Ericson, 
	 * Real-Time Collision Detection, CRC Press, 2004 (Ch. 5, p. 206).
	 * 
	 * @param point the point to be checked
	 * @return {@code true} iff the point lies inside this 2D triangle
	 */
	public boolean containsPoint(Vector2D point) {
		double pab = point.sub(a).cross(b.sub(a));
		double pbc = point.sub(b).cross(c.sub(b));
		if (!hasSameSign(pab, pbc)) {
			return false;
		}
		double pca = point.sub(c).cross(a.sub(c));
		if (!hasSameSign(pab, pca)) {
			return false;
		}
		return true;
	}

	/**
	 * Tests if a given point lies in the circumcircle of this triangle. Let the
	 * triangle ABC appear in counterclockwise (CCW) order. Then when det &gt; 0,
	 * the point lies inside the circumcircle through the three points a, b and c.
	 * If instead det &lt; 0, the point lies outside the circumcircle. When det = 0,
	 * the four points are cocircular. If the triangle is oriented clockwise (CW)
	 * the result is reversed. 
	 * See Christer Ericson, Real-Time Collision Detection, CRC Press, 2004 (Ch. 3, p. 34).
	 * 
	 * @param point the point to be checked
	 * @return {@code true} iff the point lies inside the circumcircle through the
	 *         three points a, b, and c of the triangle
	 */
	protected boolean isPointInCircumCircle(Vector2D point) {
		final double a11 = a.getX() - point.getX();
		final double a21 = b.getX() - point.getX();
		final double a31 = c.getX() - point.getX();

		final double a12 = a.getY() - point.getY();
		final double a22 = b.getY() - point.getY();
		final double a32 = c.getY() - point.getY();
		
		final double a13 = a11 * a11 + a12 * a12;
		final double a23 = a21 * a21 + a22 * a22;
		final double a33 = a31 * a31 + a32 * a32;

		final double det = 
				a11 * a22 * a33 + a12 * a23 * a31 + 
				a13 * a21 * a32 - a13 * a22 * a31 - 
				a12 * a21 * a33 - a11 * a23 * a32;

		return isOrientedCCW ? det > 0.0 : det < 0.0;
	}

	/**
	 * Tests if a given point lies in the circumcircle of this triangle. Let the
	 * triangle ABC appear in counterclockwise (CCW) order. Then when det &gt; 0,
	 * the point lies inside the circumcircle through the three points a, b and c.
	 * If instead det &lt; 0, the point lies outside the circumcircle. When det = 0,
	 * the four points are cocircular. If the triangle is oriented clockwise (CW)
	 * the result is reversed. 
	 * See Christer Ericson, Real-Time Collision Detection, CRC Press, 2004 (Ch. 3, p. 32).
	 * Since triangles are immutable, this property can be pre-calculated.
	 * 
	 * @return {@code true} iff the triangle abc is oriented counterclockwise (CCW)
	 */
	private boolean findIfOrientedCCW() {
		double a11 = a.getX() - c.getX();
		double a21 = b.getX() - c.getX();
		double a12 = a.getY() - c.getY();
		double a22 = b.getY() - c.getY();
		double det = a11 * a22 - a12 * a21;
		return det > 0.0;
	}

	/**
	 * Test if this triangle is oriented counterclockwise (CCW).
	 * This property is pre-calculated.
	 * 
	 * @return {@code true} iff the triangle ABC is oriented counterclockwise (CCW)
	 */
	protected boolean isOrientedCCW() {
		return isOrientedCCW;
	}

	/**
	 * Returns {@code true} if this triangle contains the given edge.
	 * 
	 * @param edge the edge to be tested
	 * @return {@code true} iff this triangle contains the specified edge
	 */
	public boolean containsEdge(Edge2D edge) {
		return (a == edge.a || b == edge.a || c == edge.a) && (a == edge.b || b == edge.b || c == edge.b);
	}

	/**
	 * Returns the vertex of this triangle opposite to the specified edge.
	 * 
	 * @param edge the edge (which must be contained in this triangle)
	 * @return the triangle vertex opposite to the specified edge
	 */
	public Vector2D getOppositeVertex(Edge2D edge) {
		final Vector2D p1 = edge.a;
		final Vector2D p2 = edge.b;
		if ((a == p1 && b == p2) || (a == p2 && b == p1)) {
			return c;
		}
		if ((a == p1 && c == p2) || (a == p2 && c == p1)) {
			return b;
		}
		if ((b == p1 && c == p2) || (b == p2 && c == p1)) {
			return a;
		} 
		throw new IllegalArgumentException("Specified edge is not part of this triangle");
	}

	/**
	 * Checks if the given vertex is amongst the triangle's vertices.
	 * 
	 * @param vertex the vertex to be checked
	 * @return {@code true} if the vertex is one of the corners of this triangle
	 */
	public boolean hasVertex(Vector2D vertex) {
		return (a == vertex || b == vertex || c == vertex);
	}

	/**
	 * Calculates the minimum distance from the specified point to this triangle.
	 * The result is returned as an {@link Edge2D.Distance} instance, representing 
	 * the point's distance to the closest edge of this triangle.
	 * 
	 * @param point the point to be checked
	 * @return the edge of this triangle that is closest to the specified point
	 */
	public Edge2D.Distance findMinEdgeDistance(Vector2D point) {
		Edge2D.Distance[] distances = {
				new Edge2D(a, b).distanceFromPoint(point),
				new Edge2D(b, c).distanceFromPoint(point),
				new Edge2D(c, a).distanceFromPoint(point)	
			};
		Arrays.sort(distances);
		return distances[0];
	}

	/**
	 * Tests if the two arguments have the same sign.
	 * @param a first quantity
	 * @param b second quantity
	 * @return {@code true} iff both arguments have the same sign
	 */
	private boolean hasSameSign(double a, double b) {
		return Math.signum(a) == Math.signum(b);
		//return a * b > 0;
	}

	@Override
	public String toString() {
		return Triangle2D.class.getSimpleName() + "[" + a + ", " + b + ", " + c + "]";
	}

	@Override
	public Pnt2d[] getPoints() {
		return new Pnt2d[] {a, b, c};
	}

}
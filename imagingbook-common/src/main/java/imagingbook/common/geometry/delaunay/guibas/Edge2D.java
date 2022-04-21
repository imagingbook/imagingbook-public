package imagingbook.common.geometry.delaunay.guibas;

/**
 * This class represents a 2D edge (line segment), specified
 * by its two end-points. Instances of this class are immutable.
 */
public class Edge2D {

	protected final Vector2D a;
	protected final Vector2D b;

	/**
	 * Constructor of the 2D edge class used to create a new edge instance from two
	 * 2D vectors describing the edge's vertices.
	 * 
	 * @param a first vertex of the edge
	 * @param b second vertex of the edge
	 */
	protected Edge2D(Vector2D a, Vector2D b) {
		this.a = a;
		this.b = b;
	}

	private double getMinDistance(Vector2D point) {
		return getClosestPoint(point).sub(point).mag();
	}

	/**
	 * Calculates the point ON this edge that is closest to the
	 * specified point.
	 * @param point the point whose distance is to me calculated
	 * @return the closest point on this edge
	 */
	private Vector2D getClosestPoint(Vector2D point) {
		Vector2D ab = b.sub(a);
		double t = point.sub(a).dot(ab) / ab.dot(ab); // TODO: check for zero denominator?
		if (t < 0.0) {
			t = 0.0;
		} else if (t > 1.0) {
			t = 1.0;
		}
		return a.add(ab.mult(t));
	}

	/**
	 * Creates and returns a new {@link Edge2D.Distance} object, representing
	 * the minimum distance between this edge and the specified point.
	 * @param point the point to calculate the distance for
	 * @return a new {@link Edge2D.Distance} instance
	 */
	protected Distance distanceFromPoint(Vector2D point) {
		return new Distance(point);
	}

	/**
	 * Non-static inner class representing the distance of a particular point to the
	 * associated (enclosing) {@link Edge2D} instance.
	 */
	protected class Distance implements Comparable<Distance> {

		private final double distance;

		protected Distance(Vector2D point) {
			this(Edge2D.this.getMinDistance(point));
		}

		protected Distance(double distance) {
			this.distance = distance;
		}

		protected Edge2D getEdge() {
			return Edge2D.this;
		}

		protected double getDistance() {
			return this.distance;
		}

		@Override
		public int compareTo(Distance o) {
			return Double.compare(this.distance, o.distance);
		}
	}

}
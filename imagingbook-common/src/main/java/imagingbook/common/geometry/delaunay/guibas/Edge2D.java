/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.delaunay.guibas;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * This class represents a 2D edge (line segment), specified
 * by its two end-points. Instances of this class are immutable.
 */
public class Edge2D {

	protected final Pnt2d a, b;

	/**
	 * Constructor of the 2D edge class used to create a new edge instance from two
	 * 2D vectors describing the edge's vertices.
	 * 
	 * @param a first vertex of the edge
	 * @param b second vertex of the edge
	 */
	protected Edge2D(Pnt2d a, Pnt2d b) {
		this.a = a;
		this.b = b;
	}

	private double getMinDistance(Pnt2d point) {
		//return getClosestPoint(point).minus(point).mag();
		return getClosestPoint(point).distance(point);
	}

	/**
	 * Calculates the point on this edge that is closest to the
	 * specified point.
	 * @param point the point whose distance is to be calculated
	 * @return the closest point on this edge
	 */
	private Pnt2d getClosestPoint(Pnt2d point) {
		Pnt2d ab = b.minus(a);
		double t = point.minus(a).dot(ab) / ab.dot(ab); // TODO: check for zero denominator?
		if (t < 0.0) {
			t = 0.0;
		} else if (t > 1.0) {
			t = 1.0;
		}
		return a.plus(ab.mult(t));
	}

	/**
	 * Creates and returns a new {@link Edge2D.Distance} object, representing
	 * the minimum distance between this edge and the specified point.
	 * @param point the point to calculate the distance for
	 * @return a new {@link Edge2D.Distance} instance
	 */
	protected Distance distanceFromPoint(Pnt2d point) {
		return new Distance(point);
	}

	/**
	 * Non-static inner class representing the distance of a particular point to the
	 * associated (enclosing) {@link Edge2D} instance.
	 */
	protected class Distance implements Comparable<Distance> {

		private final double distance;

		protected Distance(Pnt2d point) {
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
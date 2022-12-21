/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.hulls;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.shape.ShapeProducer;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * This class calculate the convex hull of a 2D point set. It is based on the convex hull implementation provided by the
 * Apache Commons Math library, in particular classes {@link ConvexHull2D} and {@link MonotoneChain} [1]. See Sec. 8.4.2
 * of [2] for additional details.
 * </p>
 * <p>
 * [1] <a href="https://commons.apache.org/proper/commons-math/index.html">
 * https://commons.apache.org/proper/commons-math/index.html</a> <br> [2] W. Burger, M.J. Burge, <em>Digital Image
 * Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/06/24
 */
public class ConvexHull implements ShapeProducer {
	
	private final ConvexHull2D hull;
	private final Pnt2d[] vertices;

	/**
	 * Constructor, creates a {@link ConvexHull} instance from an {@link Iterable} over {@link Pnt2d}. At least one
	 * distinct point is required.
	 *
	 * @param points an iterator over 2D points
	 */
	public ConvexHull(Iterable<Pnt2d> points) {
		if (!points.iterator().hasNext()) {
			throw new IllegalArgumentException("empty point sequence, at least one input point required");
		}
		List<Vector2D> pts = toVector2D(points);
		this.hull = new MonotoneChain().generate(pts);
		Vector2D[] vecs = hull.getVertices();
		this.vertices = new Pnt2d[vecs.length];
		for (int i = 0; i < vecs.length; i++) {
			vertices[i] = PntDouble.from(vecs[i]);
		}
	}

	/**
	 * Constructor, creates a {@link AxisAlignedBoundingBox} instance from an array of {@link Pnt2d} points. At least
	 * one distinct point is required.
	 *
	 * @param points an array of 2D points
	 */
	public ConvexHull(Pnt2d[] points) {
		this(() -> Arrays.stream(points).iterator());
	}
	
	private static List<Vector2D> toVector2D(Iterable<Pnt2d> points) {
		List<Vector2D> vecs = new ArrayList<Vector2D>();
		for (Pnt2d p : points) {
			vecs.add(new Vector2D(p.getX(), p.getY()));
		}
		return vecs;
	}

	/**
	 * Returns a sequence of 2D points on the convex hull (in counter-clockwise order).
	 *
	 * @return sequence of 2D points on the convex hull
	 */
	public Pnt2d[] getVertices() {
		return this.vertices;
	}
	
//	@Deprecated
//	public Line2D[] getSegments() {
//		Segment[] origSegments = hull.getLineSegments();
//		Line2D[] newSegments = new Line2D.Double[origSegments.length];
//		for (int i = 0; i < origSegments.length; i++) {
//			Segment seg = origSegments[i];
//			Vector2D start = seg.getStart();
//			Vector2D end = seg.getEnd();
//			newSegments[i] = new Line2D.Double(start.getX(), start.getY(), end.getX(), end.getY());
//		}
//		return newSegments;
//	}
	
	// --------------------------------------------------------------------
	


	@Override
	public Shape getShape(double scale) {
		if (vertices.length < 2) {	// degenerate case (single point)
			return vertices[0].getShape(scale);
		}
		else {
			Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
			path.moveTo(vertices[0].getX(), vertices[0].getY());
			for (int i = 1; i < vertices.length; i++) {
				path.lineTo(vertices[i].getX(), vertices[i].getY());
			}
			path.closePath();
			return path;
		}
	}
	
	// --------------------------------------------------------------------
	
	public static final double DefaultContainsTolerance = 1e-12;
	
	public boolean contains(Pnt2d p) {
		return contains(p, DefaultContainsTolerance);
	}

	/**
	 * Checks if this convex hull contains the specified point. This method is used instead of
	 * {@link Path2D#contains(double, double)} to avoid false results due to roundoff errors.
	 *
	 * @param p some 2D point
	 * @param tolerance positive quantity for being outside
	 * @return true if the point is inside the hull
	 */
	public boolean contains(Pnt2d p, double tolerance) {
		for (int i = 0; i < vertices.length; i++) {
			int j = (i + 1) % vertices.length;
			AlgebraicLine line = AlgebraicLine.from(vertices[i], vertices[j]);
			double dist = line.getSignedDistance(p);
			// positive signed distance means that the point is to the left
			if (dist + tolerance < 0) {
				return false;
			}
		}
		return true;
	}

}

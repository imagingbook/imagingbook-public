/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.hulls;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;

/**
 * This class serves to calculate the convex hull of a binary region
 * or a closed contour, given as a sequence of point coordinates.
 * It is based on the convex hull implementation provided by the
 * Apache Commons Math library, in particular the classes 
 * {@link ConvexHull2D} and {@link MonotoneChain}.
 * 
 * @author W. Burger
 * @version 2020/10/11
 */
public class ConvexHull {
	
	private final ConvexHull2D hull;
	
	// public constructors ------------------------
	
	public ConvexHull(Iterable<Pnt2d> iterable) {
		this.hull = new MonotoneChain().generate(convertToVector2D(iterable));
	}
	
	// public methods ------------------------
	
	public Pnt2d[] getVertices() {
		Vector2D[] vecs = hull.getVertices();	// apparently vertices are ordered, but is this guaranteed?
		Pnt2d[] pnts = new Pnt2d[vecs.length];
		for (int i = 0; i < vecs.length; i++) {
			pnts[i] = PntDouble.from(vecs[i]);
		}
		return pnts;
	}
	
	public Line2D[] getSegments() {
		Segment[] origSegments = hull.getLineSegments();
		Line2D[] newSegments = new Line2D.Double[origSegments.length];
		for (int i = 0; i < origSegments.length; i++) {
			Segment seg = origSegments[i];
			Vector2D start = seg.getStart();
			Vector2D end = seg.getEnd();
			newSegments[i] = new Line2D.Double(start.getX(), start.getY(), end.getX(), end.getY());
		}
		return newSegments;
	}
	
	// --------------------------------------------------------------------
	
	private Collection<Vector2D> convertToVector2D(Iterable<Pnt2d> iterable) {
		Collection<Vector2D> vecs = new ArrayList<Vector2D>();
		for (Pnt2d p : iterable) {
			vecs.add(new Vector2D(p.getX(), p.getY()));
		}
		return vecs;
	}

}

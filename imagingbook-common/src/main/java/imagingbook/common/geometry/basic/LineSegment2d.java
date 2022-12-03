/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.basic;

import java.awt.Shape;
import java.awt.geom.Path2D;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.shape.ShapeProducer;

/**
 * Described a 2D line segment defined by two end points.
 * Instances are immutable.
 * 
 * @author WB
 */
public class LineSegment2d implements ShapeProducer {
	
	private final Pnt2d p1, p2;
	
	/**
	 * Constructor for arbitrary 2D points.
	 * @param p1 first end point
	 * @param p2 second end point
	 */
	public LineSegment2d(Pnt2d p1, Pnt2d p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	/**
	 * Constructor for integer coordinates.
	 * @param x1 x-coordinate of first point
	 * @param y1 y-coordinate of first point
	 * @param x2 x-coordinate of second point
	 * @param y2 y-coordinate of second point
	 */
	public LineSegment2d(int x1, int y1, int x2, int y2) {
		this.p1 = PntInt.from(x1, y1);
		this.p2 = PntInt.from(x2, y2);
	}
	
	/**
	 * Constructor for double coordinates.
	 * @param x1 x-coordinate of first point
	 * @param y1 y-coordinate of first point
	 * @param x2 x-coordinate of second point
	 * @param y2 y-coordinate of second point
	 */
	public LineSegment2d(double x1, double y1, double x2, double y2) {
		this.p1 = PntDouble.from(x1, y1);
		this.p2 = PntDouble.from(x2, y2);
	}
	
	/**
	 * Returns the first end point.
	 * @return the first end point
	 */
	public Pnt2d getP1() {
		return p1;
	}
	
	/**
	 * Returns the second end point.
	 * @return the second end point
	 */
	public Pnt2d getP2() {
		return p2;
	}
	
	// ------------------------------------------------------------

	@Override
	public Shape getShape(double scale) {
		Path2D path = new Path2D.Double();
		path.moveTo(p1.getX(), p1.getY());
		path.lineTo(p2.getX(), p2.getY());
		return path;
	}
	
}

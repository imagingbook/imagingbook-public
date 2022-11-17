/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.delaunay;

import java.awt.Shape;
import java.awt.geom.Path2D;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.shape.ShapeProducer;

/** 
 * Interface specifying a 2D triangle.
 */
public interface Triangle extends ShapeProducer {	// TODO: better integrate with other 2D primitives
	
	/**
	 * Returns an array of points used by the triangulation in the order 
	 * of their insertion.
	 * 
	 * @return an array of points
	 */
	Pnt2d[] getPoints();
	
	@Override
	public default Shape getShape(double scale) {
		Path2D path = new Path2D.Double();
		Pnt2d[] pts = this.getPoints();
		Pnt2d a = pts[0];
		Pnt2d b = pts[1];
		Pnt2d c = pts[2];
		path.moveTo(a.getX(), a.getY());
		path.lineTo(b.getX(), b.getY());
		path.lineTo(c.getX(), c.getY());
//		path.lineTo(a.getX(), a.getY());
		path.closePath();
		return path;
	}
	
}


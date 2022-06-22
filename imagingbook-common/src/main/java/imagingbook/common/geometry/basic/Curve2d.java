/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.basic;

import java.awt.Shape;

import imagingbook.common.geometry.shape.ShapePointIterator;

/**
 * Interface specifying a 2D curve capable of calculating the minimum
 * distance to a given point.
 *  
 * @author WB
 *
 */
public interface Curve2d {
	
	/**
	 * Returns the minimum absolute distance from the specified point to this curve.
	 * 
	 * @param p a 2D point
	 * @return the minimum distance (always positive)
	 */
	public double getDistance(Pnt2d p);
	
	// ---------------------------------------------------------------------------
	
	/**
	 * Checks if all points of the specified AWT shape are sufficiently
	 * close to this 2D curve. This is typically used to test if the shape produced
	 * by {@link #getShape()} coincide with this curve.
	 * Only the discrete sample points produced by {@link ShapePointIterator}
	 * are checked, not the points on connecting polygon segments. 
	 * Typical usage:
	 * <pre>
	 * Shape s = this.getShape();
	 * boolean ok = this.checkShape(s, 1.0);</pre>
	 * 
	 * @param s the AWT shape
	 * @param tolerance the maximum shape point distance from the curve
	 * @return true if all  points of shape s are closer to this curve than tolerance
	 */
	public default boolean checkShape(Shape s, double tolerance) {
		ShapePointIterator iter = new ShapePointIterator(s, 0.5 * tolerance);
		boolean result = true;
		while(iter.hasNext()) {
			Pnt2d p = iter.next();
			if (getDistance(p) > tolerance) {
				result = false;
				break;
			}
		}
		return result;
	}
	

}

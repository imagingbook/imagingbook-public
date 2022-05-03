/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.shape;

import java.awt.Shape;

import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;

/**
 * Implementing classes know how to create an AWT {@link Shape}.
 * 
 * @author WB
 *
 */
public interface ShapeProducer extends Curve2d {
	
	/**
	 * Returns a scaled {@link Shape} for this object
	 * (default scale is 1).
	 * Must be defined by implementing classes.
	 * The interpretation of the scale factor is left to the implementing class.
	 * For example, for {@link Pnt2d} it specifies the size of the marker 
	 * (see {@link Pnt2d#getShape(double)}.
	 * 
	 * @param scale the scale of the shape
	 * @return a {@link Shape} instance
	 */
	public Shape getShape(double scale);
	
	/**
	 * Returns a {@link Shape} for this object at the
	 * default scale (1).
	 * @return a {@link Shape} instance
	 */
	public default Shape getShape() {
		return getShape(1);
	};
	
	/**
	 * Returns a fixed sequence of {@link Shape} items for drawing this object,
	 * which must contain at least one item.
	 * This is to produce graphic representations that are too complex for
	 * a single {@link Shape} item.
	 * The returned shapes may also be displayed with different strokes or colors.
	 * <p>
	 * By default, this method returns a single item which is the primary
	 * shape (obtained by {@link #getShape(double)}).
	 * Implementing classes should override this method if more than one
	 * shape must be returned 
	 * For example, a {@link GeometricEllipse} returns three shape items:
	 * (a) the ellipse curve, (b) the center mark, (c) the major axes
	 * (see {@link GeometricEllipse#getShapes(double)}).
	 * </p>
	 * 
	 * @param scale a scale factor (may be used or ignored)
	 * @return sequence of {@link Shape} items
	 */
	public default Shape[] getShapes(double scale) {
		return new Shape[] { getShape(scale) };
	}
	
	public default Shape[] getShapes() {
		return getShapes(1);
	}
	
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

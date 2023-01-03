/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.shape;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Primitive2d;

import java.awt.Shape;

/**
 * Used to check if AWT shapes produced by {@link ShapeProducer#getShape()} match the underlying curve
 * ({@link Primitive2d}). This is mainly used to test if generated shapes (to be drawn to the screen) are sufficiently
 * accurate.
 *
 * @see ShapeProducer
 * @see Primitive2d
 */
public class ShapeChecker {

	private final double tolerance;
	
	/**
	 * Constructor.
	 * 
	 * @param tolerance maximum deviation between curve and shape
	 */
	public ShapeChecker(double tolerance) {
		this.tolerance = tolerance;
	}
	
	/**
	 * Constructor.
	 */
	public ShapeChecker() {
		this(0.5);
	}

	/**
	 * Checks if all points of the specified AWT {@link Shape} are sufficiently close to the {@link Primitive2d}
	 * instance specified in the constructor. This is typically used to test if a shape produced by
	 * {@link ShapeProducer#getShape()} coincides with this curve. Only the discrete sample points produced by
	 * {@link ShapePointIterator} are checked, not the points on connecting polygon segments. Typical usage example:
	 * <pre>
	 * GeometricCircle circle = ... ; // implements ShapeProducer and Curve2d
	 * Shape shape = circle.getShape();
	 * boolean ok = new ShapeChecker().checkShape(circle, shape);</pre>
	 *
	 * @param curve a {@link Primitive2d} instance
	 * @param shape the AWT shape to check
	 * @return true if all points of the shape are closer to the curve than tolerance
	 */
	public boolean check(Primitive2d curve, Shape shape) {
		ShapePointIterator iter = new ShapePointIterator(shape, 0.5 * tolerance);
		boolean result = true;
		while(iter.hasNext()) {
			Pnt2d p = iter.next();
			if (curve.getDistance(p) > tolerance) {
				result = false;
				break;
			}
		}
		return result;
	}

}

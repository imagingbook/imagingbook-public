package imagingbook.common.geometry.shape;

import java.awt.Shape;

import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;

/**
 * Used to check if AWT shapes produced by {@link ShapeProducer#getShape()}
 * match the underlying curve ({@link Curve2d}).
 * This is mainly used to test if generated shapes (to be drawn
 * to the screen) are sufficiently accurate.
 * 
 * @see ShapeProducer
 * @see Curve2d
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
	 * Checks if all points of the specified AWT {@link Shape} are sufficiently
	 * close to the {@link Curve2d} instance specified in the constructor.
	 * This is typically used to test if a shape produced
	 * by {@link ShapeProducer#getShape()} coincides with this curve.
	 * Only the discrete sample points produced by {@link ShapePointIterator}
	 * are checked, not the points on connecting polygon segments. 
	 * Typical usage example:
	 * <pre>
	 * GeometricCircle circle = ... ; // implements ShapeProducer and Curve2d
	 * Shape shape = circle.getShape();
	 * boolean ok = new ShapeChecker().checkShape(circle, shape);</pre>
	 * 
	 * @param curve a {@link Curve2d} instance
	 * @param shape the AWT shape to check
	 * @return true if all points of the shape are closer to the curve than tolerance
	 */
	public boolean check(Curve2d curve, Shape shape) {
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

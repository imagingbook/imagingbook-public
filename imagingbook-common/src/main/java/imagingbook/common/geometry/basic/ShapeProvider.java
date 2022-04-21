package imagingbook.common.geometry.basic;

import java.awt.Shape;

import imagingbook.common.geometry.ellipse.GeometricEllipse;

/**
 * Implementing classes know how to create an AWT {@link Shape}.
 * TODO: Rename to ShapeProducer or merge with {@link Curve2d}?
 * @author WB
 *
 */
public interface ShapeProvider {
	
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
	 * @return
	 */
	public default Shape[] getShapes(double scale) {
		return new Shape[] { getShape(scale) };
	}
	
	public default Shape[] getShapes() {
		return getShapes(1);
	}
	
}

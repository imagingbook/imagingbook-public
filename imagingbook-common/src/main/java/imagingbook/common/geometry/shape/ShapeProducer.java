/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.shape;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementing classes know how to create an AWT {@link Shape}.
 * 
 * @author WB
 *
 */
public interface ShapeProducer {

	/**
	 * Returns a scaled {@link Shape} for this object (default scale is 1). Must be defined by implementing classes. The
	 * interpretation of the scale factor is left to the implementing class. For example, for {@link Pnt2d} it specifies
	 * the size of the marker (see {@link Pnt2d#getShape(double)}.
	 *
	 * @param scale the scale of the shape
	 * @return a {@link Shape} instance
	 */
	public Shape getShape(double scale);

	/**
	 * Returns a {@link Shape} for this object at the default scale (1).
	 *
	 * @return a {@link Shape} instance
	 */
	public default Shape getShape() {
		return getShape(1);
	};

	/**
	 * Returns a fixed sequence of {@link Shape} items for drawing this object, which must contain at least one item.
	 * This is to produce graphic representations that are too complex for a single {@link Shape} item. The returned
	 * shapes may also be displayed with different strokes or colors.
	 * <p>
	 * By default, this method returns a single item which is the primary shape (obtained by {@link #getShape(double)}).
	 * Implementing classes should override this method if more than one shape must be returned For example, a
	 * {@link GeometricEllipse} returns three shape items: (a) the ellipse curve, (b) the center mark, (c) the major
	 * axes (see {@link GeometricEllipse#getShapes(double)}).
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
	
	// TODO: experimental
	public static Pnt2d[] getShapePoints(Shape shape) {
	    double[] coords = new double[6];
	    PathIterator pathIterator = shape.getPathIterator(null);

	    List<Pnt2d> points = new ArrayList<>();
	    
	    while (!pathIterator.isDone()) {
	    	int segmentType = pathIterator.currentSegment(coords);
	        switch (segmentType) {
	        
	        case PathIterator.SEG_MOVETO :
	            System.out.printf("move to x1=%f, y1=%f\n", coords[0], coords[1]);
	            points.add(Pnt2d.from(coords[0], coords[1]));
	            break;
	            
	        case PathIterator.SEG_LINETO :
	            System.out.printf("line to x1=%f, y1=%f\n", coords[0], coords[1]);
	            points.add(Pnt2d.from(coords[0], coords[1]));
	            break;
	            
	        case PathIterator.SEG_QUADTO :
	            System.out.printf("quad to x1=%f, y1=%f, x2=%f, y2=%f\n", coords[0], coords[1], coords[2], coords[3]);
	            points.add(Pnt2d.from(coords[0], coords[1]));
	            break;
	            
	        case PathIterator.SEG_CUBICTO :
	            System.out.printf("cubic to x1=%f, y1=%f, x2=%f, y2=%f, x3=%f, y3=%f\n",
	                    coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
	            points.add(Pnt2d.from(coords[0], coords[1]));
	            break;
	            
	        case PathIterator.SEG_CLOSE :
	            System.out.printf("close\n");
	            break;
	            
	        default : throw new RuntimeException("unknown path segment type " + segmentType);
	        }
	        
	        
	        pathIterator.next();
	    }
	    return points.toArray(new Pnt2d[points.size()]);
	}
	
	
}

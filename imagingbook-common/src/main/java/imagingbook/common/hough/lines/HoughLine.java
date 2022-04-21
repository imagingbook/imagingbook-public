/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.hough.lines;

import static imagingbook.common.math.Arithmetic.sqr;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.ShapeProvider;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.line.HessianLine;

/**
 * This class represents a straight line used by the Hough transform
 * (see {@link imagingbook.common.hough.HoughTransformLines}).
 * It inherits from {@link HessianLine} which is, in turn, a subclass of 
 * {@link AlgebraicLine}.
 * It adds an arbitrary reference point and a counter for pixel votes.
 */
public class HoughLine extends HessianLine implements Comparable<HoughLine>, ShapeProvider {
	
	private final int count;			// pixel votes for this line
	private final double xRef, yRef;	// reference point
	
	
	// static factory methods -------------------------------
	
	public static HoughLine fromPoints(Pnt2d p1, Pnt2d p2, Pnt2d pRef, int count) {
		return new HoughLine(AlgebraicLine.from(p1, p2), pRef.getX(), pRef.getY(), count);
	}
	
	// constructors -----------------------------------------
	
	public HoughLine(double a, double b, double c, double xR, double yR, int count) {
		super(a, b, c);
		this.xRef = xR;
		this.yRef = yR;
		this.count = count;
	}

	/**
	 * Constructor.
	 * @param angle the line's normal angle (see {@link HessianLine})
	 * @param radius the line's radius (distance to reference point)
	 * @param xRef reference point x-coordinate
	 * @param yRef reference point y-coordinate
	 * @param count pixel votes for this line
	 */
	public HoughLine(double angle, double radius, double xRef, double yRef, int count) {
		super(angle, radius);
		this.xRef = xRef;
		this.yRef = yRef;
		this.count = count;
	}
	
	public HoughLine(AlgebraicLine line) {
		this(line, 0.0, 0.0, 0);
	}
	
	/**
	 * Constructor. 
	 * Creates a new {@link HoughLine} instance from a given
	 * {@link AlgebraicLine} (or any subclass) instance.
	 * The line parameters are adjusted to the specified reference point
	 * (actually only parameter c is modified, since a change of reference point
	 * effects only a shift of the line).
	 * The two lines are equivalent, i.e., contain the same points (x,y).
	 * Thus the distance from a given point (x,y) is the same from the original
	 * line and the new line.
	 * @param line an existing line ({@link AlgebraicLine} or subclass)
	 * @param xR reference point x-coordinate
	 * @param yR reference point y-coordinate
	 * @param count pixel votes for this line
	 */
	public HoughLine(AlgebraicLine line, double xR, double yR, int count) {
		super(line.getA(),
			  line.getB(),
			  line.getC() + line.getA()*(xR-line.getXref()) + line.getB()*(yR-line.getYref())); // = a', b', c'
		this.xRef = xR;
		this.yRef = yR;
		this.count = count;
	}
	
	// getter/setter methods ------------------------------------------
	
	/**
	 * @return The accumulator count associated with this line.
	 */
	public int getCount() {
		return count;
	}
	
	@Override
	public double getXref() {
		return xRef;
	}
	
	@Override
	public double getYref() {
		return yRef;
	}
	
	// other methods ------------------------------------------
	
	/**
	 * Required by the {@link Comparable} interface, used for sorting lines by their
	 * point count (in descending order, i.e., strong lines come first).
	 * @param other another {@link HoughLine} instance.
	 */
	@Override
	public int compareTo(HoughLine other) {
		return Integer.compare(other.count, this.count);
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f, xRef = %.3f, yRef = %.3f, count = %d>",
				this.getClass().getSimpleName(), getAngle(), getRadius(), getXref(), getYref(), count);
	}
	
	// ------------------------------------------------------------------------------
	
	public Shape getShape(int width, int height) {
		double xRef = this.getXref();
		double yRef = this.getYref();
		double length = Math.sqrt(sqr(width) + sqr(height));
		double angle = this.getAngle();
		double radius = this.getRadius();
		// unit vector perpendicular to the line
		double dx = Math.cos(angle);	
		double dy = Math.sin(angle);
		// calculate the line's center point (closest to the reference point)
		double x0 = xRef + radius * dx;
		double y0 = yRef + radius * dy;
		// calculate the line end points (using normal vectors)
		double x1 = x0 + dy * length;
		double y1 = y0 - dx * length;
		double x2 = x0 - dy * length;
		double y2 = y0 + dx * length;
		Path2D path = new Path2D.Double();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		return path;
	}

	@Override
	public Shape getShape(double length) {
		double xRef = this.getXref();
		double yRef = this.getYref();
//		double length = Math.sqrt(sqr(width) + sqr(height)); //Math.sqrt(sqr(xRef) + sqr(yRef));
		double angle = this.getAngle();
		double radius = this.getRadius();
		// unit vector perpendicular to the line
		double dx = Math.cos(angle);	
		double dy = Math.sin(angle);
		// calculate the line's center point (closest to the reference point)
		double x0 = xRef + radius * dx;
		double y0 = yRef + radius * dy;
		// calculate the line end points (using normal vectors)
//		float x1 = (float) (x0 + dy * length);
//		float y1 = (float) (y0 - dx * length);
//		float x2 = (float) (x0 - dy * length);
//		float y2 = (float) (y0 + dx * length);
//		float[] xpoints = { x1, x2 };
//		float[] ypoints = { y1, y2 };
		//Roi roi = new PolygonRoi(xpoints, ypoints, Roi.POLYLINE);
		
		double x1 = x0 + dy * length;
		double y1 = y0 - dx * length;
		double x2 = x0 - dy * length;
		double y2 = y0 + dx * length;
		Path2D path = new Path2D.Double();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		return path;
	}
	

//	/**
//	 * Creates a vector line to be used an element in an ImageJ graphic overlay
//	 * (see {@link ij.gui.Overlay}). The length of the displayed line 
//	 * is equivalent to the distance of the reference point (typically the
//	 * image center) to the coordinate origin.
//	 * @return the new line
//	 * @deprecated
//	 */
//	public PolygonRoi makeLineRoi() {
//		double length = Math.sqrt(xRef * xRef + yRef * yRef);
//		return this.makeLineRoi(length);
//	}
	
//	/**
//	 * Creates a vector line to be used an element in an ImageJ graphic overlay
//	 * (see {@link ij.gui.Overlay}). The length of the displayed line 
//	 * is measured from its center point (the point closest to the reference
//	 * point) in both directions.
//	 * 
//	 * @param length the length of the line
//	 * @return the new line
//	 */
//	public PolygonRoi makeLineRoi(double length) {
//		// unit vector perpendicular to the line
//		double dx = Math.cos(angle);	
//		double dy = Math.sin(angle);
//		// calculate the line's center point (closest to the reference point)
//		double x0 = xRef + radius * dx;
//		double y0 = yRef + radius * dy;
//		// calculate the line end points (using normal vectors)
//		float x1 = (float) (x0 + dy * length);
//		float y1 = (float) (y0 - dx * length);
//		float x2 = (float) (x0 - dy * length);
//		float y2 = (float) (y0 + dx * length);
//		float[] xpoints = { x1, x2 };
//		float[] ypoints = { y1, y2 };
//		return new PolygonRoi(xpoints, ypoints, Roi.POLYLINE);
//	}

}

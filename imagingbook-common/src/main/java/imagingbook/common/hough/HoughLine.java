/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.hough;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.line.HessianLine;

/**
 * This class represents a straight line of the form
 * (x - xRef) * cos(angle) + (y - yRef) * sin(angle) = radius.
 * It is used by the Hough transform (see {@link imagingbook.common.hough.HoughTransformLines}).
 * It inherits from {@link HessianLine} which is, in turn, a subclass of 
 * {@link AlgebraicLine}.
 * It adds an arbitrary reference point (xRef, yRef) and a counter (count) for pixel votes.
 * Instances are immutable.
 */
public class HoughLine extends HessianLine implements Comparable<HoughLine> {
	
	private final double xRef, yRef;	// reference point
	private final int count;			// pixel votes for this line
	
	// static factory methods -------------------------------
	
	public static HoughLine from(Pnt2d p1, Pnt2d p2, Pnt2d pRef, int count) {
		return new HoughLine(AlgebraicLine.from(p1, p2), pRef.getX(), pRef.getY(), count);
	}
	
	// constructors -----------------------------------------

	/**
	 * Constructor. Creates a new {@link HoughLine} instance from the
	 * specified {@link HessianLine} parameters (angle, radius),
	 * an arbitrary reference point (xRef, yRef) and count.
	 * 
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
	 * @param xRef reference point x-coordinate
	 * @param yRef reference point y-coordinate
	 * @param count pixel votes for this line
	 */
	public HoughLine(AlgebraicLine line, double xRef, double yRef, int count) {
		super(new AlgebraicLine(line.A, line.B, line.C + line.A * (xRef - line.getXref()) + line.B * (yRef - line.getYref())));
		this.xRef = xRef;
		this.yRef = yRef;
		this.count = count;
	}
	
	/**
	 * Convenience constructor. Creates a new {@link HoughLine} instance from a given
	 * {@link AlgebraicLine} (or any subclass) instance with the same reference point 
	 * as the original line and zero count.
	 * 
	 * @param line a {@link AlgebraicLine} instance
	 */
	public HoughLine(AlgebraicLine line) {
		this(line, line.getXref(), line.getYref(), 0);
	}
	
	// ------------------------------------------
	
	@Override
	public double getXref() {
		return xRef;
	}
	
	@Override
	public double getYref() {
		return yRef;
	}
	
	public int getCount() {
		return count;
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
	
}

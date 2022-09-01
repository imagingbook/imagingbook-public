/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;


import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * This class represents a straight line in Hessian normal form, i.e., 
 * x * cos(angle) + y * sin(angle) = radius.
 * It is merely a subclass of {@link AlgebraicLine} with a different constructor
 * and getter methods for angle and radius.
 * Instances are immutable. Reference point is (0,0).
 */
public class HessianLine extends AlgebraicLine {
	
	// static factory methods ----------------------------------------
	
	public static HessianLine from(Pnt2d p1, Pnt2d p2) {
		return new HessianLine(AlgebraicLine.from(p1, p2));
	}
	
	// constructors --------------------------------------------------

	/**
	 * Constructor. Creates a new {@link HessianLine} instance with the
	 * specified angle and radius.
	 * Note that this really creates a normalized {@link AlgebraicLine}.
	 * The values returned by {@link #getAngle()} and {@link #getRadius()}
	 * may not be identical to the values passed to this constructor.
	 * 
	 * @param angle the line's angle
	 * @param radius the line's radius
	 */
	public HessianLine(double angle, double radius) {
		super(Math.cos(angle), Math.sin(angle), -radius);	// = A, B, C
	}
	
	public HessianLine(double a, double b, double c) {
		super(a, b, c);			// creates a normalized line
	}
	
	public HessianLine(AlgebraicLine al) {
		super(al.getParameters());
	}
	
	public double getAngle() {
		return Math.atan2(B, A);
	}
	
	public double getRadius() {
		return -C;
	}
	
	// -------------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f>",
				this.getClass().getSimpleName(), getAngle(), getRadius());
	}

}

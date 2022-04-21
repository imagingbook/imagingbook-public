/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;

//import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * This class represents a straight line in Hessian normal form, i.e., 
 * x * cos(angle) + y * sin(angle) = radius.
 * It is a specialization (subclass) of {@link AlgebraicLine}.
 * Instances are immutable.
 * 
 * TODO: This class is unfinished! Handling reference points to be added/completed!
 */
public class HessianLine extends AlgebraicLine {
	
	private final double angle;
	private final double radius;
	private final Pnt2d refPnt;	// reference point x_r, TODO: always zero!
	
	
	// static factory methods ----------------------------------------
	
	public static HessianLine fromPoints(Pnt2d p1, Pnt2d p2) {
		return new HessianLine(AlgebraicLine.from(p1, p2));
	}
	
	// constructors --------------------------------------------------

	public HessianLine(double angle, double radius) {
		this(Math.cos(angle), Math.sin(angle), -radius);	// = a, b, c
	}
	
	public HessianLine(double a, double b, double c) {
		super(a, b, c);	// creates a normalized line
		this.angle = Math.atan2(this.getB(), this.getA());
		this.radius = -this.getC(); // ... / Math.sqrt(sqr(this.a) + sqr(this.b));
		this.refPnt = Pnt2d.from(0, 0);
	}
	
	public HessianLine(AlgebraicLine L) {
		this(L.getA(), L.getB(), L.getC());
	}
	
	// getter methods ------------------------------------------
	
	public double getAngle() {
		return angle;
	}

	public double getRadius() {
		return radius;
	}
	
	@Override
	public double getXref() {
		return refPnt.getX();
	}
	
	@Override
	public double getYref() {
		return refPnt.getY();
	}
	
	// -------------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f>",
				this.getClass().getSimpleName(), angle, radius);
	}

}

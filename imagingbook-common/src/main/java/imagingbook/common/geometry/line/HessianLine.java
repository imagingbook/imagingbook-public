/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;

//import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * This class represents a straight line in Hessian normal form, i.e., 
 * x * cos(angle) + y * sin(angle) = radius.
 * It is a specialization (subclass) of {@link AlgebraicLine}.
 * Instances are immutable. Reference point is (0,0).
 */
public class HessianLine extends AlgebraicLine {
	
	public final double angle;
	public final double radius;
	
	// static factory methods ----------------------------------------
	
	public static HessianLine fromPoints(Pnt2d p1, Pnt2d p2) {
		return new HessianLine(AlgebraicLine.from(p1, p2));
	}
	
	// constructors --------------------------------------------------

	public HessianLine(double angle, double radius) {
		this(Math.cos(angle), Math.sin(angle), -radius);	// = A, B, C
	}
	
	public HessianLine(double A, double B, double C) {
		super(A, B, C);						// creates a normalized line
		this.angle = Math.atan2(this.B, this.A);
		this.radius = -this.C; 			// ... / Math.sqrt(sqr(this.a) + sqr(this.b));
	}
	
	public HessianLine(AlgebraicLine L) {
		this(L.A, L.B, L.C);
	}
	
	// -------------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f>",
				this.getClass().getSimpleName(), angle, radius);
	}

}

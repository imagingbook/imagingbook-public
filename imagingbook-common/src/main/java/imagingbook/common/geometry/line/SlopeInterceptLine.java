/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.line;

import java.util.Locale;

import imagingbook.common.math.Arithmetic;

/**
 * This class represents a line in slope-intercept form: y = k x + d.
 * Instances are immutable. Not all possible lines in the 2D plane
 * can be represented.
 * A {@link SlopeInterceptLine} is merely a {@link AlgebraicLine}
 * with a special constructor and getter methods for k, d.
 */
public class SlopeInterceptLine extends AlgebraicLine {
	
	public SlopeInterceptLine(double k, double d) {
		super(k, -1, d);
	}
	
	public SlopeInterceptLine(AlgebraicLine al) {
		super(al.getParameters());
		if (Arithmetic.isZero(al.B)) {
			throw new IllegalArgumentException("cannot convert vertical line (B=0)");
		}
	}
	
	public double getK() {
		return A / -B;	// = k
	}

	public double getD() {
		return C / -B;	// = d
	}
	
	/**
	 * Returns the line's y-value for the specified x-position.
	 * @param x position along the x-axis
	 * @return the associated y-value
	 */
	public double getY(double x) {
		return (A * x + C) / -B; 	// = y = k * x + d
	}
	
	// --------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <A=%.3f B=%.3f C=%.3f k=%.3f, d=%.3f>",
				this.getClass().getSimpleName(), A, B, C, getK(), getD());
	}
	
	// --------------------------------------------------
	
//	public static void main (String[] args) {
//		Pnt2d p1 = Pnt2d.from(1, 2);
//		Pnt2d p2 = Pnt2d.from(4, 3);
//		
//		AlgebraicLine al1 = AlgebraicLine.from(p1, p2);
//		System.out.println("al1 = " + al1);
//		
//		SlopeInterceptLine sl = new SlopeInterceptLine(al1);
//		System.out.println("sl = " + sl);
//		System.out.println("sl k = " + (sl.A / -sl.B));
//		System.out.println("sl d = " + (sl.C / -sl.B));
//		
//		AlgebraicLine al2 = AlgebraicLine.from(sl);
//		System.out.println("al2 = " + al2);
//		
//		System.out.println("al1 = al2 ? " + al1.equals(al2, 1e-6));
//	}
/*
al1 = AlgebraicLine <a=0.316, b=-0.949, c=1.581>
sl = SlopeInterceptLine <A=0.316 B=-0.949 C=1.581 k=0.333, d=1.667>
sl k = 0.33333333333333337
sl d = 1.6666666666666665
al2 = AlgebraicLine <a=0.316, b=-0.949, c=1.581>
al1 = al2 ? true
*/
}

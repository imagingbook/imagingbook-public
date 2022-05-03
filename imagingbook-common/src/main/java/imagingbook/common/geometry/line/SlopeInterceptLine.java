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

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;

/**
 * This class represents a line in slope-intercept form: y = k x + d.
 * Instances are immutable. Not all possible lines in the 2D plane
 * can be represented.
 * A {@link SlopeInterceptLine} is really a {@link AlgebraicLine}
 * with two additional fields and a special constructor.
 */
public class SlopeInterceptLine extends AlgebraicLine {
	
	private final double k, d;
	
	public SlopeInterceptLine(double k, double d) {
		super(k, -1, d);
		this.k = k;
		this.d = d;
	}
	
	public double getK() {
//		return k;
		return A / -B;
	}

	public double getD() {
//		return d;
		return C / - B;
	}
	
	public double[] getParameters() {
		return new double[] {k, d};
	}
	
	public static SlopeInterceptLine from(AlgebraicLine al) {
		double[] p = al.getParameters(); 	// =(A,B,C)
		if (Arithmetic.isZero(p[1])) { 		// B == 0?
			throw new IllegalArgumentException("cannot convert vertical line (B=0)");
		}
		return new SlopeInterceptLine(-p[0]/p[1], -p[2]/p[1]);	// =(-A/B, -C/B)
	}
	
	public double getY(double x) {
		return k * x + d;
	}
	
	// --------------------------------------------------
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof SlopeInterceptLine) {
			return this.equals((SlopeInterceptLine) other, Arithmetic.EPSILON_DOUBLE);
		}
		else {
			return false;
		}
	}
	
	public boolean equals(SlopeInterceptLine other, double tolerance) {
		return 
				Arithmetic.equals(k, other.k, tolerance) &&
				Arithmetic.equals(d, other.d, tolerance);
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <k=%.3f, d=%.3f> A=%.3f B=%.3f C=%.3f",
				this.getClass().getSimpleName(), k, d, A, B, C);
	}
	
	// --------------------------------------------------
	
	public static void main (String[] args) {
		Pnt2d p1 = Pnt2d.from(1, 2);
		Pnt2d p2 = Pnt2d.from(4, 3);
		
		AlgebraicLine al1 = AlgebraicLine.from(p1, p2);
		System.out.println("al1 = " + al1);
		
		SlopeInterceptLine sl = SlopeInterceptLine.from(al1);
		System.out.println("sl = " + sl);
		System.out.println("sl k = " + (sl.A / -sl.B));
		System.out.println("sl d = " + (sl.C / -sl.B));
		
		AlgebraicLine al2 = AlgebraicLine.from(sl);
		System.out.println("al2 = " + al2);
		
		System.out.println("al1 = al2 ? " + al1.equals(al2, 1e-6));
	}
	/*
	al1 = AlgebraicLine <a=-0.316, b=0.949, c=-1.581>
	sl = SlopeInterceptLine <k=0.333, d=1.667>
	al2 = AlgebraicLine <a=0.316, b=-0.949, c=1.581>
	al1 = al2 ? true
	*/
}

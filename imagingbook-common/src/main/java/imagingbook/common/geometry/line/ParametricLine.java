/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.line;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Matrix;

import java.util.Locale;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * This class represents a line in parametric form: x = s + t v, where s is a start point on the line, v is a direction
 * vector, and t is a real variable. Instances are immutable. See Sec. 10.1 and Appendix F.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/18
 */
public class ParametricLine {
	
	private final double[] s, v;
	
	public ParametricLine(double[] s, double[] v) {
		if (s.length != 2 || v.length != 2) {
			throw new IllegalArgumentException("vectors s, v must be of length 2");
		}
		if (Arithmetic.isZero(Matrix.normL2squared(v))) {
			throw new IllegalArgumentException("direction vector (v) must be nonzero");
		}
		this.s = s.clone();
		this.v = v.clone();
	}
	
	public double[] getS() {
		return s;
	}

	public double[] getV() {
		return v;
	}
	
	// --------------------------
	
	public static ParametricLine from(AlgebraicLine al) {
		double[] p = al.getParameters();
		// not needed, since algebraic lines are always normalized (i.e. A^2 + B^2 = 1)
		double norm2 = sqr(p[0]) + sqr(p[1]);		// = A^2 + B^2
		double scale = -p[2] / norm2;				// = -C / ||(A,B)||^2
		double[] s = {scale * p[0], scale * p[1]};	// = (-CA, -CB)
		double[] v = {-p[1], p[0]};					// = (-B, A)
		return new ParametricLine(s, v);
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <s=[%.3f, %.3f], v=[%.3f, %.3f]>",
				this.getClass().getSimpleName(), s[0], s[1], v[0], v[1]);
	}
	
	// --------------------------
	
	public static void main (String[] args) {
		Pnt2d p1 = Pnt2d.from(1, 2);
		Pnt2d p2 = Pnt2d.from(4, 3);
		
		AlgebraicLine al1 = AlgebraicLine.from(p1, p2);
		System.out.println("al1 = " + al1);
		
		ParametricLine pl = ParametricLine.from(al1);
		System.out.println("pl = " + pl);
		
		AlgebraicLine al2 = AlgebraicLine.from(pl);
		System.out.println("al2 = " + al2);
		
		System.out.println("al1 = al2 ? " + al1.equals(al2, 1e-6));
	}

	/*
	al1 = AlgebraicLine <a=-0.316, b=0.949, c=-1.581>
	pl = ParametricLine <s=[-0.500, 1.500], v=[-0.949, -0.316]>
	al2 = AlgebraicLine <a=0.316, b=-0.949, c=1.581>
	al1 = al2 ? true
	*/

}

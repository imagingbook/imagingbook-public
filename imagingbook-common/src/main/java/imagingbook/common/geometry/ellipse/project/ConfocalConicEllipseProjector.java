/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.ellipse.project;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import imagingbook.common.geometry.ellipse.GeometricEllipse;

/**
 * <p>
 * Calculates an approximate closest point on the ellipse for a given 2D point inside
 * or outside the ellipse, using "confocal conic distance approximation" [1].
 * See Sec. 11.2.3 (Alg. 11.12) and Appendix F.3.1 of [2] for details.
 * </p>
 * <p>
 * [1] P. L. Rosin. Ellipse fitting using orthogonal hyperbolae and stirling’s
 * oval. Graphical Models and Image Processing 60(3), 209–213 (1998). <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/17
 */
public class ConfocalConicEllipseProjector extends EllipseProjector {
	
	private final double ra, rb, ra2, rb2;
	
	public ConfocalConicEllipseProjector(GeometricEllipse ellipse) {
		super(ellipse);
		this.ra = ellipse.ra;
		this.rb = ellipse.rb;
		this.ra2 = sqr(ra);
		this.rb2 = sqr(rb);
	}
	
	// ellipse projection in quadrant 1 of u/v space
	@Override
	protected double[] projectCanonical(double[] uv) {
		// uv is supposed to be in quadrant 1 of canonical frame
		double u = uv[0];
		double v = uv[1];
		double u2 = sqr(u);
		double v2 = sqr(v);
		double fe2 = ra2 - rb2;
		double b = (u2 + v2 + fe2);
		double sa2 = 0.5 * (b - sqrt(sqr(b) - 4 * u2 * fe2));
		double sb2 = fe2 - sa2;	
		double c = 1 / sqrt(ra2 * sb2 + rb2 * sa2);	
		return new double[] {c * ra * sqrt(sa2 * (rb2 + sb2)), c * rb * sqrt(sb2 * (ra2 - sa2))};
	}
	
}

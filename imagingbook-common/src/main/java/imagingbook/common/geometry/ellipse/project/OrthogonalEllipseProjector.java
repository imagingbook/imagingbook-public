/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.ellipse.project;

import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.math.Arithmetic;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * Calculates the closest point on the ellipse for a given 2D point inside or outside the ellipse, using orthogonal
 * projection of points onto the ellipse. This is a robust algorithm based on [1]. See Sec.11.2.2 (Alg. 11.9) of [2] for
 * details. In contrast to the Newton-based algorithm (see {@link OrthogonalEllipseProjectorNewton}, this version uses
 * the bisection method for root finding and returns valid results for points close to the x- and y-axis but requires
 * significantly more iterations to converge.
 * </p>
 * <p>
 * [1] D. Eberly: "Distance from a point to an ellipse, an ellipsoid, or a hyperellipsoid", Technical Report, Geometric
 * Tools, www.geometrictools.com, Redmont, WA (June 2013). <br> [2] W. Burger, M.J. Burge, <em>Digital Image Processing
 * &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/04/09
 * @see OrthogonalEllipseProjectorNewton
 * @see ConfocalConicEllipseProjector
 */
public class OrthogonalEllipseProjector extends EllipseProjector {
	
	private static final int MaxIterations = 150;
	private static final double Epsilon = 1e-6;
	
	private final double ra, rb, rab;
	private int lastIterationCount = 0;	// number of root-finding iterations performed in last projection
	
	public OrthogonalEllipseProjector(GeometricEllipse ellipse) {
		super(ellipse);
		this.ra = ellipse.ra;
		this.rb = ellipse.rb;
		this.rab = sqr(ra / rb);	// ratio of squared axes lengths
	}
	
	@Override
	protected double[] projectCanonical(double[] u1) {
		// coordinates of p (mapped to first quadrant of canonical coordinates)
		final double u = u1[0];	// u,v are both positive
		final double v = u1[1];
		
		double[] ub = null;	// the ellipse contact point (in canonical coordinates)
		lastIterationCount = 0;
		
		if (v > 0) {
			if (u > 0) {
				double uu = u / ra;
				double vv = v / rb;
				double ginit = sqr(uu) + sqr(vv) - 1;
				if (!isZero(ginit)) {
					double s = getRoot(uu, vv, ginit);
					ub = new double[] {rab * u / (s + rab), v / (s + 1)};
				}
				else {
					ub = new double[] {u, v};
				}
			}
			else {	// u = 0
				ub = new double[] {0, rb};
			}
		}	
		else {	// v = 0
			double numer0 = ra * u;
			double denom0 = sqr(ra) - sqr(rb);
			if (numer0 < denom0) {
				double xde0 = numer0 / denom0;
				ub = new double[] {ra * xde0, rb * Math.sqrt(1 - sqr(xde0))};
			}
			else {
				ub = new double[] {ra, 0};
			}
		}
		return ub;
	}

	// Find the root of function
	// G(s) = [(rab * uu) / (s + rab)]^2 + [vv / (s + 1)]^2 - 1
	// using the bisection method.
	private double getRoot(double uu, double vv, double g0) {
		double s0 = vv - 1;
		double s1 = (g0 < 0) ? 0 : Math.hypot(rab * uu, vv) - 1;
		double s = 0;
		
		int i;
		for (i = 0; i < MaxIterations; i++) {
			s = (s0 + s1) / 2;
			if (Arithmetic.equals(s, s0) || Arithmetic.equals(s, s1)) {
				break;
			}
			double g = sqr((rab * uu)/(s + rab)) + sqr(vv/(s + 1)) - 1; // = G(s)
			if (g > Epsilon) {			// G(s) is positive
				s0 = s;
			}
			else if (g < -Epsilon) {	// G(s) is negative
				s1 = s;
			}
			else {						// G(s) ~ 0 (done)
				break;
			}
		}
		if (i >= MaxIterations) {
			throw new RuntimeException(this.getClass().getSimpleName() + 
					": max. iteration count exceeded");
		}
		lastIterationCount = i;
		return s;
	}
	
	// for statistics only
	public int getLastIterationCount() {
		return this.lastIterationCount;
	}

}
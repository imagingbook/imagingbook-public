/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.ellipse.project;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;

import imagingbook.common.geometry.ellipse.GeometricEllipse;

/**
 * <p>
 * Calculates the closest point on the ellipse for a given 2D point inside or
 * outside the ellipse, using orthogonal projection of points onto the ellipse.
 * This is a robust algorithm based on [1]. See Sec.11.2.2 (Alg. 11.9) of [2]
 * for details. This version uses the Newton-method for root finding, which is
 * quick but may fail to return a valid result if the target point is close to
 * the x- or y-axis. See {@link OrthogonalEllipseProjector} for a robust
 * solution or {@link ConfocalConicEllipseProjector} for an approximate but
 * non-iterative (i.e., fast) alternative.
 * </p>
 * <p>
 * [1] D. Eberly: "Distance from a point to an ellipse, an ellipsoid, or a
 * hyperellipsoid", Technical Report, Geometric Tools, www.geometrictools.com,
 * Redmont, WA (June 2013). <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/09
 * @see OrthogonalEllipseProjector
 * @see ConfocalConicEllipseProjector
 */
public class OrthogonalEllipseProjectorNewton extends EllipseProjector {
	
	private final static double NewtonMinStep = 1e-6;
	private final static int MaxIterations = 100;
	
	private final double ra, rb, ra2, rb2;
	private int lastIterationCount = 0;	// number of root-finding iterations performed in last projection
	
	public OrthogonalEllipseProjectorNewton(GeometricEllipse ellipse) {
		super(ellipse);
		this.ra = ellipse.ra;
		this.rb = ellipse.rb;
		this.ra2 = sqr(ra);
		this.rb2 = sqr(rb);
	}
	
	@Override
	protected double[] projectCanonical(double[] u1) {
		// coordinates of p (mapped to first quadrant)
		final double u = u1[0];	
		final double v = u1[1]; 
		
		double[] ub = null;	// the unknown ellipse point

		if (u + v < 1e-6) {	// (u,v) is very close to the ellipse center; u,v >= 0
			ub = new double[] {0, rb};
		}
		else {						
			double t = max(ra * u - ra2, rb * v - rb2);
			double gprev = Double.POSITIVE_INFINITY;
			double deltaT, deltaG;
			int k = 0;
			do {
				k = k + 1;
				double g  = sqr((ra * u) / (t + ra2)) + sqr((rb * v) / (t + rb2)) - 1;
				double dg = 2 * (sqr(ra * u) / pow(t + ra2, 3) + sqr(rb * v) / pow(t + rb2, 3));
				deltaT = g / dg;
				t = t + deltaT; 			// Newton iteration
				
				// in rare cases g(t) is very flat and checking deltaT is not enough for convergence!
				deltaG = g - gprev;			// change of g value
				gprev = g;	
				
			}  while(abs(deltaT) > NewtonMinStep && abs(deltaG) > NewtonMinStep && k < MaxIterations);
			
			lastIterationCount = k;		// remember iteration count
			
			if (k >= MaxIterations) {
				throw new RuntimeException("max. mumber of iterations exceeded");
			}
			
			ub = new double[] {ra2 * u / (t + ra2), rb2 * v / (t + rb2)};
		}
		return ub;
	}

	// for statistics only
	
	public int getLastIterationCount() {
		return this.lastIterationCount;
	}

}

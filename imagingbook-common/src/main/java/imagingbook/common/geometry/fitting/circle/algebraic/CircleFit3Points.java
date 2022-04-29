/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;


import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;

/**
 * Performs an exact circle fit to 3 given (non-collinear) points.
 * If the fit is unsuccessful, {@link #getParameters()} 
 * returns {@code null}.
 * 
 * @author WB
 *
 */
public class CircleFit3Points extends CircleFitAlgebraic {
	
	private final double[] p;
	
	public CircleFit3Points(Pnt2d p0, Pnt2d p1, Pnt2d p2) {
		final double x0 = p0.getX();
		final double y0 = p0.getY();
		final double x1 = p1.getX();
		final double y1 = p1.getY();
		final double x2 = p2.getX();
		final double y2 = p2.getY();
		
		final double A = x0 * (y1 - y2) - y0 * (x1 - x2) + x1 * y2 - x2 * y1;		
		if (Arithmetic.isZero(A)) {
			this.p = null;
		} 
		else {
			final double R0 = sqr(x0) + sqr(y0);
			final double R1 = sqr(x1) + sqr(y1);
			final double R2 = sqr(x2) + sqr(y2);
			final double B = R0 * (y2 - y1) + R1 * (y0 - y2) + R2 * (y1 - y0);
			final double C = R0 * (x1 - x2) + R1 * (x2 - x0) + R2 * (x0 - x1);
			final double D = R0 * (x2 * y1 - x1 * y2) + R1 * (x0 * y2 - x2 * y0) + R2 * (x1 * y0 - x0 * y1);	
			this.p = new double[] {A, B, C, D};
		}
	}
	
	public CircleFit3Points(Pnt2d[] pts) {
		this(pts[0], pts[1], pts[2]);
	}

	@Override
	public double[] getParameters() {
		return this.p;
	}

}

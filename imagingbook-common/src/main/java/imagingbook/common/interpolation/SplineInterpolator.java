/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.interpolation;

import imagingbook.common.image.access.ScalarAccessor;

public class SplineInterpolator implements PixelInterpolator {
	private final double a;	
	private final double b;

	
	public SplineInterpolator(ScalarAccessor ia) {
		this(0.5, 0.0); // default is a Catmull-Rom spline
	}
	
	public SplineInterpolator(double a, double b) {
		super();
		this.a = a;
		this.b = b;
	}
	
	
	@Override
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y) {
		final int u0 = (int) Math.floor(x);	//use floor to handle negative coordinates too
		final int v0 = (int) Math.floor(y);
		double q = 0;
		for (int j = 0, v = v0 - 1; j <= 3; j++, v++) {
//			int v = v0 + j - 1;
			double p = 0;
			for (int i = 0, u = u0 - 1; i <= 3; i++, u++) {
//				int u = u0 + i - 1;
				p = p + w_cs(x - u) * ia.getVal(u, v);
			}
			q = q + w_cs(y - v) * p;
		}
		return (float) q;
	}	
	
	private double w_cs(double x) {
		x = Math.abs(x);
		double w = 0;
		if (x < 1) 
			w = (-6*a - 9*b + 12) * x*x*x + (6*a + 12*b - 18) * x*x - 2*b + 6;
		else if (x < 2) 
			w = (-6*a - b) * x*x*x + (30*a + 6*b) * x*x + (-48*a - 12*b) * x + 24*a + 8*b;
		return w/6;
	}

}

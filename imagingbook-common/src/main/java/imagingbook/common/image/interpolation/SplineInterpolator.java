/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image.interpolation;

import imagingbook.common.image.access.ScalarAccessor;

/**
 * <p>
 * A {@link PixelInterpolator} implementing spline interpolation in 2D.
 * See Sec. 22.4 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @see CatmullRomInterpolator
 */
public class SplineInterpolator implements PixelInterpolator {
	private final double a;	
	private final double b;

	/**
	 * Constructor for creating a custom spline interpolator.
	 * @param a spline control parameter
	 * @param b spline control parameter
	 */
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
	
	@Override
	public double getWeight(double x) {
		return w_cs(x);
	}

}

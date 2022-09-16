/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.interpolation;

import imagingbook.common.image.access.ScalarAccessor;

/**
 * <p>
 * A {@link PixelInterpolator} implementing bicubic interpolation in 2D.
 * See Sec. 22.5.3 (Alg. 22.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class BicubicInterpolator implements PixelInterpolator {
	
	private final double a;		// sharpness factor
	
	/**
	 * Constructor using default sharpness factor a = 0.5;.
	 */
	public BicubicInterpolator() {
		this(0.5);
	}
	
	/**
	 * Constructor accepting a default sharpness factor a.
	 * @param a sharpness factor
	 */
	public BicubicInterpolator(double a) {
		this.a = a;
	}
	
	@Override
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y) {
		final int u0 = (int) Math.floor(x);
		final int v0 = (int) Math.floor(y);
		double q = 0;
		for (int j = 0, v = v0 - 1; j <= 3; j++, v++) {
//			int v = v0 - 1 + j;
			double p = 0;
			for (int i = 0, u = u0 - 1; i <= 3; i++, u++) {
//				int u = u0 - 1 + i;
				p = p + w_cub(x - u) * ia.getVal(u, v);
			}
			q = q + w_cub(y - v) * p;
		}
		return (float) q;
	}
	
	// 1D cubic interpolation
	private final double w_cub(double x) {
		x = Math.abs(x);
		double z = 0;
		if (x < 1)
			z = (-a + 2) * x * x * x + (a - 3) * x * x + 1;
		else if (x < 2)
			z = -a * x * x * x + 5 * a * x * x - 8 * a * x + 4 * a;
		return z;
	}

	@Override
	public double getWeight(double x) {
		return w_cub(x);
	}


}

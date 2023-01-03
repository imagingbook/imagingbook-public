/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.image.interpolation;

import imagingbook.common.image.access.ScalarAccessor;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * A {@link PixelInterpolator} implementing Lanczos interpolation in 2D. See Sec. 22.5.4 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class LanczosInterpolator implements PixelInterpolator {
	
	private final int n;	// order (tap count) of this interpolator
	private final int d; 	// kernel width
	
	/**
	 * Constructor creating a Lanczos interpolator of order n = 2.
	 */
	public LanczosInterpolator() {
		this(2);
	}
	
	/**
	 * Constructor creating a Lanczos interpolator of arbitrary order n &ge; 2.
	 * @param n order of the interpolator
	 */
	public LanczosInterpolator(int n) {
		if (n < 2) {
			throw new IllegalArgumentException("Lanczos order must be >= 2");
		}
		this.n = n; // order >= 2
		this.d = 2 * n - 1;
	}
	
	@Override
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y) {
		final int u0 = (int) Math.floor(x); // use floor to handle negative coordinates too
		final int v0 = (int) Math.floor(y);
		double q = 0;
		for (int j = 0; j <= d; j++) {
			int v = v0 + j - n + 1;
			double p = 0;
			for (int i = 0; i <= d; i++) {
				int u = u0 + i - n + 1;
				p = p + wLn(x - u) * ia.getVal(u, v);
			}
			q = q + wLn(y - v) * p;
		}
		return (float) q;
	}
	
	
	private static final double pi = Math.PI;
	private static final double pi2 = sqr(pi);
	
	private double wLn(double x) { // 1D Lanczos interpolator of order n
		final double r = Math.abs(x);
		if (r < 0.001) 
			return 1.0;
		if (r < n) {
			return n * (Math.sin(pi * r / n) * Math.sin(pi * r)) / (pi2 * sqr(r));
		}
		else 
			return 0.0;
	}

	@Override
	public double getWeight(double x) {
		return wLn(x);
	}


}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.interpolation;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.image.access.ScalarAccessor;


public class LanczosInterpolator implements PixelInterpolator {
	
	private final int n;	// order (tap count) of this interpolator
	
	public LanczosInterpolator(ScalarAccessor ia) {
		this(2);
	}
	
	public LanczosInterpolator(int n) {
		this.n = n; // order >= 2
	}
	
	@Override
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y) {
		final int u0 = (int) Math.floor(x); // use floor to handle negative coordinates too
		final int v0 = (int) Math.floor(y);
		double q = 0;
		for (int j = 0; j <= 2 * n - 1; j++) {
			int v = v0 + j - n + 1;
			double p = 0;
			for (int i = 0; i <= 2 * n - 1; i++) {
				int u = u0 + i - n + 1;
				p = p + wLn(x - u) * ia.getVal(u, v);
			}
			q = q + wLn(y - v) * p;
		}
		return (float) q;
	}
	
	
	static final double pi = Math.PI;
	static final double pi2 = sqr(pi);
	
	private double wLn(double x) { // 1D Lanczos interpolator of order n
		double r = Math.abs(x);
		if (r < 0.001) return 1.0;
		if (r < n) {
			return n * (Math.sin(pi * r / n) * Math.sin(pi * r)) / (pi2 * sqr(r));
		}
		else return 0.0;
	}


}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.image.interpolation;
import imagingbook.common.image.access.ScalarAccessor;

/**
 * <p>
 * A {@link PixelInterpolator} implementing bilinear interpolation in 2D. See Sec. 22.5.2 of [1] for additional
 * details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class BilinearInterpolator implements PixelInterpolator {
	
	/**
	 * Constructor.
	 */
	public BilinearInterpolator() {
	}

	@Override
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y) {
		final int u = (int) Math.floor(x);
		final int v = (int) Math.floor(y);
		
		final double a = x - u;	// a >= 0
		final double b = y - v;	// b >= 0
		
		final double A = ia.getVal(u, v);
		final double B = ia.getVal(u + 1, v);
		final double C = ia.getVal(u, v + 1);
		final double D = ia.getVal(u + 1, v + 1);
		
		final double E = A + a * (B - A);
		final double F = C + a * (D - C);
		final double G = E + b * (F - E);
		
		return (float) G;
	}

	/**
	 * Corresponds to function w_lin(x), see Eqn. 22.11 in [1].
	 * TODO: test, not used currently.
	 */
	@Override
	public double getWeight(double x) {
		x = Math.abs(x);
		return (x < 1) ? (1.0 - x) : 0.0;
	}
	
}

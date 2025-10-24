/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.image.interpolation;
import imagingbook.common.image.access.ScalarAccessor;


/**
 * <p>
 * A {@link PixelInterpolator} implementing nearest-neighbor interpolation in 2D. See Sec. 22.5.1 of [1] for additional
 * details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class NearestNeighborInterpolator implements PixelInterpolator {
	
	/**
	 * Constructor.
	 */
	public NearestNeighborInterpolator() {
	}
	
	@Override
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y) {
		final int u = (int) Math.rint(x);
		final int v = (int) Math.rint(y);
		return ia.getVal(u, v);
	}

	/**
	 * Corresponds to function w_nn(x), see Eqn. 22.10 in [1].
	 * TODO: test, not used currently.
	 */
	@Override
	public double getWeight(double x) {
		if (-0.5 <= x && x < 0.5)
			return 1;
		else
			return 0;
	}
	
}

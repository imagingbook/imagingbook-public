/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.geometry.mappings.linear;

import imagingbook.common.math.Arithmetic;

/**
 * <p>
 * This class represents a 2D scaling transformation (as a special case of affine transformation). See Secs. 21.1.3 and
 * 21.3.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Scaling2D extends AffineMapping2D {
	
	public Scaling2D(Scaling2D sc) {
		super(sc);
	}

	/**
	 * Constructor. Creates a mapping that scales along the x- and y-axis by the associated factors.
	 *
	 * @param sx the scale factor in x-direction
	 * @param sy the scale factor in y-direction
	 */
	public Scaling2D(double sx, double sy) {
		super(
			checkZero(sx), 0,  0,
			0,  checkZero(sy), 0);
	}
	
	private static double checkZero(double s) {
		if (Arithmetic.isZero(s)) {
			throw new IllegalArgumentException("zero scale parameter " + s);
		}
		return s;
	}
	
	/**
	 * Constructor. Creates a uniform scaling in x and y.
	 * @param s the common scale factor
	 */
	public Scaling2D(double s) {
		this(s, s);
	}

	/**
	 * {@inheritDoc}
	 * @return a new scaling transformation
	 */
	@Override
	public Scaling2D getInverse() {
		return new Scaling2D(1/a00, 1/a11);
	}
	
	/**
	 * {@inheritDoc}
	 * @return a new scaling transformation
	 */
	@Override
	public Scaling2D duplicate() {
		return new Scaling2D(this);
	}
}



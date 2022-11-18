/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.geometry.mappings.linear;

/**
 * <p>
 * This class represents a 2D shear transformation (as a special case of affine
 * transformation). See Secs. 21.1.3 and 21.3.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public class Shear2D extends AffineMapping2D {
	
	/**
	 * Constructor Creates a shear transform.
	 * @param bx shear factor in x-direction
	 * @param by shear factor in y-direction
	 */
	public Shear2D(double bx, double by) {
		super( // calls constructor of AffineMapping
			1,  bx, 0,
			by, 1,  0);
	}
	
	public Shear2D(Shear2D sh) {
		super(sh);
	}
	
	/**
	 * {@inheritDoc}
	 * @return a new shear mapping
	 */
	@Override
	public Shear2D duplicate() {
		return new Shear2D(this);
	}
	
}



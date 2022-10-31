/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image.interpolation;

/**
 * <p>
 * A {@link PixelInterpolator} implementing Catmull-Rom interpolation in 2D
 * See Sec. 22.4.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @see SplineInterpolator
 */
public class CatmullRomInterpolator extends SplineInterpolator {

	/**
	 * Constructor.
	 */
	public CatmullRomInterpolator() {
		super(0.5, 0.0);
	}	

}

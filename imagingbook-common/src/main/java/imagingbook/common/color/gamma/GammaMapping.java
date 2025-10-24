/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.gamma;

/**
 * Interface to be implemented by gamma correction classes. All in/out component values are assumed to be in [0,1].
 * Implementing classes must at least define a {@code double} version of the required methods but are free to override
 * the associated {@code float} versions.
 *
 * @author WB
 * @version 2022/11/16
 */
public interface GammaMapping {
	
	/**
	 * Forward Gamma mapping (from linear to non-linear component values).
	 * 
	 * @param a linear component value in [0,1]
	 * @return the gamma-corrected (non-linear) component value
	 */
	public double applyFwd(double a);

	/**
	 * Float version of {@link #applyFwd(double)}
	 *
	 * @param a linear component value in [0,1]
	 * @return the gamma-corrected (non-linear) component value
	 */
	public default float applyFwd(float a) {
		return (float) applyFwd((double) a);
	}
	
	/**
	 * Inverse Gamma mapping (from non-linear to linear component values).
	 * 
	 * @param b non-linear (Gamma-corrected) component value in [0,1]
	 * @return the linear component value
	 */
	public double applyInv(double b);
	
	/**
     * Float version of {@link #applyInv(double)}
     * @param b non-linear (Gamma-corrected) component value in [0,1]
     * @return the linear component value
     */
	public default float applyInv(float b) {
		return (float) applyInv((double) b);
	}
	
}

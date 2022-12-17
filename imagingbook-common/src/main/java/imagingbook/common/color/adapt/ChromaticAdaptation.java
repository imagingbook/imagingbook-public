/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.adapt;

/**
 * Interface to be implemented by chromatic adaptation transforms.
 * 
 * @author WB
 */
public interface ChromaticAdaptation {

	/**
	 * Transforms the specified XYZ source color coordinates to target coordinates.
	 * The specified color coordinates are interpreted relative to (source) white point (W1).
	 * Returns a new color adapted to (target) white point W2.
	 * 
	 * @param xyz the original color point w.r.t. the source white point (W1)
	 * @return the associated color w.r.t. the target white point (W2).
	 */
	public float[] applyTo(float[] xyz);
	
	/**
	 * Double version of {@link #applyTo(float[])}.
	 * 
	 * @param xyz the original color point w.r.t. the source white point (W1)
	 * @return the associated color w.r.t. the target white point (W2).
	 */
	public double[] applyTo(double[] xyz);

}

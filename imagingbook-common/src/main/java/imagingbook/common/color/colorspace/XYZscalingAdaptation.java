/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

/**
 * <p>
 * This class represents a linear chromatic adaptation transform, mapping XYZ
 * color coordinates from a source white (reference) point to a target white
 * point. Both white points are passed to the constructor. The actual color
 * mapping is done by method {@link #applyTo(float[])}. See the Section 14.6 of
 * [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/14
 * @see BradfordAdaptation
 */
public class XYZscalingAdaptation implements ChromaticAdaptation {
	
	private final double[] W21;		// vector with diagonal scale factors

	/**
	 * Returns a {@link XYZscalingAdaptation} instance for the specified
	 * white point coordinates.
	 * 
	 * @param W1 source white point (to map from)
	 * @param W2 target white point (to map to)
	 * @return a {@link XYZscalingAdaptation} instance
	 */
	public static XYZscalingAdaptation getInstance(double[] W1, double[] W2) {
		return new XYZscalingAdaptation(W1, W2);
	}
	
	/**
	 * Returns a {@link XYZscalingAdaptation} instance for the specified
	 * illuminants (white points).
	 * 
	 * @param illum1 source illuminant (white point to map from)
	 * @param illum2 target illuminant (white point to map to)
	 * @return a {@link XYZscalingAdaptation} instance
	 */
	public static XYZscalingAdaptation getInstance(Illuminant illum1, Illuminant illum2) {
		return getInstance(illum1.getXYZ(), illum2.getXYZ());
	}
	
	/** Constructor (non-public). */
	private  XYZscalingAdaptation(double[] XYZ1, double[] XYZ2) {
		W21 = new double[3];
		for (int i = 0; i < 3; i++) {
			W21[i] = XYZ2[i] / XYZ1[i];
		}
	}

	@Override
	public float[] applyTo(float[] XYZ1) {
		final float[] XYZ2 = new float[3];
		for (int i = 0; i < 3; i++) {
			XYZ2[i] = (float) (XYZ1[i] * W21[i]);
		}
		return XYZ2;
	}

	@Override
	public double[] applyTo(double[] XYZ1) {
		final double[] XYZ2 = new double[3];
		for (int i = 0; i < 3; i++) {
			XYZ2[i] = XYZ1[i] * W21[i];
		}
		return XYZ2;
	}

}

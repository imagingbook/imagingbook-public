/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

/**
 * <p>
 * This class represents a linear chromatic adaptation transform,
 * mapping XYZ color coordinates from a source white (reference) point
 * to a target white point. 
 * Both white points are passed to the constructor.
 * The actual color mapping is done by method {@link #applyTo(float[])}.
 * See the Section 14.6 of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 */
public class XYZscalingAdaptation implements ChromaticAdaptation {
	
	private final double[] W21;

	/**
	 * Constructor accepting two white points (XYZ-coordinates).
	 * @param W1 source white point
	 * @param W2 target white point
	 */
	public XYZscalingAdaptation(double[] W1, double[] W2) {
		W21 = new double[3];
		for (int i = 0; i < 3; i++) {
			W21[i] = W2[i] / W1[i];
		}
	}
	
	/**
	 * Constructor accepting two {@link Illuminant} instances for
	 * specifying the source and target white points.
	 * @param illum1 source illuminant
	 * @param illum2 target illuminant
	 */
	public XYZscalingAdaptation(Illuminant illum1, Illuminant illum2) {
		this(illum1.getXYZ(), illum2.getXYZ());
	}

	@Override
	public float[] applyTo(float[] XYZ1) {
		final float[] XYZ2 = new float[3];
		for (int i = 0; i < 3; i++) {
			XYZ2[i] = (float) (XYZ1[i] * W21[i]);
		}
		return XYZ2;
	}

}

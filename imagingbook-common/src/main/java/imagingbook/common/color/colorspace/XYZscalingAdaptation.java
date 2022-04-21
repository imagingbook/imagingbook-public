/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;


public class XYZscalingAdaptation implements ChromaticAdaptation {
	
	private final double[] W21;

	public XYZscalingAdaptation(double[] W1, double[] W2) {
		W21 = new double[3];
		for (int i = 0; i < 3; i++) {
			W21[i] = W2[i] / W1[i];
		}
	}
	
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

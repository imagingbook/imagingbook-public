/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import imagingbook.common.color.adapt.BradfordAdaptation;
import imagingbook.common.color.adapt.ChromaticAdaptation;

import static imagingbook.common.color.cie.StandardIlluminant.D50;
import static imagingbook.common.color.cie.StandardIlluminant.D65;

/**
 * A color space implementing this interface indicates that it can convert internal color values directly to and from
 * D65-based XYZ coordinates, without going through the standard (D50-based) Profile Conversion Space (PCS). This is
 * useful for converting between D65-based color spaces since it avoids additional conversions to and from D50-based
 * PCS. The interface specifies methods {@link #toCIEXYZ65(float[])} and {@link #fromCIEXYZ65(float[])} for direct
 * conversion between the color space's native color values and D65 XYZ-coordinates, respectively.
 *
 * @author WB
 * @version 2022/11/15
 */
public interface DirectD65Conversion {
	
	static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	
	// methods required by all implementations of ColorSpace:
	public float[] toCIEXYZ(float[] value);
	public float[] fromCIEXYZ(float[] value);

	// -----------------------------------------------------------------

	/**
	 * Analogous to {@link #fromCIEXYZ(float[])} but accepts D65-based instead of D50-based PCS color values. D65-based
	 * color spaces should override this method to avoid the additional D65 to D50 conversion.
	 *
	 * @param xyz65 D65-based XYZ color values
	 * @return color values in this color space
	 */
	public float[] fromCIEXYZ65(float[] xyz65);
	// public default float[] fromCIEXYZ65(float[] xyz65) {
	// 	float[] XYZ50 = catD65toD50.applyTo(xyz65);
	// 	return this.fromCIEXYZ(XYZ50);
	// }

	/**
	 * Analogous to {@link #toCIEXYZ(float[])} but returns D65-based instead of D50-based PCS color values. D65-based
	 * color spaces should override this method to avoid the additional D50 to D65 conversion.
	 *
	 * @param value color value in this color space
	 * @return D65-based XYZ color values
	 */
	public float[] toCIEXYZ65(float[] value);
	// public default float[] toCIEXYZ65(float[] value) {
	// 	float[] XYZ50 = this.toCIEXYZ(value);
	// 	return catD50toD65.applyTo(XYZ50);
	// }
	
}

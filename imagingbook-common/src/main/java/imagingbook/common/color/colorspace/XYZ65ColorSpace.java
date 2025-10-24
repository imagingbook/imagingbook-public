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

import java.awt.color.ColorSpace;

import static imagingbook.common.color.cie.StandardIlluminant.D50;
import static imagingbook.common.color.cie.StandardIlluminant.D65;

/**
 * <p>
 * Implementation of a D65-based XYZ color space, as a substitute for Java's built-in standard connection space
 * (obtained with {@code  ColorSpace.getInstance(ColorSpace.CS_CIEXYZ)}), with improved accuracy. The reference white
 * point of this color space is D50 and any XYZ coordinate passed to {@link #fromCIEXYZ(float[])} is assumed to be
 * relative to D50 as well, thus no conversion (color adaptation) is necessary between XYZ values. See Sec. 14.1.1 of
 * [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/14
 * @see XYZ50ColorSpace
 */
@SuppressWarnings("serial")
public class XYZ65ColorSpace extends ColorSpace implements DirectD65Conversion {
	private static final sRGB65ColorSpace srgbCS = sRGB65ColorSpace.getInstance();
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	private static final XYZ65ColorSpace instance = new XYZ65ColorSpace();

	public static XYZ65ColorSpace getInstance() {
		return instance;
	}

	private XYZ65ColorSpace() {
		super(TYPE_XYZ, 3);
	}

	// ----------------------------------------------------

	@Override	// convert this D65-based XYZ to sRGB
	public float[] toRGB(float[] thisXYZ65) {
		float[] XYZ50 = this.toCIEXYZ(thisXYZ65);	// could be done directly!!
		float[] srgb = srgbCS.fromCIEXYZ(XYZ50);
		return srgb;
	}

	@Override
	public float[] fromRGB(float[] srgb) {
		float[] XYZ50 = srgbCS.toCIEXYZ(srgb);
		return this.fromCIEXYZ(XYZ50);
	}
	
	// -------------------------------------------------
	
	@Override	// convert this CS D65 XYZ to standard D50 XYZ
	public float[] toCIEXYZ(float[] thisXYZ65) {
		return catD65toD50.applyTo(thisXYZ65);
	}
	
	@Override	// convert standard D50 XYZ to this CS D65 XYZ
	public float[] fromCIEXYZ(float[] XYZ50) {
		return catD50toD65.applyTo(XYZ50);
	}
	
	// -------------------------------------------------

	@Override
	public float[] fromCIEXYZ65(float[] XYZ65) {
		return XYZ65;
	}

	@Override
	public float[] toCIEXYZ65(float[] XYZ65) {
		return XYZ65;
	}

}

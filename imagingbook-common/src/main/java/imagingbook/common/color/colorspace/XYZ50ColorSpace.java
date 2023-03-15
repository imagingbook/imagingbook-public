/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

/**
 * <p>
 * Implementation of a D50-based XYZ color space, as a substitute for Java's built-in standard connection space
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
 * @see XYZ65ColorSpace
 */
@SuppressWarnings("serial")
public class XYZ50ColorSpace extends ColorSpace implements DirectD65Conversion {
	private static sRGB65ColorSpace srgbCS = sRGB65ColorSpace.getInstance();
	
	private static final XYZ50ColorSpace instance = new XYZ50ColorSpace();
	
	public static XYZ50ColorSpace getInstance() {
		return instance;
	}
	
	private XYZ50ColorSpace() {
		super(TYPE_XYZ, 3);
	}

	// -------------------------------------------------

	@Override	// convert (D50-based) thisXyz to sRGB
	public float[] toRGB(float[] thisXyz) {
		return srgbCS.fromCIEXYZ(thisXyz);
	}

	@Override	// convert sRGB to (D50-based) thisXyz
	public float[] fromRGB(float[] srgb) {
		float[] thisXyz = srgbCS.toCIEXYZ(srgb);
		return thisXyz;
	}
	
	// -------------------------------------------------

	@Override
	public float[] toCIEXYZ(float[] thisXyz) {
		return thisXyz;	// nothing to do since D50-based XYZ already
	}

	@Override
	public float[] fromCIEXYZ(float[] xyz50) {
		return xyz50;		// nothing to do, since D50-based XYZ is what we want
	}
	
}

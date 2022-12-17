/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import java.awt.color.ICC_ColorSpace;

import imagingbook.common.color.cie.NamedIccProfile;
import imagingbook.common.color.cie.StandardIlluminant;
import imagingbook.common.math.Matrix;


/**
 * <p>
 * This color space class is based on the "AdobeRGB1998.icc" profile. It only
 * serves as an example for creating color spaces from ICC profiles.
 * See Sec. 14.5 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; 
 * An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/13
 */
@SuppressWarnings("serial")
public class AdobeRgbColorSpace extends ICC_ColorSpace implements DirectD65Conversion, RgbReferenceData {

	private static AdobeRgbColorSpace instance = null;
	
	private AdobeRgbColorSpace() {
		super(NamedIccProfile.AdobeRGB1998.getProfile());	// constructor of ICC_ColorSpace
	}

	/**
	 * Returns an instance of {@link AdobeRgbColorSpace}.
	 * @return an instance of {@link AdobeRgbColorSpace}
	 */
	public static AdobeRgbColorSpace getInstance() {
		if (instance == null) {
			instance = new AdobeRgbColorSpace();
		}
		return instance;
	}
	
	// Taken from Adobe RGB (1998) Color Image Encoding (Version 2005-05)
	private static final double[][] Mrgbi = 
		{{0.57667, 0.18556, 0.18823},
		 {0.29734, 0.62736, 0.07529},
		 {0.02703, 0.07069, 0.99134}};
	
	@Override
	public float[] getWhitePoint() {
		// (0.9505, 1.0000, 1.0891) 
		return Matrix.toFloat(StandardIlluminant.D65.getXYZ());
	}
	
	@Override
	public float[] getPrimary(int idx) {
		return Matrix.toFloat(Matrix.getColumn(Mrgbi, idx));
	}

}

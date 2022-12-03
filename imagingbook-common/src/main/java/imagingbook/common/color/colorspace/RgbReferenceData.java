/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.color.colorspace;

/**
 * This interface specifies methods for retrieving XYZ coordinates of white
 * point and RGB primaries from the implementing color space.
 * 
 * @author WB
 * @version 2022/11/15
 */
public interface RgbReferenceData {
	
//	public float[] toCIEXYZ(float[] value);
//	public float[] fromCIEXYZ(float[] value);
//	public float[] toRGB(float[] value);
//	public float[] fromRGB(float[] value);
	
	/**
	 * Returns the XYZ coordinates of the white point
	 * associated with this color space (typically D65 or D50).
	 * 
	 * @return the white point
	 */
	public float[] getWhitePoint();
	
	/**
	 * Returns the XYZ color coordinates for the primary color with the specified
	 * index, measured relative to the white point of this color space (see
	 * {@link #getWhitePoint()}).
	 * 
	 * @param idx the color index (R = 0, G = 1, B = 2)
	 * @return the XYZ coordinate for the primary color
	 */
	public float[] getPrimary(int idx) ;
	
}

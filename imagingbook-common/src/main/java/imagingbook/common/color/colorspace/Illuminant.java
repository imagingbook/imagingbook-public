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
 * Enumeration compiling selected illuminants and the associated coordinates
 * in CIE-XYZ color space.
 * 
 * @author WB
 *
 */
public enum Illuminant {
	/** Equal energy illuminant (5400K). */
	E	(1/3.0, 1/3.0),						// 5400K, Equal energy (x = y = 1/3)
	/** D50 standard illuminant (5000K). */
	D50	(0.964296, 1.00000, 0.825100),		// 5000K
	/** D55 standard illuminant (5500K). */
	D55 (0.33411, 0.34877),					// 5500K
	/** D65 standard illuminant used for television and sRGB color space (6500K). */
	D65 (0.950456, 1.00000, 1.088754),		// 6500K, Television, sRGB color space
	/** D75 standard illuminant (7500K). */
	D75 (0.29968, 0.31740),					// 7500K
	/** Incandescent tungsten (2856K). */
	A	(0.45117, 0.40594),					// 2856K, Incandescent tungsten
	/** Obsolete, direct sunlight at noon (4874K). */
	B	(0.3498, 0.3527),					// 4874K, Obsolete, direct sunlight at noon
	/** Obsolete, north sky daylight (6774K). */
	C	(0.31039, 0.31905),					// 6774K, Obsolete, north sky daylight 
	/** Cool White Fluorescent CWF (4200K). */
	F2	(0.37928, 0.36723),					// 4200K, Cool White Fluorescent (CWF)
	/** Broad-Band Daylight Fluorescent (6500K). */
	F7	(0.31565, 0.32951),					// 6500K, Broad-Band Daylight Fluorescent 
	/** Narrow Band White Fluorescent (4000K). */
	F11	(0.38543, 0.37110);					// 4000K, Narrow Band White Fluorescent 
	
	private final double X, Y, Z;
	
	private Illuminant(double X, double Y, double Z) {
		this.X = X; this.Y = Y; this.Z = Z;
	}
	
	private Illuminant(double x, double y) {
		Y = 1.0;
		X = x * Y / y; 
		Z = (1.0 - x - y) * Y / y;
	}
	
	// ----------------------------------------------

	/**
	 * Returns the illuminant's X-coordinate in CIE-XYZ color space.
	 * @return the X-coordinate
	 */
	public double getX() {
		return X;
	}

	/**
	 * Returns the illuminant's Y-coordinate in CIE-XYZ color space.
	 * @return the Y-coordinate
	 */
	public double getY() {
		return Y;
	}

	/**
	 * Returns the illuminant's Z-coordinate in CIE-XYZ color space.
	 * @return the Z-coordinate
	 */
	public double getZ() {
		return Z;
	}

	/**
	 * Returns the illuminant's coordinate in CIE-XYZ color space.
	 * @return the XYZ-coordinate
	 */
	public double[] getXYZ() {
		return new double[] {X, Y, Z};
	}
	
}

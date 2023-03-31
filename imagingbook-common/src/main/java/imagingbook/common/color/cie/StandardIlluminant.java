/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.cie;

import imagingbook.common.math.Arithmetic;

/**
 * <p>
 * Enumeration of selected standard illuminants. See Sec. 14.1.3 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public enum StandardIlluminant implements Illuminant {
	/** Equal energy illuminant, neutral point (5400K). */
	N	(1.0/3, 1.0/3),
	/** D50 standard illuminant (5000K). */
	D50 (0.3457, 0.3585),
	/** D55 standard illuminant (5500K). */
	D55 (0.33411, 0.34877),	
	/** D65 standard illuminant used for television and sRGB color space (6500K). */
	D65 (0.3127, 0.3290),
	/** D75 standard illuminant (7500K). */
	D75 (0.29968, 0.31740),
	/** Incandescent tungsten (2856K). */
	A	(0.45117, 0.40594),
	/** Obsolete, direct sunlight at noon (4874K). */
	B	(0.3498, 0.3527),
	/** Obsolete, north sky daylight (6774K). */
	C	(0.31039, 0.31905),
	/** Cool White Fluorescent CWF (4200K). */
	F2	(0.37928, 0.36723),
	/** Broad-Band Daylight Fluorescent (6500K). */
	F7	(0.31565, 0.32951),
	/** Narrow Band White Fluorescent (4000K). */
	F11	(0.38543, 0.37110);

	private final double x, y;
	private final double X, Y, Z;
	
	private StandardIlluminant(double x, double y) {
		if (Arithmetic.isZero(y)) {
			throw new IllegalArgumentException("illuminant y cannot be zero");
		}
		this.x = x;
		this.y = y;
		double[] XYZ = CieUtils.xyYToXYZ(x, y, 1);
		this.X = XYZ[0];
		this.Y = XYZ[1];
		this.Z = XYZ[2];
		// this.Y = 1.0;
		// this.X = x * this.Y / y;
		// this.Z = (1.0 - x - y) * this.Y / y;
	}
	
	@Override
	public double[] getXYZ() {
		return new double[] {X, Y, Z};
	}

	@Override
	public double[] getXy() {
		return new double[] {x, y};
	}

	
}

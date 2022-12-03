/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.color.colorspace;

import static imagingbook.common.color.cie.StandardIlluminant.D50;
import static imagingbook.common.color.cie.StandardIlluminant.D65;

import java.awt.color.ColorSpace;

import imagingbook.common.color.adapt.BradfordAdaptation;
import imagingbook.common.color.adapt.ChromaticAdaptation;
import imagingbook.common.color.cie.StandardIlluminant;


/**
 * <p>
 * This class implements the CIELab color space. See Sec. 14.2 of [1] for
 * details. All component values are assumed to be in [0,1]. Conversion from/to
 * sRGB is implemented directly through D65-based XYZ coordinates, i.e., without
 * conversion to Java's D50-based profile connection space. The methods
 * fromCIEXYZ/toCIEXYZ still return D50-based XYZ coordinates in Java's profile
 * connection space. This is a singleton class with no public constructors, use
 * {@link #getInstance()} to obtain the single instance.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/01
 */
@SuppressWarnings("serial")
public class LabColorSpace extends ColorSpace implements DirectD65Conversion {
	private static final sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();
	private static final LabColorSpace instance = new LabColorSpace();
	
	public static LabColorSpace getInstance() {
		return instance;
	}

	// D65 reference white point:
	private static final double[] XYZref = StandardIlluminant.D65.getXYZ();

	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);

	/**
	 * Constructor.
	 */
	private LabColorSpace() {
		super(TYPE_Lab, 3);
	}

	// XYZ50 -> CIELab: returns Lab values from XYZ (relative to D50)
	/**
	 * Converts color points in D50-based XYZ space to CIELab coordinates.
	 * This method implements {@link ColorSpace#fromCIEXYZ(float[])}), assuming that 
	 * the specified color coordinate is in D50-based XYZ space (with components in [0,1]).
	 * See also {@link #fromCIEXYZ65(float[])} for a D65-based version.
	 * 
	 * @param XYZ50 a color in D50-based XYZ space (components in [0,1])
	 * @return the associated CIELab color
	 */
	@Override
	public float[] fromCIEXYZ(float[] XYZ50) {	
		float[] XYZ65 = catD50toD65.applyTo(XYZ50);
		return this.fromCIEXYZ65(XYZ65);
	}

	// XYZ65 -> CIELab: returns Lab values from XYZ (relative to D65)
	@Override
	public float[] fromCIEXYZ65(float[] XYZ65) {
		double xx = f1(XYZ65[0] / XYZref[0]);	
		double yy = f1(XYZ65[1] / XYZref[1]);
		double zz = f1(XYZ65[2] / XYZref[2]);
		double L = 116.0 * yy - 16.0;
		double a = 500.0 * (xx - yy);
		double b = 200.0 * (yy - zz);
		return new float[] {(float)L, (float)a, (float)b};
	}

	// CIELab -> XYZ50: returns XYZ values (relative to D50) from Lab
	@Override // returns D50-based XYZ from Lab values
	public float[] toCIEXYZ(float[] Lab) {
		float[] XYZ65 = this.toCIEXYZ65(Lab);
		return catD65toD50.applyTo(XYZ65);
	}

	// CIELab -> XYZ65: returns XYZ values (relative to D65) from Lab
	@Override
	public float[] toCIEXYZ65(float[] Lab) {
		double ll = (Lab[0] + 16.0) / 116.0;
		double Y65 = XYZref[1] * f2(ll);
		double X65 = XYZref[0] * f2(ll + Lab[1] / 500.0);
		double Z65 = XYZref[2] * f2(ll - Lab[2] / 200.0);
		return new float[] {(float)X65, (float)Y65, (float)Z65};
	}

	//sRGB -> CIELab (direct, without adaptation to D50)
	@Override
	public float[] fromRGB(float[] srgb) {
		float[] xyz65 = srgbCS.toCIEXYZ65(srgb);
		return this.fromCIEXYZ65(xyz65);
	}

	//CIELab -> sRGB (direct, without adaptation to D50)
	@Override
	public float[] toRGB(float[] Lab) {
		float[] XYZ65 = this.toCIEXYZ65(Lab);
		return srgbCS.fromCIEXYZ65(XYZ65);
	}

	//---------------------------------------------------------------------

	private static final double Epsilon = 216.0/24389;
	private static final double Kappa = 841.0/108;

	// Gamma correction for L* (forward)
	private double f1 (double c) {
		return (c > Epsilon) ? Math.cbrt(c) : (Kappa * c) + (16.0 / 116);
	}

	// Gamma correction for L* (inverse)
	private double f2 (double c) {
		double c3 = c * c * c; //Math.pow(c, 3.0);
		return (c3 > Epsilon) ? c3 : (c - 16.0 / 116) / Kappa;
	}

}

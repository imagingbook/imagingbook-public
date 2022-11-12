/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;

import java.awt.color.ColorSpace;

import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;


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
public class LabColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;
	private static final sRgbColorSpace sRGBcsp = sRgbColorSpace.getInstance();
	
	private static final LabColorSpace instance = new LabColorSpace();
	
	public static LabColorSpace getInstance() {
		return instance;
	}

	// D65 reference white point:
	private static final double[] XYZref = D65.getXYZ();

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
	@Override  // returns Lab values from D50-based XYZ
	public float[] fromCIEXYZ(float[] XYZ50) {	
		float[] XYZ65 = catD50toD65.applyTo(XYZ50);
		return fromCIEXYZ65(XYZ65);
	}

	// XYZ65 -> CIELab: returns Lab values from XYZ (relative to D65)
	/**
	 * Converts color points in D65-based XYZ space to CIELab coordinates.
	 * See also {@link #fromCIEXYZ(float[])}.
	 * @param XYZ65 a color in D65-based XYZ space (components in [0,1])
	 * @return the associated CIELab color
	 */
	public float[] fromCIEXYZ65(float[] XYZ65) {
		double xx = f1(XYZ65[0] / XYZref[0]);	
		double yy = f1(XYZ65[1] / XYZref[1]);
		double zz = f1(XYZ65[2] / XYZref[2]);
		float L = (float)(116.0 * yy - 16.0);
		float a = (float)(500.0 * (xx - yy));
		float b = (float)(200.0 * (yy - zz));
		return new float[] {L, a, b};
	}

	// CIELab -> XYZ50: returns XYZ values (relative to D50) from Lab
	/**
	 * Converts the specified CIELab color to D50-based XYZ coordinates.
	 * This method implements {@link ColorSpace#toCIEXYZ(float[])}) assuming that
	 * the XYZ color coordinate is D50-based.
	 * See also {@link #toCIEXYZ65(float[])} for a D65-based version.
	 * @param Lab CIELab color
	 * @return XYZ coordinates (D50-based)
	 */
	@Override // returns D50-based XYZ from Lab values
	public float[] toCIEXYZ(float[] Lab) {
		float[] XYZ65 = toCIEXYZ65(Lab);
		return catD65toD50.applyTo(XYZ65);
	}

	// CIELab -> XYZ65: returns XYZ values (relative to D65) from Lab
	/**
	 * Converts the specified CIELab color to D65-based XYZ coordinates.
	 * @param Lab CIELab color
	 * @return XYZ coordinates (D65-based)
	 */
	public float[] toCIEXYZ65(float[] Lab) {
		double ll = ( Lab[0] + 16.0 ) / 116.0;
		float Y65 = (float) (XYZref[1] * f2(ll));
		float X65 = (float) (XYZref[0] * f2(ll + Lab[1] / 500.0));
		float Z65 = (float) (XYZref[2] * f2(ll - Lab[2] / 200.0));
		return new float[] {X65, Y65, Z65};
	}

	//sRGB -> CIELab (direct, without adaptation to D50)
	/**
	 * Transforms a sRGB color value to a CIELab color.
	 * This method implements {@link ColorSpace#fromRGB(float[])} assuming
	 * D65-based sRGB color coordinates.
	 * Note that sRGB values are assumed to be in [0,1] (see
	 * {@link RgbUtils#normalize(int...)} for conversion from [0,255] int-values).
	 * @param srgb a sRGB color (D65-based)
	 * @return the associated CIELab color
	 */
	@Override
	public float[] fromRGB(float[] srgb) {
//		float[] XYZ50 = sRGBcsp.toCIEXYZ(srgb);
//		return this.fromCIEXYZ(XYZ50);
		float[] XYZ65 = sRGBcsp.toCIEXYZ65(srgb);
//		System.out.println("XYZ65 = " + Matrix.toString(XYZ65));
		return this.fromCIEXYZ65(XYZ65);
	}

	//CIELab -> sRGB (direct, without adaptation to D50)
	/**
	 * Transforms a CIELab color value to a sRGB color
	 * This method implements {@link ColorSpace#toRGB(float[])} assuming 
	 * D65-based sRGB color coordinates.
	 * Note that the returned RGB values are in [0,1] (see
	 * {@link RgbUtils#unnormalize(float[])} for conversion to [0,255] int-values).
	 * @param Lab a CIELab color
	 * @return sRGB coordinates (D65-based)
	 */
	@Override
	public float[] toRGB(float[] Lab) {
//		float[] XYZ50 = toCIEXYZ(Lab);
//		return sRGBcsp.fromCIEXYZ(XYZ50);
		float[] XYZ65 = toCIEXYZ65(Lab);
		return sRGBcsp.fromCIEXYZ65(XYZ65);
	}

	//---------------------------------------------------------------------

	private static final double epsilon = 216.0/24389;
	private static final double kappa = 841.0/108;

	// Gamma correction for L* (forward)
	private double f1 (double c) {
		return (c > epsilon) ? Math.cbrt(c) : (kappa * c) + (16.0 / 116);
	}

	// Gamma correction for L* (inverse)
	private double f2 (double c) {
		double c3 = c * c * c; //Math.pow(c, 3.0);
		return (c3 > epsilon) ? c3 : (c - 16.0 / 116) / kappa;
	}

	//---------------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(6);
		LabColorSpace cs = LabColorSpace.getInstance();
		float[] lab = cs.fromCIEXYZ65(Matrix.toFloat(XYZref));
		System.out.println("XYZref = " + Matrix.toString(XYZref));
		System.out.println("Lab = " + Matrix.toString(lab));
		float[] xyz = cs.toCIEXYZ65(lab);
		System.out.println("XYZ = " + Matrix.toString(xyz));
	}
}

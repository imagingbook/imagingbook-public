/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.Illuminant.D50;
import static imagingbook.common.color.colorspace.Illuminant.D65;

import java.awt.color.ColorSpace;


/**
 * This class implements the CIELab color space.
 * Conversion from/to sRGB is implemented directly through D65-based XYZ
 * coordinates, i.e., without conversion to Java's D50-based profile 
 * connection space. The methods fromCIEXYZ/toCIEXYZ still return D50-based XYZ 
 * coordinates in Java's profile connection space.
 * 
 * @author W. Burger
 * @version 2022/09/01
 */
@SuppressWarnings("serial")
public class LabColorSpace extends ColorSpace {

	// D65 reference white point:
	private static final double Xref = D65.getX(); 	// 0.950456
	private static final double Yref = D65.getY(); 	// 1.000000
	private static final double Zref = D65.getZ();	// 1.088754

	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = new BradfordAdaptation(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = new BradfordAdaptation(D50, D65);

	
	/**
	 * Constructor.
	 */
	public LabColorSpace() {
		super(TYPE_Lab, 3);
	}

	// XYZ50 -> CIELab: returns Lab values from XYZ (relative to D50)
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: This implementation (required by {@link ColorSpace}) assumes that 
	 * the specified color coordinate is in D50-based XYZ space (with components in [0,1]).
	 * See also {@link #fromCIEXYZ65(float[])} for a D65-based version.
	 * </p>
	 */
	@Override
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
		double xx = f1(XYZ65[0] / Xref);	
		double yy = f1(XYZ65[1] / Yref);
		double zz = f1(XYZ65[2] / Zref);
		float L = (float)(116.0 * yy - 16.0);
		float a = (float)(500.0 * (xx - yy));
		float b = (float)(200.0 * (yy - zz));
		return new float[] {L, a, b};
	}

	// CIELab -> XYZ50: returns XYZ values (relative to D50) from Lab
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: This implementation (required by {@link ColorSpace}) converts a
	 * CIELab color to a color coordinate in D50-based XYZ space.
	 * See also {@link #toCIEXYZ65(float[])} for a D65-based version.
	 * </p>
	 */
	@Override
	public float[] toCIEXYZ(float[] Lab) {
		float[] XYZ65 = toCIEXYZ65(Lab);
		return catD65toD50.applyTo(XYZ65);
	}

	// CIELab -> XYZ65: returns XYZ values (relative to D65) from Lab
	/**
	 * Converts the specified CIELab color to D65-based XYZ coordinates.
	 * @param Lab CIELab color
	 * @return D65-based XYZ coordinates
	 */
	public float[] toCIEXYZ65(float[] Lab) {
		double ll = ( Lab[0] + 16.0 ) / 116.0;
		float Y65 = (float) (Yref * f2(ll));
		float X65 = (float) (Xref * f2(ll + Lab[1] / 500.0));
		float Z65 = (float) (Zref * f2(ll - Lab[2] / 200.0));
		return new float[] {X65, Y65, Z65};
	}

	//sRGB -> CIELab (direct, without adaptation to D50)
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: This implementation (required by {@link ColorSpace}) converts a
	 * D65-based sRGB color coordinate assumed to the associated
	 * CIELab color.
	 * </p>
	 */
	@Override
	public float[] fromRGB(float[] srgb) {
		float[] XYZ65 = new sRgb65ColorSpace().toCIEXYZ(srgb);
		return fromCIEXYZ65(XYZ65);
	}

	//CIELab -> sRGB (direct, without adaptation to D50)
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: This implementation (required by {@link ColorSpace}) converts a
	 * CIELab color to a D65-based sRGB color coordinate.
	 * </p>
	 */
	@Override
	public float[] toRGB(float[] Lab) {
		float[] XYZ65 = toCIEXYZ65(Lab);
		float[] srgb = new sRgb65ColorSpace().fromCIEXYZ(XYZ65);
		return srgb;
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

}

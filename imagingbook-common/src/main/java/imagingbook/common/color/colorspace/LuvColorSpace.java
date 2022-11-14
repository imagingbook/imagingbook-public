/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

import imagingbook.common.color.RgbUtils;


/**
 * <p>
 * This class implements the CIELuv color space. See Sec. 14.2 of [1] for
 * additional details. All component values are assumed to be in [0,1].
 * Conversion from/to sRGB is implemented directly through D65-based XYZ
 * coordinates, i.e., without conversion to Java's D50-based profile connection
 * space. The methods fromCIEXYZ/toCIEXYZ still return D50-based XYZ coordinates
 * in Java's profile connection space. This is a singleton class with no public
 * constructors, use {@link #getInstance()} to obtain the single instance.
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
public class LuvColorSpace extends CustomColorSpace {
	private static final sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();
	private static final LuvColorSpace instance = new LuvColorSpace();
	
	public static LuvColorSpace getInstance() {
		return instance;
	}
		
	// D65 reference white point:
	private static final double[] XYZref = StandardIlluminant.D65.getXYZ();
	private static final double uuref = fu(XYZref[0], XYZref[1], XYZref[2]); // u'_n
	private static final double vvref = fv(XYZref[0], XYZref[1], XYZref[2]); // v'_n
	
	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(StandardIlluminant.D65, StandardIlluminant.D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(StandardIlluminant.D50, StandardIlluminant.D65);
	
	/**
	 * Constructor.
	 */
	private LuvColorSpace() {
		super(TYPE_Luv,3);
	}
	
	// XYZ50->CIELuv: returns Luv values from XYZ (relative to D50)
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: This implementation (required by {@link ColorSpace}) assumes that 
	 * the specified color coordinate is in D50-based XYZ space (with components in [0,1]).
	 * See also {@link #fromCIEXYZ65(double[])} for a D65-based version.
	 * </p>
	 */
	@Override
	public double[] fromCIEXYZ(double[] XYZ50) {	
		double[] XYZ65 = catD50toD65.applyTo(XYZ50);
		return this.fromCIEXYZ65(XYZ65);
	}
	
	// XYZ65->CIELuv: returns Luv values from XYZ (relative to D65)
	/**
	 * Converts color points in D65-based XYZ space to CIELuv coordinates.
	 * See also {@link #fromCIEXYZ(float[])}.
	 * @param XYZ65 a color in D65-based XYZ space (components in [0,1])
	 * @return the associated CIELuv color
	 */
	@Override
	public double[] fromCIEXYZ65(double[] XYZ65) {	
		double X = XYZ65[0];
		double Y = XYZ65[1];	
		double Z = XYZ65[2];
		double YY = f1(Y / XYZref[1]);  	// Y'
		double uu = fu(X,Y,Z); 		// u'
		double vv = fv(X,Y,Z); 		// v'
		double L = (116.0 * YY - 16.0); 		//L*
		double u = (13 * L * (uu - uuref));  	//u*
		double v = (13 * L * (vv - vvref));  	//v*
		return new double[] {L, u, v};
	}
	
	// CIELab->XYZ50: returns XYZ values (relative to D50) from Luv
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: This implementation (required by {@link ColorSpace}) converts a
	 * CIELuv color to a color coordinate in D50-based XYZ space.
	 * See also {@link #toCIEXYZ65(double[])} for a D65-based version.
	 * </p>
	 */
	@Override
	public double[] toCIEXYZ(double[] Luv) {
		double[] XYZ65 = this.toCIEXYZ65(Luv);
		return catD65toD50.applyTo(XYZ65);
	}
	
	/**
	 * Converts the specified CIELuv color to D65-based XYZ coordinates.
	 * @param Luv CIELuv color
	 * @return the associated D65-based XYZ coordinates
	 */
	@Override
	public double[] toCIEXYZ65(double[] Luv) {
		double L = Luv[0];
		double u = Luv[1];
		double v = Luv[2];
		double Y = XYZref[1] * f2((L + 16) / 116.0);
		double uu = (L < 0.00001) ? uuref : u / (13 * L) + uuref; // u'
		double vv = (L < 0.00001) ? vvref : v / (13 * L) + vvref; // v'
		double X = Y * ((9*uu)/(4*vv));
		double Z = Y * ((12 - 3 * uu - 20 * vv) / (4 * vv));
		double[] XYZ65 = new double[] {X, Y, Z};
		return XYZ65;
	}
	
	//sRGB->CIELuv
	/**
	 * Transforms a sRGB color value to a CIELuv color.
	 * This method implements {@link ColorSpace#fromRGB(float[])} assuming
	 * D65-based sRGB color coordinates.
	 * Note that sRGB values are assumed to be in [0,1] (see
	 * {@link RgbUtils#normalize(int...)} for conversion from [0,255] int-values).
	 * @param srgb a sRGB color (D65-based)
	 * @return the associated CIELuv color
	 */
	@Override
	public double[] fromRGB(double[] srgb) {
		double[] XYZ65 = srgbCS.toCIEXYZ65(srgb);
		return this.fromCIEXYZ65(XYZ65);
	}
	
	//CIELuv->sRGB
	/**
	 * Transforms a CIELuv color value to a sRGB color
	 * This method implements {@link ColorSpace#toRGB(float[])} assuming 
	 * D65-based sRGB color coordinates.
	 * Note that the returned RGB values are in [0,1] (see
	 * {@link RgbUtils#unnormalize(float[])} for conversion to [0,255] int-values).
	 * @param Luv a CIELuv color
	 * @return sRGB coordinates (D65-based)
	 */
	@Override
	public double[] toRGB(double[] Luv) {
		double[] XYZ65 = this.toCIEXYZ65(Luv);
		double[] srgb = srgbCS.fromCIEXYZ65(XYZ65);
		return srgb;
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
		final double c3 = c * c * c; //Math.pow(c, 3.0);
		return (c3 > Epsilon) ? c3 : (c - 16.0 / 116) / Kappa;
	}
	
	private static double fu (double X, double Y, double Z) { // X,Y,Z must be positive
		// fails if 0.001 is used!
		return (X < 0.00001) ? 0 : (4 * X) / (X + 15 * Y + 3 * Z);
	}
	
	private static double fv (double X, double Y, double Z) { // X,Y,Z must be positive
		return (Y < 0.00001) ? 0 : (9 * Y) / (X + 15 * Y + 3 * Z);
	}

}

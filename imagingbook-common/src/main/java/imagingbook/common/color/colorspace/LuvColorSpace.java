/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.colorspace;

import imagingbook.common.color.adapt.BradfordAdaptation;
import imagingbook.common.color.adapt.ChromaticAdaptation;
import imagingbook.common.color.cie.StandardIlluminant;

import java.awt.color.ColorSpace;


/**
 * <p>
 * This class implements the CIELuv color space. See Sec. 14.2 of [1] for additional details. All component values are
 * assumed to be in [0,1]. Conversion from/to sRGB is implemented directly through D65-based XYZ coordinates, i.e.,
 * without conversion to Java's D50-based profile connection space. The methods fromCIEXYZ/toCIEXYZ still return
 * D50-based XYZ coordinates in Java's profile connection space. This is a singleton class with no public constructors,
 * use {@link #getInstance()} to obtain the single instance.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/01
 */
@SuppressWarnings("serial")
public class LuvColorSpace extends ColorSpace implements DirectD65Conversion {
	private static final sRGB65ColorSpace srgbCS = sRGB65ColorSpace.getInstance();
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
	
	/** Constructor (non-public). */
	private LuvColorSpace() {
		super(TYPE_Luv,3);
	}
	
	// XYZ50->CIELuv: returns Luv values from XYZ (relative to D50)
	@Override
	public float[] fromCIEXYZ(float[] XYZ50) {	
		float[] XYZ65 = catD50toD65.applyTo(XYZ50);
		return this.fromCIEXYZ65(XYZ65);
	}
	
	// XYZ65->CIELuv: returns Luv values from XYZ (relative to D65)
	@Override
	public float[] fromCIEXYZ65(float[] XYZ65) {	
		double X = XYZ65[0];
		double Y = XYZ65[1];	
		double Z = XYZ65[2];
		double YY = f1(Y / XYZref[1]);  	// Y'
		double uu = fu(X,Y,Z); 		// u'
		double vv = fv(X,Y,Z); 		// v'
		double L = (116.0 * YY - 16.0); 		//L*
		double u = (13 * L * (uu - uuref));  	//u*
		double v = (13 * L * (vv - vvref));  	//v*
		return new float[] {(float)L, (float)u, (float)v};
	}
	
	// CIELab->XYZ50: returns XYZ values (relative to D50) from Luv
	@Override
	public float[] toCIEXYZ(float[] Luv) {
		float[] XYZ65 = this.toCIEXYZ65(Luv);
		return catD65toD50.applyTo(XYZ65);
	}
	
	// CIELab->XYZ65
	@Override
	public float[] toCIEXYZ65(float[] Luv) {
		double L = Luv[0];
		double u = Luv[1];
		double v = Luv[2];
		double Y = XYZref[1] * f2((L + 16) / 116.0);
		double uu = (L < 0.00001) ? uuref : u / (13 * L) + uuref; // u'
		double vv = (L < 0.00001) ? vvref : v / (13 * L) + vvref; // v'
		double X = Y * ((9*uu)/(4*vv));
		double Z = Y * ((12 - 3 * uu - 20 * vv) / (4 * vv));
		return new float[] {(float)X, (float)Y, (float)Z};
	}
	
	//sRGB -> CIELuv
	@Override
	public float[] fromRGB(float[] srgb) {
		float[] XYZ65 = srgbCS.toCIEXYZ65(srgb);
		return this.fromCIEXYZ65(XYZ65);
	}
	
	//CIELuv -> sRGB
	@Override
	public float[] toRGB(float[] Luv) {
		float[] XYZ65 = this.toCIEXYZ65(Luv);
		float[] srgb = srgbCS.fromCIEXYZ65(XYZ65);
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

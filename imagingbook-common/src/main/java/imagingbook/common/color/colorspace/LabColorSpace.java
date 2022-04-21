/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
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
 * @version 2022/01/28
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

	// the only constructor:
	public LabColorSpace() {
		super(TYPE_Lab, 3);
	}

	// XYZ50->CIELab: returns Lab values from XYZ (relative to D50)
	@Override
	public float[] fromCIEXYZ(float[] XYZ50) {	
		float[] XYZ65 = catD50toD65.applyTo(XYZ50);	
		return fromCIEXYZ65(XYZ65);
	}

	// XYZ65->CIELab: returns Lab values from XYZ (relative to D65)
	public float[] fromCIEXYZ65(float[] XYZ65) {
		double xx = f1(XYZ65[0] / Xref);	
		double yy = f1(XYZ65[1] / Yref);
		double zz = f1(XYZ65[2] / Zref);
		float L = (float)(116.0 * yy - 16.0);
		float a = (float)(500.0 * (xx - yy));
		float b = (float)(200.0 * (yy - zz));
		return new float[] {L, a, b};
	}

	// CIELab->XYZ50: returns XYZ values (relative to D50) from Lab
	@Override
	public float[] toCIEXYZ(float[] Lab) {
		float[] XYZ65 = toCIEXYZ65(Lab);
		return catD65toD50.applyTo(XYZ65);
	}

	// CIELab->XYZ65: returns XYZ values (relative to D65) from Lab
	public float[] toCIEXYZ65(float[] Lab) {
		double ll = ( Lab[0] + 16.0 ) / 116.0;
		float Y65 = (float) (Yref * f2(ll));
		float X65 = (float) (Xref * f2(ll + Lab[1] / 500.0));
		float Z65 = (float) (Zref * f2(ll - Lab[2] / 200.0));
		return new float[] {X65, Y65, Z65};
	}

	//sRGB->CIELab (direct, without adaptation to D50)
	@Override
	public float[] fromRGB(float[] srgb) {
		// get linear rgb components:
		double r = sRgbUtil.gammaInv(srgb[0]);
		double g = sRgbUtil.gammaInv(srgb[1]);
		double b = sRgbUtil.gammaInv(srgb[2]);

		// convert to XYZ (D65-based, Poynton/ITU709) 
		float X = (float) (0.412453 * r + 0.357580 * g + 0.180423 * b);
		float Y = (float) (0.212671 * r + 0.715160 * g + 0.072169 * b);
		float Z = (float) (0.019334 * r + 0.119193 * g + 0.950227 * b);

		float[] XYZ65 = new float[] {X, Y, Z}; 
		return fromCIEXYZ65(XYZ65);
	}

	//CIELab->sRGB (direct, without adaptation to D50)
	@Override
	public float[] toRGB(float[] Lab) {
		float[] XYZ65 = toCIEXYZ65(Lab);
		double X = XYZ65[0];
		double Y = XYZ65[1];
		double Z = XYZ65[2];
		// XYZ -> RGB (linear components)
		double r = (3.240479 * X + -1.537150 * Y + -0.498535 * Z);
		double g = (-0.969256 * X + 1.875992 * Y + 0.041556 * Z);
		double b = (0.055648 * X + -0.204043 * Y + 1.057311 * Z);

		// RGB -> sRGB (nonlinear components)
		float rr = (float) sRgbUtil.gammaFwd(r);
		float gg = (float) sRgbUtil.gammaFwd(g);
		float bb = (float) sRgbUtil.gammaFwd(b);

		return new float[] {rr,gg,bb} ; //sRGBcs.fromCIEXYZ(XYZ50);
	}

	//---------------------------------------------------------------------

	private static final double epsilon = 216.0/24389;
	private static final double kappa = 841.0/108;

	// Gamma correction for L* (forward)
	private double f1 (double c) {
		if (c > epsilon) // 0.008856
			return Math.cbrt(c);
		else
			return (kappa * c) + (16.0 / 116);
	}

	// Gamma correction for L* (inverse)
	private double f2 (double c) {
		double c3 = c * c * c; //Math.pow(c, 3.0);
		if (c3 > epsilon)
			return c3;
		else
			return (c - 16.0 / 116) / kappa;
	}

	// ----------------------------------------------------------------------

	// moved to tests:
//	public static void main(String[] args) {	// TODO: move to tests
//		int sr = 128;
//		int sg = 1;
//		int sb = 128;
//		System.out.format("Input (sRGB) = %d, %d, %d\n", sr, sg, sb);
//		System.out.format("XYZref = %10g, %10g, %10g\n", Xref,Yref,Zref);
//
//		ColorSpace cs = new LabColorSpace();
//		//float[] luv = cs.fromCIEXYZ(new float[] {.1f,.5f,.9f});
//		float[] lab = cs.fromRGB(new float[] {sr/255f, sg/255f, sb/255f});
//
//		System.out.format("Lab = %8f, %8f, %8f\n", lab[0],lab[2],lab[2]);
//		//float[] xyz = cs.toCIEXYZ(luv);
//		float[] srgb = cs.toRGB(lab);
//		System.out.format("sRGB = %8f, %8f, %8f\n", 
//				Math.rint(255*srgb[0]), Math.rint(255*srgb[1]), Math.rint(255*srgb[2]));
//	}
}

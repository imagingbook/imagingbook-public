/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.Illuminant.D65;

import java.awt.color.ColorSpace;


/**
 * This class implements the CIELuv color space.
 * Conversion from/to sRGB is implemented directly through D65-based XYZ
 * coordinates, i.e., without chromatic adaptation to Java's D50-based profile 
 * connection space. The methods fromCIEXYZ/toCIEXYZ still return D50-based XYZ 
 * coordinates in Java's profile connection space.
 * 
 * @author W. Burger
 * @version 2022/01/28
 */
@SuppressWarnings("serial")
public class LuvColorSpace extends ColorSpace {
		
	// D65 reference white point:
	private static final double Xref = D65.getX(); 	// 0.950456
	private static final double Yref = D65.getY(); 	// 1.000000
	private static final double Zref = D65.getZ();	// 1.088754
	
	private static final double uuref = fu(Xref, Yref, Zref); // u'_n
	private static final double vvref = fv(Xref, Yref, Zref); // v'_n
	
	// chromatic adaptation objects:
	private static final ChromaticAdaptation catD65toD50 = new BradfordAdaptation(Illuminant.D65, Illuminant.D50);
	private static final ChromaticAdaptation catD50toD65 = new BradfordAdaptation(Illuminant.D50, Illuminant.D65);
	
	public LuvColorSpace() {
		super(TYPE_Lab,3);
	}
	
	// XYZ50->CIELuv: returns Luv values from XYZ (relative to D50)
	@Override
	public float[] fromCIEXYZ(float[] XYZ50) {	
		float[] XYZ65 = catD50toD65.applyTo(XYZ50);
		return fromCIEXYZ65(XYZ65);
	}
	
	// XYZ65->CIELuv: returns Luv values from XYZ (relative to D65)
	private float[] fromCIEXYZ65(float[] XYZ65) {	
		double X = XYZ65[0];
		double Y = XYZ65[1];	
		double Z = XYZ65[2];
		double YY = f1(Y / Yref);  	// Y'
		double uu = fu(X,Y,Z); 		// u'
		double vv = fv(X,Y,Z); 		// v'
		float L = (float)(116.0 * YY - 16.0); 		//L*
		float u = (float)(13 * L * (uu - uuref));  	//u*
		float v = (float)(13 * L * (vv - vvref));  	//v*
		return new float[] {L, u, v};
	}
	
	// CIELab->XYZ50: returns XYZ values (relative to D50) from Luv
	@Override
	public float[] toCIEXYZ(float[] Luv) {
		float[] XYZ65 = toCIEXYZ65(Luv);
		return catD65toD50.applyTo(XYZ65);
	}
	
	private float[] toCIEXYZ65(float[] Luv) {
		double L = Luv[0];
		double u = Luv[1];
		double v = Luv[2];
		float Y = (float) (Yref * f2((L + 16) / 116.0));
		double uu = (L<0.00001) ? uuref : u / (13 * L) + uuref; // u'
		double vv = (L<0.00001) ? vvref : v / (13 * L) + vvref; // v'
		float X = (float) (Y * ((9*uu)/(4*vv)));
		float Z = (float) (Y * ((12 - 3 * uu - 20 * vv) / (4 * vv)));
		float[] XYZ65 = new float[] {X, Y, Z};
		return XYZ65;
	}
	
	//sRGB->CIELuv
	@Override
	public float[] fromRGB(float[] srgb) {
		// get linear rgb components:
		double r = sRgbUtil.gammaInv(srgb[0]);
		double g = sRgbUtil.gammaInv(srgb[1]);
		double b = sRgbUtil.gammaInv(srgb[2]);
		
		// convert to XYZ (Poynton / ITU 709) 
		float X = (float) (0.412453 * r + 0.357580 * g + 0.180423 * b);
		float Y = (float) (0.212671 * r + 0.715160 * g + 0.072169 * b);
		float Z = (float) (0.019334 * r + 0.119193 * g + 0.950227 * b);
		
		float[] XYZ65 = new float[] {X, Y, Z}; 
		return fromCIEXYZ65(XYZ65);
	}
	
	//CIELuv->sRGB
	@Override
	public float[] toRGB(float[] Luv) {
		float[] XYZ65 = toCIEXYZ65(Luv);
		double X = XYZ65[0];
		double Y = XYZ65[1];
		double Z = XYZ65[2];
		// XYZ -> RGB (linear components)
		double r = ( 3.240479 * X + -1.537150 * Y + -0.498535 * Z);
		double g = (-0.969256 * X +  1.875992 * Y +  0.041556 * Z);
		double b =  (0.055648 * X + -0.204043 * Y +  1.057311 * Z);

//		if (RgbGamutChecker.checkOutOfGamut(r, g, b) && RgbGamutChecker.markOutOfGamutColors) { // REMOVE ************************** !!	
//			r = RgbGamutChecker.oGred; 
//			g = RgbGamutChecker.oGgrn; 
//			b = RgbGamutChecker.oGblu;
//		}
		
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
	
	private static double fu (double X, double Y, double Z) { // X,Y,Z must be positive
		if (X < 0.00001)	// fails if 0.001 is used!
			return 0;
		else
			return (4 * X) / (X + 15 * Y + 3 * Z);
	}
	
	private static double fv (double X, double Y, double Z) { // X,Y,Z must be positive
		if (Y < 0.00001)
			return 0;
		else
			return (9 * Y) / (X + 15 * Y + 3 * Z);
	}
	
	//---------------------------------------------------------------------
	
	// moved to tests:
	 
//    public static void main(String[] args) {
//    	int sr = 128;
//    	int sg = 1;
//    	int sb = 128;
//    	System.out.format("Input (sRGB) = %d, %d, %d\n", sr, sg, sb);
//    	System.out.format("XYZref = %f, %f, %f\n", Xref, Yref, Zref);
//    	
//    	LuvColorSpace cs = new LuvColorSpace();
//    	//float[] luv = cs.fromCIEXYZ(new float[] {.1f,.5f,.9f});
//    	float[] luv = cs.fromRGB(new float[] {sr/255f, sg/255f, sb/255f});
//
//    	System.out.format("Luv = %.2f, %.2f, %.2f\n", luv[0],luv[2],luv[2]);
//    	//float[] xyz = cs.toCIEXYZ(luv);
//    	float[] srgb = cs.toRGB(luv);
//    	System.out.format("sRGB = %.2f, %.2f, %.2f\n", 
//    			Math.rint(255*srgb[0]), Math.rint(255*srgb[1]), Math.rint(255*srgb[2]));
//    	
//    }

}

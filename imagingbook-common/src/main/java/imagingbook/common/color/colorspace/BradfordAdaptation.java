/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import static imagingbook.common.math.Matrix.multiply;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

/**
 * <p>
 * This class represents a linear chromatic adaptation transform,
 * mapping XYZ color coordinates from a source white (reference) point
 * to a target white point. 
 * Both white points are passed to the constructor.
 * The actual color mapping is done by method {@link #applyTo(float[])}.
 * The underlying linear transformation is specified by a 3x3 matrix,
 * which may be retrieved by {@link #getAdaptationMatrix()}).
 * See the Section 14.6 of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; 
 * An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 */
public class BradfordAdaptation implements ChromaticAdaptation {
	
	// CAT forward transform matrix from XYZ to "virtual" RGB scaling coordinates
	private static final double[][] MCAT = {
	    { 0.8951,  0.2664, -0.1614},
	    {-0.7502,  1.7135,  0.0367},
	    { 0.0389, -0.0685,  1.0296}};
	   
	// CAT inverse transform matrix from "virtual" RGB scaling coordinates back to XYZ
	private static final double[][] MCATi = Matrix.inverse(MCAT);	  // we always invert for precision reasons
//		{{ 0.9869929054667120, -0.1470542564209900, 0.1599626516637310}, 
//		 { 0.4323052697233940,  0.5183602715367770, 0.0492912282128560}, 
//		 {-0.0085286645751770,  0.0400428216540850, 0.9684866957875500}}
	
	// the complete color adaptation transformation matrix for transforming source XYZ to target XYZ,
	// XYZ2 = Madapt * XYZ1, with
	// Madapt = MCAT^-1 * DiagRGB * MCAT
	private final double[][] Madapt;
	
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Constructor accepting two white points (XYZ-coordinates).
	 * 
	 * @param W1 source white point
	 * @param W2 target white point
	 */
	public BradfordAdaptation(double[] W1, double[] W2) {
		double[] rgb1 = multiply(MCAT, W1);
		double[] rgb2 = multiply(MCAT, W2);
		double[][] Mscale = rgbMatrix(rgb1, rgb2);
		Madapt = multiply(MCATi, multiply(Mscale, MCAT));
	}
	
	/**
	 * Constructor accepting two {@link Illuminant} instances for
	 * specifying the source and target white points.
	 * 
	 * @param illum1 source illuminant
	 * @param illum2 target illuminant
	 */
	public BradfordAdaptation(Illuminant illum1, Illuminant illum2) {
		this(illum1.getXYZ(), illum2.getXYZ());
	}
	
	// ---------------------------------------------------------------------------------
	
	public static double[][] getMCAT() {
		return MCAT;
	}
	
	public static double[][] getMCATi() {
		return MCATi;
	}
	
	// ---------------------------------------------------------------------------------
	
	// transformation of color coordinates
	@Override
	public float[] applyTo(float[] xyz) {
		// XYZ2 = Mcat . XYZ1
		float[] XYZ2 = new float[3];
		for (int i = 0; i < 3; i++) {
			XYZ2[i] = (float) (Madapt[i][0] * xyz[0] + Madapt[i][1] * xyz[1] + Madapt[i][2] * xyz[2]);
		}
		return XYZ2;
	}
	
	/**
	 * Returns the composite color adaptation transformation matrix.
	 * @return the color adaptation transformation matrix
	 */
	public double[][] getAdaptationMatrix() {
		return this.Madapt;
	}
	

	// Creates a diagonal matrix with the ratios of the rgb components
	// obtained by transforming the two white points
	private double[][] rgbMatrix(double[] rgb1, double[] rgb2) {
		if (rgb1.length != rgb2.length) {
			throw new IllegalArgumentException();
		}
		final int n = rgb1.length;
		double[][] M = new double[n][n];
		for (int i = 0; i < n; i++) {
			M[i][i] = rgb2[i] / rgb1[i];
		}
		return M;
	}
	
	// ------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(16);
		System.out.println("MCATi = \n" + Matrix.toString(MCATi));
		double[][] X = Matrix.multiply(MCAT, MCATi);
		System.out.println("MCAT * MCATi = \n" + Matrix.toString(X));
		
		
		
		BradfordAdaptation adapt = new BradfordAdaptation(StandardIlluminant.D65, StandardIlluminant.D50);	// adapts from D65 -> D50
		
		System.out.println("Mcat = \n" + Matrix.toString(adapt.getAdaptationMatrix()));
		System.out.println();
		
		
//		
//		ColorSpace cs = sRgb65ColorSpace.getInstance();
//		float[] red = {1, 0, 0};
//		float[] grn = {0, 1, 0};
//		float[] blu = {0, 0, 1};
//		
//		float[] rgb1 = blu;
//		
//		System.out.println("rgb1 = " + Matrix.toString(rgb1));
//		float[] XYZ65 = cs.toCIEXYZ(rgb1);
//		System.out.println("XYZ65 = " + Matrix.toString(XYZ65));
//		
//		double[] xy65 = CieUtil.XYZToXy(Matrix.toDouble(XYZ65));
//		System.out.println("xy65 = " + Matrix.toString(xy65));
//		
//		float[] XYZ50 = adapt.applyTo(XYZ65);
//		System.out.println("XYZ50 = " + Matrix.toString(XYZ50));
//		
//		double[] xy50 = CieUtil.XYZToXy(Matrix.toDouble(XYZ50));
//		System.out.println("xy50 = " + Matrix.toString(xy50));
//		
//		float[] rgb2 = cs.fromCIEXYZ(XYZ65);
//		System.out.println("rgb2 = " + Matrix.toString(rgb2));
		
	}
	
	
}

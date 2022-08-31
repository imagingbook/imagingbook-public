/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.colorspace;

import static imagingbook.common.math.Matrix.multiply;

/**
 * <p>
 * This class represents a linear chromatic adaptation transform,
 * mapping XYZ color coordinates from a source white (reference) point
 * to a target white point. 
 * Both white points are passed to the constructor.
 * The actual color mapping is done by method {@link #applyTo(float[])}.
 * The underlying linear transformation is specified by matrix 'Mcat',
 * which may be retrieved by {@link #getMcat()}).
 * See the Section 14.6 of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 */
public class BradfordAdaptation implements ChromaticAdaptation {
	
	// CAT transform matrices (forward and inverse)
	private static double[][] Mfwd = {
	    { 0.8951,  0.2664, -0.1614},
	    {-0.7502,  1.7135,  0.0367},
	    { 0.0389, -0.0685,  1.0296}};
	    
	private static double[][] Minv = {	
		{ 0.9869929055, -0.1470542564, 0.1599626517}, 
		{ 0.4323052697,  0.5183602715, 0.0492912282},
		{-0.0085286646,  0.0400428217, 0.9684866958}};
	
//	private static double[][] Minv = Matrix.inverse(Mfwd);
	
	//	the complete color adaptation transformation matrix
	private final double[][] Mcat;
	
	/**
	 * Constructor accepting two white points (XYZ-coordinates).
	 * @param W1 source white point
	 * @param W2 target white point
	 */
	public BradfordAdaptation(double[] W1, double[] W2) {
		double[] rgb1 = multiply(Mfwd, W1);
		double[] rgb2 = multiply(Mfwd, W2);
		double[][] Mrgb = rgbMatrix(rgb1, rgb2);
		Mcat = multiply(Minv, multiply(Mrgb, Mfwd));
	}
	
	/**
	 * Constructor accepting two {@link Illuminant} instances for
	 * specifying the source and target white points.
	 * @param illum1 source illuminant
	 * @param illum2 target illuminant
	 */
	public BradfordAdaptation(Illuminant illum1, Illuminant illum2) {
		this(illum1.getXYZ(), illum2.getXYZ());
	}
	
	// transformation of color coordinates
	@Override
	public float[] applyTo(float[] XYZ1) {
		// XYZ2 = Mcat . XYZ1
		float[] XYZ2 = new float[3];
		for (int i = 0; i < 3; i++) {
			XYZ2[i] = (float) (Mcat[i][0] * XYZ1[0] + Mcat[i][1] * XYZ1[1] + Mcat[i][2] * XYZ1[2]);
		}
		return XYZ2;
	}
	
	/**
	 * Returns the color adaptation transformation matrix (Mcat).
	 * @return the color adaptation transformation matrix
	 */
	public double[][] getMcat() {
		return this.Mcat;
	}
	

	// Creates a diagonal matrix with the ratios of the rgb components
	// obtained by transforming the two white points
	// TODO: no deed to create a matrix!!
	private double[][] rgbMatrix(double[] rgb1, double[] rgb2) {
		if (rgb1.length != rgb2.length)
			throw new IllegalArgumentException();
		int n = rgb1.length;
		double[][] Madapt = new double[n][n];
		for (int i = 0; i < n; i++) {
			Madapt[i][i] = rgb2[i] / rgb1[i];
		}
		return Madapt;
	}
	
	// ------------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		
//		BradfordAdaptation adapt = new BradfordAdaptation(Illuminant.D65, Illuminant.D50);	// adapts from D65 -> D50
//		
//		System.out.println("Mcat = \n" + Matrix.toString(adapt.getMcat()));
//		System.out.println();
//		
//		
//		PrintPrecision.set(4);
//		ColorSpace cs = new sRgb65ColorSpace();
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
//		float[] xy65 = CieUtil.xyzToxy(XYZ65);
//		System.out.println("xy65 = " + Matrix.toString(xy65));
//		
//		float[] XYZ50 = adapt.applyTo(XYZ65);
//		System.out.println("XYZ50 = " + Matrix.toString(XYZ50));
//		
//		float[] xy50 = CieUtil.xyzToxy(XYZ50);
//		System.out.println("xy50 = " + Matrix.toString(xy50));
//		
//		float[] rgb2 = cs.fromCIEXYZ(XYZ65);
//		System.out.println("rgb2 = " + Matrix.toString(rgb2));
//		
//	}
//	
	
}

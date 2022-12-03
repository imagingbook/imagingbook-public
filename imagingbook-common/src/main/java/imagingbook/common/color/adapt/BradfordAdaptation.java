/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.color.adapt;

import static imagingbook.common.math.Matrix.inverse;
import static imagingbook.common.math.Matrix.multiply;

import imagingbook.common.color.cie.Illuminant;

/**
 * <p>
 * This class represents a linear chromatic adaptation transform,
 * mapping XYZ color coordinates from a source white (reference) point
 * to a target white point. 
 * Both white points are passed to the constructor.
 * The actual color mapping is done by method {@link #applyTo(float[])}.
 * The underlying linear transformation is specified by a 3x3 matrix,
 * which may be retrieved by {@link #getAdaptationMatrix()}).
 * See Sec. 14.6 of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; 
 * An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/14
 * @see XYZscalingAdaptation
 */
public class BradfordAdaptation implements ChromaticAdaptation {
	
	// CAT forward transform matrix from XYZ to "virtual" RGB scaling coordinates
	private static final double[][] MCAT = {
	    { 0.8951,  0.2664, -0.1614},
	    {-0.7502,  1.7135,  0.0367},
	    { 0.0389, -0.0685,  1.0296}};
	   
	// CAT inverse transform matrix from "virtual" RGB scaling coordinates back to XYZ
	private static final double[][] MCATi = inverse(MCAT);	  // we always invert for precision reasons
//		{{ 0.9869929054667120, -0.1470542564209900, 0.1599626516637310}, 
//		 { 0.4323052697233940,  0.5183602715367770, 0.0492912282128560}, 
//		 {-0.0085286645751770,  0.0400428216540850, 0.9684866957875500}}
	
	// the complete color adaptation transformation matrix for transforming source XYZ to target XYZ,
	// XYZ2 = Madapt * XYZ1, with
	// Madapt = MCAT^-1 * DiagRGB * MCAT
	private final double[][] Madapt;
	private final double[][] Mdiag;
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Returns a {@link BradfordAdaptation} instance for the specified
	 * white point coordinates.
	 * 
	 * @param W1 source white point (to map from)
	 * @param W2 target white point (to map to)
	 * @return a {@link BradfordAdaptation} instance 
	 */
	public static BradfordAdaptation getInstance(double[] W1, double[] W2) {
		return new BradfordAdaptation(W1, W2);
	}
	
	/**
	 * Returns a {@link BradfordAdaptation} instance for the specified
	 * illuminants (white points).
	 * 
	 * @param illum1 source illuminant (white point to map from)
	 * @param illum2 target illuminant (white point to map to)
	 * @return a {@link BradfordAdaptation} instance
	 */
	public static BradfordAdaptation getInstance(Illuminant illum1, Illuminant illum2) {
		return getInstance(illum1.getXYZ(), illum2.getXYZ());
	}
	
	/** Constructor (non-public) accepting two white points (XYZ-coordinates). */
	private BradfordAdaptation(double[] W1, double[] W2) {
		double[] rgb1 = multiply(MCAT, W1);
		double[] rgb2 = multiply(MCAT, W2);
		this.Mdiag = getRgbWhiteRatioMatrix(rgb1, rgb2);
		this.Madapt = multiply(MCATi, multiply(Mdiag, MCAT));
	}
	
	// ---------------------------------------------------------------------------------
	
	public static double[][] getMCAT() {
		return MCAT;
	}
	
	public static double[][] getMCATi() {
		return MCATi;
	}
	
	// ---------------------------------------------------------------------------------
	
	@Override
	public float[] applyTo(float[] XYZA) {
		// XYZB = Madapt . XYZA
		float[] XYZB = new float[3];
		for (int i = 0; i < 3; i++) {
			XYZB[i] = (float) (Madapt[i][0] * XYZA[0] + Madapt[i][1] * XYZA[1] + Madapt[i][2] * XYZA[2]);
		}
		return XYZB;
	}
	
	@Override
	public double[] applyTo(double[] XYZA) {
		// XYZB = Madapt . XYZA
		double[] XYZB = new double[3];
		for (int i = 0; i < 3; i++) {
			XYZB[i] = Madapt[i][0] * XYZA[0] + Madapt[i][1] * XYZA[1] + Madapt[i][2] * XYZA[2];
		}
		return XYZB;
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
	private static double[][] getRgbWhiteRatioMatrix(double[] rgbA, double[] rgbB) {
		if (rgbA.length != rgbB.length) {
			throw new IllegalArgumentException();
		}
		final int n = rgbA.length;
		double[][] M = new double[n][n];
		for (int i = 0; i < n; i++) {
			M[i][i] = rgbB[i] / rgbA[i];
		}
		return M;
	}
		
}

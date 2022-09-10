/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.linear;

import imagingbook.common.math.Matrix;

/**
 * This class represents a 1D filter kernel.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class Kernel1D {
	
	private final float[] h;
	private final int xc;
	private final int width;
	
	/**
	 * Constructor.
	 * The kernel's hot spot is at its center.
	 * The kernel is normalized.
	 * 
	 * @param h the 1D kernel array
	 */
	public Kernel1D(float[] h) {
		this(h, (h.length - 1) / 2, true);
	}
	
	/**
	 * Constructor.
	 * The hot spot index must be specified.
	 * The kernel is optionally normalized.
	 * 
	 * @param h the 1D kernel array
	 * @param xc the x-coordinate of the kernel's hot spot, default is (width-1)/2
	 * @param normalize if true the kernel is normalized (to sum = 1)
	 */
	public Kernel1D(float[] h, int xc, boolean normalize) {
		this.h = (normalize) ? normalize(h) : Matrix.duplicate(h);
		this.width = h.length;
		this.xc = xc;
	}
	
	/**
	 * Returns the kernel's 1D array.
	 * @return the kernel's 1D array
	 */
	public float[] getH() {
		return h;
	}
	
	/**
	 * Returns the width (length) of this kernel.
	 * @return the kernel's width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the center coordinate of this kernel.
	 * @return the kernel's center coordinate
	 */
	public int getXc() {
		return xc;
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Normalizes the specified array such that its sum becomes 1.
	 * Throws an exception if the array's sum is zero.
	 * The original array is not modified.
	 * 
	 * @param A a 1D array
	 * @return the normalized array
	 */
	public static float[] normalize(float[] A) throws ArithmeticException {
		float s = (float) (1.0 / Matrix.sum(A));
		if (!Double.isFinite(s))	// isZero(s)
			throw new ArithmeticException("zero kernel sum, cannot normalize");
		return Matrix.multiply(s, A);
	}

}

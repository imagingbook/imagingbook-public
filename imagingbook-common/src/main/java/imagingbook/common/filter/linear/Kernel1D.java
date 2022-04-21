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
	 * Convenience constructor.
	 * Assumes that the kernel's hot spot is at its center,
	 * does normalization by default.
	 * 
	 * @param h the 1D kernel array
	 */
	public Kernel1D(float[] h) {
		this(h, (h.length - 1) / 2, true);
	}
	
	/**
	 * Full constructor.
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

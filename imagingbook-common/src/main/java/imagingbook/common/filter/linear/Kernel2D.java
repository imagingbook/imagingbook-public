package imagingbook.common.filter.linear;

import imagingbook.common.math.Matrix;

/**
 * This class represents a 2D filter kernel.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class Kernel2D {
	
	private final float[][] H;	// H[y][x] !!
	private final int xc, yc;
	private final int width, height;
	
	/**
	 * Convenience constructor.
	 * Assumes that the kernel's hot spot is at its center,
	 * does normalization by default.
	 * 
	 * @param H the 2D kernel array
	 */
	public Kernel2D(float[][] H) {
		this(H, (H[0].length - 1) / 2, (H.length - 1) / 2, true);
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param H the 2D kernel array
	 * @param xc the x-coordinate of the kernel's hot spot, default is (width-1)/2
	 * @param yc the y-coordinate of the kernel's hot spot, default is (height-1)/2
	 * @param normalize if true the kernel is normalized (to sum = 1)
	 */
	public Kernel2D(float[][] H, int xc, int yc, boolean normalize) {
		this.H = (normalize) ? normalize(H) : Matrix.duplicate(H);
		this.width = H[0].length;
		this.height = H.length;
		this.xc = xc;
		this.yc = yc;
		validate();
	}
	
	/**
	 * Creates the effective 2D kernel from two 1D kernels.
	 * 
	 * @param Hx the 1D kernel for the x-direction
	 * @param Hy the 1D kernel for the y-direction
	 * @param normalize if true the kernel is normalized (to sum = 1)
	 */
	public Kernel2D(Kernel1D Hx, Kernel1D Hy, boolean normalize) {
		this(getEffectiveKernel(Hx, Hy), Hx.getXc(), Hy.getXc(), normalize);
	}
	
	/**
	 * Calculates the effective 2D kernel array from two 1D kernels
	 * assumed to be applied in x- and y-direction, respectively.
	 * The result is not normalized.
	 * Use the constructor {@link #Kernel2D(Kernel1D, Kernel1D, boolean)}
	 * to create the equivalent (optionally normalized) 2D kernel.
	 * Note that the kernel coordinates are {@code H[y][x]}.
	 * 
	 * @param Hx the 1D kernel for the x-direction
	 * @param Hy the 1D kernel for the y-direction
	 * @return the effective 2D kernel matrix
	 */
	public static float[][] getEffectiveKernel(Kernel1D Hx, Kernel1D Hy) {
		float[] kernelX = Hx.getH();
		float[] kernelY = Hy.getH();
		
		float[][] kernel = new float[kernelX.length][kernelY.length];
		for (int y = 0; y < kernelY.length; y++) {
			for (int x = 0; x < kernelX.length; x++) {
				kernel[y][x] = kernelX[x] * kernelY[y];
			}
		}
		return kernel;
	}
	
	private void validate() {
		if (!Matrix.isRectangular(H))
			throw new IllegalArgumentException("non-rectangular filter kernel");
		// TODO: should we check if xc/yc is inside the kernel? does it matter?
	}
	
	/**
	 * Returns the kernel's 2D array.
	 * Note that the kernel coordinates are {@code H[y][x]}.
	 * @return the kernel's 2D array
	 */
	public float[][] getH() {
		return H;
	}
	
	/**
	 * Returns the width of this kernel.
	 * @return the kernel's width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of this kernel.
	 * @return the kernel's height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the horizontal center coordinate of this kernel.
	 * @return the kernel's x-center coordinate
	 */
	public int getXc() {
		return xc;
	}
	
	/**
	 * Returns the vertical center coordinate of this kernel.
	 * @return the kernel's y-center coordinate
	 */
	public int getYc() {
		return yc;
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Normalizes the specified array such that its sum becomes 1.
	 * Throws an exception if the array's sum is zero.
	 * The original array is not modified.
	 * 
	 * @param A a 2D array
	 * @return the normalized array
	 */
	public static float[][] normalize(float[][] A) throws ArithmeticException {
		float s = (float) (1.0 / Matrix.sum(A));
		if (!Double.isFinite(s))
			throw new ArithmeticException("zero kernel sum, cannot normalize");
		return Matrix.multiply(s, A);
	}

}

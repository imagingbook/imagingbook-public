package imagingbook.common.filter.linear;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * This class represents a 2D filter kernel.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianKernel2D extends Kernel2D {
	
	public GaussianKernel2D(double sigmaX, double sigmaY) {
		super(makeGaussKernel2D(sigmaX, sigmaY));
	}
	
	public GaussianKernel2D(double sigma) {
		super(makeGaussKernel2D(sigma, sigma));
	}

	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough
	 * to avoid truncation effects. The associated array is odd-sized in
	 * both dimensions.
	 * The returned kernel is normalized.
	 * 
	 * @param sigmaX the width (standard deviation) of the Gaussian in x-direction
	 * @param sigmaY the width (standard deviation) of the Gaussian in y-direction
	 * @return the Gaussian filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigmaX, double sigmaY){
		final int radX = (int) Math.ceil(GaussianKernel1D.SIZE_FACTOR * sigmaX);
		final int radY = (int) Math.ceil(GaussianKernel1D.SIZE_FACTOR * sigmaY);
		final int sizeX = radX + radX + 1;
		final int sizeY = radY + radY + 1;

		final float[][] kernel = new float[sizeX][sizeY]; //center cell = kernel[rad][rad]
		final double sigmaX2 = (sigmaX > 0.1) ? sqr(sigmaX) : 0.1;
		final double sigmaY2 = (sigmaY > 0.1) ? sqr(sigmaY) : 0.1;
		
		for (int i = 0; i < sizeY; i++) {
			final double  b = sqr(radY - i) / (2 * sigmaY2);
			for (int j = 0; j < sizeX; j++) {
				final double a = sqr(radX - j) / (2 * sigmaX2);
				double g = Math.exp(-(a + b));
				kernel[i][j] = (float) g;
			}
		}
		return normalize(kernel);	// we normalize just in case
	}
}

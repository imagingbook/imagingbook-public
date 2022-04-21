package imagingbook.common.filter.linear;

import imagingbook.common.image.access.OutOfBoundsStrategy;

/**
 * This class implements a 2D Gaussian filter by extending
 * {@link LinearFilter}.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianFilter extends LinearFilter {
	
	public static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	/**
	 * Constructor.
	 * @param sigma the width of the 2D Gaussian in x- and y-direction
	 */
	public GaussianFilter(double sigma) {
		super(new GaussianKernel2D(sigma));
	}
	
	/**
	 * Constructor.
	 * @param sigmaX the width of the 2D Gaussian in x-direction
	 * @param sigmaY the width of the 2D Gaussian in y-direction
	 */
	public GaussianFilter(double sigmaX, double sigmaY) {
		super(new GaussianKernel2D(sigmaX, sigmaY));
	}
}

package imagingbook.common.filter.linear;

import imagingbook.common.filter.GenericFilter;
import imagingbook.common.filter.GenericFilterScalarSeparable;
import imagingbook.common.image.data.PixelPack.PixelSlice;

/**
 * This class represents a 2D linear filter that is x/y-separable and
 * specified by two 1D-kernels.
 * It is based on {@link GenericFilter} and {@link GenericFilterScalarSeparable},
 * which take care of all data copying and filter mechanics.
 * Since it is a "scalar" filter, pixel values are treated as scalars.
 * If the processed image has more than one component 
 * (e.g., a RGB color image), this filter is automatically 
 * and independently applied to all (scalar-valued) components.
 * To apply to an image, use the {@link #applyTo(ij.process.ImageProcessor)}
 * method, for example.
 */
public class LinearFilterSeparable extends GenericFilterScalarSeparable {

	private float[] hX = null;			// the horizontal kernel array
	private float[] hY = null;			// the vertical kernel array
	private int width = 0;				// width of the effective kernel
	private int height = 0;				// height of the effective kernel
	private int xc = 0;						// 'hot spot' x-position
	private int yc = 0;						// 'hot spot' y-position
	
	/**
	 * Constructor, takes a 1D convolution kernel to be applied both
	 * in x- and y-direction. 
	 * 
	 * @param kernelXY a 1D convolution kernel
	 */
	public LinearFilterSeparable(Kernel1D kernelXY) {
		this(kernelXY, kernelXY);
	}
	
	/**
	 * Constructor, takes two 1D convolution kernels to be applied
	 * in x- and y-direction, respectively.
	 * One (but not both) of the supplied kernels may be {@code null}.
	 * If this is the case, filtering in the associated direction
	 * is skipped.
	 * 
	 * @param kernelX a 1D kernel for convolution in x-direction
	 * @param kernelY a 1D kernel for convolution in y-direction
	 */
	public LinearFilterSeparable(Kernel1D kernelX, Kernel1D kernelY) {
		super();
		
		if (kernelX == null && kernelY == null) {
			throw new RuntimeException("both X/Y kernels may not be null");
		}
		
		if (kernelX == null) {
			this.doX = false;	// skip X-part
		}
		else {
			this.hX = kernelX.getH();
			this.width = kernelX.getWidth();
			this.xc = kernelX.getXc();
		}
		
		if (kernelY == null) {
			this.doY = false;	// skip Y-part
		}
		else {
			this.hY = kernelY.getH();
			this.height = kernelY.getWidth();
			this.yc = kernelY.getXc();
		}
	}

	// ------------------------------------------------------------------------

	// 1D convolution in x-direction
	@Override
	protected float doPixelX(PixelSlice source, int u, final int v) {
		double sum = 0;
		for (int i = 0; i < width; i++) {
			int ui = u + i - xc;
			sum = sum + source.getVal(ui, v) * hX[i];
		}
		return (float)sum;
	}
	
	// 1D convolution in y-direction
	@Override
	protected float doPixelY(PixelSlice source, final int u, int v) {
		double sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			sum = sum + source.getVal(u, vj) * hY[j];
		}
		return (float)sum;
	}

}

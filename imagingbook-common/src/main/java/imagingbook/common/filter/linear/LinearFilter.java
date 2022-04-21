package imagingbook.common.filter.linear;

import imagingbook.common.filter.GenericFilter;
import imagingbook.common.filter.GenericFilterScalar;
import imagingbook.common.image.data.PixelPack.PixelSlice;

/**
 * This class represents a 2D linear filter specified by an
 * arbitrary 2D convolution kernel.
 * It is based on {@link GenericFilter} and {@link GenericFilterScalar},
 * which take care of all data copying and filter mechanics.
 * Since it is a "scalar" filter, pixel values are treated as scalars.
 * If the processed image has more than one component 
 * (e.g., a RGB color image), this filter is automatically 
 * and independently applied to all (scalar-valued) components.
 * To apply to an image, use the {@link #applyTo(ij.process.ImageProcessor)}
 * method, for example.
 */
public class LinearFilter extends GenericFilterScalar {

	private final float[][] H;			// the kernel matrix, note H[y][x]!
	private final int xc, yc;			// 'hot spot' coordinates
	
	/**
	 * Constructor. Only the 2D filter kernel (see {@link Kernel2D}) needs to be specified.
	 * 
	 * @param kernel the 2D filter kernel
	 */
	public LinearFilter(Kernel2D kernel) {
		this.H = kernel.getH();
		this.xc = kernel.getXc();
		this.yc = kernel.getYc();
	}

	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		double sum = 0;
		for (int j = 0; j < H.length; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < H[j].length; i++) {
				int ui = u + i - xc;
				sum = sum + plane.getVal(ui, vj) * H[j][i];
			}
		}
 		return (float)sum;
//		return convolve(source, H, u, v, xc, yc);
	}
	
//	public static float convolve(PixelSlice plane, float[][] H, int u, int v, int xc, int yc) {
//		double sum = 0;
//		for (int j = 0; j < H.length; j++) {
//			int vj = v + j - yc;
//			for (int i = 0; i < H[j].length; i++) {
//				int ui = u + i - xc;
//				sum = sum + plane.getVal(ui, vj) * H[j][i];
//			}
//		}
// 		return (float)sum;
//	}

}

package imagingbook.common.filter;

import imagingbook.common.image.data.PixelPack;

/**
 * <p>
 * This (abstract) class represents a filter which treats all pixel data 
 * as vectors.
 * The filter calls the method {@link #doPixel(PixelPack, int, int)}
 * for each image pixel, which must be implemented by any concrete
 * sub-class.
 * </p>
 * <p>
 * Thus a custom filter based on this class only needs to specify the steps
 * to be performed for a single pixel. The remaining filter mechanics
 * including multiple components, out-of-bounds coordinate handling,
 * multiple passes and data copying are handled by this class and its super-class
 * (see {@link GenericFilter}).
 * </p>
 */
public abstract class GenericFilterVector extends GenericFilter {

	private int iter = 0;
	private int iterMax = 1;	// for progress reporting only
	
	@Override 
	protected void runPass(PixelPack sourcePack, PixelPack targetPack) {
		final int width = sourcePack.getWidth();
		final int height = sourcePack.getHeight();
		iterMax = width * height;
		iter = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				targetPack.setVec(u, v, doPixel(sourcePack, u, v)); // single pixel operation
				iter++;
			}
		}
		iter = 0;
	}
	
	/**
	 * This method defines the steps to be performed for a single image pixel and
	 * must be implemented by any concrete sub-class.
	 * The source data are passed as a {@link PixelPack} container, which
	 * holds the pixel values of all image components.
	 * The method {@link PixelPack#getVec(int, int)} should be used to read
	 * individual pixel vectors. These data should not be modified but
	 * the (float[]) result of the single-pixel calculation must be returned.
	 * Implementations are free to return the same float-array at each invocation,
	 * i.e., there is no need to allocate a new array every time.
	 * 
	 * @param source the vector-valued image data
	 * @param u the current x-position
	 * @param v the current y-position
	 * @return the result of the filter calculation for this pixel
	 */
	protected abstract float[] doPixel(PixelPack source, int u, int v);
	
	// -----------------------------------------------------------------
	
	// helper method for copying vector pixels, TODO: should move to Utils or so
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}
	
	@Override
	protected final double reportProgress(double subProgress) {
		double localProgress = (double) iter /iterMax;
		//System.out.println("GenericFilterVector: reportProgress() - returning " + localProgress);
		return super.reportProgress(localProgress);
	}

}

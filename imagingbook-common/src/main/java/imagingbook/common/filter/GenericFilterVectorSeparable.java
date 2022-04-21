package imagingbook.common.filter;

import imagingbook.common.image.data.PixelPack;

/**
 * This (abstract) class represents a generic vector filter whose pixel-operation
 * is x/y-separable.
 * It is similar to {@link GenericFilterVector} but requires two methods to
 * be implemented by concrete sub-classes: 
 * {@link #doPixelX(PixelPack, int, int)} and {@link #doPixelY(PixelPack, int, int)}
 * for the x- and y-pass, respectively,
 * which are invoked in exactly this order.
 * The remaining filter mechanics
 * including out-of-bounds coordinate handling,
 * multiple passes and data copying are handled by this class and its super-class
 * (see {@link GenericFilter}). 
 */
public abstract class GenericFilterVectorSeparable extends GenericFilter { // GenericFilterVector
	
	private int iter = 0;
	private int iterMax = 1;	// for progress reporting only
	
	@Override 
	protected void runPass(PixelPack source, PixelPack target) {
		final int width = source.getWidth();
		final int height = source.getHeight();
		iterMax = width * height * 2;
		iter = 0;
		
		// X-part
		//IJ.log("X-part +++++++++++++++++++++++++++++++++");
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setVec(u, v, doPixelX(source, u, v)); // single pixel operation
				iter++;
			}
		}
		
		target.copyTo(source);
		
		// Y-part
		//IJ.log("Y-part +++++++++++++++++++++++++++++++++");
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setVec(u, v, doPixelY(source, u, v)); // single pixel operation
				iter++;
			}
		}
		iter = 0;
	}
	
	// ------------------------------------------------------------------------

	/**
	 * Applies a 1D filter operation in x-direction.
	 * This method must be implemented by concrete sub-classes.
	 * This method is invoked before {@link #doPixelY(PixelPack, int, int)}.
	 * The source data are passed as a {@link PixelPack} container, which
	 * holds the vector values of all image components.
	 * The method {@link PixelPack#getVec(int, int)} should be used to read
	 * individual pixel vectors. These data should not be modified but
	 * the (float[]) result of the single-pixel calculation must be returned.
	 * Implementations are free to return the same float-array at each invocation,
	 * i.e., there is no need to allocate a new array every time.
	 * 
	 * @param source the scalar-valued data for a single image component
	 * @param u the current x-position
	 * @param v the current y-position
	 * @return the result of the filter calculation for this pixel
	 */
	protected abstract float[] doPixelX(PixelPack source, int u, int v);

	/**
	 * Applies a 1D filter operation in y-direction.
	 * This method must be implemented by concrete sub-classes.
	 * This method is invoked after {@link #doPixelX(PixelPack, int, int)}.
	 * The source data are passed as a {@link PixelPack} container, which
	 * holds the vector values of all image components.
	 * The method {@link PixelPack#getVec(int, int)} should be used to read
	 * individual pixel vectors. These data should not be modified but
	 * the (float[]) result of the single-pixel calculation must be returned.
	 * Implementations are free to return the same float-array at each invocation,
	 * i.e., there is no need to allocate a new array every time.
	 * 
	 * @param source the scalar-valued data for a single image component
	 * @param u the current x-position
	 * @param v the current y-position
	 * @return the result of the filter calculation for this pixel
	 */
	protected abstract float[] doPixelY(PixelPack source, int u, int v);
	
	// ------------------------------------------------------------------------

	@Override
	protected final double reportProgress(double subProgress) {
		double localProgress = (double) iter /iterMax;
		//System.out.println("GenericFilterVector: reportProgress() - returning " + localProgress);
		return super.reportProgress(localProgress);
	}
	
}

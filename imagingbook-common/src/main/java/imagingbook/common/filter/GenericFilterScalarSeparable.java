package imagingbook.common.filter;

import imagingbook.common.image.data.PixelPack;
import imagingbook.common.image.data.PixelPack.PixelSlice;

/**
 * This (abstract) class represents a generic scalar filter whose pixel-operation
 * is x/y-separable.
 * It is similar to {@link GenericFilterScalar} but requires two methods to
 * be implemented by concrete sub-classes: 
 * {@link #doPixelX(PixelSlice, int, int)} and {@link #doPixelY(PixelSlice, int, int)}
 * for the x- and y-pass, respectively,
 * which are invoked in exactly this order.
 * If the processed image has more than one component 
 * (e.g., a RGB color image), this filter is automatically 
 * and independently applied to all (scalar-valued) components.
 * The remaining filter mechanics
 * including multiple components, out-of-bounds coordinate handling,
 * multiple passes and data copying are handled by this class and its super-class
 * (see {@link GenericFilter}). 
 */
public abstract class GenericFilterScalarSeparable extends GenericFilter {
	
	protected boolean doX = true;	// allow implementing sub-classes to deactivate x/y pass
	protected boolean doY = true;
	
	// for progress reporting only
	private int slice;
	private int sliceMax = 1;
	
	private int iter = 0;
	private int iterMax = 1;	// for progress reporting only
	
	
	// apply filter to a stack of pixel planes (1 pass)
	@Override
	protected void runPass(PixelPack source, PixelPack target) {
		sliceMax = source.getDepth();
		for (int k = 0; k < sliceMax; k++) {
			//IJ.log("+++++++ starting slice " + k);
			this.slice = k;
			runSlice(source.getSlice(k), target.getSlice(k));	// default behavior: apply filter to each plane, place results in target
		}
	}
	
	// a bit wasteful, as we provide a separate target for each plane
	private void runSlice(PixelSlice source, PixelSlice target) {
		final int width = source.getWidth();
		final int height = source.getHeight();
		this.iterMax = width * height * 2;
		this.iter = 0;
		
		if (doX) {
			// IJ.log("doing X-part =============================== ");
			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					target.setVal(u, v, doPixelX(source, u, v));
					this.iter++;
				}
			}
			target.copyTo(source);
		}
		
		if (doY) {
			// IJ.log("doing Y-part =============================== ");
			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					target.setVal(u, v, doPixelY(source, u, v));
					this.iter++;
				}
			}
		}
		
		this.iter = 0;
	}
	
	// ------------------------------------------------------------------------

	/**
	 * Applies a 1D filter operation in x-direction.
	 * This method must be implemented by concrete sub-classes.
	 * This method is invoked before {@link #doPixelY(PixelSlice, int, int)}.
	 * The source data are passed as a {@link PixelSlice} container, which
	 * holds the scalar values of one image component.
	 * The method {@link PixelSlice#getVal(int, int)} should be used to read
	 * individual pixel values. These data should not be modified but
	 * the (float) result of the single-pixel calculation must be returned.
	 * 
	 * @param source the scalar-valued data for a single image component
	 * @param u the current x-position
	 * @param v the current y-position
	 * @return the result of the filter calculation for this pixel
	 */
	protected abstract float doPixelX(PixelSlice source, int u, int v);

	/**
	 * Applies a 1D filter operation in y-direction.
	 * This method must be implemented by concrete sub-classes.
	 * This method is invoked after {@link #doPixelX(PixelSlice, int, int)}.
	 * The source data are passed as a {@link PixelSlice} container, which
	 * holds the scalar values of one image component.
	 * The method {@link PixelSlice#getVal(int, int)} should be used to read
	 * individual pixel values. These data should not be modified but
	 * the (float) result of the single-pixel calculation must be returned.
	 * 
	 * @param source the scalar-valued data for a single image component
	 * @param u the current x-position
	 * @param v the current y-position
	 * @return the result of the filter calculation for this pixel
	 */
	protected abstract float doPixelY(PixelSlice source, int u, int v);
	
	// ------------------------------------------------------------------------
	
	@Override
	protected final double reportProgress(double subProgress) {
		double loopProgress = (this.iter + subProgress) / this.iterMax;
		//IJ.log("   loopProgress = " + loopProgress);
		double sliceProgress = (this.slice + loopProgress) / this.sliceMax;
		//IJ.log("   sliceProgress = " + sliceProgress);
		//System.out.println("reportProgress: GenericFilterScalar " + localProgress);
		return super.reportProgress(sliceProgress);
	}

	
}

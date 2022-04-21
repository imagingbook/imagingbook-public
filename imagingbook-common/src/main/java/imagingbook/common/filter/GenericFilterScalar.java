package imagingbook.common.filter;

import imagingbook.common.image.data.PixelPack;
import imagingbook.common.image.data.PixelPack.PixelSlice;

/**
 * <p>
 * This (abstract) class represents a filter which treats all pixel values 
 * as scalars. If the processed image has more than one component 
 * (e.g., a RGB color image), this filter is automatically 
 * and independently applied to all (scalar-valued) components.
 * The filter calls the method {@link #doPixel(PixelSlice, int, int)}
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
public abstract class GenericFilterScalar extends GenericFilter {
	
	// for progress reporting only
	private int slice;
	private int sliceMax = 1;
	
	private int iter;
	private int iterMax = 1;
	
	// apply filter to a stack of pixel slices (1 pass)
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
	private void runSlice(PixelSlice sourcePlane, PixelSlice targetPlane) {
		final int width = sourcePlane.getWidth();
		final int height = sourcePlane.getHeight();
		this.iterMax = width * height;
		this.iter = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				targetPlane.setVal(u, v, doPixel(sourcePlane, u, v));
				this.iter++;
			}
		}
		this.iter = 0;
	}

	// this method every scalar filter must implement
	// calculate the result value for a single pixel
	/**
	 * This method defines the steps to be performed for a single image pixel and
	 * must be implemented by any concrete sub-class.
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
	protected abstract float doPixel(PixelSlice source, int u, int v);
	
	// -------------------------------------------------------------------

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

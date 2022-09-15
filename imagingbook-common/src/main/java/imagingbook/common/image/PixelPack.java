/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image;

import java.util.Arrays;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.access.GridIndexer2D;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.common.image.access.OutOfBoundsStrategy;

/**
 * <p>
 * This class defines a generic data container for scalar and
 * vector-valued images, using float-values throughout.
 * Its primary use is in the {@link GenericFilter} framework.
 * </p>
 * <p>
 * A {@link PixelPack} may represent images with an arbitrary number of
 * components. Scalar images (such as {@link ByteProcessor}, 
 * {@link ShortProcessor} and {@link FloatProcessor}) have 1 component,
 * color images (such as {@link ColorProcessor}) typically have 3 components.
 * Conversion methods to and from ImageJ's processor classes are provided,
 * with optional scaling of pixel component values.
 * </p>
 * <p>
 * Internally, pixel data are stored as 1-dimensional {@code float} arrays,
 * one array for each component.
 * Method {@link #getData()} may be used to access the internal data directly.
 * Individual components may be extracted as a {@link PixelSlice} using
 * method {@link #getSlice(int)}.
 * </p>
 * <p>
 * Methods {@link #getPix(int, int)} and {@link #setPix(int, int, float...)} are
 * provided to read and write individual pixels, which are 
 * ALWAYS of type {@code float[]} (even if the underlying image is scalar-valued).
 * Pixel values returned for positions outside the image boundaries depend 
 * on the {@link OutOfBoundsStrategy} specified by the constructor
 * (e.g., {@link #PixelPack(ImageProcessor, double, OutOfBoundsStrategy)}).
 * </p>
 * <p>Here is a simple usage example:</p>
 * <pre>
 * ColorProcessor ip1 = ... ;	// some color image
 * PixelPack pack = new PixelPack(ip1);
 * // process pack:
 * float[] val = pack.getPix(0, 0);
 * pack.setPix(0, 0, 128, 19, 255);
 * ...
 * ColorProcessor ip2 = pack.toColorProcessor();
 * </pre>
 * <p>
 * A related concept for providing unified access to images is {@link ImageAccessor}.
 * In contrast to {@link PixelPack}, {@link ImageAccessor} does not duplicate
 * any data but reads and writes the original {@link ImageProcessor} pixel data
 * directly.
 * </p>
 * 
 * @author WB
 * @version 2022/09/03
 * @see ImageAccessor
 */
public class PixelPack {
	
	/** The default out-of-bounds strategy (see {@link OutOfBoundsStrategy}). */
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NearestBorder;

	protected final int width;
	protected final int height;
	protected final int depth;
	protected final float[][] data;
	protected final int length;
	private final GridIndexer2D indexer;
	
	// --------------------------------------------------------------------
	
	/**
	 * Constructor. Creates a blank (zero-valued) pack of pixel data.
	 * @param width the image width
	 * @param height the image height
	 * @param depth the number of channels (slices)
	 * @param obs strategy to be used when reading from out-of-bounds coordinates (pass {@code null} for default)
	 */
	public PixelPack(int width, int height, int depth, OutOfBoundsStrategy obs) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.length = width * height;
		this.data = new float[depth][length];
		this.indexer = GridIndexer2D.create(width, height, obs);
	}
	
	/**
	 * Constructor. Creates a pack of pixel data from the given 
	 * {@link ImageProcessor} object.
	 * Does not scale pixel values and uses
	 * {@link #DefaultOutOfBoundsStrategy} as the out-of-bounds strategy
	 * (see {@link OutOfBoundsStrategy}).
	 * @param ip the source image
	 */
	public PixelPack(ImageProcessor ip) {
		this(ip, 1.0, DefaultOutOfBoundsStrategy);
	}
	
	/**
	 * Constructor. Creates a pack of pixel data from the given 
	 * {@link ImageProcessor} object, using the specified out-of-bounds strategy.
	 * @param ip the source image
	 * @param scale scale factor applied to pixel components
	 * @param obs strategy to be used when reading from out-of-bounds coordinates (pass {@code null} for default)
	 */
	public PixelPack(ImageProcessor ip, double scale, OutOfBoundsStrategy obs) {
		this(ip.getWidth(), ip.getHeight(), ip.getNChannels(), obs);
		copyFromImageProcessor(ip, scale);
	}
	
	/**
	 * Constructor. Creates a new {@link PixelPack} with the same dimension
	 * as the original without copying the contained pixel data
	 * (initialized to zero).
	 * 
	 * @param orig the original {@link PixelPack}
	 */
	public PixelPack(PixelPack orig) {
		this(orig, false);
	}
	
	/**
	 * Constructor. Creates a new {@link PixelPack} with the same dimension
	 * as the original.
	 * Optionally the original pixel data are copied, otherwise they are initialized 
	 * to zero values.
	 * 
	 * @param orig the original {@link PixelPack}
	 * @param copyData set true to copy pixel data
	 */
	public PixelPack(PixelPack orig, boolean copyData) {
		this(orig.getWidth(), orig.getHeight(), orig.getDepth(), orig.indexer.getOutOfBoundsStrategy());
		if (copyData) {
			orig.copyTo(this);
		}
	}
	
	// --------------------------------------------------------------------
	
	@Deprecated // use getPix(int u, int v, float[] vals)
	public float[] getVec(int u, int v, float[] vals) {
		getPix(u, v, vals);
		return vals;
	}
	
	@Deprecated	// use getPix(int u, int v)
	public float[] getVec(int u, int v) {
		return getPix(u, v);
	}
	
	@Deprecated	// use setPix(int u, int v, float ... vals) 
	public void setVec(int u, int v, float ... vals) {
		setPix(u, v, vals);
	}
	
	// --------------------------------------------------------------------

	/**
	 * Reads the pixel data at the specified image position.
	 * The supplied array is filled.
	 * The length of this array must match corresponds the number of slices in this
	 * pixel pack.
	 * The values returned for out-of-bounds positions depend on this
	 * pixel-pack's out-of-bounds strategy.
	 * 
	 * @param u the x-position
	 * @param v the y-position
	 * @param vals a suitable array of pixel data
	 */
	public void getPix(int u, int v, float[] vals) {
		if (vals == null) 
			vals = new float[depth];
		final int idx = indexer.getIndex(u, v);
		if (idx < 0) {	// i = -1 --> default value (zero)
			Arrays.fill(vals, 0);
		}
		else {	
//			for (int k = 0; k < depth; k++) {
//				vals[k] = data[k][i];
//			}
			getPix(idx, vals);
		}
	}
	
	/**
	 * Returns the pixel data at the specified position as a {@code float[]}.
	 * The values returned for out-of-bounds positions depend on this
	 * pixel-pack's out-of-bounds strategy.
	 * 
	 * @param u the x-position
	 * @param v the y-position
	 * @return the array of pixel component values
	 */
	public float[] getPix(int u, int v) {
		float[] vals = new float[depth];
		getPix(u, v, vals);
		return vals;
	}
	
	/**
	 * Reads the pixel data at the specified 1D index.
	 * The supplied array is filled.
	 * The length of this array must match corresponds the number of slices in this
	 * pixel pack.
	 * The index is not checked, the corresponding pixel must always be inside
	 * the image bounds, otherwise an exception will be thrown.
	 * 
	 * @param idx a valid 1D pixel index (in row-major order)
	 * @param vals a suitable array of pixel data
	 */
	public void getPix(int idx, float[] vals) {
		for (int k = 0; k < depth; k++) {
			vals[k] = data[k][idx];
		}
	}
	
	/**
	 * Returns the pixel data at the specified position as a {@code float[]}.
	 * The index is not checked, the corresponding pixel must always be inside
	 * the image bounds, otherwise an exception will be thrown.
	 * @param idx a valid 1D pixel index (in row-major order)
	 * @return the array of pixel component values
	 */
	public float[] getPix(int idx) {
		float[] vals = new float[depth];
		getPix(idx, vals);
		return vals;
	}

	
	/**
	 * Sets the pixel data at the specified pixel position.
	 * The length of the value array corresponds to the number of slices 
	 * (components) in this pixel pack.
	 * @param u the x-position
	 * @param v the y-position
	 * @param vals the pixel's component values (may also be a {@code float[]})
	 */
	public void setPix(int u, int v, float ... vals) {
		final int idx = indexer.getIndex(u, v);
		if (idx >= 0) {
//			for (int k = 0; k < depth && k < vals.length; k++) {
//				data[k][i] = vals[k];
//			}
			setPix(idx, vals);
		}
	}
	
	public void setPix(int idx, float ... vals) {
		for (int k = 0; k < depth && k < vals.length; k++) {
			data[k][idx] = vals[k];
		}
	}
	
	/**
	 * Copies the contents of one pixel pack to another.
	 * The involved pixel packs must have the same dimensions.
	 * @param other another pixel pack
	 */
	public void copyTo(PixelPack other) {
		if (!this.isCompatibleTo(other)) {
			throw new IllegalArgumentException("pixel packs of incompatible size, cannot copy");
		}
		for (int k = 0; k < this.depth; k++) {
			System.arraycopy(this.data[k], 0, other.data[k], 0, this.length);
		}
	}
	
	/**
	 * Checks is this pixel pack has the same dimensions as another
	 * pixel pack, i.e., can be copied to it.
	 * @param other the other pixel pack
	 * @return true if both have the same dimensions
	 */
	public boolean isCompatibleTo(PixelPack other) {
		if (this.data.length == other.data.length && this.length == other.length) { // TODO: check width/height too
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Checks is this pixel pack has the same dimensions as the
	 * specified {@link ImageProcessor} instance, i.e., can be copied to it.
	 * @param ip the image processor instance
	 * @return true if compatible
	 */
	public boolean isCompatibleTo(ImageProcessor ip) {
		if (this.getWidth() != ip.getWidth() || this.getHeight() != ip.getHeight()) {
			return false;
		}
		if (this.getDepth() != ip.getNChannels()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the kth {@link PixelSlice}.
	 * An exception is thrown if the specified slice does not exist.
	 * @param k the slice index (0,...,K-1)
	 * @return the kth {@link PixelSlice}
	 */
	public PixelSlice getSlice(int k) throws IllegalArgumentException {
		if (k < 0 || k >= depth) {
			throw new IllegalArgumentException("illegal slice number " + k);
		}
		return new PixelSlice(k);
	}
	
	/**
	 * Creates and returns a new {@link PixelSlice} with the same dimensions
	 * and out-of-bounds strategy as this {@link PixelPack}.
	 * @return a new pixel slice
	 */
	public PixelSlice getEmptySlice() {
		return new PixelSlice();
	}
	
	/**
	 * Returns an array of {@link PixelSlice} instances
	 * for this {@link PixelPack}. All pixel data are shared.
	 * 
	 * @return an array of {@link PixelSlice}
	 */
	public PixelSlice[] getSlices() {
		PixelSlice[] slices = new PixelSlice[depth];
		for (int k = 0; k < depth; k++) {
			slices[k] = getSlice(k);
		}
		return slices;
	}
	
	/**
	 * Returns the {@link FloatProcessor} for the kth pixel slice.
	 * An exception is thrown if the specified slice does not exist.
	 * @param k the slice index (0,...,K-1)
	 * @return the kth {@link FloatProcessor}
	 */
	public FloatProcessor getFloatProcessor(int k) {
		return getSlice(k).getFloatProcessor();
	}
	
	
	/**
	 * Returns an array of {@link FloatProcessor} instances
	 * for this {@link PixelPack}. All pixel data are shared.
	 * 
	 * @return an array of {@link FloatProcessor}
	 */
	public FloatProcessor[] getFloatProcessors() {
		FloatProcessor[] processors = new FloatProcessor[depth];
		for (int k = 0; k < depth; k++) {
			processors[k] = getFloatProcessor(k);
		}
		return processors;
	}
	
	/**
	 * Returns a reference to this {@link PixelPack}'s internal data
	 * array, which is always two-dimensional:
	 * dimension 1 is the slice (component) index,
	 * dimension 2 is the pixel index (each slice is a 1D array).
	 * @return the pixel pack's data array
	 */
	public float[][] getData() {
		return data;
	}
	
	/**
	 * Returns the width of the associated image.
	 * @return the image width
	 */
	public int getWidth() {
//		return this.indexer.getWidth();
		return this.width;
	}
	
	/**
	 * Returns the height of the associated image.
	 * @return the image height
	 */
	public int getHeight() {
//		return this.indexer.getHeight();
		return this.height;
	}
	
	/**
	 * Returns the depth (number of slices) of the associated image.
	 * @return the image depth
	 */
	public int getDepth() {
		return this.depth;
	}
	
	/**
	 * Returns the out-of-bounds strategy.
	 * @return the out-of-bounds strategy
	 */
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return this.indexer.getOutOfBoundsStrategy();
	}

	/**
	 * Sets all values of this pixel pack to zero.
	 */
	public void zero() {
		for (int k = 0; k < depth; k++) {
			getSlice(k).zero();
		}
	}
	
	/**
	 * Returns the pixel values in the 3x3 neighborhood around the
	 * specified position.
	 * The returned float-array has the structure {@code [x][y][k]},
	 * with x,y = 0,...,2  and k is the slice index.
	 * 
	 * @param uc the center x-position
	 * @param vc the center x-position
	 * @param nh a float array to be filled in (or null)
	 * @return the neighborhood array
	 */
	public float[][][] get3x3Neighborhood(int uc, int vc, float[][][] nh) {
		if (nh == null) 
			nh = new float[3][3][depth];
		for (int i = 0; i < 3; i++) {
			int u = uc - 1 + i;
			for (int j = 0; j < 3; j++) {
				int v = vc - 1 + j;
				nh[i][j] = getPix(u, v);
			}
		}
		return nh;
	}
	
//	/**
//	 * Copies the contents of this pixel pack to the supplied {@link ImageProcessor}
//	 * instance, if compatible. Otherwise an exception is thrown.
//	 * 
//	 * @param ip the target image processor
//	 */
//	public void copyToImageProcessor(ImageProcessor ip) {
//		copyToImageProcessor(this, ip);
//	}
	
	// -------------------------------------------------------------------
	
	/**
	 * Inner class representing a single (scalar-valued) component of a 
	 * (vector-valued) {@link PixelPack}.
	 *
	 */
	public class PixelSlice {
		private final int idx;
		private final float[] vals;
		
		/**
		 * Constructor. Creates a pixel slice for the specified component.
		 * @param idx the slice (component) index
		 */
		PixelSlice(int idx) {
			this.idx = idx;
			this.vals = data[idx];
		}
		
		/** Constructor. Creates an empty (zero-valued) pixel slice with the same
		 * properties as the containing pixel pack but not associated
		 * with it.
		 */
		PixelSlice() {
			this.idx = -1;
			this.vals = new float[length];
		}
		
		/**
		 * Returns the pixel value for the specified image position.
		 * @param u the x-position
		 * @param v the y-position
		 * @return the slice value
		 */
		public float getVal(int u, int v) {
			int i = indexer.getIndex(u, v);
			return (i >= 0) ? vals[i] : 0;
		}
		
		/**
		 * Sets the pixel value at the specified image position.
		 * @param u the x-position
		 * @param v the y-position
		 * @param val the new value
		 */
		public void setVal(int u, int v, float val) {
			int i = indexer.getIndex(u, v);
			if (i >= 0) {
				vals[i] = val;
			}
		}
		
		/**
		 * Returns the slice index for this pixel slice, i.e, the
		 * component number in the containing pixel pack.
		 * -1 is returned if the pixel slice is not associated with
		 * a pixel pack.
		 * @return the slice index
		 */
		public int getIndex() {
			return idx;
		}
		
		/** 
		 * Returns a reference to the data array associated with this pixel slice.
		 * @return the data array
		 */
		public float[] getArray() {
			return vals;
		}
		
		/**
		 * Returns a {@link FloatProcessor} for this {@link PixelSlice} 
		 * sharing the internal pixel data (nothing is copied).
		 * 
		 * @return a {@link FloatProcessor}
		 */
		public FloatProcessor getFloatProcessor() {
			return new FloatProcessor(getWidth(), getHeight(), vals);
		}
		
		/**
		 * Returns the length (number of pixels) of the associated 1D pixel array.
		 * @return the length of the image array
		 */
		public int getLength() {
			return vals.length;
		}
		
		/**
		 * Returns the width of the associated image
		 * (see also {@link GridIndexer2D}).
		 * @return the image width
		 */
		public int getWidth() {
			return PixelPack.this.getWidth();
		}
		
		/**
		 * Returns the height of the associated image
		 * (see also {@link GridIndexer2D}).
		 * @return the image height
		 */
		public int getHeight() {
			return PixelPack.this.getHeight();
		}
		
		/**
		 * Sets all pixel values to zero.
		 */
		public void zero() {
			Arrays.fill(this.vals, 0);
		}
		
		/**
		 * Copies the contents of this pixel slice to another
		 * pixel slice.
		 * @param other the pixel slice to modified
		 */
		public void copyTo(PixelSlice other) {
			System.arraycopy(this.vals, 0, other.vals, 0, this.vals.length);
		}
		
		/**
		 * Returns the pixel values from the 3x3 neighborhood centered at
		 * the specified position.
		 * The 3x3 array {@code nh[x][y]} has the coordinates
		 * x = 0,..,2 and y = 0,..,2; 
		 * the value at position {@code [1][1]} belongs to the 
		 * specified position.
		 * If a non-null array is supplied, it is filled and returned.
		 * If null, a new array is created and returned.
		 * 
		 * @param uc the center x-position
		 * @param vc the center y-position
		 * @param nh a 3x3 array or null
		 * @return a 3x3 array of pixel values
		 */
		public float[][] get3x3Neighborhood(int uc, int vc, float[][] nh) {
			if (nh == null) 
				nh = new float[3][3];
			for (int i = 0; i < 3; i++) {
				int u = uc - 1 + i;
				for (int j = 0; j < 3; j++) {
					int v = vc - 1 + j;
					nh[i][j] = getVal(u, v);
				}
			}
			return nh;
		}
	}
	
	// -------------------------------------------------------------------

	/**
	 * Copies the contents of an image processor to an existing
	 * pixel pack, which must be compatible w.r.t. size and depth.
	 * Does not scale pixel values.
	 * 
	 * @param ip the image processor to be copied
	 */
	public void copyFromImageProcessor(ImageProcessor ip) {
		copyFromImageProcessor(ip, 1.0);
	}
	
	/**
	 * Copies the contents of an image processor to an existing
	 * pixel pack, which must be compatible w.r.t. size and depth.
	 * Applies the specified scale factor to the pixel component values.
	 * 
	 * @param ip the image processor to be copied
	 * @param scale scale factor applied to pixel component values
	 */
	public void copyFromImageProcessor(ImageProcessor ip, double scale) {
		if (!isCompatibleTo(ip) ){
			throw new IllegalArgumentException("copyFromImageProcessor(): incompatible ImageProcessor/PixelPack)");
		}
		if (ip instanceof ByteProcessor)
			copyFromByteProcessor((ByteProcessor)ip, (float) scale);
		else if (ip instanceof ShortProcessor)
			copyFromShortProcessor((ShortProcessor)ip, (float) scale);
		else if (ip instanceof FloatProcessor)
			copyFromFloatProcessor((FloatProcessor)ip, (float) scale);
		else if (ip instanceof ColorProcessor)
			copyFromColorProcessor((ColorProcessor)ip, (float) scale);
		else 
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
	}
	
	private void copyFromByteProcessor(ByteProcessor ip, final float scale) {
		byte[] pixels = (byte[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			data[0][i] = scale * (0xff & pixels[i]);
		}
	}
	
	private void copyFromShortProcessor(ShortProcessor ip, final float scale) {
		short[] pixels = (short[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			data[0][i] = scale * (0xffff & pixels[i]);
		}
	}
	
	private void copyFromFloatProcessor(FloatProcessor ip, final float scale) {
		float[] pixels = (float[]) ip.getPixels();
//		System.arraycopy(pixels, 0, data[0], 0, pixels.length);
		for (int i = 0; i < pixels.length; i++) {
			data[0][i] = scale * pixels[i];
		}
	}
	
	private void copyFromColorProcessor(ColorProcessor ip, final float scale) {
		final int[] pixels = (int[]) ip.getPixels();
		final int[] rgb = new int[3];
		for (int i = 0; i < pixels.length; i++) {
			RgbUtils.decodeIntToRgb(pixels[i], rgb);
			data[0][i] = scale * rgb[0];
			data[1][i] = scale * rgb[1];
			data[2][i] = scale * rgb[2];
		}
	}
	
	// --------------------------------------------------------------------
	
	/**
	 * Converts this {@link PixelPack} to a new {@link ByteProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 1.
	 * Pixel values are rounded, no scale factor is applied.
	 * 
	 * @return a new {@link ByteProcessor} instance
	 */
	public ByteProcessor toByteProcessor() {
		return toByteProcessor(1.0);
	}
	
	/**
	 * Converts this {@link PixelPack} to a new {@link ByteProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 1.
	 * Applies the specified scale factor to pixel values.
	 * 
	 * @param scale scale factor applied to pixel values (before rounding)
	 * @return a new {@link ByteProcessor} instance
	 */
	public ByteProcessor toByteProcessor(double scale) {
		if (depth != 1) {
			throw new UnsupportedOperationException("cannot convert to ByteProcessor, depth = " + depth);
		}
		ByteProcessor ip = new ByteProcessor(width, height);
		copyToByteProcessor(ip, (float)scale);
		return ip;
	}
	
	/**
	 * Converts this {@link PixelPack} to a new {@link ShortProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 1.
	 * Pixel values are rounded, no scale factor is applied.
	 * 
	 * @return a new {@link ShortProcessor} instance
	 */
	public ShortProcessor toShortProcessor() {
		return toShortProcessor(1.0);
	}
	
	/**
	 * Converts this {@link PixelPack} to a new {@link ShortProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 1.
	 * Applies the specified scale factor to pixel values.
	 * 
	 * @param scale scale factor applied to pixel values (before rounding)
	 * @return a new {@link ShortProcessor} instance
	 */
	public ShortProcessor toShortProcessor(double scale) {
		if (depth != 1) {
			throw new UnsupportedOperationException("cannot convert to ShortProcessor, depth = " + depth);
		}
		ShortProcessor ip = new ShortProcessor(width, height);
		copyToShortProcessor(ip, (float)scale);
		return ip;
	}
	
	/**
	 * Converts this {@link PixelPack} to a new {@link FloatProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 1.
	 * No scale factor is applied to pixel values.
	 * 
	 * @return a new {@link FloatProcessor} instance
	 */
	public FloatProcessor toFloatProcessor() {
		return toFloatProcessor(1.0);
	}
	
	/**
	 * Converts this {@link PixelPack} to a new {@link FloatProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 1.
	 * 
	 * @param scale scale factor applied to pixel values
	 * @return a new {@link FloatProcessor} instance
	 */
	public FloatProcessor toFloatProcessor(double scale) {
		if (depth != 1) {
			throw new UnsupportedOperationException("cannot convert to FloatProcessor, depth = " + depth);
		}
		FloatProcessor ip = new FloatProcessor(width, height);
		copyToFloatProcessor(ip, (float)scale);
		return ip;
	}
	
	/**
	 * Converts this {@link PixelPack} to a new {@link ColorProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 3.
	 * Component values are rounded, no scale factor is applied.
	 * 
	 * @return a new {@link ColorProcessor} instance
	 */
	public ColorProcessor toColorProcessor() {
		return toColorProcessor(1.0);
	}
	
	/**
	 * Converts this {@link PixelPack} to a new {@link ColorProcessor} instance.
	 * An exception is thrown if the depth of the pack is not equal 3.
	 * 
	 * @param scale scale factor applied to component values (before rounding)
	 * @return a new {@link ColorProcessor} instance
	 */
	public ColorProcessor toColorProcessor(double scale) {
		if (depth != 3) {
			throw new UnsupportedOperationException("cannot convert to ColorProcessor, depth = " + depth);
		}
		ColorProcessor ip = new ColorProcessor(width, height);
		copyToColorProcessor(ip, (float)scale);
		return ip;
	}
	
	// --------------------------------------------------------------------
	
	/**
	 * Copies the contents of a pixel pack to an existing
	 * image processor. They must be compatible w.r.t. size and depth.
	 * Component values are rounded if necessary, no scale factor is applied.
	 * 
	 * @param ip the receiving image processor
	 */
	public void copyToImageProcessor(ImageProcessor ip) {
		copyToImageProcessor(ip, 1.0);
	}
	
	/**
	 * Copies the contents of a pixel pack to an existing
	 * image processor. They must be compatible w.r.t. size and depth.
	 * Component values are rounded if necessary, after the
	 * specified scale factor is applied.
	 * 
	 * @param ip the receiving image processor
	 * @param scale  scale factor applied to pixel values
	 */
	public void copyToImageProcessor(ImageProcessor ip, double scale) {
		if (!this.isCompatibleTo(ip) ){
			throw new IllegalArgumentException("copyToImageProcessor(): incompatible ImageProcessor/PixelPack)");
		}
		if (ip instanceof ByteProcessor)
			copyToByteProcessor((ByteProcessor)ip, (float)scale);
		else if (ip instanceof ShortProcessor)
			copyToShortProcessor((ShortProcessor)ip, (float)scale);
		else if (ip instanceof FloatProcessor)
			copyToFloatProcessor((FloatProcessor)ip, (float)scale);
		else if (ip instanceof ColorProcessor)
			copyToColorProcessor((ColorProcessor)ip, (float)scale);
		else
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
	}
	
	private void copyToByteProcessor(ByteProcessor ip, float scale) {
		byte[] pixels = (byte[]) ip.getPixels();
		float[] P = this.data[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clipByte(Math.round(scale * P[i]));
			pixels[i] = (byte) val;
		}
	}
	
	private void copyToShortProcessor(ShortProcessor ip, float scale) {
		short[] pixels = (short[]) ip.getPixels();
		float[] P = this.data[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clipShort(Math.round(scale * P[i]));
			pixels[i] = (short) val;
		}
	}
	
	private void copyToFloatProcessor(FloatProcessor ip, float scale) {
		float[] pixels = (float[]) ip.getPixels();
		float[] P = this.data[0];
//		System.arraycopy(P, 0, pixels, 0, P.length);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = scale * P[i];
		}
	}
	
	private void copyToColorProcessor(ColorProcessor ip, float scale) {
		int[] pixels = (int[]) ip.getPixels();
		float[] R = this.data[0];
		float[] G = this.data[1];
		float[] B = this.data[2];
		for (int i = 0; i < pixels.length; i++) {
			int r = clipByte(Math.round(scale * R[i]));
			int g = clipByte(Math.round(scale * G[i]));
			int b = clipByte(Math.round(scale * B[i]));
			pixels[i] = RgbUtils.encodeRgbToInt(r, g, b);
		}
	}
	
	// --------------------------------------------------------------------------
	
	private int clipByte(int val) {
		if (val < 0) return 0;
		if (val > 255) return 255;
		return val;
	}
	
	private int clipShort(int val) {
		if (val < 0) return 0;
		if (val > 65535) return 65535;
		return val;
	}

}

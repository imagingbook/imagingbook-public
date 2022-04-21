package imagingbook.common.image.data;

import java.util.Arrays;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.filter.GenericFilter;
import imagingbook.common.image.access.GridIndexer2D;
import imagingbook.common.image.access.OutOfBoundsStrategy;

/**
 * This class defines a generic data container for scalar and
 * vector-valued images, using float-values throughout.
 * Its primary use is in the {@link GenericFilter} framework. 
 * 
 * @author WB
 * @version 2021/01/14
 */
public class PixelPack {
	
	/** The default out-of-bounds strategy (see {@link OutOfBoundsStrategy}). */
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NearestBorder;

	private final int depth;
	private final float[][] data;
	private final int length;
	private final GridIndexer2D indexer;
	
	// --------------------------------------------------------------------
	
	/**
	 * Constructor. Creates a blank (zero-valued) pack of pixel data.
	 * @param width the image width
	 * @param height the image height
	 * @param depth the number of channels (slices)
	 * @param obs the strategy to be used when reading from out-of-bounds coordinates
	 */
	public PixelPack(int width, int height, int depth, OutOfBoundsStrategy obs) {
		this.depth = depth;
		this.length = width * height;
		this.data = new float[depth][length];
		this.indexer = GridIndexer2D.create(width, height, obs);
	}
	
	/**
	 * Constructor. Creates a pack of pixel data from the given 
	 * {@link ImageProcessor} object. Uses
	 * {@link #DefaultOutOfBoundsStrategy} as the out-of-bounds strategy
	 * (see {@link OutOfBoundsStrategy}).
	 * @param ip the source image
	 */
	public PixelPack(ImageProcessor ip) {
		this(ip, DefaultOutOfBoundsStrategy);
	}
	
	/**
	 * Creates a pack of pixel data from the given 
	 * {@link ImageProcessor} object, using the specified out-of-bounds strategy.
	 * @param ip the source image
	 * @param obs the strategy to be used when reading from out-of-bounds coordinates
	 */
	public PixelPack(ImageProcessor ip, OutOfBoundsStrategy obs) {
		this(ip.getWidth(), ip.getHeight(), ip.getNChannels(), obs);
		copyFromImageProcessor(ip, this);
	}
	
	/**
	 * Constructor. Duplicates an existing {@link PixelPack} without copying 
	 * the contained pixel data.
	 * @param orig the original {@link PixelPack}
	 */
	public PixelPack(PixelPack orig) {
		this(orig, false);
	}
	
	/**
	 * Constructor. Duplicates an existing {@link PixelPack}, optionally copying 
	 * the contained pixel data.
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

	/**
	 * Returns the pixel data at the specified position as a float-vector.
	 * If the supplied array is non-null, it is filled in and returned,
	 * otherwise a new array is returned.
	 * The length of this array corresponds to the number of slices in this
	 * pixel pack.
	 * The values returned at out-of-bounds positions depends on this
	 * pixel-pack's out-of-bounds strategy.
	 * 
	 * @param u the x-position
	 * @param v the y-position
	 * @param vals a suitable 
	 * @return the array of pixel data
	 */
	public float[] getVec(int u, int v, float[] vals) {
		if (vals == null) 
			vals = new float[depth];
		final int i = indexer.getIndex(u, v);
		if (i < 0) {	// i = -1 --> default value (zero)
			Arrays.fill(vals, 0);
		}
		else {	
			for (int k = 0; k < depth; k++) {
				vals[k] = data[k][i];
			}
		}
		return vals;
	}
	
	// returns a new pixel array
	public float[] getVec(int u, int v) {
		return getVec(u, v, new float[depth]);
	}
	
	/**
	 * Sets the pixel data at the specified pixel position.
	 * The length of the value array corresponds to the number of slices in this
	 * pixel pack.
	 * @param u the x-position
	 * @param v the y-position
	 * @param vals a float vector with the values for this pixel
	 */
	public void setVec(int u, int v, float ... vals) {
		final int i = indexer.getIndex(u, v);
		if (i >= 0) {
			for (int k = 0; k < depth && k < vals.length; k++) {
				data[k][i] = vals[k];
			}
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
	 * Returns a reference to the specified {@link PixelSlice}.
	 * An exception is thrown if the specified slice does not exist.
	 * @param k the slice index (0,...,K-1)
	 * @return a reference to the pixel slice
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
	 * Returns a reference to this {@link PixelPack}'s internal data
	 * array, which is always two-dimensional:
	 * dimension 1 is the slice index,
	 * dimension 2 is the pixel index (each slice is a 1D array).
	 * @return the pixel pack's data array
	 */
	public float[][] getArrays() {
		return data;
	}
	
	/**
	 * Returns the width of the associated image.
	 * @return the image width
	 */
	public int getWidth() {
		return this.indexer.getWidth();
	}
	
	/**
	 * Returns the height of the associated image.
	 * @return the image height
	 */
	public int getHeight() {
		return this.indexer.getHeight();
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
				nh[i][j] = getVec(u, v);
			}
		}
		return nh;
	}
	
	/**
	 * Copies the contents of this pixel pack to the supplied {@link ImageProcessor}
	 * instance, if compatible. Otherwise an exception is thrown.
	 * 
	 * @param ip the target image processor
	 */
	public void copyToImageProcessor(ImageProcessor ip) {
		copyToImageProcessor(this, ip);
	}
	
	// -------------------------------------------------------------------
	
	/**
	 * Inner class representing a single (scalar-valued) pixel slice of a 
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
	 * Utility method. Copies the contents of an image processor to an existing
	 * pixel pack. They must be compatible w.r.t. size and depth.
	 * 
	 * @param ip the image processor to be copied
	 * @param pack the receiving pixel pack
	 */
	public static void copyFromImageProcessor(ImageProcessor ip, PixelPack pack) {
		if (!pack.isCompatibleTo(ip) ){
			throw new IllegalArgumentException("copyFromImageProcessor(): incompatible ImageProcessor/PixelPack)");
		}
		if (ip instanceof ByteProcessor)
			copyFromByteProcessor((ByteProcessor)ip, pack);
		else if (ip instanceof ShortProcessor)
			copyFromShortProcessor((ShortProcessor)ip, pack);
		else if (ip instanceof FloatProcessor)
			copyFromFloatProcessor((FloatProcessor)ip, pack);
		else if (ip instanceof ColorProcessor)
			copyFromColorProcessor((ColorProcessor)ip, pack);
		else 
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
	}
	
	private static void copyFromByteProcessor(ByteProcessor ip, PixelPack pack) {
		final float[][] P = pack.data;
		byte[] pixels = (byte[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			P[0][i] = 0xff & pixels[i];
		}
	}
	
	private static void copyFromShortProcessor(ShortProcessor ip, PixelPack pack) {
		final float[][] P = pack.data;
		short[] pixels = (short[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			P[0][i] = 0xffff & pixels[i];
		}
	}
	
	private static void copyFromFloatProcessor(FloatProcessor ip, PixelPack pack) {
		final float[][] P = pack.data;
		float[] pixels = (float[]) ip.getPixels();
		System.arraycopy(pixels, 0, P[0], 0, pixels.length);
	}
	
	private static void copyFromColorProcessor(ColorProcessor ip, PixelPack pack) {
		final float[][] P = pack.data;
		final int[] pixels = (int[]) ip.getPixels();
		final int[] rgb = new int[3];
		for (int i = 0; i < pixels.length; i++) {
			RgbUtils.intToRgb(pixels[i], rgb);
			P[0][i] = rgb[0];
			P[1][i] = rgb[1];
			P[2][i] = rgb[2];
		}
	}
	
	// --------------------------------------------------------------------
	
	/**
	 * Copies the contents of a pixel pack to an existing
	 * image processor. They must be compatible w.r.t. size and depth.
	 * 
	 * @param pack the source pixel pack
	 * @param ip the receiving image processor
	 */
	public static void copyToImageProcessor(PixelPack pack, ImageProcessor ip) {
		if (!pack.isCompatibleTo(ip) ){
			throw new IllegalArgumentException("copyToImageProcessor(): incompatible ImageProcessor/PixelPack)");
		}
		if (ip instanceof ByteProcessor)
			copyToByteProcessor(pack, (ByteProcessor)ip);
		else if (ip instanceof ShortProcessor)
			copyToShortProcessor(pack, (ShortProcessor)ip);
		else if (ip instanceof FloatProcessor)
			copyToFloatProcessor(pack, (FloatProcessor)ip);
		else if (ip instanceof ColorProcessor)
			copyToColorProcessor(pack, (ColorProcessor)ip);
		else
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
	}
	
	private static void copyToByteProcessor(PixelPack pack, ByteProcessor ip) {
		byte[] pixels = (byte[]) ip.getPixels();
		float[] P = pack.data[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clampByte(Math.round(P[i]));
			pixels[i] = (byte) val;
		}
	}
	
	private static void copyToShortProcessor(PixelPack pack, ShortProcessor ip) {
		short[] pixels = (short[]) ip.getPixels();
		float[] P = pack.data[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clampShort(Math.round(P[i]));
			pixels[i] = (short) val;
		}
	}
	
	private static void copyToFloatProcessor(PixelPack pack, FloatProcessor ip) {
		float[] pixels = (float[]) ip.getPixels();
		float[] P = pack.data[0];
		System.arraycopy(P, 0, pixels, 0, P.length);
	}
	
	private static void copyToColorProcessor(PixelPack pack, ColorProcessor ip) {
		int[] pixels = (int[]) ip.getPixels();
		float[] R = pack.data[0];
		float[] G = pack.data[1];
		float[] B = pack.data[2];
		for (int i = 0; i < pixels.length; i++) {
			int r = clampByte(Math.round(R[i]));
			int g = clampByte(Math.round(G[i]));
			int b = clampByte(Math.round(B[i]));
			pixels[i] = RgbUtils.rgbToInt(r, g, b);
		}
	}
	
	// --------------------------------------------------------------------------
	
	private static int clampByte(int val) {
		if (val < 0) return 0;
		if (val > 255) return 255;
		return val;
	}
	
	private static int clampShort(int val) {
		if (val < 0) return 0;
		if (val > 65535) return 65535;
		return val;
	}

}

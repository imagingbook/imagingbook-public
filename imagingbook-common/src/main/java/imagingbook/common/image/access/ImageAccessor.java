/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image.access;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.interpolation.InterpolationMethod;

/**
 * <p>
 * An 'image accessor' is a wrapper around some {@link ImageProcessor} object to allow
 * unified (read and write) access to its pixels values.
 * This abstract class defines unified access functionality to all 4 types of images available in ImageJ:
 * 8-bit, 16-bit, float, and color images.
 * All pixel values are of type {@code float[]}, either containing a single element (for
 * scalar-valued images) or three elements (for color images).
 * </p>
 * <p>
 * A generic {@link ImageAccessor} is created, e.g., by {@link #create(ImageProcessor)}, which
 * returns an instance of {@link ByteAccessor}, {@link ShortAccessor}, {@link FloatAccessor} or
 * {@link RgbAccessor}.
 * {@link ImageAccessor} itself can access any ImageJ image using 
 * the methods {@link #getPix(int, int)}, {@link #getPix(double, double)}
 * for retrieving pixel values and {@link #setPix(int, int, float[])}
 * to modify pixel values.
 * </p>
 * <p>
 * In addition, the accessors for scalar-valued images ({@link ByteAccessor}, {@link ShortAccessor},
 * {@link FloatAccessor}) provide the methods
 * {@link ScalarAccessor#getVal(int, int)}, {@link ScalarAccessor#getVal(double, double)} and 
 * {@link ScalarAccessor#setVal(int, int, float)}
 * to read and write scalar-valued pixels passed as single {@code float} values.
 * <br>
 * The methods {@link #getPix(double, double)} and {@link ScalarAccessor#getVal(double, double)} perform interpolation at non-integer coordinates
 * using the specified {@link InterpolationMethod}.
 * </p>
 * 
 * @author WB
 * @version 2020/12/27
 */
public abstract class ImageAccessor {
	
	static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.ZeroValues;
	static final InterpolationMethod DefaultInterpolationMethod = InterpolationMethod.Bilinear;
	
	protected final ImageProcessor ip;
	protected final int width;
	protected final int height;
	protected final GridIndexer2D indexer;		// implements the specified OutOfBoundsStrategy
	protected final OutOfBoundsStrategy outOfBoundsStrategy;
	protected final InterpolationMethod interpolationMethod;
	
	/**
	 * Creates a new {@code ImageAccessor} instance for the given image,
	 * using the default out-of-bounds strategy and interpolation methods.
	 * The concrete type of the returned instance depends on the specified image.
	 * 
	 * @param ip the source image
	 * @return a new {@code ImageAccessor} instance
	 */
	public static final ImageAccessor create(ImageProcessor ip) {
		return create(ip, DefaultOutOfBoundsStrategy, DefaultInterpolationMethod);
	}
	
	/**
	 * Creates a new {@code ImageAccessor} instance for the given image,
	 * using the specified out-of-bounds strategy and interpolation methods.
	 * The concrete type of the returned instance depends on the specified image.
	 * 
	 * @param ip the source image
	 * @param obs the out-of-bounds strategy (use {@code null} for default settings)
	 * @param ipm the interpolation method (use {@code null} for default settings)
	 * @return a new {@code ImageAccessor} instance
	 */
	public static ImageAccessor create(ImageProcessor ip,  OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		if (ip instanceof ColorProcessor) {
			return new RgbAccessor((ColorProcessor)ip, obs, ipm);
		}
		else {
			return ScalarAccessor.create(ip, obs, ipm);
		}
	}
	
	// constructor (used by all subtypes)
	protected ImageAccessor(ImageProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		this.ip = ip;
		this.width  = ip.getWidth();
		this.height = ip.getHeight();
		this.outOfBoundsStrategy = (obs != null) ? obs : DefaultOutOfBoundsStrategy;
		this.interpolationMethod = (ipm != null) ? ipm : DefaultInterpolationMethod;
		this.indexer = GridIndexer2D.create(width, height, this.outOfBoundsStrategy);
	}
	
	/**
	 * Returns the width of the associated image.
	 * @return the image width.
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of the associated image.
	 * @return the image height.
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the source {@link ImageProcessor} associated with this
	 * {@link ImageAccessor}.
	 * 
	 * @return the image processor
	 */
	public ImageProcessor getProcessor() {
		return this.ip;
	}
	
	/**
	 * Returns the depth (number of components) of this image
	 * accessor. 1 is returned if the image is scalar-valued.
	 * 
	 * @return the image depth.
	 */
	public abstract int getDepth();
	
	/**
	 * Returns the {@link OutOfBoundsStrategy} specified for this
	 * {@link ImageAccessor}.
	 * 
	 * @return the out-of-bounds strategy
	 */
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return outOfBoundsStrategy;
	}

	/**
	 * Returns the {@link InterpolationMethod} specified for this
	 * {@link ImageAccessor}.
	 * 
	 * @return the interpolation method
	 */
	public InterpolationMethod getInterpolationMethod() {
		return interpolationMethod;
	}
	
	/**
	 * Returns the pixel value for the specified floating-point 
	 * position as a {@code float[]} with either 1 element for scalar-valued images
	 * and or more elements (e.g., 3 for for RGB images).
	 * 
	 * @param u the x-coordinate
	 * @param v the y-coordinate
	 * @return the pixel value ({@code float[]})
	 */
	public abstract float[] getPix(int u, int v);
	
	/**
	 * Returns the interpolated pixel value for the specified floating-point 
	 * position as a {@code float[]} with either 1 element for scalar-valued images
	 * and or more elements (e.g., 3 for for RGB images).
	 * Interpolation is used non-integer coordinates.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the interpolated pixel value ({@code float[]})
	 */
	public abstract float[] getPix(double x, double y);		// returns interpolated pixel value at real position (x, y)
	
	/**
	 * Sets the pixel value at the specified integer position.
	 * The new value must be provided as {@code float[]} with 
	 * 1 element for scalar-valued images or 3 elements for RGB images.
	 * @param u the x-coordinate
	 * @param v the y-coordinate
	 * @param val the new pixel value ({@code float[]})
	 */
	public abstract void setPix(int u, int v, float[] val);
	
	/**
	 * Returns the value of the pixel's k-th component at the
	 * specified position. If the associated image is scalar-valued,
	 * this is equivalent to component 0.
	 * See also {@link #getDepth()}.
	 * 
	 * @param u the x-coordinate
	 * @param v the y-coordinate
	 * @param k the component index
	 * @return the component value ({@code float})
	 */
	public abstract float getVal(int u, int v, int k);
	
	
	/**
	 * Returns the interpolated value of the pixel's k-th component at the
	 * specified position. If the associated image is scalar-valued,
	 * this is equivalent to component 0.
	 * See also {@link #getDepth()}.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param k the component index
	 * @return the interpolated component value ({@code float[]})
	 */
	public abstract float getVal(double x, double y, int k);
	
	/**
	 * Sets the value of the pixel's k-th component at the
	 * specified position. If the associated image is scalar-valued,
	 * this is equivalent to component 0.
	 * See also {@link #getDepth()}.
	 * 
	 * @param u the x-coordinate
	 * @param v the y-coordinate
	 * @param k the component index
	 * @param val the new component value
	 */
	public abstract void setVal(int u, int v, int k, float val);
	
	/**
	 * Returns the {@link ImageAccessor} for the k-th component;
	 * the result is a sub-type of {@link ScalarAccessor}.
	 * In the case of a scalar-valued image, THIS object is returned.
	 * 
	 * @param k the component index
	 * @return the component accessor.
	 */
	public abstract ScalarAccessor getComponentAccessor(int k);
	
	protected abstract void checkComponentIndex(int k);
		
}

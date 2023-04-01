/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.image;

import ij.process.ImageProcessor;

/**
 * <p>
 * Instances of this class perform the transformation between 2D image coordinates and indexes into the associated 1D
 * pixel array and vice versa. As usual images are assumed to be stored in row-major order. Instances of this class do
 * not hold any image data themselves, they just perform the indexing task. This is used, for example, by class
 * {@link PixelPack}, which provides a universal image data container which uses {@link GridIndexer2D} internally.
 * </p>
 * <p>
 * The (abstract) method {@link #getIndex(int, int)} returns the 1D array index for a pair of 2D image coordinates. It
 * is implemented by the inner subclasses {@link DefaultValueIndexer}, {@link MirrorImageIndexer} and
 * {@link NearestBorderIndexer}. They exhibit different behaviors when accessing out-of-image coordinates (see
 * {@link OutOfBoundsStrategy}).
 * </p>
 *
 * @author WB
 * @version 2022/09/17
 * @see OutOfBoundsStrategy
 */
public abstract class GridIndexer2D {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NearestBorder;

	/**
	 * Creates and returns a new {@link GridIndexer2D} with the specified size and {@link OutOfBoundsStrategy}.
	 *
	 * @param width grid size (horizontal)
	 * @param height grid size (vertical)
	 * @param obs out-of-bounds strategy
	 * @return a new {@link GridIndexer2D}
	 */
	public static GridIndexer2D create(int width, int height, OutOfBoundsStrategy obs) {
		if (obs == null) {
			obs = DefaultOutOfBoundsStrategy;
		}
		switch (obs) {
		case DefaultValue: return new DefaultValueIndexer(width, height);
		case NearestBorder	: return new NearestBorderIndexer(width, height);
		case MirrorImage	: return new MirrorImageIndexer(width, height);
		case ThrowException	: return new ExceptionIndexer(width, height);
		}
		return null;
	}

	/**
	 * Creates and returns a new {@link GridIndexer2D} for the specified image and {@link OutOfBoundsStrategy}.
	 * @param ip the image to be associated with the returned indexer
	 * @param obs out-of-bounds strategy
	 * @return a new {@link GridIndexer2D}
	 */
	public static GridIndexer2D create(ImageProcessor ip, OutOfBoundsStrategy obs) {
		return create(ip.getWidth(), ip.getHeight(), obs);
	}
	
	final int width;
	final int height;
	final OutOfBoundsStrategy obs;

	private GridIndexer2D(int width, int height, OutOfBoundsStrategy obs) {
		this.width = width;
		this.height = height;
		this.obs = obs;
	}

	/**
	 * Returns the 1D array index for a given pair of image coordinates. For u, v coordinates outside the image, the
	 * returned index depends on the implementing subclass of {@link GridIndexer2D}. As a general rule, this method either
	 * returns a valid 1D array index or throws an exception.
	 * Subclasses implement (override) this method.
	 *
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return 1D array index
	 */
	public abstract int getIndex(int u, int v);

	/**
	 * Returns the 1D array index for a given pair of image coordinates, assuming that the specified position is
	 * inside the image.
	 *
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return the associated 1D index
	 */
	protected int getWithinBoundsIndex(int u, int v) {
		return width * v + u;
	}
	
	/**
	 * Returns the width of the associated image.
	 * @return the image width
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of the associated image.
	 * @return the image height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the out-of-bounds strategy (see {qlink OutOfBoundsStrategy} used by this indexer.
	 *
	 * @return the out-of-bounds strategy
	 */
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return this.obs;
	}
	
	// ---------------------------------------------------------

	/**
	 * This indexer returns the closest border pixel for coordinates outside the image bounds. This is the most common
	 * method. There is no public constructor. To instantiate use method
	 * {@link GridIndexer2D#create(int, int, OutOfBoundsStrategy)} with {@link OutOfBoundsStrategy#NearestBorder}.
	 */
	public static class NearestBorderIndexer extends GridIndexer2D {
		
		NearestBorderIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.NearestBorder);
		}

		@Override
		public int getIndex(int u, int v) {
			if (u < 0) {
				u = 0;
			}
			else if (u >= width) {
				u = width - 1;
			}
			if (v < 0) {
				v = 0;
			}
			else if (v >= height) {
				v = height - 1;
			}
			return getWithinBoundsIndex(u, v);
		}
	}

	/**
	 * This indexer returns mirrored image values for coordinates outside the image bounds. There is no public
	 * constructor. To instantiate use method {@link GridIndexer2D#create(int, int, OutOfBoundsStrategy)} with
	 * {@link OutOfBoundsStrategy#MirrorImage}.
	 */
	public static class MirrorImageIndexer extends GridIndexer2D {
		
		MirrorImageIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.MirrorImage);
		}

		@Override
		public int getIndex(int u, int v) {
			// fast modulo operation for positive divisors only
			u = u % width;
			if (u < 0) {
				u = u + width; 
			}
			v = v % height;
			if (v < 0) {
				v = v + height; 
			}
			return getWithinBoundsIndex(u, v);
		}
	}

	/**
	 * This indexer returns -1 for coordinates outside the image bounds, indicating that a (predefined) default pixel value
	 * should be used. There is no public constructor. To instantiate use method
	 * {@link GridIndexer2D#create(int, int, OutOfBoundsStrategy)} with {@link OutOfBoundsStrategy#DefaultValue}.
	 */
	public static class DefaultValueIndexer extends GridIndexer2D {
		
		DefaultValueIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.DefaultValue);
		}

		@Override
		public int getIndex(int u, int v) {
			if (u < 0 || u >= width || v < 0 || v >= height) {
				return -1;
			}
			else {
				return getWithinBoundsIndex(u, v);
			}
		}
	}

	/**
	 * This indexer throws an exception if coordinates outside image bounds are accessed. There is no public
	 * constructor. To instantiate use method {@link GridIndexer2D#create(int, int, OutOfBoundsStrategy)} with
	 * {@link OutOfBoundsStrategy#ThrowException}.
	 */
	public static class ExceptionIndexer extends GridIndexer2D {
		
		ExceptionIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.ThrowException);
		}

		@Override
		public int getIndex(int u, int v) throws OutOfImageException {
			if (u < 0 || u >= width || v < 0 || v >= height) {
				throw new OutOfImageException(
						String.format("out-of-image position [%d,%d]", u, v));
			}
			else 
				return getWithinBoundsIndex(u, v);
		}
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Exception to be thrown by {@link ExceptionIndexer}.
	 */
	public static class OutOfImageException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public OutOfImageException(String message) {
			super(message);
		}
	}

}



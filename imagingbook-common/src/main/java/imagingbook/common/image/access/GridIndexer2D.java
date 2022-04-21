/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image.access;

import imagingbook.common.image.data.PixelPack;

/**
 * <p>Instances of this class perform the transformation between 2D image coordinates 
 * and indexes into the associated 1D pixel array and vice versa.
 * As usual images are assumed to be stored in row-major order.
 * Objects of this class do not hold any image date themselves, they just
 * perform the indexing task. 
 * Class {@link PixelPack} provides a universal image data container which 
 * uses {@link GridIndexer2D} internally.
 * </p>
 * <p>
 * The (abstract) method {@link #getIndex(int, int)} returns the 1D array index
 * for a pair of 2D image coordinates. It is implemented by
 * the inner subclasses {@link ZeroValueIndexer}, {@link MirrorImageIndexer} and
 * {@link NearestBorderIndexer}. They exhibit different behaviors when accessing
 * out-of-image coordinates (see {@link OutOfBoundsStrategy}).
 * </p>
 */
public abstract class GridIndexer2D implements Cloneable {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NearestBorder;
	
	public static GridIndexer2D create(int width, int height, OutOfBoundsStrategy obs) {
		obs = (obs != null) ? obs : DefaultOutOfBoundsStrategy;
		switch (obs) {
		case ZeroValues 		: return new ZeroValueIndexer(width, height);
		case NearestBorder		: return new NearestBorderIndexer(width, height);
		case MirrorImage		: return new MirrorImageIndexer(width, height);
		case ThrowException	: return new ExceptionIndexer(width, height);
		}
		return null;
	}
	
	protected final int width;
	protected final int height;
	protected final OutOfBoundsStrategy obs;

	private GridIndexer2D(int width, int height, OutOfBoundsStrategy obs) {
		this.width = width;
		this.height = height;
		this.obs = obs;
	}
	
	/**
	 * Returns the 1D array index for a given pair of image coordinates.
	 * Subclasses implement (override) this method.
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return 1D array index
	 */
	public abstract int getIndex(int u, int v);
	
	private int getWithinBoundsIndex(int u, int v) {
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
	 * Returns the out-of-bounds strategy (see {qlink OutOfBoundsStrategy} 
	 * used by this indexer.
	 * @return the out-of-bounds strategy
	 */
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return this.obs;
	}
	
	// ---------------------------------------------------------

	/** 
	 * This indexer returns the closest border pixel for coordinates
	 * outside the image bounds. This is the most common method.
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
			return super.getWithinBoundsIndex(u, v);
		}
	}
	
	/** 
	 * This indexer returns mirrored image values for coordinates
	 * outside the image bounds. 
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
			return super.getWithinBoundsIndex(u, v);
		}
	}
	
	/** 
	 * This indexer returns -1 for coordinates outside the image
	 * bounds, indicating that a (predefined) default value should be used.
	 */
	public static class ZeroValueIndexer extends GridIndexer2D {
		
		ZeroValueIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.ZeroValues);
		}

		@Override
		public int getIndex(int u, int v) {
			if (u < 0 || u >= width || v < 0 || v >= height) {
				return -1;
			}
			else {
				return super.getWithinBoundsIndex(u, v);
			}
		}
	}
	
	/**
	 * This indexer throws an exception if coordinates outside
	 * image bounds are accessed.
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
				return super.getWithinBoundsIndex(u, v);
		}
	}
	
	// -----------------------------------------------------------
	
	public static class OutOfImageException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public OutOfImageException(String message) {
			super(message);
		}
	}

}



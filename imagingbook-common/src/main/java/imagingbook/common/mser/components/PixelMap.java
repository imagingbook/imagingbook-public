/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.mser.components;

import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * Basically a 2D array of pixels which holds all necessary information about the image geometry, keeps track of which
 * pixels have been visited and knows how to access neighboring pixels (currently 4-neighborhood only).
 *
 * @author WB
 * @version 2022/11/19
 */
public class PixelMap {
	// TODO: Bring in line with binary region neighborhoods (type).

	/** Image width */
	public final int width;
	
	/** Image height */
	public final int height;
	
	private final Pixel[][] pixels;
	
	/**
	 * Constructor.
	 * @param ip source image
	 */
	public PixelMap(ImageProcessor ip) {
		this.width = ip.getWidth();
		this.height = ip.getHeight();
		this.pixels = makeImagePoints(ip);
	}
	
	private Pixel[][] makeImagePoints(ImageProcessor ip) {
		Pixel[][] ipts = new Pixel[width][height];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				ipts[u][v] = new Pixel(u, v, ip.get(u, v));
			}
		}
		return ipts;
	}

	/**
	 * Returns the {@link Pixel} instance at the specified position.
	 *
	 * @param u horizontal position
	 * @param v vertical position
	 * @return the {@link Pixel} instance
	 */
	public Pixel getPixel(int u, int v) {
		return pixels[u][v];
	}

	/**
	 * Returns a new 1D array (i.e., a "flattened" vector in row-first order) of {@link Pixel} elements, e.g., for
	 * sorting pixels by value.
	 *
	 * @return a 1D array of pixels
	 */
	public Pixel[] getPixelVector() {
		final Pixel[] pix = new Pixel[width * height];
		int i = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				pix[i] = pixels[u][v];
				i++;
			}
		}
		return pix;
	}
	
	/**
	 * Sets all pixels to unvisited and resets next-neighbor search directions.
	 */
	void reset() {
		//this.visited.unsetAll();
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				pixels[u][v].reset();
			}
		}
	}
	
	// --------------------------------------------------------
	
	private static final int[] dX = {1, 0, -1, 0};
	private static final int[] dY = {0, -1, 0, 1};

	/**
	 * A pixel value which knows its coordinates. This is a non-static class, i.e., {@link Pixel} instances can only
	 * exist in the context of a {@link PixelMap} instance.
	 */
	public class Pixel extends PntInt implements Comparable<Pixel> {
		
		public final int val;		// the pixel value
		private byte dir = 0;		// next-neighbor search direction

		// only the enclosing PixelMap can instantiate pixels
		public Pixel(int x, int y, int val) {
			super(x, y);	// Pnt2d.PntInt
			this.val = val;
		}
		
		/**
		 * Sets this pixel to unvisited and resets its next-neighbor search direction.
		 */
		public void reset() {
			this.dir = 0;
		}

		/**
		 * Gets the next neighbor of this pixel that is inside the containing image.
		 *
		 * @return the next neighboring {@link Pixel} or {@code null} if no more neighbors
		 */
		public Pixel getNextNeighbor() {
			int u = -1, v = -1;
			boolean found = false;
			while (this.dir < 4 && !found) {
				// try direction dir
				u = this.x + dX[this.dir];	// coordinates of neighbor in direction n
				v = this.y + dY[this.dir];
				found = (u >= 0 && u < width && v >= 0 && v < height);
				this.dir++;
			}
			if (found) {
				return PixelMap.this.getPixel(u, v);
			}
			return null;
		}

		@Override	// sorts by increasing val
		public int compareTo(Pixel other) {
			//return val - other.val;
			return Integer.compare(this.val, other.val);
		}

		@Override
		public String toString() {
			return String.format("%s[x=%d, y=%d, val=%d]", Pixel.class.getSimpleName(), x, y, val);
		}

	}
}

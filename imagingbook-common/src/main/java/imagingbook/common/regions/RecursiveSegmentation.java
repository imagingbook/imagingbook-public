/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.regions;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;

/**
 * <p>
 * Binary region segmentation based on a recursive (depth-first) flood filling
 * algorithm. See Sec. 8.1.1 (Alg. 8.2) of [1] for additional details.
 * Note that this implementation may easily run out of stack space and
 * should be used for demonstration purposes only.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2020/04/01
 * @version 2022/09/28 revised
 */
public class RecursiveSegmentation extends BinaryRegionSegmentation {

	/**
	 * Constructor. Creates a new region segmentation from the specified image,
	 * which is not modified. The input image is considered binary, with 0 values
	 * for background pixels and values &ne; 0 for foreground pixels.
	 * The 4-neighborhood is used by default ({@link BinaryRegionSegmentation#DefaultNeighborhoodT}).
	 * 
	 * @param ip the binary input image to be segmented
	 */
	public RecursiveSegmentation(ByteProcessor ip) {
		this(ip, DefaultNeighborhoodT);
	}
	
	/**
	 * Constructor. Creates a new region segmentation from the specified image and
	 * neighborhood type (4- or 8-neighborhood). The input image is considered
	 * binary, with 0 values for background pixels and values &ne; 0 for foreground
	 * pixels.
	 * 
	 * @param ip the binary input image to be segmented
	 * @param nh the neighborhood type (4- or 8-neighborhood)
	 */
	public RecursiveSegmentation(ByteProcessor ip, NeighborhoodType2D nh) {
		super(ip, nh);
	}
	
	@Override
	boolean applySegmentation(ByteProcessor ip) {
		try{
			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					if (getLabel(u, v) == Foreground) {	// = unlabeled foreground
						// start a new region
						int label = getNextLabel();
						floodFill(u, v, label);
					}
				}
			}
		} catch(StackOverflowError e) {
			return false;
		}
		return true;
	}

	private void floodFill(int x, int y, int label) {
		if ((x >= 0) && (x < width) && (y >= 0) && (y < height) && getLabel(x, y) == Foreground) {
			setLabel(x, y, label);
			floodFill(x + 1, y, label);
			floodFill(x, y + 1, label);
			floodFill(x, y - 1, label);
			floodFill(x - 1, y, label);
			if (NT == NeighborhoodType2D.N8) {
				floodFill(x + 1, y + 1, label);
				floodFill(x - 1, y + 1, label);
				floodFill(x + 1, y - 1, label);
				floodFill(x - 1, y - 1, label);
			}
		}
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.regions;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

import java.util.Deque;
import java.util.LinkedList;

/**
 * <p>
 * Binary region segmentation based on a breadth-first flood filling algorithm using a queue. See Sec. 8.1.1 (Alg. 8.2)
 * of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/28 revised
 */
public class BreadthFirstSegmentation extends BinaryRegionSegmentation {

	/**
	 * Constructor. Creates a new region segmentation from the specified image, which is not modified. The input image
	 * is considered binary, with 0 values for background pixels and values &ne; 0 for foreground pixels. The
	 * 4-neighborhood is used by default ({@link BinaryRegionSegmentation#DefaultNeighborhoodT}).
	 *
	 * @param ip the binary input image to be segmented
	 */
	public BreadthFirstSegmentation(ByteProcessor ip) {
		this(ip, DefaultNeighborhoodT);
	}

	/**
	 * Constructor. Creates a new region segmentation from the specified image and neighborhood type (4- or
	 * 8-neighborhood). The input image is considered binary, with 0 values for background pixels and values &ne; 0 for
	 * foreground pixels.
	 *
	 * @param ip the binary input image to be segmented
	 * @param nh the neighborhood type (4- or 8-neighborhood)
	 */
	public BreadthFirstSegmentation(ByteProcessor ip, NeighborhoodType2D nh) {
		super(ip, nh);
	}
	
	@Override
	boolean applySegmentation(ByteProcessor ip) {
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) == Foreground) {
					// start a new region
					int label = getNextLabel();
					//IJ.log(String.format("assigning label %d at (%d,%d), maxLabel=%d", label, u, v, maxLabel));
					floodFill(u, v, label);
				}
			}
		}
		return true;
	}

	private void floodFill(int u, int v, int label) {
		Deque<PntInt> Q = new LinkedList<>();	//queue contains pixel coordinates
		Q.addLast(PntInt.from(u, v));
		while (!Q.isEmpty()) {
			PntInt p = Q.removeFirst();	// get the next point to process
			int x = p.x;
			int y = p.y;
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height) && getLabel(x, y) == Foreground) {
				setLabel(x, y, label);
				Q.addLast(PntInt.from(x + 1, y));
				Q.addLast(PntInt.from(x, y + 1));
				Q.addLast(PntInt.from(x, y - 1));
				Q.addLast(PntInt.from(x - 1, y));
				if (NT == NeighborhoodType2D.N8) {
					Q.addLast(PntInt.from(x + 1, y + 1));
					Q.addLast(PntInt.from(x - 1, y + 1));
					Q.addLast(PntInt.from(x + 1, y - 1));
					Q.addLast(PntInt.from(x - 1, y - 1));
				}
			}
		}
	}

}

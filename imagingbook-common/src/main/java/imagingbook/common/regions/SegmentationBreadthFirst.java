/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.regions;

import java.util.Deque;
import java.util.LinkedList;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.regions.segment.BinaryRegionSegmentation;
import imagingbook.common.regions.segment.BreadthFirstSegmentation;

/**
 * Binary region labeler based on a breadth-first flood filling
 * algorithm using a stack.
 * Detected regions are 8-connected.
 * 
 * @author WB
 * @version 2020/04/01
 * @deprecated Replaced by {@link BreadthFirstSegmentation}.
 */
public class SegmentationBreadthFirst extends BinaryRegionSegmentation {
	
	/**
	 * Constructor. Creates a new breadth-first (flood-fill) binary region segmenter.
	 * 
	 * @param ip A binary input image with 0 values for background pixels and values &gt; 0
	 * for foreground pixels.
	 */
	public SegmentationBreadthFirst(ByteProcessor ip) {
		this(ip, DEFAULT_NEIGHBORHOOD);
	}
	
	public SegmentationBreadthFirst(ByteProcessor ip, NeighborhoodType2D nh) {
		super(ip, nh);
	}
	
	@Override
	protected boolean applySegmentation() {
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) == FOREGROUND) {
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
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height) && getLabel(x, y) == FOREGROUND) {
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

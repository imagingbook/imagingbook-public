/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.filter.nonlinear;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.GuiTools;

public class CircularMask {
	
	private final int center;			// mask center position
	private final int count;			// number of nonzero mask elements
	private final int[][] mask;			// mask[x][y]  specifies the support region
		
	public CircularMask(double radius) {
		center = Math.max((int) Math.ceil(radius), 1);
		//IJ.log("mask radius = " + center);
		int mWidth = 2 * center + 1;		// width/height of mask array
		mask = new int[mWidth][mWidth];			// initialized to zero
		int cnt = 0;
		double r2 = radius * radius + 1; 		// add 1 to get mask shape similar to ImageJ
		for (int u = 0; u < mWidth; u++) {
			int x = u - center;
			for (int v = 0; v < mWidth; v++) {
				int y = v - center;
				if (x*x + y*y <= r2) {
					mask[u][v] = 1;
					cnt = cnt + 1;
				}
			}
		}
		count = cnt;
	}
	
	public int getCenter() {
		return center;
	}
	
	public int getCount() {
		return count;
	}
	
	public int[][] getMask() {
		return mask;
	}
	
	public void show(String title) {
		ByteProcessor bp = new FloatProcessor(this.getMask()).convertToByteProcessor(false);
		bp.threshold(0);
		ImagePlus im = new ImagePlus(title, bp);
		im.show();
		GuiTools.zoomExact(im, 32);
	}

}

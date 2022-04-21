/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Filters;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.Arrays;

/** 
 * A hand-crafted median filter for 8-bit grayscale images.
 * 
 * @author WB
 *
 */
public class Filter_Median implements PlugInFilter {

	final int r = 3; // the size of the filter is (2r+1)x(2r+1)

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		ImageProcessor copy = ip.duplicate();

		// vector to hold pixels from (2K+1)x(2K+1) neighborhood:
		int[] A = new int[(2 * r + 1) * (2 * r + 1)];
		
		// index of center vector element:
		int n = 2 * (r * r + r);
		
		for (int u = r; u <= M - r - 2; u++) {
			for (int v = r; v <= N - r - 2; v++) {
				// fill the pixel vector A for filter position (u,v):
				int k = 0;
				for (int i = -r; i <= r; i++) {
					for (int j = -r; j <= r; j++) {
						A[k] = copy.getPixel(u + i, v + j);
						k++;
					}
				}
				// sort vector A and take the center element A[n]:
				Arrays.sort(A);
				ip.putPixel(u, v, A[n]);
			}
		}
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch02_HistogramsStatistics;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Compute_Histogram implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G + NO_CHANGES; 
	}
    
	public void run(ImageProcessor ip) {
		int[] h = new int[256]; // histogram array
		final int M  = ip.getWidth();
		final int N = ip.getHeight();

		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int i = ip.getPixel(u, v);
				h[i] = h[i] + 1;
			}
		}

		// ... histogram h[] can now be used
		IJ.showMessage("This plugin only calculates the histogram but does not show anything");
	}
}

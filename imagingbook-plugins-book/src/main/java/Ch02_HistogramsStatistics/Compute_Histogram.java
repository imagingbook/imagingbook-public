/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch02_HistogramsStatistics;

import Ch03_PointOperations.Equalize_Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * <p>
 * ImageJ plugin which calculates (and lists) the histogram of
 * a 8-bit grayscale image. Alternatively the histogram could be 
 * obtained with ImageJs built-in method (see also {@link Equalize_Histogram}):
 * </p>
 * <pre>
 * int[] h = ip.getHistogram();</pre>
 * <p>
 * See Sec. 2.3 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public class Compute_Histogram implements PlugInFilter {

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES; 
	}
    
	@Override
	public void run(ImageProcessor ip) {
		int[] h = new int[256]; 	// histogram array (initialized to zero values)
		final int M  = ip.getWidth();
		final int N = ip.getHeight();

		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int i = ip.getPixel(u, v);
				h[i] = h[i] + 1;
			}
		}
		
		// alternative to the above:
		// int[] h = ip.getHistogram();

		// ... histogram h[] may now be used
		
		// listing histogram values:
		IJ.log("  i       h[i]");
		for (int i = 0; i < h.length; i++) {
			IJ.log(String.format("%3d: %8d", i, h[i]));
		}
	}
}

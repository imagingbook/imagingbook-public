/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch03_PointOperations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * <p>
 * This ImageJ plugin increases the contrast of the selected
 * grayscale image by 50%. The image is modified.
 * See Sec. 3.1.1 (Prog. 3.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class Raise_Contrast implements PlugInFilter {

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;
	}
    
	@Override
	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();

		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int a = ip.get(u, v);
				int b = (int) (a * 1.5 + 0.5);	// safe form of rounding since a >= 0
				if (b > 255)
					b = 255; 					// clamp to the maximum value
				ip.set(u, v, b);
			}
		}
	}
	
}

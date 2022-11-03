/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch13_ColorImages;

import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * <p>
 * ImageJ plugin, counts the distinct colors in the current color image.
 * See Sec. 13.3 (Prog. 13.10) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @see imagingbook.common.color.statistics.ColorStatistics
 */
public class Count_Colors implements PlugInFilter {
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		int n = countColors((ColorProcessor) ip);
		IJ.log("This image has " + n + " different colors.");
	}
	
	/**
	 * Determines how many different colors are contained in the specified
	 * 24 bit full-color RGB image.
	 * Replicated from {@link imagingbook.common.color.statistics.ColorStatistics}.
	 * 
	 * @param cp a RGB image
	 * @return the number of distinct colors
	 */
	private int countColors (ColorProcessor cp) { 
		// duplicate pixel array and sort
		int[] pixels = (int[]) cp.getPixelsCopy();
		Arrays.sort(pixels);  
		
		int k = 1;	// image contains at least one color
		for (int i = 0; i < pixels.length - 1; i++) {
			if (pixels[i] != pixels[i + 1])
				k = k + 1;
		}
		return k;
	}

}

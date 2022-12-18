/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.statistics;

import ij.process.ColorProcessor;

import java.util.Arrays;

/**
 * Defines static methods for calculating statistics of color images.
 * 
 * @author WB
 *
 */
public abstract class ColorStatistics {
	
	private ColorStatistics() {}
	
	/**
	 * Determines how many different colors are contained in the specified
	 * 24 bit full-color RGB image.
	 * 
	 * @param cp a RGB image
	 * @return the number of distinct colors
	 */
	public static int countColors(ColorProcessor cp) { 
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

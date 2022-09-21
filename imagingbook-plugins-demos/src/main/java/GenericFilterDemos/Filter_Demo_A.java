/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package GenericFilterDemos;

import ij.ImagePlus;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Filter_Demo_A implements PlugInFilter {
	
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;				// works for any type of image!
	}

	public void run(ImageProcessor I) {
		float[] H = { 							// coefficient array H is one-dimensional!
			0.075f, 0.125f, 0.075f,
			0.125f, 0.200f, 0.125f,
			0.075f, 0.125f, 0.075f };

		Convolver cv = new Convolver();
		cv.setNormalize(false);			// turn off kernel normalization
		cv.convolve(I, H, 3, 3);		// apply the filter H to I
	}
}
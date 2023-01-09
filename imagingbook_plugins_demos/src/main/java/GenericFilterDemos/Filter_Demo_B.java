/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package GenericFilterDemos;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.core.plugin.JavaDocHelp;

public class Filter_Demo_B implements PlugInFilter, JavaDocHelp {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;	// works for any type of image!
	}

	public void run(ImageProcessor I) {
		float[] H = { 		// this is a 1D array!
				1, 2, 1,
				2, 4, 2,
				1, 2, 1 
		};
		I.convolve(H, 3, 3);		// apply H as a 3 x 3 filter kernel
	}
}
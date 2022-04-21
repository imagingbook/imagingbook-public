/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package GenericFilterDemos;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.common.image.access.ScalarAccessor;

/**
 * This plugin demonstrates the of class {@link ScalarAccessor} for
 * unified access to scalar-valued images.
 * The plugin adds 20 brightness units to each pixel of the active image. 
 * It can be applied to any scalar image type.
 * TODO: move to other package
 * 
 * @author WB
 *
 * @see ImageAccessor
 * @see ScalarAccessor
 *
 */
public class ImageAccessor_Demo_Scalar implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G + DOES_16 + DOES_32;
	}

	public void run(ImageProcessor ip) {
		final int width = ip.getWidth();
		final int height = ip.getHeight();
		
		ScalarAccessor ia = ScalarAccessor.create(ip, null, null);
		
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float val = ia.getVal(u, v);		// get a single value (float)
				ia.setVal(u, v, val  + 20);
			}
		}
	}

}
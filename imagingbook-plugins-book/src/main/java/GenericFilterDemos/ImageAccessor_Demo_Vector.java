/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package GenericFilterDemos;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.common.image.access.ScalarAccessor;
import imagingbook.common.image.access.VectorAccessor;

/**
 * This plugin demonstrates the of class {@link ImageAccessor} for
 * unified access to scalar and vector-valued images.
 * The plugin adds 20 brightness units to each component (color
 * plane) of the active image. It can be applied to any image type.
 * Image pixels are treated as vectors, even if only a single
 * component is present.
 * TODO: move to other package
 * 
 * @author WB
 *
 * @see ImageAccessor
 * @see ScalarAccessor
 * @see VectorAccessor
 */
public class ImageAccessor_Demo_Vector implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		final int width = ip.getWidth();
		final int height = ip.getHeight();
		
		ImageAccessor ia = ImageAccessor.create(ip, null, null);
		IJ.log(ia.toString());
		
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float[] vals = ia.getPix(u, v);		// get a single pixel (vector)
				for (int i = 0; i < vals.length; i++) {
					vals[i] += 20;
				}
				ia.setPix(u, v, vals);
			}
		}
	}

}
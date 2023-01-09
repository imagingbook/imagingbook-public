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
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.linear.GaussianKernel2D;
import imagingbook.common.filter.linear.Kernel2D;
import imagingbook.common.filter.linear.LinearFilter;
import imagingbook.core.plugin.JavaDocHelp;

/**
 * This ImageJ plugin shows how to construct a generic linear filter using the classes {@link LinearFilter} and
 * {@link Kernel2D}. This plugin works for all types of images. On RGB color images, the filter is applied independently
 * to the three color channels. The active image is modified.
 *
 * @author WB
 * @see LinearFilter
 * @see GenericFilter
 * @see Kernel2D
 * @see GaussianKernel2D
 */
public class Gaussian_Filter implements PlugInFilter, JavaDocHelp {
	
	static double SIGMA = 3.0;

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {	
		Kernel2D kernel = new GaussianKernel2D(SIGMA);
		GenericFilter filter = new LinearFilter(kernel);
		filter.applyTo(ip);
    }

}

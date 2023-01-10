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
import imagingbook.common.filter.linear.Kernel1D;
import imagingbook.common.filter.linear.LinearFilterSeparable;
import imagingbook.core.jdoc.JavaDocHelp;

/**
 * This ImageJ plugin shows how to construct a separable linear filter using the classes {@link LinearFilterSeparable}
 * and {@link GenericFilter}. This plugin works for all types of images. On RGB color images, the filter is applied
 * independently to the three color channels. The active image is modified.
 *
 * @author WB
 */
public class Linear_Filter_3x3_Separable implements PlugInFilter, JavaDocHelp {
	
	static double SIGMA = 3.0;

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {	
		Kernel1D kernelX = new Kernel1D(new float[] {1,2,1});
		Kernel1D kernelY = new Kernel1D(new float[] {1,2,1});
		
		GenericFilter filter = new LinearFilterSeparable(kernelX, kernelY);
    	filter.applyTo(ip);
    }

}

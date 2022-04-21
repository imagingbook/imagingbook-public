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
import imagingbook.common.filter.GenericFilter;
import imagingbook.common.filter.linear.Kernel2D;
import imagingbook.common.filter.linear.LinearFilter;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.common.util.progress.ij.ProgressBarMonitor;

/**
 * This ImageJ plugin shows how to construct a generic linear filter
 * using the classes {@link LinearFilter} and {@link Kernel2D}.
 * It also shows the use of classes {@link ProgressMonitor} and
 * {@link ProgressBarMonitor}
 * This plugin works for all types of images.
 * On RGB color images, the filter is applied independently
 * to the three color channels.
 * The active image is modified.
 * 
 * @author WB
 *
 * @see GenericFilter
 * @see LinearFilter
 */
public class Linear_Filter_3x3 implements PlugInFilter {
	
	private static float[][] H = {	// 3x3 smoothing kernel
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
//	private static float[][] H = {	// 2D impulse
//			{0, 0, 0},
//			{0, 1, 0},
//			{0, 0, 0}};

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	GenericFilter filter = new LinearFilter(new Kernel2D(H));
    	try (ProgressMonitor m = new ProgressBarMonitor(filter)) {
    		m.setWaitTime(100);	// 100ms (default is 250ms)
    		filter.applyTo(ip);
    	}
    }

}

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
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;

/**
 * ImageJ plugin, simply displays the histogram and cumulative histogram
 * of a grayscale image in two new windows.
 * The input image is not modified.
 * 
 * @author WB
 * @see HistogramUtils
 * @see HistogramPlot
 */
public class Show_Histogram implements PlugInFilter { 
	
	private ImagePlus im;
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		int[] h = ip.getHistogram();
		String title = im.getShortTitle();
		(new HistogramPlot(h, "Histogram of " + title)).show();
		(new HistogramPlot(HistogramUtils.cdf(h), "Cum. Histogram of " + title)).show();
	}
	
}


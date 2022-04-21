/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin shows how data can be communicated from one
 * plugin to another. In this example, THIS plugin ({@link Data_Transfer_Plugin_Producer})
 * calculates a histogram that is subsequently retrieved by ANOTHER
 * plugin {@link Data_Transfer_Plugin_Consumer}. 
 * Data are stored as a <strong>property</strong> of the associated image (of type {@link ImagePlus}).
 * Note that the stored data should contain no instances of self-defined
 * classes, since these may be re-loaded when performing compile-and-run.
 * 
 * @author WB
 *
 */
public class Data_Transfer_Plugin_Producer implements PlugInFilter {
	
	// Create a unique (publicly visible) property key:
	public static final String HistKey = 
			Data_Transfer_Plugin_Producer.class.getCanonicalName() + "histogram";
	
	ImagePlus im;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;}
		
	public void run(ImageProcessor ip) {
		int[] hist = ip.getHistogram();
		// add histogram to image properties:
		im.setProperty(HistKey, hist);
	}
}

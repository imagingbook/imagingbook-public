/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin shows how data can be communicated from one
 * plugin to another. In this example, ANOTHER plugin ({@link Data_Transfer_Plugin_Producer})
 * calculates a histogram that is subsequently retrieved by THIS
 * plugin {@link Data_Transfer_Plugin_Consumer}. Data are stored as a property of the associated
 * image (of type {@link ImagePlus}).
 * Note that the stored data should contain no instances of self-defined
 * classes, since these may be re-loaded when performing compile-and-run.
 * 
 * @author W. Burger
 *
 */
public class Data_Transfer_Plugin_Consumer implements PlugInFilter {
	ImagePlus im;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;}

	public void run(ImageProcessor ip) {
		String key = Data_Transfer_Plugin_Producer.HistKey;	// property key from the producer plugin class
		Object prop = im.getProperty(key);
		if (prop == null) {
			IJ.error("found no histogram for image " + im.getTitle());	
		}
		else {
			int[] hist = (int[]) prop;
			IJ.log("found histogram of length " + hist.length);
			// process the histogram ...
		}
		
		// delete the stored data if not needed any longer:
		// im.setProperty(key, null);
		
	}
}

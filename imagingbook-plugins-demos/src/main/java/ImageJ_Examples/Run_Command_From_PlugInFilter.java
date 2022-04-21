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
 * This plugin demonstrates how to run another ImageJ command (plugin)
 * from our own PlugInFilter using the IJ.run() method.
 * Note that PlugInFilter locks the current image until is is finished,
 * thus we need to unlock() it before running the other command.
 * 
 * @author WB
 */
public class Run_Command_From_PlugInFilter implements PlugInFilter {
	
	ImagePlus im;

	public int setup(String args, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}
	
	public void run(ImageProcessor ip) {
		im.unlock();					// unlock the image to run other commands
		IJ.run(im, "Invert", "");		// run another command
		im.lock();						// lock the image again (just to be sure)
		// ... continue with this plugin
	}


}

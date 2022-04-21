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
 * This ImageJ plugin modifies and re-displays the current image 
 * repeatedly.
 * 
 * @author WB
 *
 */
public class UpdateAndDraw_Demo implements PlugInFilter {
	
	ImagePlus im;

	public int setup(String arg, ImagePlus im) {
		this.im = im;		// keep reference to associated ImagePlus
		return DOES_ALL; 	// this plugin accepts any image
	}

	public void run(ImageProcessor ip) {
		for (int i = 0; i < 10; i++) {
			// modify this image:
			ip.smooth();
			ip.rotate(30);
			// redisplay this image:
			im.updateAndDraw();
			// sleep so user can watch this:
			IJ.wait(100);
		}
	}
}

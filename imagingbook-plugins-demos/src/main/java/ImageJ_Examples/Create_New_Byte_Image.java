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
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * This plugin demonstrates how to create and display a 
 * new byte image (to show the histogram of the input image).
 */
public class Create_New_Byte_Image implements PlugInFilter {
	
	ImagePlus im;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		// obtain the histogram of ip:
		int[] hist = ip.getHistogram();
		int K = hist.length;
		
		// create the histogram image:
		ImageProcessor histIp = new ByteProcessor(K, 100);
		histIp.setValue(255);	// white = 255
		histIp.fill();

		// draw the histogram values as black bars in histIp here, 
		// for example, using histIp.putpixel(u, v, 0)
		// ...
		
		// compose a nice title:
		String imTitle = im.getShortTitle();
		String histTitle = "Histogram of " + imTitle;
		
		// display the histogram image:
		ImagePlus histIm = new ImagePlus(histTitle, histIp);
		histIm.show(); 
	}
}

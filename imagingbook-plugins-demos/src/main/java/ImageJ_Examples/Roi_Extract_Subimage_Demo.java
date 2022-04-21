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
 * This ImageJ plugin shows how a subimage is extracted from
 * a given image using the bounding box of the currently selected 
 * region of interest (ROI). Note that the resulting image is
 * of the same type as the original.
 * 
 * @author WB
 * @version 2022/04/01
 */
public class Roi_Extract_Subimage_Demo implements PlugInFilter {

	ImagePlus im = null;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		// The ROI obtained from ImageProcessor always exists even without a user selection.
		// It is a rectangle with int coordinates.
		// By default (no user selection) the ROI contains the whole image.
		// Rectangle roi = ip.getRoi();
		ImageProcessor ip2 = ip.crop();		// extracts the subimage defined by the ROI
		new ImagePlus(im.getShortTitle() + "-extracted", ip2).show();
	}
}

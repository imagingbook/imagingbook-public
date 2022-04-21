/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import java.awt.Point;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin shows processing the inside of 
 * the currently selected region of interest (ROI)
 * using the {@link Point} iterator of {@link Roi}. 
 * The plugin works for RGB color images and merely inverts the
 * each pixel contained in the ROI.
 * 
 * @author WB
 * @version 2022/04/01
 */
public class Roi_Processing_Demo2 implements PlugInFilter {

	private ImagePlus im;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		Roi roi = im.getRoi();
		
		// Use Roi's Point iterator to visit all contained pixels:
		for (Point pnt : roi) {
			int p = ip.getPixel(pnt.x, pnt.y);
			ip.putPixel(pnt.x, pnt.y, ~p);	// invert color values
		}

	}
}

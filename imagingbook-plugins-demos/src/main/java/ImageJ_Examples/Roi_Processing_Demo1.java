/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin shows how an image operation can be confined
 * to the currently selected region of interest (ROI). Note that
 * it uses the ROI attached to the associated {@link ImagePlus} instance,
 * which may be of any shape (not just a rectangle).
 * The plugin works for RGB color images and merely inverts the
 * each pixel contained in the ROI.
 * 
 * @author WB
 * @version 2022/04/01
 */
public class Roi_Processing_Demo1 implements PlugInFilter {

	private ImagePlus im = null;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		// The ROI obtained from ImagePlus may be of any type:
		Roi roi = im.getRoi();	
		Rectangle bounds = roi.getBounds();

		int rL = bounds.x;
		int rT = bounds.y;
		int rR = rL + bounds.width;
		int rB = rT + bounds.height;

		// Scan the rectangle and process all pixels inside the ROI:
		for (int v = rT; v < rB; v++) {
			for (int u = rL; u < rR; u++) {
				if (roi.contains(u, v)) {
					int p = ip.getPixel(u, v);
					ip.putPixel(u, v, ~p);	// invert color values
				}

			}
		}
	}
}

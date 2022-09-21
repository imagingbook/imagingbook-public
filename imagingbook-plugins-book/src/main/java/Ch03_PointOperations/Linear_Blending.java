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
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;

/**
 * This plugin demonstrates alpha blending between two images:
 * imBG: the background image (passed to and modified by the run method),
 * imFG: the foreground image (selected in a user dialog).
 * New (simpler) version using the {@code imagingbook.lib.ij.IjUtils.getOpenImages} library method.
 * 
 * @author WB
 * @version 2022/04/01
 */
public class Linear_Blending implements PlugInFilter {
	
	private static double alpha = 0.5;	// transparency of foreground image
	private ImagePlus imFG = null;		// foreground image (to be selected)
	
	@Override
	public int setup(String arg, ImagePlus imBG) {
		return DOES_8G;
	}	
	
	@Override
	public void run(ImageProcessor ipBG) {	// ipBG = background image
		if(!runDialog() || imFG == null) {
			return;
		}
		ImageProcessor ipFG = imFG.getProcessor().convertToByte(false);
		ipFG = ipFG.duplicate();
		ipFG.multiply(1 - alpha);
		ipBG.multiply(alpha);
		ipBG.copyBits(ipFG, 0, 0, Blitter.ADD);
	}	

	boolean runDialog() {
		// get list of open images and their titles:
		ImagePlus[] images = IjUtils.getOpenImages(true);
		String[] titles = new String[images.length];
		for (int i = 0; i < images.length; i++) {
			titles[i] = images[i].getShortTitle();
		}
		// create the dialog and show:
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addChoice("Foreground image:", titles, titles[0]);
		gd.addNumericField("Alpha value (0,..,1)", alpha, 2);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) {
			return false;
		}

		imFG = images[gd.getNextChoiceIndex()];
		alpha = gd.getNextNumber();
		return true;
	}
}

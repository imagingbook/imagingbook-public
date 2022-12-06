/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch03_PointOperations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;

/**
 * <p>
 * This plugin demonstrates linear (alpha) blending between two images:
 * </p>
 * <ul>
 * <li>{@code imBG}: the background image (the current active image),</li>
 * <li>{@code imFG}: the foreground image (selected in a user dialog).</li>
 * </ul>
 * <p>
 * Both images must be open when the plugin is started. The images may be of different size. The transparency of the
 * foreground image is initially set to &alpha; = 0.5 but may be adjusted in the user dialog. See Sec. 3.8.5 (Prog. 3.5)
 * of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/04/01
 */
public class Linear_Blending implements PlugInFilter {
	
	private static double ALPHA = 0.5;	// transparency of foreground image
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
		ipFG.multiply(1 - ALPHA);
		ipBG.multiply(ALPHA);
		ipBG.copyBits(ipFG, 0, 0, Blitter.ADD);
	}	

	private boolean runDialog() {
		// get list of open images and their titles:
		ImagePlus[] images = IjUtils.getOpenImages(true);
		String[] titles = new String[images.length];
		for (int i = 0; i < images.length; i++) {
			titles[i] = images[i].getShortTitle();
		}
		// create the dialog and show:
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addChoice("Foreground image:", titles, titles[0]);
		gd.addNumericField("Alpha value (0,..,1)", ALPHA, 2);
		gd.showDialog();
		
		if (gd.wasCanceled()) {
			return false;
		}

		imFG = images[gd.getNextChoiceIndex()];
		ALPHA = gd.getNextNumber();
		return true;
	}
}

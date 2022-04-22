/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package Thresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.adaptive.SauvolaThresholder;
import imagingbook.common.threshold.adaptive.SauvolaThresholder.Parameters;

/**
 * Demo plugin showing the use of the {@link SauvolaThresholder} class.
 *
 * @author WB
 * @version 2022/04/01
 */
public class Adaptive_Sauvola implements PlugInFilter {
	
	private static Parameters params = new Parameters();
	private static boolean showThresholdImage = false;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		
		if (!runDialog(params))
			return;
		
		ByteProcessor I = (ByteProcessor) ip;
		SauvolaThresholder thr = new SauvolaThresholder(params);
		ByteProcessor Q = thr.getThreshold(I);
		thr.threshold(I, Q);
		
		if (showThresholdImage) {
			(new ImagePlus("Sauvola-Threshold", Q)).show();
		}
	}
	
	boolean runDialog(Parameters params) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		params.addToDialog(gd);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		params.getFromDialog(gd);
		return true;
	}
}

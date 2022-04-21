/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Thresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.adaptive.AdaptiveThresholder;
import imagingbook.common.threshold.adaptive.BernsenThresholder;
import imagingbook.common.threshold.adaptive.BernsenThresholder.Parameters;

/**
 * Demo plugin showing the use of the {@link BernsenThresholder} class.
 * 
 * @author WB
 * @version 2022/04/01
 */
public class Adaptive_Bernsen implements PlugInFilter {
	
	private static Parameters params = new Parameters();
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog(params))
			return;
		
		ByteProcessor I = (ByteProcessor) ip;	
		AdaptiveThresholder thr = new BernsenThresholder(params);
		ByteProcessor Q = thr.getThreshold(I);
		thr.threshold(I, Q);
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

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch09_AutomaticThresholding;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.adaptive.AdaptiveThresholder;
import imagingbook.common.threshold.adaptive.BernsenThresholder;
import imagingbook.common.threshold.adaptive.BernsenThresholder.Parameters;

/**
 * </p>
 * ImageJ plugin showing the use of the {@link BernsenThresholder} class
 * (see Sec. 9.2.1 of [1] for additional details).
 * This plugin works on 8-bit grayscale images only. The original image
 * is modified to a binary image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/01
 * @see imagingbook.common.threshold.adaptive.BernsenThresholder
 */
public class Adaptive_Bernsen implements PlugInFilter {
	
	private static Parameters params = new Parameters();
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
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
		addToDialog(params, gd);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}	
		
		getFromDialog(params, gd);
		return true;
	}
}

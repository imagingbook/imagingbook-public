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
import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.ij.IjUtils.askForSampleImage;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.adaptive.AdaptiveThresholder;
import imagingbook.common.threshold.adaptive.InterpolatingThresholder;
import imagingbook.common.threshold.adaptive.InterpolatingThresholder.Parameters;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin demonstrating the use of the {@link InterpolatingThresholder} class.
 * See Sec. 9.4 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/01
 * @see imagingbook.common.threshold.adaptive.InterpolatingThresholder
 */
public class Adaptive_Interpolating implements PlugInFilter {
	
	private static Parameters params = new Parameters();

	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Adaptive_Interpolating() {
		if (noCurrentImage()) {
			askForSampleImage(GeneralSampleImage.Kepler);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog(params))
			return;
		
		ByteProcessor I = (ByteProcessor) ip;
		AdaptiveThresholder thr = new InterpolatingThresholder(params);
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

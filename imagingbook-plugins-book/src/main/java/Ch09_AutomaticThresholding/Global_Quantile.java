/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch09_AutomaticThresholding;

import static imagingbook.common.ij.DialogUtils.askForSampleImage;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.global.GlobalThresholder;
import imagingbook.common.threshold.global.QuantileThresholder;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin showing the use of the {@link QuantileThresholder} class.
 * See Sec. 9.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/02
 * @see imagingbook.common.threshold.global.QuantileThresholder
 */
public class Global_Quantile implements PlugInFilter {
	
	private static double quantile = 0.5;

	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Global_Quantile() {
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
		ByteProcessor bp = (ByteProcessor) ip;
		
		quantile = IJ.getNumber("Black quantile [0,1]", quantile);
		if (quantile < 0) quantile = 0;
		if (quantile > 1) quantile = 1;
		
		GlobalThresholder thr = new QuantileThresholder(quantile);
		int q = thr.getThreshold(bp);
		if (q >= 0) {
			IJ.log("threshold = " + q);
			ip.threshold(q);
		}
		else {
			IJ.showMessage("no threshold found");
		}
	}
}

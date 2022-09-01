/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Thresholding;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.global.GlobalThresholder;
import imagingbook.common.threshold.global.QuantileThresholder;

/**
 * Demo plugin showing the use of the {@link QuantileThresholder} class.
 * 
 * @author WB
 * @version 2022/04/02
 */
public class Global_Quantile implements PlugInFilter {
	
	static double quantile = 0.5;

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

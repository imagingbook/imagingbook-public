/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch09_AutomaticThresholding;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.global.GlobalThresholder;
import imagingbook.common.threshold.global.MinErrorThresholder;

/**
 * <p>
 * ImageJ plugin showing the use of the {@link MinErrorThresholder} class.
 * See Sec. 9.1.6 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/02
 */
public class Global_MinError implements PlugInFilter {
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		
		GlobalThresholder thr = new MinErrorThresholder();		
		int q = thr.getThreshold(bp); 

		if (q > 0) {
			IJ.log("threshold = " + q);
			bp.threshold(q);
		}
		else {
			IJ.showMessage("no threshold found");
		}

	}
	
}


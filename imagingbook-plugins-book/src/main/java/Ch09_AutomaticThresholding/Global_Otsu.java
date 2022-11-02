/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch09_AutomaticThresholding;

import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.ij.IjUtils.requestSampleImage;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.global.OtsuThresholder;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin showing the use of the {@link OtsuThresholder} class.
 * See Sec. 9.1.4 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; 
 * An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/02
 * @see imagingbook.common.threshold.global.OtsuThresholder
 */
public class Global_Otsu implements PlugInFilter {
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Global_Otsu() {
		if (noCurrentImage()) {
			requestSampleImage(GeneralSampleImage.Kepler);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}	

	@Override
	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		
		OtsuThresholder thr = new OtsuThresholder();
		int q = thr.getThreshold(bp);
		if (q >= 0) {
			IJ.log("threshold = " + q);
			bp.threshold(q);
		}
		else {
			IJ.showMessage("no threshold found");
		}
	}

}

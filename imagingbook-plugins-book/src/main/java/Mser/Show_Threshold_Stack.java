/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Mser;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Show_Threshold_Stack implements PlugInFilter {
	
	private ImagePlus img;
	
	@Override
	public int setup(String arg0, ImagePlus img) {
		this.img = img;
		return DOES_8G; // | NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {

		ImageStack stack = makeThresholdStack((ByteProcessor)ip);
		new ImagePlus("ThresholdStack of " + img.getShortTitle(), stack).show();

	}
	
	/**
	 * Creates a stack of binary images, one for thresholding the 
	 * given image at each gray vale 0,..,255.
	 * @param bp
	 * @return
	 */
	private ImageStack makeThresholdStack(ByteProcessor bp) {
		int width = bp.getWidth();
		int height = bp.getHeight();
		ImageStack stack = new ImageStack(width, height);
		for (int level = 0; level < 256; level++) {
			ByteProcessor bp2 = (ByteProcessor) bp.duplicate();
			bp2.threshold(level);
			stack.addSlice("Level " + level, bp2);
		}
		return stack;
	}

}

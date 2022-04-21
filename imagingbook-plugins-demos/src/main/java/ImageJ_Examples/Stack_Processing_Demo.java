/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * <p>
 * This ImageJ plugin demonstrates how to iterate over the frames (slices)
 * of an image stack. In this case each frame is simply inverted. No new
 * frames are added to the stack.
 * Note that stack slices are numbered from 1,...,K (i.e., there is no slice 0)!
 * </p>
 * <p>
 * If {@link STACK_REQUIRED} is omitted in {@link #setup(String, ImagePlus)},
 * the plugin will also work on ordinary (single) images.
 * </p>
 * 
 * @author WB
 *
 */
public class Stack_Processing_Demo implements PlugInFilter {
	
	ImagePlus im = null;	// keep a reference to the associated ImagePlus object
	
	public int setup(String args, ImagePlus im) {
		this.im = im;
		return DOES_ALL + STACK_REQUIRED;
	}

	public void run(ImageProcessor ignored) {
		ImageStack stack = im.getImageStack();
		int K = stack.getSize();
		for (int k = 1; k <= K; k++) {	// NOTE: slices are numbered from 1,...,K !!
			ImageProcessor ip = stack.getProcessor(k);
			ip.invert();
		}
	}
}

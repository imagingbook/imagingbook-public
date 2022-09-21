/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch13_ColorImages;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin converts an RGB color image to a grayscale image
 * using a specific set of component weights for caclulating the 
 * luminance (luma) values.
 * @author Wilbur
 * @version 2013-12-02
 */
public class Convert_Rgb_To_Gray implements PlugInFilter {
		
	ImagePlus imp;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB + NO_CHANGES;	
	}

	public void run(ImageProcessor ip) {
		
		ColorProcessor cp = (ColorProcessor) ip;
		cp.setRGBWeights(0.2126, 0.7152, 0.0722); // ITU BR.709 luma weights
		ByteProcessor bp = cp.convertToByteProcessor();
		(new ImagePlus(imp.getShortTitle() + " (gray)", bp)).show();
	}
}

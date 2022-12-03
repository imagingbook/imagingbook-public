/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch13_ColorImages;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * <p>
 * ImageJ plugin, converts an RGB color image to a grayscale image using a
 * specific set of component weights (ITU BR.709) for calculating luminance
 * (luma) values. See Sec.13.2.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2013/12/02
 */
public class Convert_Rgb_To_Gray implements PlugInFilter {
		
	ImagePlus im;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		cp.setRGBWeights(0.2126, 0.7152, 0.0722); // use ITU BR.709 luma weights
		ByteProcessor bp = cp.convertToByteProcessor();
		new ImagePlus(im.getShortTitle() + " (gray)", bp).show();
	}
}

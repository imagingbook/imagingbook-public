/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch13_Color_Images;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin, converts an RGB color image to a grayscale image using a specific set of component weights (ITU
 * BR.709) for calculating luminance (luma) values. See Sec.13.2.1 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/09
 */
public class Convert_Rgb_To_Gray implements PlugInFilter, JavaDocHelp {
		
	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Convert_Rgb_To_Gray() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}
	
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

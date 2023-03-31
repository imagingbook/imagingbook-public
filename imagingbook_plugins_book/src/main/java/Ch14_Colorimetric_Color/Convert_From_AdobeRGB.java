/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch14_Colorimetric_Color;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.colorspace.AdobeRgbColorSpace;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.color.ColorSpace;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * ImageJ plugin, converts a color image with RGB components assumed to be in AdobeRGB
 * color space to sRGB color space.
 *
 * @author WB
 * @version 2023/03/15
 *
 * @see AdobeRgbColorSpace
 */
public class Convert_From_AdobeRGB implements PlugInFilter, JavaDocHelp {

	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Convert_From_AdobeRGB() {
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
		String title = im.getShortTitle();

		ColorProcessor cp1 = (ColorProcessor) ip;
		ColorProcessor cp2 = (ColorProcessor) cp1.duplicate();

		ColorSpace acs = AdobeRgbColorSpace.getInstance();				// AdobeRGB1998 color space
		ColorSpace scs = ColorSpace.getInstance(ColorSpace.CS_sRGB);	// D50-based sRGB color space

		int[] pix1 = (int[]) cp1.getPixels();		// AdobeRGB pixels
		int[] pix2 = (int[]) cp2.getPixels();		// sRGB pixels

		for (int i = 0; i < pix1.length; i++) {
			int[] aRGB = RgbUtils.intToRgb(pix1[i]);	// AdobeRGB int to int[] in [0,255]
			float[] argb = RgbUtils.normalize(aRGB);	// AdobeRGB float[] in [0,1]
			float[] xyz = acs.toCIEXYZ(argb);			// convert AdobeRGB to XYZ
			float[] srgb = scs.fromCIEXYZ(xyz);			// convert XYZ to sRGB [0,1]
			int[] sRGB = RgbUtils.denormalize(srgb);	// sRGB [0,255]
			pix2[i] = RgbUtils.rgbToInt(sRGB);			// sRGB int[] to int
		}

		new ImagePlus(title + " (sRGB)", cp2).show();
	}

}

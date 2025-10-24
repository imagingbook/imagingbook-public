/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch03_Point_Operations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin does the same as the {@link Raise_Contrast} plugin but uses the one-dimensional pixel array to
 * read and writes pixel values without calling any intermediate access methods, which is obviously more efficient. See
 * Sec. 3.1.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @see Raise_Contrast
 */
public class Raise_Contrast_Fast implements PlugInFilter, JavaDocHelp {
	
	/** Contrast scale factor. */
	public static double S = 1.5;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Raise_Contrast_Fast() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}

		// ip is assumed to be of type ByteProcessor
		byte[] pixels = (byte[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			int a = 0xFF & pixels[i];
			int b = (int) (a * S + 0.5);
			if (b > 255)
				b = 255;
			pixels[i] = (byte) (0xFF & b);
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addNumericField("Contrast scale (S > 0)", S, 2);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		S = gd.getNextNumber();
		return true;
	}
}

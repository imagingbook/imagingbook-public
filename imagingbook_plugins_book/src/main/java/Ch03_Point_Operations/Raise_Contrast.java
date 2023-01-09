/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch03_Point_Operations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin increases the contrast of the selected grayscale image by 50%. The image is modified. See Sec.
 * 3.1.1 (Prog. 3.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Raise_Contrast implements PlugInFilter, JavaDocHelp {
	
	/** Contrast scale factor. */
	public static double S = 1.5;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Raise_Contrast() {
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

		int w = ip.getWidth();
		int h = ip.getHeight();

		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int a = ip.get(u, v);
				int b = (int) (a * S + 0.5);	// safe form of rounding since a >= 0
				if (b > 255)
					b = 255; 					// clamp to the maximum value
				ip.set(u, v, b);
			}
		}
	}
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Contrast scale (S > 0)", S, 2);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		S = gd.getNextNumber();
		return true;
	}
}

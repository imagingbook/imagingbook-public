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
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * <p>
 * ImageJ plugin, continuously desaturates the current image directly in RGB
 * color space (without conversion to HSV or HSB). The degree of desaturation is
 * controlled by factor S &in; [0,1]. With S = 1, the image is unchanged, with S
 * = 0 the resulting image is completely colorless (gray). See Sec. 13.2.2
 * (Prog. 13.5) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public class Desaturate_RGB implements PlugInFilter {
	
	public static double S = 0.3; // color saturation factor

	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog()) {
			return;
		}
		
		final int w = ip.getWidth();
		final int h = ip.getHeight();
		
		//iterate over all pixels
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {

				// get int-packed color pixel
				int c = ip.get(u, v);

				// disassemble RGB components from int color pixel
				int r = (c & 0xff0000) >> 16;
				int g = (c & 0x00ff00) >> 8;
				int b = (c & 0x0000ff);

				//compute equivalent gray value (luma, ITU BR.601 weights)
				double y = 0.299 * r + 0.587 * g + 0.114 * b;

				// linearly interpolate $(yyy) --> (rgb)
				r = clamp(y + S * (r - y));
				g = clamp(y + S * (g - y));
				b = clamp(y + S * (b - y));

				// reassemble color pixel to int
				c = ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff; 
				ip.set(u, v, c);
			}
		}
	}
	
	private int clamp(double x) {
		int z = (int) Math.round(x);
		if (z < 0) return 0;
		if (z > 255) return 255;
		return z;
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage("Saturates or desaturates the current image.");
		gd.addNumericField("Saturation factor (S = 0..1)", S, 2);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		S = gd.getNextNumber();
		if (S < 0) S = 0;
		if (S > 1) S = 1;
		return true;
	}

}

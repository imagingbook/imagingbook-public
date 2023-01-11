/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch13_Color_Images;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin, increases the brightness of a RGB color image by 10 units (each color component) without bit
 * operations (Version 2). See Sec. 13.1 (Prog. 13.2) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Brighten_Rgb_2 implements PlugInFilter, JavaDocHelp {
	
	private static int BrightnessDelta = 10;	// increase by 10 units
	static final int R = 0, G = 1, B = 2;		// component indices

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Brighten_Rgb_2() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;	// this plugin works on RGB images 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}

		//make sure image is of type ColorProcessor
		ColorProcessor cp = (ColorProcessor) ip; 
		int[] RGB = new int[3];

		for (int v = 0; v < cp.getHeight(); v++) {
			for (int u = 0; u < cp.getWidth(); u++) {
				cp.getPixel(u, v, RGB); 
				// RGB[R] = Math.min(RGB[R] + BrightnessDelta, 255);  // limit to 255
				// RGB[G] = Math.min(RGB[G] + BrightnessDelta, 255);
				// RGB[B] = Math.min(RGB[B] + BrightnessDelta, 255);

				RGB[R] = clamp(RGB[R] + BrightnessDelta);  // limit to 0,..,255
				RGB[G] = clamp(RGB[G] + BrightnessDelta);
				RGB[B] = clamp(RGB[B] + BrightnessDelta);
				cp.putPixel(u, v, RGB); 
			}
		}
	}

	private int clamp(int val) {
		if (val < 0) return 0;
		if (val > 255) return 255;
		return val;
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addNumericField("Brightness delta", BrightnessDelta, 0);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		BrightnessDelta = (int) gd.getNextNumber();
		return true;
	}
}

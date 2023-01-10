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
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin, increases the brightness of a RGB color image by 10 units (each color component) using bit operations
 * (Version 1). See Sec. 13.1 (Prog. 13.1) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Brighten_Rgb_1 implements PlugInFilter, JavaDocHelp {
	
	private static int BrightnessDelta = 10;	// increase by 10 units

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Brighten_Rgb_1() {
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

		int[] pixels = (int[]) ip.getPixels();  

		for (int i = 0; i < pixels.length; i++) { 
			int c = pixels[i];	                   
			// split color pixel into rgb-components
			int r = (c & 0xff0000) >> 16;          
			int g = (c & 0x00ff00) >> 8; 
			int b = (c & 0x0000ff);

			// modify colors
			// Note that clamping components individually may result in strange colors!
			r = clamp(r + BrightnessDelta);
			g = clamp(g + BrightnessDelta);
			b = clamp(b + BrightnessDelta);

			// reassemble color pixel and insert into pixel array
			pixels[i] = ((r & 0xff)<<16) | ((g & 0xff)<<8) | b & 0xff; 
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

/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch13_Color_Images;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin, increases the brightness of an indexed color image by 10 units (each color component). See Sec. 13.1
 * (Prog. 13.3) of [1] for details. Note that only the color model (RGB lookup table) of the image is changed while the
 * actual pixel values are not modified.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Brighten_Index_Image implements PlugInFilter {
	
	private static int BrightnessDelta = 10;	// increase by 10 units

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Brighten_Index_Image() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.FlowerIdx256);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + DOES_8C;	// this plugin works on indexed color images 
	}

	@Override
	public void run(ImageProcessor ip) {
		ColorModel cm =  ip.getColorModel();
		if (!(cm instanceof IndexColorModel)) {
			IJ.error("Color model not of type IndexedColorModel");
			return;
		}

		if (!runDialog()) {
			return;
		}

		IndexColorModel icm = (IndexColorModel) cm;
		int pixBits = icm.getPixelSize(); 
		int nColors = icm.getMapSize(); 
		
		//retrieve the current lookup tables (maps) for R,G,B
		byte[] rMap = new byte[nColors]; 
		byte[] gMap = new byte[nColors]; 
		byte[] bMap = new byte[nColors];
		
		icm.getReds(rMap);  
		icm.getGreens(gMap);
		icm.getBlues(bMap);  
		
		// modify the lookup tables	only
		for (int idx = 0; idx < nColors; idx++){ 
			int r = 0xff & rMap[idx];	//mask to treat as unsigned byte 
			int g = 0xff & gMap[idx];
			int b = 0xff & bMap[idx];

			rMap[idx] = clamp(r + BrightnessDelta);
			gMap[idx] = clamp(g + BrightnessDelta);
			bMap[idx] = clamp(b + BrightnessDelta);
		}
		
		//create a new color model and apply to the image
		IndexColorModel icm2 = new IndexColorModel(pixBits, nColors, rMap, gMap, bMap);  
		ip.setColorModel(icm2);
	}

	private byte clamp(int val) {
		if (val < 0) return (byte) 0;
		if (val > 255) return (byte) 255;
		return (byte) val;
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Brightness delta", BrightnessDelta, 0);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		BrightnessDelta = (int) gd.getNextNumber();
		return true;
	}

}


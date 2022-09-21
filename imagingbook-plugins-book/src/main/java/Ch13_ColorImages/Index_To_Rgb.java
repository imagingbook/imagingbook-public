/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch13_ColorImages;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

public class Index_To_Rgb implements PlugInFilter {
	static final int R = 0, G = 1, B = 2;
	
	ImagePlus imp;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8C + NO_CHANGES; // does not alter original image	
	}

	public void run(ImageProcessor ip) {
		ColorModel cm =  ip.getColorModel();
		if (!(cm instanceof IndexColorModel)) {
			IJ.error("Color model not of type IndexedColorModel");
			return;
		}
		// retrieve the lookup tables (maps) for R,G,B:
		IndexColorModel icm = (IndexColorModel) ip.getColorModel(); 
		int nColors = icm.getMapSize(); 
		
		byte[] rMap = new byte[nColors]; 
		byte[] gMap = new byte[nColors]; 
		byte[] bMap = new byte[nColors];
		
		icm.getReds(rMap); 
		icm.getGreens(gMap);
		icm.getBlues(bMap);
		  
		// create a new 24-bit RGB image:
		int w = ip.getWidth();
		int h = ip.getHeight();
		ColorProcessor cp = new ColorProcessor(w, h);
		int[] RGB = new int[3];
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int idx = ip.getPixel(u, v);
				RGB[R] = 0xFF & rMap[idx];
				RGB[G] = 0xFF & gMap[idx];
				RGB[B] = 0xFF & bMap[idx];
				cp.putPixel(u, v, RGB); 
			}
		}
		ImagePlus cwin = new ImagePlus(imp.getShortTitle() + " (RGB)", cp);
		cwin.show();
	}
}


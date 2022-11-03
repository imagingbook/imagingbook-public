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
import ij.process.ImageProcessor;

/**
 * <p>
 * ImageJ plugin, increases the brightness of an indexed color image by 10 units
 * (each color component). See Sec. 13.1 (Prog. 13.3) of [1] for details. Note
 * that only the color model (RGB lookup table) of the image is changed while the
 * actual pixel values are not modified.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class Brighten_Index_Image implements PlugInFilter {
	
	private static int INCREASE = 10;	// increase by 10 units
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + DOES_8C;	// this plugin works on indexed color images 
	}

	@Override
	public void run(ImageProcessor ip) {
		ColorModel cm =  ip.getColorModel();
//		IJ.log("Color Model=" + cm + " " + ip.isColorLut());
		
		if (!(cm instanceof IndexColorModel)) {
			IJ.error("Color model not of type IndexedColorModel");
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
		
		// modify the lookup tables	
		for (int idx = 0; idx < nColors; idx++){ 
			int r = 0xff & rMap[idx];	//mask to treat as unsigned byte 
			int g = 0xff & gMap[idx];
			int b = 0xff & bMap[idx];   
			rMap[idx] = (byte) Math.min(r + INCREASE, 255); 
			gMap[idx] = (byte) Math.min(g + INCREASE, 255);
			bMap[idx] = (byte) Math.min(b + INCREASE, 255);
		}
		
		//create a new color model and apply to the image
		IndexColorModel icm2 = new IndexColorModel(pixBits, nColors, rMap, gMap, bMap);  
		ip.setColorModel(icm2);
	}

}


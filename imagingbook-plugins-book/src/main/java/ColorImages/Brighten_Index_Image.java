/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package ColorImages;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Brighten_Index_Image implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + DOES_8C;	// this plugin works on indexed color images 
	}

	public void run(ImageProcessor ip) {
		ColorModel cm =  ip.getColorModel();
		if (!(cm instanceof IndexColorModel)) {
			IJ.error("Color model not of type IndexedColorModel");
			return;
		}
		
		IndexColorModel icm = (IndexColorModel) cm; 
		//IJ.write("Color Model=" + ip.getColorModel() + " " + ip.isColorLut());
	
		int pixBits = icm.getPixelSize(); 
		int nColors = icm.getMapSize(); 
		
		//retrieve the current lookup tables (maps) for R,G,B
		byte[] rMap = new byte[nColors]; 
		byte[] gMap = new byte[nColors]; 
		byte[] bMap = new byte[nColors];
		
		icm.getReds(rMap);  
		icm.getGreens(gMap);
		icm.getBlues(bMap);  
		
		//modify the lookup tables	
		for (int idx = 0; idx < nColors; idx++){ 
			int r = 0xff & rMap[idx];	//mask to treat as unsigned byte 
			int g = 0xff & gMap[idx];
			int b = 0xff & bMap[idx];   
			rMap[idx] = (byte) Math.min(r + 10, 255); 
			gMap[idx] = (byte) Math.min(g + 10, 255);
			bMap[idx] = (byte) Math.min(b + 10, 255); 
		}
		
		//create a new color model and apply to the image
		IndexColorModel icm2 = new IndexColorModel(pixBits, nColors, rMap, gMap, bMap);  
		ip.setColorModel(icm2);
	}

}


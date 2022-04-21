/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ColorImages;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

/**
 * Makes a specific color of the current (index) color image 
 * transparent.
 * 
 * @author WB
 *
 */
public class Make_Index_Transparent implements PlugInFilter {
	
	static int tidx = 2;	// index of transparent color
	
	private ImagePlus im;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8C + ROI_REQUIRED + NO_CHANGES;	// this plugin works on indexed color images 
	}

	public void run(ImageProcessor ip) {
		ColorModel cm =  ip.getColorModel();
		if (!(cm instanceof IndexColorModel)) {
			IJ.showMessage("Color model not of type IndexedColorModel");
			return;
		}
		
		Roi roi = im.getRoi();
		if (!(roi instanceof PointRoi)) {
			IJ.showMessage("point ROI required");
			return;
		}
		
		PointRoi proi = (PointRoi) roi;
		Point p = proi.getContainedPoints()[0];
		tidx = ip.get(p.x, p.y);
		
		IJ.log("transparent pixel index = " + tidx);
			
		ImageProcessor ip2 = ip.duplicate();
		
		
		IndexColorModel icm = (IndexColorModel) cm; 
		IJ.log("Color Model=" + ip2.getColorModel() + " " + ip2.isColorLut());
		
	
		int pixBits = icm.getPixelSize(); 
		int nColors = icm.getMapSize(); 
		
		//retrieve the current lookup tables (maps) for R,G,B
		byte[] tRed = new byte[nColors]; icm.getReds(tRed);  
		byte[] tGrn = new byte[nColors]; icm.getGreens(tGrn);  
		byte[] tBlu = new byte[nColors]; icm.getBlues(tBlu);  
		
		// Set the color table for the transparent pixel to gray.
		// Note that ImageJ always shows the transparent pixels 'white'.
		// GIF export works as expected, pixels are actually transparent.
		// PNG export does not consider transparency but inserts the associated
		// color from the color table.
		byte gray = (byte) (0xFF & 200);
		tRed[tidx] = gray;	
		tGrn[tidx] = gray;
		tBlu[tidx] = gray;
		
		//create a new color model and apply to the image
		IndexColorModel icm2 = new IndexColorModel(pixBits, nColors, tRed, tGrn, tBlu, tidx);  
		IJ.log("transparency = " + icm.getTransparency());

		ip2.setColorModel(icm2);
		ImagePlus im2 = new ImagePlus(im.getTitle() + "-tranparent", ip2);
		im2.setTypeToColor256();
		im2.show();
	}

}


/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch13_Color_Images;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import static imagingbook.common.ij.IjUtils.noCurrentImage;


/**
 * <p>
 * ImageJ plugin, converts an indexed color image to a full-color RGB image. Creates a new image, the original image is
 * not modififed. See Sec. 13.1 (Prog. 13.3) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Index_To_Rgb implements PlugInFilter {
	private static final int R = 0, G = 1, B = 2;
	
	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Index_To_Rgb() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.FlowerIdx256);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8C + NO_CHANGES;	// DOES_8C +
	}

	@Override
	public void run(ImageProcessor ip) {
		ColorModel cm =  ip.getColorModel();

		if (!(cm instanceof IndexColorModel)) {
			IJ.error("Color model not of type IndexedColorModel but " + cm.getClass().getSimpleName());
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
		new ImagePlus(im.getShortTitle() + " (RGB)", cp).show();
	}
}
/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin shows how to access the one-dimensional pixel array
 * of a 8-bit (= byte) grayscale image.
 * Each pixel value is increased by 10 without range limiting, i.e., bright values
 * wrap back to black etc.
 * 
 * @author WB
 *
 */
public class Direct_Byte_Pixel_Access implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G; 	// this plugin accepts 8-bit grayscale images
	}

	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;	// DOES_8G only, thus this must work
		byte[] pixels = (byte[]) bp.getPixels();
		int w = bp.getWidth();
		int h = bp.getHeight();

		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int p = 0xFF & pixels[v * w + u]; // bitmask 0xFF is needed for unsigned values
				p = p + 10;
				pixels[v * w + u] = (byte) (0xFF & p); 
			}
		}
	}

}

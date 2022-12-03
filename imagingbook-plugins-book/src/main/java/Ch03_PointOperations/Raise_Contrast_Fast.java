/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch03_PointOperations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * <p>
 * This ImageJ plugin does the same as the {@link Raise_Contrast} plugin but uses 
 * the one-dimensional pixel array to read and writes pixel values without calling
 * any intermediate access methods, which is obviously more efficient.
 * See Sec. 3.1.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public class Raise_Contrast_Fast implements PlugInFilter {
	
	/** Contrast scale factor. */
	public static double S = 1.5;

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		// ip is assumed to be of type ByteProcessor
		byte[] pixels = (byte[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			int a = 0xFF & pixels[i];
			int b = (int) (a * S + 0.5);
			if (b > 255)
				b = 255;
			pixels[i] = (byte) (0xFF & b);
		}
	}
}

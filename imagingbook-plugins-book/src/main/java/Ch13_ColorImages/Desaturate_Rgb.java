/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch13_ColorImages;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Desaturate_Rgb implements PlugInFilter {
	static double sat = 0.3; // color saturation factor

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;
	}
	
	public void run(ImageProcessor ip) { 
		//iterate over all pixels
		for (int v = 0; v < ip.getHeight(); v++) {
			for (int u = 0; u < ip.getWidth(); u++) {

				//get int-packed color pixel
				int c = ip.get(u, v);

				//extract RGB components from color pixel
				int r = (c & 0xff0000) >> 16;
				int g = (c & 0x00ff00) >> 8;
				int b = (c & 0x0000ff);

				//compute equivalent gray value
				double y = 0.299 * r + 0.587 * g + 0.114 * b;

				// linearly interpolate $(yyy) --> (rgb)
				r = (int) (y + sat * (r - y));
				g = (int) (y + sat * (g - y));
				b = (int) (y + sat * (b - y));

				// reassemble color pixel
				c = ((r & 0xff)<<16) | ((g & 0xff)<<8) | b & 0xff; 
				ip.set(u, v, c);
			}
		}
	}

}

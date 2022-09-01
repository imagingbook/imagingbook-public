/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package PointOperations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Gamma_Correction implements PlugInFilter {

	@Override
	public int setup(String arg, ImagePlus img) {
		return DOES_8G;
	}
    
	@Override
	public void run(ImageProcessor ip) {
		// works for 8-bit images only 
	    int K = 256;
	    int aMax = K - 1;
	    double GAMMA = 2.8;   
	
	    // create and fill the lookup table:
	    int[] Fgc = new int[K];                
	
	    for (int a = 0; a < K; a++) {
	        double aa = (double) a / aMax;	// scale to [0,1]
	        double bb = Math.pow(aa,GAMMA);	// gamma function
	        // scale back to [0,255]
	        int b = (int) Math.round(bb * aMax); 
	        Fgc[a] = b;  
	    }
	    
	    ip.applyTable(Fgc);  // modify the image
	}	
}

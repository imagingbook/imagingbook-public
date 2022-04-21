/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Filters;

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class Filter_Smooth_3x3 implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }

    public void run(ImageProcessor ip) {
    	int M = ip.getWidth();
    	int N = ip.getHeight();

    	//3x3 filter matrix:
    	double[][] H = {
    			{0.075, 0.125, 0.075},
    			{0.125, 0.200, 0.125},
    			{0.075, 0.125, 0.075}};

    	ImageProcessor copy = ip.duplicate();

    	for (int u = 1; u <= M - 2; u++) {
    		for (int v = 1; v <= N - 2; v++) {
    			// compute filter result for position (u,v):
    			double sum = 0;
    			for (int i = -1; i <= 1; i++) {
    				for (int j = -1; j <= 1; j++) {
    					int p = copy.getPixel(u + i, v + j);
    					// get the corresponding filter coefficient:
    					double c = H[j + 1][i + 1];
    					sum = sum + c * p;
    				}
    			}
    			int q = (int) Math.round(sum);
    			ip.putPixel(u, v, q);
    		}
    	}
    }

}

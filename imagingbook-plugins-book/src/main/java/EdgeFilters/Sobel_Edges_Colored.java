/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package EdgeFilters;

import java.awt.Color;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Sobel_Edges_Colored implements PlugInFilter {
    static float[] sobelX = {
    		-1, 0, 1,
            -2, 0, 2,
            -1, 0, 1 };
    static float[] sobelY = {
			-1, -2, -1,
			 0,  0,  0,
			 1,  2,  1 };

    public int setup(String arg, ImagePlus I) {
        return DOES_8G + PlugInFilter.NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
        FloatProcessor Ix = (FloatProcessor) ip.convertToFloat();
        FloatProcessor Iy = (FloatProcessor) Ix.duplicate();
        Ix.convolve(sobelX, 3, 3);
        Iy.convolve(sobelY, 3, 3);
        
        (new ImagePlus("Ix", Ix)).show();
        (new ImagePlus("Iy", Iy)).show();
        
        FloatProcessor Ix2 = (FloatProcessor) Ix.duplicate(); Ix2.sqr();
        FloatProcessor Iy2 = (FloatProcessor) Iy.duplicate(); Iy2.sqr();
        
        FloatProcessor E = (FloatProcessor) Ix2.duplicate();
        E.copyBits(Iy2, 0, 0, Blitter.ADD);
        E.sqrt();
        
        FloatProcessor Phi = makeOrientation(Ix, Iy);
        
        ColorProcessor cp = makeColorEdges(E, Phi);
       
        (new ImagePlus("E", E)).show();
        (new ImagePlus("Phi", Phi)).show();
        (new ImagePlus("E Colors", cp)).show();
    }
    
    FloatProcessor makeOrientation(FloatProcessor fpx, FloatProcessor fpy) {
    	FloatProcessor orient = (FloatProcessor) fpx.duplicate();
    	int M = fpx.getPixelCount();
    	for (int i=0; i<M; i++) {
    		double dx = fpx.getf(i);
    		double dy = fpy.getf(i);
    		float phi = (float) Math.atan2(dy, dx);
    		orient.setf(i, phi);
    	}
		return orient;
    }
    
    ColorProcessor makeColorEdges(FloatProcessor mag, FloatProcessor ort) {
    	// mag is all positive
    	// ort is in -pi ... +pi
    	int w = mag.getWidth();
    	int h = mag.getHeight();
    	int M = mag.getPixelCount();
    	mag.resetMinAndMax();
    	ort.resetMinAndMax();
    	double maxMag = mag.getMax();
    	//IJ.log("Max:"+maxMag);
    	int[] colorPixels = new int[M];
    	for (int i=0; i<M; i++) {
    		float m = mag.getf(i);
    		float p = ort.getf(i);
    		float hue = (float)(p / (2 * Math.PI)) + 1;
    		float saturation = (float) (m / maxMag); //1;
    		float brightness = 1; //(float) (m / maxMag);
    		colorPixels[i] = Color.HSBtoRGB( hue, saturation, brightness);
    	}
    	return new ColorProcessor(w,h,colorPixels);
    }
   

}

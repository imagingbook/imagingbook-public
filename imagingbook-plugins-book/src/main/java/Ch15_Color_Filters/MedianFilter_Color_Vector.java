/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch15_Color_Filters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.filter.nonlinear.VectorMedianFilter;
import imagingbook.common.filter.nonlinear.VectorMedianFilter.Parameters;
import imagingbook.common.math.VectorNorm.NormType;

/**
 * This plugin applies a vector median filter to a RGB color image.
 * @author WB
 * @version 2013/05/30
 */
public class MedianFilter_Color_Vector implements PlugInFilter {
	
    @Override
	public int setup(String arg, ImagePlus imp) {
         return DOES_RGB;
    }

    @Override
	public void run(ImageProcessor ip) {
    	Parameters params = new VectorMedianFilter.Parameters();
    	if (!setParameters(params)) return;
    	
    	params.distanceNorm = NormType.L1;
    	params.radius = 3.0;

    	VectorMedianFilter filter = new VectorMedianFilter(params);
    	filter.applyTo(ip);
    	
 //   	IJ.log("Pixels modified: " + filter.modifiedCount + " of " + (ip.getPixelCount()));
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Median Filter");
		gd.addNumericField("Radius", params.radius, 1);
		gd.addEnumChoice("Distance norm", params.distanceNorm);
//		gd.addCheckbox("Mark modified pixels", params.markModifiedPixels);
//		gd.addCheckbox("Show mask", params.showMask);
		
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		
		params.radius = Math.max(gd.getNextNumber(),0.5);
		params.distanceNorm = gd.getNextEnumChoice(NormType.class);
//		params.markModifiedPixels = gd.getNextBoolean();
//		params.showMask = gd.getNextBoolean();
		return true;
    }
}


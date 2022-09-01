/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package ColorFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.filter.nonlinear.ScalarMedianFilter;
import imagingbook.common.filter.nonlinear.ScalarMedianFilter.Parameters;

/**
 * This plugin applies a scalar median filter to all three planes
 * of a RGB color image.
 * @author W. Burger
 * @version 2013/05/30
 */
public class MedianFilter_Color_Scalar implements PlugInFilter {
	
	ImagePlus imp;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB;
    }

    public void run(ImageProcessor ip) {
    	Parameters params = new ScalarMedianFilter.Parameters();
    	if (!setParameters(params)) 
    		return;
    	ScalarMedianFilter filter = new ScalarMedianFilter(params);
    	filter.applyTo(ip);	// apply destructively
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Scalar Median Filter");
		gd.addNumericField("Radius", params.radius, 1);
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		params.radius = Math.max(gd.getNextNumber(), 1);
		return true;
    }

}


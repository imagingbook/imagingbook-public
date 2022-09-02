/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package ColorFilters;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.ColorStack;
import imagingbook.common.color.colorspace.ColorStack.ColorStackType;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.linear.GaussianFilterSeparable;

/**
 * This plugin performs a Gaussian filter in a user-selectable color space.
 * Demonstrates the use of {@link ColorStack} and {@link GenericFilter}
 * and {@link GaussianFilterSeparable} (using a x/y-separable 2D kernel).
 * 
 * @author W. Burger
 * @version 2022/09/02
 */
public class Gaussian_Filter_Color implements PlugInFilter {
	
	static double sigma = 3.0;
	static int nIterations = 1;
	static ColorStackType csType = ColorStackType.sRGB;
	
	ImagePlus imp = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	if (!getParameters()) 
    		return;
    	
    	ImagePlus colStack = ColorStack.createFrom(imp);
    	
    	switch (csType) {
	    	case Lab : 	ColorStack.srgbToLab(colStack); break;
			case Luv: 	ColorStack.srgbToLuv(colStack); break;
			case RGB: 	ColorStack.srgbToRgb(colStack); break;
			case sRGB: 	break;
		default:
			IJ.error("Color space " + csType.name() + " not implemented!"); 
			return;
    	}
    	
    	FloatProcessor[] processors = ColorStack.getProcessors(colStack);
    	GenericFilter filter = new GaussianFilterSeparable(sigma); // non-separable: GaussianFilter(sigma)
    	
       	for (int k = 0; k < nIterations; k++) {
       		for (FloatProcessor fp : processors) {
       			filter.applyTo(fp);
       		}
    	}
       	
       	ColorStack.toSrgb(colStack);	// convert back to sRGB
       	colStack.setTitle(imp.getShortTitle() + "-filtered-" + csType.name());
       	ImagePlus result = ColorStack.toColorImage(colStack);
       	result.show();
    }
    
    boolean getParameters() {
		GenericDialog gd = new GenericDialog("Gaussian Filter");
		gd.addEnumChoice("Color space", csType);
		gd.addNumericField("sigma", sigma, 1);
		gd.addNumericField("iterations", nIterations, 0);
		
		gd.showDialog();
		if(gd.wasCanceled())
			return false;
		
		csType = gd.getNextEnumChoice(ColorStackType.class);
		sigma = gd.getNextNumber();
		nIterations = (int)gd.getNextNumber();
		if (nIterations < 1) nIterations = 1;
		return true;
    }

}


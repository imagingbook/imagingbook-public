/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package PixelInterpolation;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.common.interpolation.PixelInterpolator.InterpolationMethod;
import imagingbook.common.util.EnumUtils;

/**
 * This ImageJ plugin demonstrates the use of various pixel
 * interpolation methods and out-of-bounds strategies.
 * Simple translation is used as the geometric transformation
 * (parameters can be specified). Note the use if the
 * {@link ImageAccessor} class which gives uniform access
 * to all types of images.
 * 
 * @author WB
 * @version 2015/12/17
 * 
 * @see InterpolationMethod
 * @see OutOfBoundsStrategy
 * @see ImageAccessor
 */
public class Interpolator_Demo implements PlugInFilter {
	
	static InterpolationMethod ipm = InterpolationMethod.Bicubic;
	static OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	
	static double dx = 10.50;	// translation parameters
	static double dy = -3.25;
	
	@Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL + NO_CHANGES;
    }

    @Override
    public void run(ImageProcessor source) {
    	if (!showDialog())
			return;
    	
    	final int w = source.getWidth();
    	final int h = source.getHeight();
    	
    	// create the target image (same type as source):
    	ImageProcessor target = source.createProcessor(w, h);
    	
    	// create an ImageAccessor for the source image:
    	ImageAccessor sA = ImageAccessor.create(source, obs, ipm);
    	
    	// create an ImageAccessor for the target image:
    	ImageAccessor tA = ImageAccessor.create(target);
    	
    	// iterate over all pixels of the target image:
    	for (int u = 0; u < w; u++) {	// discrete target position (u,v)
    		for (int v = 0; v < h; v++) {
    			double x = u + dx;	// continuous source position (x,y)
    			double y = v + dy;
    			float[] val = sA.getPix(x, y);
    			tA.setPix(u, v, val);	// update target pixel
    		}
    	}
    	
    	// display the target image:
    	(new ImagePlus("Target", target)).show();
    }
    
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Interpolation demo");
		gd.addMessage("Translation parameters:");
		gd.addNumericField("dx", dx, 2);
		gd.addNumericField("dy", dy, 2);
		
		String[] ipmOptions = EnumUtils.getEnumNames(InterpolationMethod.class);
		gd.addChoice("Interpolation method", ipmOptions, ipm.name());
		
		String[] obsOptions = EnumUtils.getEnumNames(OutOfBoundsStrategy.class);
		gd.addChoice("Out-of-bounds strategy", obsOptions, obs.name());

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		dx = gd.getNextNumber();
		dy = gd.getNextNumber();
		ipm = InterpolationMethod.valueOf(gd.getNextChoice());
		obs = OutOfBoundsStrategy.valueOf(gd.getNextChoice());
		return true;
	}
}

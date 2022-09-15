/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package GeometricOperations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.common.interpolation.InterpolationMethod;

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
 */
public class Interpolation_Demo implements PlugInFilter {
	
	static InterpolationMethod IPM = InterpolationMethod.Bicubic;
	static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	static double dx = 10.50;	// translation parameters
	static double dy = -3.25;
	
	private ImagePlus im;
	
    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL + NO_CHANGES;
    }

    public void run(ImageProcessor source) {
    	if (!showDialog())
			return;
    	
    	final int w = source.getWidth();
    	final int h = source.getHeight();
    	
    	// create the target image (same type as source):
    	ImageProcessor target = source.createProcessor(w, h);
    	
    	// create ImageAccessor's for the source and target  image:
    	ImageAccessor sA = ImageAccessor.create(source, OBS, IPM);
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
    	(new ImagePlus(im.getShortTitle() + "-transformed", target)).show();
    }
    
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Interpolation demo");
		gd.addMessage("Translation parameters:");
		gd.addNumericField("dx", dx, 2);
		gd.addNumericField("dy", dy, 2);
		gd.addEnumChoice("Interpolation method", IPM);
		gd.addEnumChoice("Out-of-bounds strategy", OBS);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		dx = gd.getNextNumber();
		dy = gd.getNextNumber();
		IPM = gd.getNextEnumChoice(InterpolationMethod.class);
		OBS = gd.getNextEnumChoice(OutOfBoundsStrategy.class);
		return true;
	}
}

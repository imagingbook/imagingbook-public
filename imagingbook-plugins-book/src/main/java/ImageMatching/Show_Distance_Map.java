/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageMatching;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.matching.DistanceTransform;
import imagingbook.common.matching.DistanceTransform.Norm;

/**
 * Demonstrates the use of the {@link DistanceTransform} class.
 * The active image is assumed to be binary (not checked).
 * 
 * @author WB
 * @version 2014-04-20
 */
public class Show_Distance_Map implements PlugInFilter {
	
	private static Norm distanceNorm = Norm.L1;
	
	private ImagePlus im;
	
    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_8G + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	if (!showDialog()) {
			return;
    	}
    	
    	DistanceTransform dt = new DistanceTransform(ip, distanceNorm);
		FloatProcessor dtIp = new FloatProcessor(dt.getDistanceMap());
		(new ImagePlus("Distance Transform of " + im.getShortTitle(), dtIp)).show();
    }
    
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Distance norm", distanceNorm);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		distanceNorm = gd.getNextEnumChoice(Norm.class);
		return true;
	}
		
}

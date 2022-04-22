/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package Edge_Preserving_Smoothing;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edgepreservingfilters.PeronaMalikFilterScalar;
import imagingbook.common.edgepreservingfilters.PeronaMalikFilterVector;
import imagingbook.common.edgepreservingfilters.PeronaMalikF.ColorMode;
import imagingbook.common.edgepreservingfilters.PeronaMalikF.Parameters;
import imagingbook.common.filter.GenericFilter;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.common.util.progress.ij.ProgressBarMonitor;

/**
 * This plugin demonstrates the use of the PeronaMalikFilter class.
 * This plugin works for all types of images and stacks.
 * 
 * @author W. Burger
 * @version 2021/01/05
 */
public class Perona_Malik_Filter implements PlugInFilter {

	private static Parameters params = new Parameters();
	
	private boolean isColor;

	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		isColor = (ip instanceof ColorProcessor);
		
		if (!getParameters())
			return;
		
		GenericFilter filter = null;
		if (isColor) {
			filter = (params.colorMode == ColorMode.SeparateChannels) ?
					new PeronaMalikFilterScalar(params) : 
					new PeronaMalikFilterVector(params);
		}
		else {
			filter = new PeronaMalikFilterScalar(params);
		}
		
		try (ProgressMonitor m = new ProgressBarMonitor(filter)) {
			filter.applyTo(ip);
		}
	}
	
	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		params.addToDialog(gd);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		params.getFromDialog(gd);
		
		return params.validate();
	}
}




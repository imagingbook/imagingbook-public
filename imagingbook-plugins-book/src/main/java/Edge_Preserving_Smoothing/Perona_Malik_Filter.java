/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Edge_Preserving_Smoothing;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.PeronaMalikFilterScalar;
import imagingbook.common.filter.edgepreserving.PeronaMalikFilterVector;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.ColorMode;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.ij.IjProgressBarMonitor;
import imagingbook.common.util.progress.ProgressMonitor;

/**
 * This plugin demonstrates the use of the PeronaMalikFilter class.
 * This plugin works for all types of images and stacks.
 * 
 * @author WB
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
		
		try (ProgressMonitor m = new IjProgressBarMonitor(filter)) {
			filter.applyTo(ip);
		}
	}
	
	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		addToDialog(params, gd);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		getFromDialog(params, gd);
		
		return params.validate();
	}
}




/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch17_EdgePreservingSmoothing;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.BilateralF.Parameters;
import imagingbook.common.filter.edgepreserving.BilateralFilterScalar;
import imagingbook.common.filter.edgepreserving.BilateralFilterScalarSeparable;
import imagingbook.common.filter.edgepreserving.BilateralFilterVector;
import imagingbook.common.filter.edgepreserving.BilateralFilterVectorSeparable;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.ij.IjProgressBarMonitor;
import imagingbook.common.util.progress.ProgressMonitor;

/**
 * This plugin demonstrates the use of the (full) BilateralFilter class. This
 * plugin works for all types of images. Given a color image, the filter is
 * applied separately to each color component if {@code UseScalarFilter} is set
 * true. Otherwise a vector filter is applied, using the specified color norm.
 * 
 * @author WB
 * @version 2022/03/30
 */
public class Bilateral_Filter implements PlugInFilter {
	
	private static Parameters params = new Parameters();
	private static boolean UseSeparableFilter = false;
	private static boolean UseScalarFilter = false;
	
	private boolean isColor;
	
	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL;
	}
	
	public void run(ImageProcessor ip) {
		isColor = (ip instanceof ColorProcessor);
		if (!getParameters())
			return;
		
		GenericFilter filter = null;	
		if (isColor && !UseScalarFilter) {  	// use a vector filter
			filter = (UseSeparableFilter) ? 
					new BilateralFilterVectorSeparable(params) : 
					new BilateralFilterVector(params);
		}
		else {									// use a scalar filter
			filter = (UseSeparableFilter) ? 
					new BilateralFilterScalarSeparable(params) : 
					new BilateralFilterScalar(params);
		}
		
		try (ProgressMonitor m = new IjProgressBarMonitor(filter)) {
			filter.applyTo(ip);
		}
	}
		
	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		addToDialog(params, gd);
		gd.addCheckbox("Use scalar filters (color only)", UseScalarFilter);
		gd.addCheckbox("Use X/Y-separable filter", UseSeparableFilter);
		
		gd.showDialog();
		if (gd.wasCanceled()) 
			return false;
		
		getFromDialog(params, gd);
		UseScalarFilter = gd.getNextBoolean();
		UseSeparableFilter = gd.getNextBoolean();
		
		params.sigmaD = Math.max(params.sigmaD, 0.5);
		params.sigmaR = Math.max(params.sigmaR, 1);
		return params.validate();
    }
}



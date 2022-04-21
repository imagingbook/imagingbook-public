/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Edge_Preserving_Smoothing;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edgepreservingfilters.BilateralFilterScalar;
import imagingbook.common.edgepreservingfilters.BilateralFilterScalarSeparable;
import imagingbook.common.edgepreservingfilters.BilateralFilterVector;
import imagingbook.common.edgepreservingfilters.BilateralFilterVectorSeparable;
import imagingbook.common.edgepreservingfilters.BilateralF.Parameters;
import imagingbook.common.filter.GenericFilter;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.common.util.progress.ij.ProgressBarMonitor;

/**
 * This plugin demonstrates the use of the (full) BilateralFilter class.
 * This plugin works for all types of images.
 * Given a color image, the filter is applied separately to
 * each color component if {@link #UseScalarFilter} is set true.
 * Otherwise a vector filter is applied, using the specified color norm.
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
		
		try (ProgressMonitor m = new ProgressBarMonitor(filter)) {
			filter.applyTo(ip);
		}
	}
		
	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		params.addToDialog(gd);
		gd.addCheckbox("Use scalar filters (color only)", UseScalarFilter);
		gd.addCheckbox("Use X/Y-separable filter", UseSeparableFilter);
		
		gd.showDialog();
		if (gd.wasCanceled()) 
			return false;
		
		params.getFromDialog(gd);
		UseScalarFilter = gd.getNextBoolean();
		UseSeparableFilter = gd.getNextBoolean();
		
		params.sigmaD = Math.max(params.sigmaD, 0.5);
		params.sigmaR = Math.max(params.sigmaR, 1);
		return params.validate();
    }
}



/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch17_EdgePreserving_Smoothing;

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
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjProgressBarMonitor;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the Bilateral filter. This plugin works for all types of images. Given a
 * color image, the filter is applied separately to each color component if {@code UseScalarFilter} is set true.
 * Otherwise a vector filter is applied, using the specified color norm. See Sec. 17.2 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 * @see BilateralFilterScalar
 * @see BilateralFilterVector
 * @see BilateralFilterScalarSeparable
 * @see BilateralFilterVectorSeparable
 */
public class Bilateral_Filter implements PlugInFilter, JavaDocHelp {
	
	private static Parameters params = new Parameters();
	private static boolean UseSeparableFilter = false;
	private static boolean UseScalarFilter = false;
	
	private boolean isColor;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Bilateral_Filter() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Postcard2c);
		}
	}

	@Override
	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		isColor = (ip instanceof ColorProcessor);
		if (!runDialog())
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
		
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		addToDialog(params, gd);
		gd.addCheckbox("Use scalar filters (color only)", UseScalarFilter);
		gd.addCheckbox("Use X/Y-separable filter (faster)", UseSeparableFilter);
		
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
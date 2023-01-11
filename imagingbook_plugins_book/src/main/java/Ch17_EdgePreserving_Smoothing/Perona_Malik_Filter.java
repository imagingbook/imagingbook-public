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
import imagingbook.common.filter.edgepreserving.PeronaMalikF.ColorMode;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.Parameters;
import imagingbook.common.filter.edgepreserving.PeronaMalikFilterScalar;
import imagingbook.common.filter.edgepreserving.PeronaMalikFilterVector;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjProgressBarMonitor;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * This ImageJ plugin demonstrates the use of the Perona-Malik filter [1]. See Sec. 17.3 of [2] for additional details.
 * This plugin works for all types of images and stacks.
 * <p>
 * [1] Pietro Perona and Jitendra Malik, "Scale-space and edge detection using anisotropic diffusion", IEEE Transactions
 * on Pattern Analysis and Machine Intelligence, vol. 12, no. 4, pp. 629-639 (July 1990).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 * @see PeronaMalikFilterScalar
 * @see PeronaMalikFilterVector
 */
public class Perona_Malik_Filter implements PlugInFilter, JavaDocHelp {

	private static Parameters params = new Parameters();
	private boolean isColor;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Perona_Malik_Filter() {
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
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		addToDialog(params, gd);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		getFromDialog(params, gd);
		return params.validate();
	}
}




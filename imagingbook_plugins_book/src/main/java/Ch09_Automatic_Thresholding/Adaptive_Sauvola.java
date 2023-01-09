/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch09_Automatic_Thresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.adaptive.SauvolaThresholder;
import imagingbook.common.threshold.adaptive.SauvolaThresholder.Parameters;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.askForSampleImage;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin demonstrating the use of the {@link SauvolaThresholder} class.
 * See Sec. 9.2 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/04/01
 * @see imagingbook.common.threshold.adaptive.SauvolaThresholder
 */
public class Adaptive_Sauvola implements PlugInFilter, JavaDocHelp {
	
	private static Parameters params = new Parameters();
	private static boolean ShowThresholdSurface = false;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Adaptive_Sauvola() {
		if (noCurrentImage()) {
			askForSampleImage(GeneralSampleImage.Kepler);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog(params))
			return;
		
		ByteProcessor I = (ByteProcessor) ip;
		SauvolaThresholder thr = new SauvolaThresholder(params);
		FloatProcessor Q = thr.getThreshold(I);
		thr.threshold(I, Q);
		
		if (ShowThresholdSurface) {
			new ImagePlus("Sauvola-Threshold", Q).show();
		}
	}
	
	boolean runDialog(Parameters params) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		addToDialog(params, gd);
		gd.addCheckbox("Show threshold surface", ShowThresholdSurface);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		getFromDialog(params, gd);
		ShowThresholdSurface = gd.getNextBoolean();
		return true;
	}
}

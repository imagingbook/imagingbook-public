/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch09_Automatic_Thresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.threshold.adaptive.NiblackThresholder;
import imagingbook.common.threshold.adaptive.NiblackThresholder.Parameters;
import imagingbook.common.threshold.adaptive.NiblackThresholder.RegionType;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.askForSampleImage;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin demonstrating the use of the {@link NiblackThresholder} class.
 * See Sec. 9.2.2 of [1] for additional details.
 * 
 * @author WB
 * @version 2022/04/01
 * @see imagingbook.common.threshold.adaptive.NiblackThresholder
 */
public class Adaptive_Niblack implements PlugInFilter {
	
//	enum RegionType { Box, Disk, Gaussian }
	
	private static RegionType regType = RegionType.Box;
	private static Parameters params = new Parameters();

	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Adaptive_Niblack() {
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
		NiblackThresholder thr = NiblackThresholder.create(regType, params);
		ByteProcessor Q = thr.getThreshold(I);
		thr.threshold(I, Q);
	}
	
	boolean runDialog(Parameters params) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Region type", regType);
		addToDialog(params, gd);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		regType = gd.getNextEnumChoice(RegionType.class);
		getFromDialog(params, gd);
		return true;
	}
}

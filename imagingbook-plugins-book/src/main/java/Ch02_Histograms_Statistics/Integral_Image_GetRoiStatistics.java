/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch02_Histograms_Statistics;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.image.IntegralImage;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Rectangle;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin first calculates the integral image for the current image (8 bit grayscale only) and uses it to
 * find the mean and variance inside the specified rectangle (ROI). Requires a rectangular ROI to be selected. Note that
 * these values are directly obtained from the integral images and do not require any iterations over image pixels or
 * other arrays. Thus such calculations are extremely quick. See Sec. 2.8 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @see IntegralImage
 */
public class Integral_Image_GetRoiStatistics implements PlugInFilter {
	
	private ImagePlus im = null;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Integral_Image_GetRoiStatistics() {
		if (noCurrentImage()) {
			if (DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor)) {
				ImagePlus cim = WindowManager.getCurrentImage();
				// set up some ROI
				cim.setRoi(new Rectangle(85, 65, 200, 150));
			}
		}
	}

	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_8G + ROI_REQUIRED + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		Roi roi = im.getRoi();
		if (roi.getType() != Roi.RECTANGLE) {
			IJ.error("Rectangular selection required!");
			return;
		}
		
		Rectangle rect = roi.getBounds();	
		int u0 = rect.x;
		int v0 = rect.y;
		int u1 = u0 + rect.width - 1;
		int v1 = v0 + rect.height - 1;
		
		IntegralImage iI = new IntegralImage((ByteProcessor) ip);
		double mean = iI.getMean(u0, v0, u1, v1);
		double var = iI.getVariance(u0, v0, u1, v1);
		
		IJ.log("ROI rectangle = " + rect);
		IJ.log("ROI area = " + rect.width * rect.height);
		IJ.log("ROI mean = " + String.format("%.3f", mean));
		IJ.log("ROI variance = " + String.format("%.3f", var));
		IJ.log("ROI stddev = " + String.format("%.3f", Math.sqrt(var)));

	}
}

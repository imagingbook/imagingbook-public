/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package EdgeFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.linear.GaussianKernel1D;

/**
 * This plugin implements an Unsharp Masking filter similar to Photoshop 
 * without thresholds, using a "clean" (sufficiently large) Gaussian filter.
 * @version 2014-03-16
 */

public class Unsharp_Masking_Filter implements PlugInFilter {

	private static double radius = 1.0;	// radius (sigma of Gaussian)
	private static double amount = 1.0; // amount of sharpening (1 = 100%)

	public int setup(String arg, ImagePlus imp) {
		if (!getParameters()) 
			return DONE;
		else 
			return DOES_8G + DOES_STACKS; 
	}
	
	public void run(ImageProcessor ip) {
		FloatProcessor I = ip.convertToFloatProcessor();
		
		//create a blurred version of the original
		ImageProcessor J = I.duplicate();
		float[] H = GaussianKernel1D.makeGaussKernel1D(radius);
		Convolver cv = new Convolver();
		cv.setNormalize(true);
		cv.convolve(J, H, 1, H.length);
		cv.convolve(J, H, H.length, 1);

		double a = amount;
		// multiply the original image by (1+a)
		I.multiply(1 + a);
		// multiply the mask image by a
		J.multiply(a);
		// subtract the weighted mask from the original
		I.copyBits(J, 0, 0, Blitter.SUBTRACT);

		//copy result back into original byte image
		ip.insert(I.convertToByte(false), 0, 0);
	}
	
	private boolean getParameters() {
		GenericDialog gd = new GenericDialog("Unsharp Mask");
		gd.addNumericField("Radius = Sigma (0.1-20)", radius, 1);
		gd.addNumericField("Amount (1-500%)", amount * 100, 0);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		//same limits as in Photoshop:
		radius = gd.getNextNumber();
		if (radius > 20) radius = 20;
		if (radius < 0.1) radius = 0.1;
		amount = gd.getNextNumber() / 100;
		if (amount > 5.0) amount = 5.0;
		if (amount < 0.01) amount = 0.01;
		return true;
	}

}

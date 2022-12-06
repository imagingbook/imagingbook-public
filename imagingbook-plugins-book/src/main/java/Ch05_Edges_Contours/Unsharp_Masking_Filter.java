/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch05_Edges_Contours;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.linear.GaussianKernel1D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This plugin implements an Unsharp Masking filter similar to Photoshop without thresholds, using a "clean"
 * (sufficiently large) Gaussian filter. This implementation uses built-in ImageJ functionality only. The original image
 * is modified. See Sec. 5.6.2 (Prog. 5.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/23
 * @see GaussianKernel1D#makeGaussKernel1D(double)
 */
public class Unsharp_Masking_Filter implements PlugInFilter {
	
	/** Filter radius (sigma of Gaussian). */
	public static double Radius = 1.0;
	/** Amount of sharpening (1.0 = 100%). */
	public static double Amount = 1.0;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Unsharp_Masking_Filter() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Boats);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G + DOES_STACKS; 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
			
		FloatProcessor I = ip.convertToFloatProcessor();
		
		//create a blurred version of the original
		ImageProcessor J = I.duplicate();
		float[] H = GaussianKernel1D.makeGaussKernel1D(Radius);
		Convolver cv = new Convolver();
		cv.setNormalize(true);
		cv.convolve(J, H, 1, H.length);
		cv.convolve(J, H, H.length, 1);

		double a = Amount;
		
		I.multiply(1 + a);						// multiply the original image by (1+a)
		J.multiply(a);							// multiply the mask image by a
		I.copyBits(J, 0, 0, Blitter.SUBTRACT);	// subtract the weighted mask from the original

		//copy result back into original byte image
		ip.insert(I.convertToByte(false), 0, 0);
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Radius (\u03C3 = 0.1 ... 20)", Radius, 1);
		gd.addNumericField("Amount (a = 1 ... 500%)", Amount * 100, 0);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		//same limits as in Photoshop:
		Radius = gd.getNextNumber();
		if (Radius > 20) Radius = 20;
		if (Radius < 0.1) Radius = 0.1;
		Amount = gd.getNextNumber() / 100;
		if (Amount > 5.0) Amount = 5.0;
		if (Amount < 0.01) Amount = 0.01;
		return true;
	}

}

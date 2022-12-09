/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch03_Point_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import java.util.Locale;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This plugin demonstrates linear (alpha) blending between two images, which are supplied as a {@link ImageStack} with
 * exactly 2 frames. The first stack frame is taken as the <em>foreground</em> image, the second as the
 * <em>background</em> image. Running the plugin inserts N &gt; 0 (N is specified by the user) additional frames
 * obtained by linearly blending the two input images with varying &alpha; values. See Sec. 3.8.5 (Prog. 3.5) of [1] for
 * additional details. The input stack is modified.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/07
 */
public class Linear_Blending_Stack implements PlugInFilter {

	private static int StepCount = 5;	// number of intermediate images
	private ImagePlus im;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Linear_Blending_Stack() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.ShipBeachSmallStack_tif);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + STACK_REQUIRED;
	}	
	
	@Override
	public void run(ImageProcessor ip) {
		ImageStack stack = im.getStack();
		if (stack.getSize() != 2) {
			IJ.error("Plugin requires stack with exactly 2 slices!");
			return;
		}

		if(!runDialog() || StepCount < 1) {
			return;
		}

		ImageProcessor ip1 = stack.getProcessor(1);
		ImageProcessor ip2 = stack.getProcessor(2);

		stack.setSliceLabel(makeSliceLabel(0.0), 1);
		stack.setSliceLabel(makeSliceLabel(1.0), 2);

		for (int i = 1; i <= StepCount; i++) {
			double alpha = (double) i / (StepCount + 1);
			ImageProcessor ipA = ip1.duplicate();
			ImageProcessor ipB = ip2.duplicate();
			ipA.multiply(1 - alpha);
			ipB.multiply(alpha);
			ipA.copyBits(ipB, 0, 0, Blitter.ADD);
			stack.addSlice(makeSliceLabel(alpha), ipA, i);
		}

		im.setStack(stack);
	}

	private String makeSliceLabel(double alpha) {
		return String.format(Locale.US, "\u03B1 = %.2f", alpha);
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Step count (N > 0)", StepCount, 0);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		StepCount = (int) gd.getNextNumber();
		return true;
	}
}

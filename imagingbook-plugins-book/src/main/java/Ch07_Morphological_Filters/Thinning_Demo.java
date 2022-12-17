/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package Ch07_Morphological_Filters;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.morphology.BinaryThinning;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates morphological thinning on binary images. See Sec. 7.2 of [1] for additional details.
 * This plugin works on 8-bit grayscale images only, the original image is modified. Optionally, the plugin creates a
 * stack of images obtained from each thinning iteration. The maximum number of thinning iterations can be specified.
 * Zero-value pixels are considered background, all other pixels are foreground. Different to ImageJ's built-in
 * morphological operators, this implementation does not incorporate the current display lookup-table (LUT).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/07
 */
public class Thinning_Demo implements PlugInFilter {

	private static boolean ShowStackAnimation = true;
	private static boolean ShowIterationCount = false;

	private int maxIterations;
	private ImagePlus im;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Thinning_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.RhinoSmallInv);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		maxIterations = Math.max(ip.getWidth(), ip.getHeight());
		
		if (!runDialog()) {
			return;
		}

		BinaryThinning thin = new BinaryThinning(maxIterations);
		ByteProcessor bp = (ByteProcessor) ip;

		if (ShowStackAnimation) {
			ImageStack stack = new ImageStack();
			stack.addSlice("initial", ip.duplicate());
			int deletions;
			do {
				deletions = thin.thinOnce(bp);
				if (deletions > 0) {
					stack.addSlice(deletions + " deletions", ip.duplicate());
				}
			}
			while (deletions > 0 && thin.getIterations() < maxIterations);
			new ImagePlus(im.getShortTitle() + "-thinning", stack).show();
		}
		else {
			thin.applyTo((ByteProcessor) ip);
		}

		if  (ShowIterationCount) {
			IJ.log("Iterations performed: " + thin.getIterations());
		}
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		if (im.isInvertedLut()) {
			gd.setInsets(0, 0, 0);
			gd.addMessage("NOTE: Image has inverted LUT (0 = white)!");
		}
		gd.addNumericField("max. iterations", maxIterations, 0);
		gd.addCheckbox("Show stack animation", ShowStackAnimation);
		gd.addCheckbox("Show actual iteration count", ShowIterationCount);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		maxIterations = (int) gd.getNextNumber();
		ShowStackAnimation = gd.getNextBoolean();
		ShowIterationCount = gd.getNextBoolean();
		return true;
	}
	
}


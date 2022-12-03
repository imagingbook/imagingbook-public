/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch25_SIFT;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.scalespace.DogScaleSpace;
import imagingbook.common.sift.scalespace.GaussianScaleSpace;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * This ImageJ plugin visualizes the hierarchical scale space structures used
 * for SIFT feature detection (see {@link SiftDetector}). Optionally the
 * Gaussian scale space or the derived DoG scale space (or both) are shown. Each
 * scale space octave is displayed as an image stack, with one frame for each
 * scale level. See Ch. 25 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/23
 */
public class Show_Scale_Space implements PlugInFilter {
	
	private static boolean ShowGaussianScaleSpace = true;
	private static boolean ShowDoGScaleSpace = false;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Show_Scale_Space() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Castle);
		}
	}
	
	// ---------------------------------------------------

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		return DOES_8G + DOES_32 + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
		
		if (!ShowGaussianScaleSpace && !ShowDoGScaleSpace) {
			return;
		}
		
		FloatProcessor fp = (FloatProcessor) ip.convertToFloat();
		SiftDetector detector = new SiftDetector(fp);
		if (ShowGaussianScaleSpace) {
			GaussianScaleSpace gss = detector.getGaussianScaleSpace();
			ImagePlus[] images = gss.getImages("Gaussian");
			for (ImagePlus im : images) {
				im.show();
			}
		}
		
		if (ShowDoGScaleSpace) {
			DogScaleSpace dss = detector.getDogScaleSpace();
			ImagePlus[] images = dss.getImages("DoG");
			for (ImagePlus im : images) {
				im.show();
			}
		}
	}
	
	// ---------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addCheckbox("Show Gaussian scale space)", ShowGaussianScaleSpace);
		gd.addCheckbox("Show DoG scale space)", ShowDoGScaleSpace);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		ShowGaussianScaleSpace = gd.getNextBoolean();
		ShowDoGScaleSpace = gd.getNextBoolean();
		return true;
	}

}

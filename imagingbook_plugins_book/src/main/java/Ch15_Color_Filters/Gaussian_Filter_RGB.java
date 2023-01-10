/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch15_Color_Filters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LinearRgb65ColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.linear.GaussianFilterSeparable;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.image.ColorPack;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.color.ColorSpace;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This plugin performs a Gaussian filter in a user-selectable color space. The default test image (opened automatically
 * if no other image is open) makes the differences noticeable. See Ch. 15 (Fig. 15.6) of [1] for details. This plugin
 * also demonstrates the use of {@link ColorPack}, {@link GenericFilter} and {@link GaussianFilterSeparable} (using a
 * x/y-separable 2D kernel).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/10
 * @see ColorPack
 * @see GenericFilter
 * @see GaussianFilterSeparable
 */
public class Gaussian_Filter_RGB implements PlugInFilter, JavaDocHelp {

	private enum ColorStackType {
		Lab, Luv, LinearRGB, sRGB;
	}

	private static double Sigma = 3.0;
	private static int Iterations = 1;
	private static ColorStackType CsType = ColorStackType.Lab;

	private ImagePlus imp = null;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Gaussian_Filter_RGB() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.ColorTest3);
		}
	}

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog())
			return;

		ColorPack colStack = new ColorPack((ColorProcessor) ip);
		ColorSpace cs = null;
		switch (CsType) {
			case Lab : 		cs = LabColorSpace.getInstance(); break;
			case Luv: 		cs = LuvColorSpace.getInstance(); break;
			case LinearRGB: cs = LinearRgb65ColorSpace.getInstance(); break;
			case sRGB: 		cs = null; break;
		}

		if (cs != null) {
			colStack.convertFromSrgbTo(cs);
		}

		FloatProcessor[] processors = colStack.getProcessors();
		GenericFilter filter = new GaussianFilterSeparable(Sigma); // non-separable: GaussianFilter(sigma)

		for (int k = 0; k < Iterations; k++) {
			for (FloatProcessor fp : processors) {
				filter.applyTo(fp);
			}
		}
		if (!colStack.getColorspace().isCS_sRGB()) {
			colStack.convertToSrgb();    // convert back to sRGB
		}
		ColorProcessor cp = colStack.toColorProcessor();
		String title = imp.getShortTitle() + "-filtered-" + CsType.name();
		ImagePlus result = new ImagePlus(title, cp);
		result.show();

	}

	boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addEnumChoice("Color space", CsType);
		gd.addNumericField("sigma", Sigma, 1);
		gd.addNumericField("iterations", Iterations, 0);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		CsType = gd.getNextEnumChoice(ColorStackType.class);
		Sigma = gd.getNextNumber();
		Iterations = (int)gd.getNextNumber();
		if (Iterations < 1) Iterations = 1;
		return true;
	}
}


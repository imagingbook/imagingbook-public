/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch14_Colorimetric_Color;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.HlsColorSpace;
import imagingbook.common.color.colorspace.HsvColorSpace;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LinearRgb65ColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.common.color.colorspace.XYZ65ColorSpace;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.image.ColorPack;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.color.ColorSpace;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * ImageJ plugin, converts a sRGB color image to Lab, Luv, HLS, HSV or Linear RGB color space and shows the resulting
 * components as a image stack (with float values).
 *
 * @author WB
 * @version 2022/12/09
 *
 * @see LabColorSpace
 * @see LuvColorSpace
 * @see HlsColorSpace
 * @see HsvColorSpace
 * @see LinearRgb65ColorSpace
 * @see XYZ65ColorSpace
 * @see imagingbook.common.image.ColorPack
 */
public class Convert_To_Color_Stack implements PlugInFilter, JavaDocHelp {

	private enum TargetSpaceType {
		Lab, Luv, HLS, HSV, LinearRGB, XYZ;
	}

	private static TargetSpaceType TargetSpace = TargetSpaceType.Lab;
	private static boolean ReconstructRGB = false;

	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Convert_To_Color_Stack() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {

		if (!runDialog()) {
			return;
		}

		String title = im.getShortTitle();

		ColorSpace csp = null;
		switch(TargetSpace) {
			case Lab: csp = LabColorSpace.getInstance(); break;
			case Luv: csp = LuvColorSpace.getInstance(); break;
			case HLS: csp = HlsColorSpace.getInstance(); break;
			case HSV: csp = HsvColorSpace.getInstance(); break;
			case LinearRGB:	csp = LinearRgb65ColorSpace.getInstance(); break;
			case XYZ: csp = XYZ65ColorSpace.getInstance(); break;
		}

		ColorPack cPack = new ColorPack((ColorProcessor) ip);
		cPack.convertFromSrgbTo(csp);

		ImageStack stack = cPack.toImageStack();
		new ImagePlus(title + " (" + TargetSpace.toString() + ")", stack).show();

		if (ReconstructRGB) {
			cPack.convertToSrgb();
			ColorProcessor cp = cPack.toColorProcessor();
			new ImagePlus(title + " (sRGB)", cp).show();
		}
	}

	// ---------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage("Convert RGB image to Lab/Luv stack.");
		gd.addEnumChoice("Target color space", TargetSpace);
		gd.addCheckbox("Reconstruct RGB image", ReconstructRGB);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		TargetSpace = gd.getNextEnumChoice(TargetSpaceType.class);
		ReconstructRGB = gd.getNextBoolean();
		return true;
	}

}

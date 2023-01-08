/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Tools_;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import static imagingbook.common.ij.DialogUtils.makeHtmlString;
import static imagingbook.core.modules.JavaDocUtils.getJavaDocUrl;

import imagingbook.core.FileUtils;
import imagingbook.core.modules.JavaDocBaseUrl;
import imagingbook.core.modules.JavaDocUtils;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

import java.lang.annotation.Annotation;

public class Test_GenericDialog_Help implements PlugInFilter {

	private static String HelpText = makeHtmlString(
		"This ImageJ plugin visualizes the hierarchical scale space structures",
		"used for <strong>SIFT feature</strong> detection. Optionally the Gaussian scale space or the",
		"derived DoG scale space (or both) are shown. Each scale space octave",
		"is displayed as an image stack, with one frame for each scale level."
	);

	private static ImageResource SampleImage = GeneralSampleImage.Castle;
	private static boolean ShowGaussianScaleSpace = true;
	private static boolean ShowDoGScaleSpace = false;

	// ---------------------------------------------------

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		return NO_IMAGE_REQUIRED;
	}
	
	@Override
	public void run(ImageProcessor ip) {

		IJ.log("class path = " + FileUtils.getClassPath(this.getClass()));

		Class<?> clazz = this.getClass();
		IJ.log("class name = " + clazz.getName());

		// for (Module m : ModuleLayer.boot().modules()) {
		// 	IJ.log(m.toString());
		// }
		//
		// IJ.log("IJ module = " + IJ.class.getModule());
		// IJ.log("IJ module name = " + IJ.class.getModule().getName());
		// IJ.log("classnameC = " + this.getClass().getCanonicalName());
		// Module module = this.getClass().getModule();
		// IJ.log("module = " + module);
		// IJ.log("ann = " + module.getAnnotation(JavaDocBaseUrl.class));
		// IJ.log("url = " + getJavaDocUrl(this.getClass()));

		// if (!runDialog()) {
		// 	return;
		// }
	}
	
	// ---------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		// gd.addHelp(HelpText);
		gd.addHelp(getJavaDocUrl(this.getClass()));
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

	public static void main(String[] args) {
		String url = JavaDocUtils.getJavaDocUrl(Close_Other_Images.class);
		System.out.println(url);

	}

}

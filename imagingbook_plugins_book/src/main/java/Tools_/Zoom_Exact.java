/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Tools_;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.common.ij.GuiTools;
import imagingbook.core.jdoc.JavaDocHelp;

/**
 * ImageJ plugin, resizes the window of the given image to fit an arbitrary, user-specified magnification factor. The
 * resulting window size is limited by the current screen size. The window size is reduced if too large but the given
 * magnification factor remains always unchanged.
 *
 * @author WB
 * @version 2020/10/08
 */
public class Zoom_Exact implements PlugIn, JavaDocHelp {
	
	private static boolean LOG_OUTPUT = false;
	
	@Override
	public void run(String arg) {
		ImagePlus im = WindowManager.getCurrentImage();
		if (im == null) {
			IJ.showMessage("No image open");
			return;
		}
		
		GenericDialog gd = new GenericDialog("Zoom Exact");
		gd.addHelp(getJavaDocUrl());
		gd.addNumericField("Magnification (%): ", GuiTools.getMagnification(im) * 100, 2);
		gd.addCheckbox("Log output", LOG_OUTPUT);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		double mag = gd.getNextNumber() / 100.0;
		LOG_OUTPUT = gd.getNextBoolean();
		
		if (mag < 0.001) {
			IJ.showMessage(String.format("Out of range magnification:\n%.3f", mag));
			return;
		}
		
		if (GuiTools.zoomExact(im, mag)) {
			if (LOG_OUTPUT) {
				IJ.log(String.format("new magnification: %.3f",  GuiTools.getMagnification(im)));
			}
		}
		else {
			IJ.showMessage(Zoom_Exact.class.getSimpleName() + " failed");
		}
	}
}
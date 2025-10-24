/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package ImageJ_Demos;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import imagingbook.core.jdoc.JavaDocHelp;

/**
 * This ImageJ plugin demonstrates the basic use of {@link GenericDialog} to create a new byte image.
 *
 * @author WB
 */
public class Generic_Dialog_Example implements PlugIn, JavaDocHelp {

	private static String Title = "Untitled";
	private static int Width = 512;
	private static int Height = 512;

	public void run(String arg) {
		GenericDialog gd = new GenericDialog("New Image");
		gd.addHelp(getJavaDocUrl());
		gd.addStringField("Title:", Title);
		gd.addNumericField("Width:", Width, 0);
		gd.addNumericField("Height:", Height, 0);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return;
		
		Title = gd.getNextString();
		Width = (int) gd.getNextNumber();
		Height = (int) gd.getNextNumber();

		ImagePlus imp = NewImage.createByteImage(Title, Width, Height, 1, NewImage.FILL_WHITE);
		imp.show();
	}
}

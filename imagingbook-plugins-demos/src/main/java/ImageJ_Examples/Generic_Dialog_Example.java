/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;

public class Generic_Dialog_Example implements PlugIn {
	static String title = "Untitled";
	static int width = 512;
	static int height = 512;

	public void run(String arg) {
		
		GenericDialog gd = new GenericDialog("New Image");
		gd.addStringField("Title:", title);
		gd.addNumericField("Width:", width, 0);
		gd.addNumericField("Height:", height, 0);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return;
		
		title = gd.getNextString();
		width = (int) gd.getNextNumber();
		height = (int) gd.getNextNumber();

		ImagePlus imp = NewImage.createByteImage(title, width, height, 1, NewImage.FILL_WHITE);
		imp.show();
	}
}

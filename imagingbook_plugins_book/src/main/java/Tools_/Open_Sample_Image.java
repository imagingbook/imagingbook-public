/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Tools_;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.util.ClassUtils.getEnumConstantsSorted;

/**
 * ImageJ plugin, allows the user to select and open one of the internal sample images. The pull-down menu lists all
 * available sample images in alphabetic order. The image is loaded from the associated JAR file.
 *
 * @author WB
 * @see GeneralSampleImage
 * @see ImageResource
 */
public class Open_Sample_Image implements PlugIn, JavaDocHelp {

	@Override
	public void run(String arg) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		GeneralSampleImage[] sortedItems = getEnumConstantsSorted(GeneralSampleImage.class);
		gd.addEnumChoice("Select image", sortedItems, sortedItems[0]);	// available since ImageJ 1.54a
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;

		ImageResource ir = gd.getNextEnumChoice(GeneralSampleImage.class);
		ir.getImagePlus().show();
	}

}

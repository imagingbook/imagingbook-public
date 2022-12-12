/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Tools_;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.common.util.ClassUtils;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

import java.util.Arrays;

/**
 * ImageJ plugin, allows the user to select and open one of the internal sample images. The image is loaded from the
 * associated JAR file.
 *
 * @author WB
 * @see GeneralSampleImage
 */
public class Open_Sample_Image implements PlugIn {

	@Override
	public void run(String arg) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		GeneralSampleImage[] sortedItems = ClassUtils.getEnumConstantsSorted(GeneralSampleImage.class);
		// TODO: replace by new GenericDialog method addEnumChoice(enum[] ...)
		String[] sortedStrings =  Arrays.stream(sortedItems).map(Enum::toString).toArray(String[]::new);
		gd.addChoice("Select", sortedStrings, sortedStrings[0]);

		// GeneralSampleImage init = GeneralSampleImage.values()[0];
		// gd.addEnumChoice("Select", init);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;

		String itemString = gd.getNextChoice();
		ImageResource ir = GeneralSampleImage.valueOf(itemString);
		
		// ImageResource ir = gd.getNextEnumChoice(GeneralSampleImage.class);
		ir.getImagePlus().show();
	}

}

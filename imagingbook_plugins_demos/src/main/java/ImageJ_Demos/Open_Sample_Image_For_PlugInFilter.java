/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package ImageJ_Demos;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;


/**
 * This plugin shows a trick how to automatically load some pre-defined sample image in the case that no other image is
 * currently open. It works by defining a non-empty constructor which is invoked BEFORE ImageJ handles all the rest. All
 * the remaining parts of the plugin remain unchanged. If the specified sample image is not of the proper type is will
 * still be opened but the problem is subsequently detected by the setup() method.
 *
 * @author WB
 */
public class Open_Sample_Image_For_PlugInFilter implements PlugInFilter, JavaDocHelp {

	/**
	 * Trick, using the (normally empty) constructor to check if a suitable image is already open or not, BEFORE
	 * ImageJ's internal machinery jumps in. In this case we open an image from an internal resource, but any other
	 * action could be performed. If this is not needed, just remove the constructor or make sure a suitable image is
	 * always open. The constructor must be public.
	 */
	public Open_Sample_Image_For_PlugInFilter() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.MapleLeafSmall);
		}
	}
	
	private ImagePlus im;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (im != null) {
			IJ.log("processing " + im.getTitle());
		}
	}
	
}

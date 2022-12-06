/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch05_Edges_Contours;

import Ch16_ColorEdges.Color_Edges_Canny;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.edges.CannyEdgeDetector;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin showing the use of the Canny edge detector in its simplest form. It works on all image types. This
 * plugin simply delegates to another plugin (@link Color_Edges_Canny}). The original image is not modified. See Sec.
 * 5.5 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @see CannyEdgeDetector
 * @see Color_Edges_Canny
 */
public class Canny_Edges implements PlugInFilter {

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Canny_Edges() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Boats);
		}
	}
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_ALL + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		// delegate to another plugin:
		IjUtils.runPlugInFilter(Color_Edges_Canny.class);
	}
}

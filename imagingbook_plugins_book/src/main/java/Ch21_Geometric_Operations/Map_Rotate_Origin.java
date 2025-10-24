/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch21_Geometric_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, rotates the current image by a specified angle around the coordinate origin. See Sec. 21.1.1 of [1]
 * for details. Optionally opens a sample image if no image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see ImageMapper
 * @see Rotation2D
 */
public class Map_Rotate_Origin implements PlugInFilter, JavaDocHelp {
	
	private static double alphaDeg = 15.0; 	// rotation angle (15 degrees)
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Rotate_Origin() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Clown);
		}
	}

	@Override
    public int setup(String arg, ImagePlus im) {
        return DOES_ALL;	// works for all image types
    }

	@Override
    public void run(ImageProcessor ip) {
		double alpha =  Math.toRadians(alphaDeg);
		Mapping2D mi = new Rotation2D(alpha).getInverse(); // inverse mapping (target to source)
		new ImageMapper(mi).map(ip);
    }
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch21_Geometric_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, applies a continuous translation to the current image. See Sec. 21.1.1 of [1] for details. Optionally
 * opens a sample image if no image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see ImageMapper
 * @see Translation2D
 */
public class Map_Translate implements PlugInFilter, JavaDocHelp {
	
	private static double dx = 5.25;
	private static double dy = 7.3;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Translate() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Kepler);
		}
	}
	
    @Override
	public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
	public void run(ImageProcessor ip) {

		Translation2D imap = new Translation2D(dx, dy).getInverse();
		new ImageMapper(imap, OutOfBoundsStrategy.ZeroValues, InterpolationMethod.Bicubic).map(ip);
    }

}

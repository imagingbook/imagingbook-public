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
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, applies a non-linear "ripple" transformation to the current image. See Sec. 21.1.7 of [1] for details.
 * Optionally opens a sample image if no image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see ImageMapper
 * @see Mapping2D
 */
public class Map_Nonlinear_Ripple implements PlugInFilter, JavaDocHelp {
	
	// transformation parameters:
	private static double aX = 10;
	private static double aY = 10;
	private static double tauX = 120 / (2 * Math.PI);
	private static double tauY = 250 / (2 * Math.PI);
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Nonlinear_Ripple() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {

		Mapping2D imap = new Mapping2D() {
			@Override
			public Pnt2d applyTo(Pnt2d uv) {
				final double u = uv.getX();
				final double v = uv.getY();
				double x = u + aX * Math.sin(v / tauX);
				double y = v + aY * Math.sin(u / tauY);
				return Pnt2d.from(x, y);
			}
		};
		
		new ImageMapper(imap, OutOfBoundsStrategy.DefaultValue, InterpolationMethod.Bicubic).map(ip);
	}

}

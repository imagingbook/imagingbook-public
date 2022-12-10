/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
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
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, applies a non-linear "twirl" transformation to the current
 * image. See Sec. 21.1.7 (Exercise 21.9 and Fig. 21.18) of [1] for details.
 * Optionally opens a sample image if no image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/28
 * 
 * @see ImageMapper
 * @see Mapping2D
 */
public class Map_Nonlinear_Twirl implements PlugInFilter {
	
	private static double alpha = Math.toRadians(43.0); 	// angle (43 degrees)

	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Nonlinear_Twirl() {
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
		final double xc = 0.5 * ip.getWidth();
		final double yc = 0.5 * ip.getHeight();
		final double rmax = Math.sqrt(xc * xc + yc * yc);
		
		Mapping2D imap = new Mapping2D() {	// inverse mapping (target to source)
			@Override
			public Pnt2d applyTo(Pnt2d uv) {
				double dx = uv.getX() - xc;
				double dy = uv.getY() - yc;
				double r = Math.sqrt(dx * dx + dy * dy);
				if (r < rmax) {
					double beta = Math.atan2(dy, dx) + alpha * (rmax - r) / rmax;
					double x = xc + r * Math.cos(beta);
					double y = yc + r * Math.sin(beta);
					return Pnt2d.from(x, y);
				}
				else {
					return uv;	// return the original point
				}
			}
		};
		
		new ImageMapper(imap, OutOfBoundsStrategy.ZeroValues, InterpolationMethod.Bicubic).map(ip);
	}
	
}

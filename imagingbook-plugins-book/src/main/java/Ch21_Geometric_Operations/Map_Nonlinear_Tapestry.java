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
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, applies a non-linear "tapestry" transformation to the current
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
public class Map_Nonlinear_Tapestry implements PlugInFilter {

	private static final double a = 5.0;
	private static final double tx = 30;
	private static final double ty = 30;
	
		
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Nonlinear_Tapestry() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower_jpg);
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
		
		Mapping2D imap = new Mapping2D() {
			final double bx = 2 * Math.PI / tx;
			final double by = 2 * Math.PI / ty;
			
			@Override
			public Pnt2d applyTo(Pnt2d pnt) {
				double x = pnt.getX();
				double y = pnt.getY();
				double xx = x + a * Math.sin((x - xc) * bx);	// yes, both are sin()!
				double yy = y + a * Math.sin((y - yc) * by);
				return Pnt2d.from(xx,  yy);
			}
		};
		
		new ImageMapper(imap, null, InterpolationMethod.Bicubic).map(ip);
	}

}

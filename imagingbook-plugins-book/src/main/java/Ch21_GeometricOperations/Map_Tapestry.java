/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Ch21_GeometricOperations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.interpolation.InterpolationMethod;

/**
 * Nonlinear "tapestry" mapping, as described in book.
 * 
 * @author WB
 * @version 2021/10/04
 *
 */
public class Map_Tapestry implements PlugInFilter {

	private static final double a = 5.0;
	private static final double tx = 30;
	private static final double ty = 30;
	
			
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

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

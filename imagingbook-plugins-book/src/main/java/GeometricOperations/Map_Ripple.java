/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package GeometricOperations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.interpolation.InterpolationMethod;

public class Map_Ripple implements PlugInFilter {
	
	static double aX = 10;
	static double aY = 10;
	static double tauX = 120 / (2 * Math.PI);
	static double tauY = 250 / (2 * Math.PI);
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

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
		
		new ImageMapper(imap, OutOfBoundsStrategy.ZeroValues, InterpolationMethod.Bicubic).map(ip);
	}

}

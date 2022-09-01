/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package GeometricOperations;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.interpolation.InterpolationMethod;

public class Map_Spherical implements PlugInFilter {
	
	static double rho = 1.8;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		final double xc = 0.5 * ip.getWidth();
		final double yc = 0.5 * ip.getHeight();
		final double rmax = Math.min(xc, yc); //Math.sqrt(xc * xc + yc * yc);
		final double rmax2 = sqr(rmax);
		final double rhoFac = (1.0 - 1.0 / rho);

		Mapping2D imap = new Mapping2D() {
			
			@Override
			public Pnt2d applyTo(Pnt2d uv) {
				double u = uv.getX();
				double v = uv.getY();
				double dx = u - xc;
				double dy = v - yc;
				double dx2 = sqr(dx);
				double dy2 = sqr(dy);
				double r2 = dx2 + dy2;

				if (r2 < rmax2) {
					double z2 = rmax2 - r2;
					double z = sqrt(z2);

					double betaX = rhoFac * Math.asin(dx / sqrt(dx2 + z2));
					double x = u - z * Math.tan(betaX);

					double betaY = rhoFac * Math.asin(dy / sqrt(dy2 + z2));
					double y = v - z * Math.tan(betaY);
					return Pnt2d.from(x, y);
				}
				else { // otherwise leave point unchanged
					return uv;
				}
			}
		};
		
		new ImageMapper(imap, OutOfBoundsStrategy.ZeroValues, InterpolationMethod.Bicubic).map(ip);
	}
}
